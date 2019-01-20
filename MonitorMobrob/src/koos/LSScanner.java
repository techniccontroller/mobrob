package koos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import application.FXController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import myRobCon.KOOSCanvas;

public class LSScanner {
	
	private String ipaddress;
	private int port;
	
	private ArrayList<LSScanPoint> scanpoints;
	private ArrayList<LSCluster> clusters;
	
	private final double DBSCAN_EPS = 100;
	private final int MIN_PTS = 5;
	
	private Timeline timelineLS;
	
	private Socket clientSocketLS;
	private OutputStreamWriter outToServerLS;
	private BufferedReader inFromServerLS;

	private boolean laserActive = false;
	
	private KOOSCanvas koosCanvas;
	private FXController fxcontroller;
	
	public LSScanner(String ip, int port, FXController fxcontroller) {
		this.koosCanvas = fxcontroller.getKoosCanvas();
		this.fxcontroller = fxcontroller;
		this.ipaddress = ip;
		this.port = port;
		scanpoints = new ArrayList<LSScanPoint>();
		clusters = new ArrayList<>();
		koosCanvas.clear();
	}
	
	
	public int startLaserscannerSocket() {

		if (!this.laserActive) {

			try {
				if (clientSocketLS == null || clientSocketLS.isClosed()) {
					clientSocketLS = new Socket(ipaddress, port);
					System.out.println("Create LS socket...");
					outToServerLS = new OutputStreamWriter(clientSocketLS.getOutputStream());
					inFromServerLS = new BufferedReader(new InputStreamReader(clientSocketLS.getInputStream()));
				}

				timelineLS = new Timeline(new KeyFrame(Duration.millis(200), event2 -> {
					String message;

					message = "getData";
					try {
						if (laserActive) {
							outToServerLS.write(message);
							outToServerLS.flush();
							String data = inFromServerLS.readLine();
							addRawData(data);
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									koosCanvas.clear();
									getScanpoints().stream()
											.forEach(sp -> koosCanvas.drawDataPoint(sp.getX(), sp.getY(), 20, 20, Color.WHITE));
							
									getClusters().stream().forEach(c -> {
										LSScanPoint s = c.getMiddlePoint();
										koosCanvas.drawDataPoint(s.getX(), s.getY(), 50, 50, Color.RED);
									});
								}
							});
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.err.println("Error while reading laser stream: " + e.getMessage());
					}
				}));
				timelineLS.setCycleCount(Animation.INDEFINITE);
				timelineLS.play();

				this.laserActive = true;
				return 0;
			} catch (IOException e) {
				System.err.println("Not able to open the laser connection...");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Laser Connection");
				alert.setContentText("Not able to open the laser connection.");
				alert.showAndWait();
				this.laserActive = false;
				return -1;
			}
		} else {
			return 1;

		}
	}
	
	public void stopLaserscannerSocket()

	{

		if (this.timelineLS != null && this.timelineLS.getStatus() == Animation.Status.RUNNING) {
			// stop the timer
			this.timelineLS.stop();
		}

		try {
			if (timelineLS != null) {
				while (this.timelineLS.getStatus() == Animation.Status.RUNNING)
					;
				if (!clientSocketLS.isClosed()) {
					if (fxcontroller.showServerConfirmation("Laser") == 1) {
						outToServerLS.write("closeDriver");
						outToServerLS.flush();
					}
					;
					System.out.println("Close LS Socket...");
					clientSocketLS.shutdownOutput();
					clientSocketLS.close();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.laserActive = false;
		koosCanvas.clear();
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
	
	public ArrayList<LSScanPoint> getScanpoints(){
		return scanpoints;
	}
	
	public ArrayList<LSCluster> getClusters() {
		return clusters;
	}

	public void findClusters() {
		clusters.clear();
		LSCluster cluster = new LSCluster();
		for(int i = 0; i < scanpoints.size()-1; i++) {
			LSScanPoint currentPoint = scanpoints.get(i);
			LSScanPoint nextPoint = scanpoints.get(i+1);
	        double distBtwTwoPoints = LSScanPoint.distance(currentPoint, nextPoint);
	        if (distBtwTwoPoints < DBSCAN_EPS)
	        {
	            cluster.addPoint(nextPoint);
	        }
	        else {
	            if (cluster.size() >= MIN_PTS){
	                clusters.add(cluster);
	                cluster = new LSCluster();
	            }
	            else{
	                cluster.clear();
	            }
	        }
		}
	}
}
