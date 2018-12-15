package application;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import koos.KOOSCanvas;
import koos.LSScan;
import koos.LSScanPoint;
import utils.Utils;

public class FXController {
	@FXML
	private Button start_btn;
	@FXML
	private Button start_laser_btn;
	@FXML
	private Button btnLeft;
	@FXML
	private Button btnRight;
	@FXML
	private Button btnUp;
	@FXML
	private Button btnDown;
	@FXML
	private Button btnLeftUp;
	@FXML
	private Button btnRightUp;
	@FXML
	private GridPane paneControl;
	@FXML
	private ImageView currentFrame;
	@FXML
	private KOOSCanvas koosCanvas;

	// a timer for acquiring the video stream
	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
	private ScheduledFuture<?> timerCam;
	private ScheduledFuture<?> timerLS;

	private Timeline timeline;

	// a flag to change the button behavior
	private boolean cameraActive = false;
	// a flag to change the button behavior
	private boolean laserActive = false;

	// Socket
	private Socket clientSocketCam;
	private OutputStreamWriter outToServerCam;
	private InputStreamReader inFromServerCam;

	private Socket clientSocketLS;
	private OutputStreamWriter outToServerLS;
	private BufferedReader inFromServerLS;

	private Socket clientSocketCon;
	private OutputStreamWriter outToServerCon;
	private BufferedReader inFromServerCon;

	private LSScan lsscanner = new LSScan();

	private double mouseX, mouseY;
	private double startposX, startposY;

	public void setupControl() {
		paneControl.setBackground(
				new Background(new BackgroundFill(new Color(0, 0, 1, 0.5), new CornerRadii(10), new Insets(0))));

		paneControl.setOnMousePressed(evt -> {
			startposX = this.paneControl.getTranslateX();
			startposY = this.paneControl.getTranslateY();

			mouseX = evt.getScreenX();
			mouseY = evt.getScreenY();
		});
		paneControl.setOnMouseDragged(mouseevent -> {
			this.paneControl.setTranslateX(startposX + mouseevent.getScreenX() - mouseX);
			this.paneControl.setTranslateY(startposY + mouseevent.getScreenY() - mouseY);
		});
	}

	@FXML
	protected void controlForward(ActionEvent event) {
		try {
			if (clientSocketCon.isConnected()) {
				outToServerCon.write("move(60,60,0)\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while reading camera stream: " + e.getMessage());
		}
	}

	protected void startControlSocket() {
		try {
			if (clientSocketCon == null || clientSocketCon.isClosed()) {
				clientSocketCon = new Socket("192.168.0.111", 5054);
				System.out.println("Create Control socket...");
				outToServerCon = new OutputStreamWriter(clientSocketCon.getOutputStream());
				inFromServerCon = new BufferedReader(new InputStreamReader(clientSocketCon.getInputStream()));
			}

		} catch (IOException e) {
			System.err.println("Not able to open the control connection...");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Control Connection");
			alert.setContentText("Not able to open the control connection.");

			alert.showAndWait();
		}
	}

	protected void stopControlSocket() {
		if (!(clientSocketCon == null) && !clientSocketCon.isClosed()) {
			try {
				clientSocketCon.shutdownOutput();
				clientSocketCon.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@FXML
	protected void startCamera(ActionEvent event) {
		if (!this.cameraActive) {

			try {
				if (clientSocketCam == null || clientSocketCam.isClosed()) {
					clientSocketCam = new Socket("192.168.0.111", 5001);
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
								outToServerCam.write(message);
								outToServerCam.flush();
								char[] sizeAr = new char[16];
								inFromServerCam.read(sizeAr);
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

								BufferedImage image = ImageIO.read(new ByteArrayInputStream(decoded));

								// convert and show the frame
								Image imageToShow = SwingFXUtils.toFXImage(image, null);
								updateImageView(currentFrame, imageToShow);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.err.println("Error while reading camera stream: " + e.getMessage());
						}
					}

				};
				this.timerCam = this.pool.scheduleAtFixedRate(frameGrabber, 0, 100, TimeUnit.MILLISECONDS);

				// update the button content
				this.start_btn.setText("Stop Camera");
				this.cameraActive = true;
			} catch (IOException e) {
				System.err.println("Not able to open the camera connection...");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Camera Connection");
				alert.setContentText("Not able to open the camera connection.");

				alert.showAndWait();
			}
		} else {
			// the camera is not active at this point
			this.cameraActive = false;

			// update again the button content
			this.start_btn.setText("Start Camera");

			// stop the timer
			this.stopAcquisitionCam();

		}
	}

	@FXML
	protected void startLaser(ActionEvent event) {

		if (!this.laserActive) {

			try {
				if (clientSocketLS == null || clientSocketLS.isClosed()) {
					clientSocketLS = new Socket("192.168.0.111", 1234);
					System.out.println("Create LS socket...");
					outToServerLS = new OutputStreamWriter(clientSocketLS.getOutputStream());
					inFromServerLS = new BufferedReader(new InputStreamReader(clientSocketLS.getInputStream()));
				}

				timeline = new Timeline(new KeyFrame(Duration.millis(200), event2 -> {
					String message;

					message = "getData";
					try {
						if (laserActive) {
							outToServerLS.write(message);
							outToServerLS.flush();
							String data = inFromServerLS.readLine();
							
							lsscanner.addRawData(data);
							System.out.println(lsscanner.getScanpoints().size());
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									koosCanvas.clear();
									lsscanner.getScanpoints().stream()
											.forEach(sp -> koosCanvas.drawDataPoint(sp.getX(), sp.getY(), 20, 20, Color.WHITE));
							
									lsscanner.getClusters().stream().forEach(c -> {
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
				timeline.setCycleCount(Animation.INDEFINITE);
				timeline.play();

				/*
				 * // grab a frame every 33 ms (30 frames/sec) Runnable frameGrabber = new
				 * Runnable() {
				 * 
				 * @Override public void run() { String message;
				 * 
				 * message = "getData"; try { if (laserActive) { outToServerLS.write(message);
				 * outToServerLS.flush(); String data = inFromServerLS.readLine();
				 * koosCanvas.clear(); lsscanner.addRawData(data);
				 * lsscanner.getScanpoints().stream().forEach(sp ->
				 * koosCanvas.drawDataPoint(sp.getX(), sp.getY(), 20, 20, Color.WHITE));
				 * lsscanner.getClusters().stream().forEach(c -> { LSScanPoint s =
				 * c.getMiddlePoint(); koosCanvas.drawDataPoint(s.getX(), s.getY(),50, 50,
				 * Color.RED); }); } } catch (IOException e) { // TODO Auto-generated catch
				 * block System.err.println("Error while reading laser stream: " +
				 * e.getMessage()); } }
				 * 
				 * }; this.timerLS = this.pool.scheduleAtFixedRate(frameGrabber, 0, 200,
				 * TimeUnit.MILLISECONDS);
				 */

				// update the button content
				this.start_laser_btn.setText("Stop Laser");
				this.laserActive = true;
			} catch (IOException e) {
				System.err.println("Not able to open the laser connection...");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Laser Connection");
				alert.setContentText("Not able to open the laser connection.");
				alert.showAndWait();
			}
		} else {
			// the camera is not active at this point
			this.laserActive = false;

			// update again the button content
			this.start_laser_btn.setText("Start Laser");

			// stop the timer
			this.stopAcquisitionLS();

		}
	}

	private int showServerConfirmation(String type) {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Close Driver");
		alert.setHeaderText("Soll der " + type + " Server geschlossen werden?");

		ButtonType yes = new ButtonType("Ja");
		ButtonType no = new ButtonType("Nein");

		// Remove default ButtonTypes
		alert.getButtonTypes().clear();

		alert.getButtonTypes().addAll(yes, no);

		// option != null.
		Optional<ButtonType> option = alert.showAndWait();

		if (option.get() == null) {
			System.out.println("Close Server: no selection");
			return -1;
		} else if (option.get() == yes) {
			return 1;
		} else if (option.get() == no) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 
	 * Stop the acquisition from the camera and release all the resources
	 * 
	 */
	private void stopAcquisitionCam()

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
					if (showServerConfirmation("Camera") == 1) {
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
	}

	/**
	 * 
	 * Stop the acquisition from the camera and release all the resources
	 * 
	 */
	private void stopAcquisitionLS()

	{

		if (this.timeline != null && this.timeline.getStatus() == Animation.Status.RUNNING) {
			// stop the timer
			this.timeline.stop();
		}

		try {
			if (timeline != null) {
				while (this.timeline.getStatus() == Animation.Status.RUNNING)
					;
				if (!clientSocketLS.isClosed()) {
					if (showServerConfirmation("Laser") == 1) {
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

		/*
		 * if (this.timerLS != null && !this.timerLS.isDone()) { // stop the timer
		 * this.timerLS.cancel(true); }
		 * 
		 * 
		 * try { if (timerLS != null) { while (!timerLS.isDone()); if
		 * (!clientSocketLS.isClosed()) { if(showServerConfirmation("Laser") == 1) {
		 * outToServerLS.write("closeDriver"); outToServerLS.flush(); };
		 * System.out.println("Close LS Socket..."); clientSocketLS.shutdownOutput();
		 * clientSocketLS.close(); } }
		 * 
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	/**
	 * 
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * 
	 * @param view  the {@link ImageView} to update
	 * @param image the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image)

	{
		Utils.onFXThread(view.imageProperty(), image);
	}

	/**
	 * 
	 * On application close, stop the acquisition from the camera
	 * 
	 */
	protected void setClosed()

	{
		this.stopAcquisitionCam();
		this.stopAcquisitionLS();
		this.stopControlSocket();
	}
}
