package myRobCon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.paint.Color;

public class LSScanner {

	private String ipaddress;
	private int port;
	private VisuGUI visu;

	private ArrayList<LSScanPoint> scanpoints;
	private ArrayList<LSCluster> clusters;

	private final double DBSCAN_EPS = 100;
	private final int MIN_PTS = 5;

	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
	private ScheduledFuture<?> timerLS;

	private Socket clientSocketLS;
	private OutputStreamWriter outToServerLS;
	private BufferedReader inFromServerLS;

	private boolean laserActive = false;
	private Object lock = new Object();

	public LSScanner(String ip, int port) {
		this.ipaddress = ip;
		this.port = port;
		scanpoints = new ArrayList<LSScanPoint>();
		clusters = new ArrayList<>();
	}

	public int initLaserscannerSocket() {
		if (!laserActive) {
			try {
				if (clientSocketLS == null || clientSocketLS.isClosed()) {
					clientSocketLS = new Socket(ipaddress, port);
					System.out.println("Create LS socket...");
					outToServerLS = new OutputStreamWriter(clientSocketLS.getOutputStream());
					inFromServerLS = new BufferedReader(new InputStreamReader(clientSocketLS.getInputStream()));
				}
				laserActive = true;
				return 0;
			} catch (IOException e) {
				System.err.println("Not able to open the laser connection...");
				laserActive = false;
				return -1;
			}
		} else {
			return 1;
		}
	}

	public int startLaserscannerThread() {
		if (timerLS == null || timerLS.isDone()) {
			initLaserscannerSocket();
			Runnable scanGrabber = new Runnable() {

				@Override
				public void run() {
					String message = "getData";
					try {
						if (laserActive) {
							outToServerLS.write(message);
							outToServerLS.flush();
							String data = inFromServerLS.readLine();
							synchronized (lock) {
								addRawData(data);
								/*
								 * Platform.runLater(new Runnable() {
								 * 
								 * @Override public void run() { visu.getKoosCanvas().clear();
								 * getScanpoints().stream().forEach(sp -> visu.getKoosCanvas()
								 * .drawDataPoint(sp.getX(), sp.getY(), 20, 20, Color.WHITE));
								 * 
								 * getClusters().stream().forEach(c -> { LSScanPoint s = c.getMiddlePoint();
								 * visu.getKoosCanvas().drawDataPoint(s.getX(), s.getY(), 50, 50, Color.RED);
								 * });
								 * 
								 * visu.getKoosCanvas().drawDataPoint(200, 300, 50, 50, Color.YELLOW);
								 * visu.getKoosCanvas().drawDataPoint(2000, -300, 100, 100, Color.YELLOW); } });
								 */
							}
						}
					} catch (IOException e) {
						System.err.println("Error while reading laser stream: " + e.getMessage());
					}
				}

			};
			this.timerLS = this.pool.scheduleAtFixedRate(scanGrabber, 0, 200, TimeUnit.MILLISECONDS);
			return 0;
		} else {
			return 1;
		}
	}

	public void closeLaserSocket() {
		try {
			if (timerLS != null) {
				while (!timerLS.isDone())
					;
			}
			if (!clientSocketLS.isClosed()) {
				if (visu.showServerConfirmation("Laser") == 1) {
					outToServerLS.write("closeDriver");
					outToServerLS.flush();
				}
				System.out.println("Close LS Socket...");
				clientSocketLS.shutdownOutput();
				clientSocketLS.close();
				laserActive = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopLaserscannerThread()

	{
		if (timerLS != null && !timerLS.isDone()) {
			// stop the timer
			timerLS.cancel(true);
		}
		laserActive = false;
	}

	public void addRawData(String data) {
		scanpoints.clear();
		String koordinates[] = data.split(";");
		for (int i = 0; i < koordinates.length; i++) {
			if (koordinates[i].length() > 0) {
				LSScanPoint k = new LSScanPoint(koordinates[i]);
				if (k.getDist() > 0) {
					scanpoints.add(k);
				}
			}
		}
		findClusters();
	}

	public void findClusters() {
		clusters.clear();
		LSCluster cluster = new LSCluster();
		for (int i = 0; i < scanpoints.size() - 1; i++) {
			LSScanPoint currentPoint = scanpoints.get(i);
			LSScanPoint nextPoint = scanpoints.get(i + 1);
			double distBtwTwoPoints = LSScanPoint.distance(currentPoint, nextPoint);
			if (distBtwTwoPoints < DBSCAN_EPS) {
				cluster.addPoint(nextPoint);
			} else {
				if (cluster.size() >= MIN_PTS) {
					clusters.add(cluster);
					cluster = new LSCluster();
				} else {
					cluster.clear();
				}
			}
		}
	}

	public ArrayList<LSScanPoint> getScanpoints() {
		synchronized (lock) {
			return scanpoints;
		}
	}

	public ArrayList<LSCluster> getClusters() {
		return clusters;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setVisu(VisuGUI visu) {
		this.visu = visu;
	}
	
	public void drawRawScanPoints() {
		Platform.runLater(() -> {
			visu.getKoosCanvas().clear();
			getScanpoints().stream()
					.forEach(sp -> visu.getKoosCanvas().drawDataPoint(sp.getX(), sp.getY(), 20, 20, Color.WHITE));

			getClusters().stream().forEach(c -> {
				LSScanPoint s = c.getMiddlePoint();
				visu.getKoosCanvas().drawDataPoint(s.getX(), s.getY(), 50, 50, Color.RED);
			});
		});
	}

	public double checkBox(double x1, double y1, double x2, double y2) {
		final double boundXLow = x1 > x2 ? x2 : x1;
		final double boundXHigh = x1 > x2 ? x1 : x2;
		final double boundYLow = y1 > y2 ? y2 : y1;
		final double boundYHigh = y1 > y2 ? y1 : y2;
		LSScanPoint nearestPoint = getScanpoints().stream().filter(scanpoint -> {
			return (scanpoint.getX() > boundXLow && scanpoint.getX() < boundXHigh)
					&& (scanpoint.getY() > boundYLow && scanpoint.getY() < boundYHigh);
		}).min(Comparator.comparingDouble(LSScanPoint::getDist)).orElse(new LSScanPoint(0, 0));

		Platform.runLater(() -> {
			visu.getKoosCanvas().drawLine(x1, y1, x1, y2, 1, Color.YELLOW);
			visu.getKoosCanvas().drawLine(x1, y2, x2, y2, 1, Color.YELLOW);
			visu.getKoosCanvas().drawLine(x2, y2, x2, y1, 1, Color.YELLOW);
			visu.getKoosCanvas().drawLine(x2, y1, x1, y1, 1, Color.YELLOW);
			if(nearestPoint.getDist() > 0) visu.getKoosCanvas().drawDataPoint(nearestPoint.getX(), nearestPoint.getY(), 50, 50, Color.ORANGE);
		});
		return nearestPoint.getDist();
	}

	public double checkPolar(double angleStart, double angleEnd, double distance) {
		final double boundLow = angleStart < angleEnd ? angleStart : angleEnd;
		final double boundHigh = angleStart < angleEnd ? angleEnd : angleStart;
		LSScanPoint nearestPoint = getScanpoints().stream().filter(scanpoint -> {
			return (scanpoint.getAngle() > boundLow && scanpoint.getAngle() < boundHigh && scanpoint.getDist() < distance && scanpoint.getDist() > 0);
		}).min(Comparator.comparingDouble(LSScanPoint::getDist)).orElse(new LSScanPoint(0, 0));
		
		Platform.runLater(() -> {
			visu.getKoosCanvas().drawLine(0, 0, distance*Math.cos(Math.toRadians(angleStart)), distance*Math.sin(Math.toRadians(angleStart)), 1, Color.YELLOW);
			visu.getKoosCanvas().drawLine(0, 0, distance*Math.cos(Math.toRadians(angleEnd)), distance*Math.sin(Math.toRadians(angleEnd)), 1, Color.YELLOW);
			visu.getKoosCanvas().drawArc(0, 0, distance, angleStart, angleEnd, 1, Color.YELLOW);
			if(nearestPoint.getDist() > 0) visu.getKoosCanvas().drawDataPoint(nearestPoint.getX(), nearestPoint.getY(), 50, 50, Color.ORANGE);
		});
		return nearestPoint.getDist();
	}
}
