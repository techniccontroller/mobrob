package myRobCon;

import java.awt.Color;
import java.awt.Graphics2D;
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

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.Font;

public class Camera {

	private String ipaddress;
	private int port;
	private VisuGUI visu;

	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
	private ScheduledFuture<?> timerCam;

	// Socket
	private Socket clientSocketCam;
	private OutputStreamWriter outToServerCam;
	private InputStreamReader inFromServerCam;

	private boolean cameraActive = false;

	private BufferedImage cameraFrame;
	private Object lock = new Object();
	
	private final int height = 480;
	private final int width = 640;

	public Camera(String ip, int port) {
		this.ipaddress = ip;
		this.port = port;
	}

	public BufferedImage getBlankImage(int width, int height) {
		BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = (Graphics2D) bImage.getGraphics();

		// Clear the background with white
		g.setBackground(Color.BLACK);
		g.clearRect(0, 0, width, height);

		// Write some text
		g.setColor(Color.WHITE);
		Font font = new Font("Tahoma", Font.PLAIN, 28);
		g.setFont(font);
		g.drawString("Camera is OFF", width / 2 - 100, height / 2 - 20);

		g.dispose();
		return bImage;
	}

	public int initCameraSocket() {
		if (!cameraActive) {
			try {
				if (clientSocketCam == null || clientSocketCam.isClosed()) {
					clientSocketCam = new Socket(ipaddress, port);
					System.out.println("Create Cam socket...");
					outToServerCam = new OutputStreamWriter(clientSocketCam.getOutputStream());
					inFromServerCam = new InputStreamReader(clientSocketCam.getInputStream());
				}
				cameraActive = true;
				return 0;
			} catch (IOException e) {
				System.err.println("Not able to open the camera connection...");
				cameraActive = false;
				return -1;
			}
		} else {
			return 1;
		}
	}

	public int startCameraThread() {
		if (timerCam == null || timerCam.isDone()) {
			initCameraSocket();

			// grab a frame every 33 ms (30 frames/sec)
			Runnable frameGrabber = new Runnable() {

				@Override
				public void run() {
					
					String message = "getNewFrame";

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
								//cameraFrame = ImageProcessing.process(cameraFrame);

								Image imageToShow = SwingFXUtils.toFXImage(cameraFrame, null);
								visu.updateCameraImageView(imageToShow);
								System.out.println(cameraFrame.getHeight() + " " + cameraFrame.getWidth());
							}
						}
					} catch (IOException e) {
						System.err.println("Error while reading camera stream: " + e.getMessage());
					}
				}

			};
			this.timerCam = this.pool.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
			return 0;
		} else {
			return 1;
		}
	}

	public void closeCameraSocket() {
		try {
			if (timerCam != null) {
				while (!timerCam.isDone());
			}
			if (clientSocketCam != null && !clientSocketCam.isClosed()) {
				if (visu.showServerConfirmation("Camera") == 1) {
					outToServerCam.write("closeDriver");
					outToServerCam.flush();
				}
				System.out.println("Close Cam Socket...");
				clientSocketCam.shutdownOutput();
				clientSocketCam.close();
				Image imageToShow = SwingFXUtils.toFXImage(getBlankImage(width,  height), null);
				visu.updateCameraImageView(imageToShow);
				cameraActive = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopCameraThread()

	{
		if (this.timerCam != null && !this.timerCam.isDone()) {
			// stop the timer
			this.timerCam.cancel(true);
		}
		this.cameraActive = false;
	}

	public BufferedImage getCameraFrame() {
		synchronized (lock) {
			return cameraFrame;
		}
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
