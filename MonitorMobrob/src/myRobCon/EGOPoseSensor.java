package myRobCon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

public class EGOPoseSensor {
	private String ipaddress;
	private int port;
	private VisuGUI visu;

	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
	private ScheduledFuture<?> timerEGO;

	private Socket clientSocketEGO;
	private OutputStreamWriter outToServerEGO;
	private BufferedReader inFromServerEGO;
	
	private double angleR;
	private int xR;
	private int yR;

	private boolean sensorActive = false;
	private boolean firstSample = false;
	private Object lock = new Object();

	public EGOPoseSensor(String ip, int port) {
		this.ipaddress = ip;
		this.port = port;
	}

	public int initSensorSocket() {
		if (!sensorActive) {
			try {
				if (clientSocketEGO == null || clientSocketEGO.isClosed()) {
					clientSocketEGO = new Socket(ipaddress, port);
					System.out.println("Create LS socket...");
					outToServerEGO = new OutputStreamWriter(clientSocketEGO.getOutputStream());
					inFromServerEGO = new BufferedReader(new InputStreamReader(clientSocketEGO.getInputStream()));
				}
				sensorActive = true;
				firstSample = true;
				return 0;
			} catch (IOException e) {
				System.err.println("Not able to open the EGOSensor connection...");
				sensorActive = false;
				return -1;
			}
		} else {
			return 1;
		}
	}

	public int startSensorThread() {
		if (timerEGO == null || timerEGO.isDone()) {
			initSensorSocket();
			visu.getKoosCanvas().setXmax(1000.0);
			Runnable scanGrabber = new Runnable() {

				@Override
				public void run() {
					String message = "getData()";
					try {
						if (sensorActive) {
							outToServerEGO.write(message+"\n");
							outToServerEGO.flush();
							String data = inFromServerEGO.readLine();
							//System.out.println(data);
							synchronized (lock) {
								// Ignore first sample
								if(firstSample) {
									xR = 0;
									yR = 0;
									angleR = 0;
									firstSample = false;
								}else {
									addRawData(data);
								}
								
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										visu.getKoosCanvas().clear();
										visu.getKoosCanvas().drawRobot(xR, yR, angleR);
									}
								});
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.err.println("Error while reading EGOPose sensor stream: " + e.getMessage());
					}
				}

			};
			this.timerEGO = this.pool.scheduleAtFixedRate(scanGrabber, 0, 200, TimeUnit.MILLISECONDS);
			return 0;
		} else {
			return 1;
		}
	}

	public void closeSensorSocket() {
		try {
			if (timerEGO != null) {
				while (!timerEGO.isDone());
			}
			if (!clientSocketEGO.isClosed()) {
				if (visu.showServerConfirmation("EGOPose") == 1) {
					outToServerEGO.write("closeDriver");
					outToServerEGO.flush();
				}
				System.out.println("Close EGOPose Socket...");
				clientSocketEGO.shutdownOutput();
				clientSocketEGO.close();
				sensorActive = false;
				visu.getKoosCanvas().setXmax(4000.0);
				visu.getKoosCanvas().clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addRawData(String data) {
		if(data.length() > 0 && data.contains(";")) {
			String valuesStr[] = data.split(";");
			int dx1 = Integer.valueOf(valuesStr[2]);
			int dy1 = Integer.valueOf(valuesStr[3]);
			int dx2 = Integer.valueOf(valuesStr[0]);
			int dy2 = Integer.valueOf(valuesStr[1]); 
			System.out.format("x1: %4d  y1: %4d  x2: %4d  y2: %d --> diffs: %4d/%4d \n", dx1, dy1, dx2, dy2, (dx1-dx2), (dy1-dy2));
			angleR = angleR + (Math.atan2(dy1-dy2, 470));
			xR = xR + (int) ((dx1+dx2)/2 * Math.cos(angleR)) + (int)((dy1+dy2)/2 * Math.sin(angleR));
			yR = yR - (int) ((dx1+dx2)/2 * Math.sin(angleR)) + (int)((dy1+dy2)/2 * Math.cos(angleR));
		}
	}

	public void stopSensorThread()

	{
		if (timerEGO != null && !timerEGO.isDone()) {
			// stop the timer
			timerEGO.cancel(true);
		}
		sensorActive = false;
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
}
