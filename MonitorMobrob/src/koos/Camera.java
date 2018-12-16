package koos;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import application.FXController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

public class Camera {
	
	private String ipaddress;
	private int port;
	
	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
	private ScheduledFuture<?> timerCam;
	
	// Socket
	private Socket clientSocketCam;
	private OutputStreamWriter outToServerCam;
	private InputStreamReader inFromServerCam;
	
	private boolean cameraActive = false;
	
	private FXController fxcontroller;
	
	private BufferedImage cameraFrame;
	private Object lock = new Object();
	
	public Camera(String ip, int port, FXController fxcontroller) {
		this.fxcontroller = fxcontroller;
		this.ipaddress = ip;
		this.port = port;
	}
	
	public int startCameraSocket() {
		if (!this.cameraActive) {

			try {
				if (clientSocketCam == null || clientSocketCam.isClosed()) {
					clientSocketCam = new Socket(ipaddress, port);
					System.out.println("Create Cam socket...");
					outToServerCam = new OutputStreamWriter(clientSocketCam.getOutputStream());
					inFromServerCam = new InputStreamReader(clientSocketCam.getInputStream());
				}

				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run() {
						String message;

						message = "getNewFrame";

						try {
							if (cameraActive) {
								long starttime = System.currentTimeMillis();
								outToServerCam.write(message);
								outToServerCam.flush();
								char[] sizeAr = new char[16];
								inFromServerCam.read(sizeAr);
								System.out.println(System.currentTimeMillis() - starttime);
								int size = Integer.valueOf(new String(sizeAr).trim());
								char[] data = new char[size];
								int pos = 0;
								do {
									int read = inFromServerCam.read(data, pos, size - pos);
									// check for end of file or error
									if (read == -1) {
										break;
									} else {
										pos += read;
									}
								} while (pos < size);
								String encoded = new String(data);
								byte[] decoded = Base64.getDecoder().decode(encoded);
								synchronized (lock) {
									cameraFrame = ImageIO.read(new ByteArrayInputStream(decoded));
									cameraFrame = ImageProcessing.process(cameraFrame);
									// convert and show the frame
								
									Image imageToShow = SwingFXUtils.toFXImage(cameraFrame, null);
									fxcontroller.updateCameraImageView(imageToShow);
								}
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.err.println("Error while reading camera stream: " + e.getMessage());
						}
					}

				};
				this.timerCam = this.pool.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				this.cameraActive = true;
				return 0;
			} catch (IOException e) {
				System.err.println("Not able to open the camera connection...");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Camera Connection");
				alert.setContentText("Not able to open the camera connection.");
				alert.showAndWait();
				return -1;
			}
		} else {
			return 1;

		}
	}
	
	public void stopCameraSocket()

	{
		if (this.timerCam != null && !this.timerCam.isDone()) {
			// stop the timer
			this.timerCam.cancel(true);
		}

		try {
			if (timerCam != null) {
				while (!timerCam.isDone())
					;
				if (!clientSocketCam.isClosed()) {
					if (fxcontroller.showServerConfirmation("Camera") == 1) {
						outToServerCam.write("closeDriver");
						outToServerCam.flush();
					}
					;
					System.out.println("Close Cam Socket...");
					clientSocketCam.shutdownOutput();
					clientSocketCam.close();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.cameraActive = false;
	}

	public BufferedImage getCameraFrame() {
		synchronized (lock) {
			return cameraFrame;
		}
	}
}
