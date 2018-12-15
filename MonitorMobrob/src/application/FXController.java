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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import koos.Actuator;
import koos.Camera;
import koos.KOOSCanvas;
import koos.LSScanner;
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


	private LSScanner lsscanner;
	private Actuator actuator;
	private Camera camera;

	private double mouseX, mouseY;
	private double startposX, startposY;
	
	private KeyCode previousKey = null;

	public void initPeri() {
		lsscanner = new LSScanner("192.168.0.111", 1234, this);
		actuator = new Actuator("192.168.0.111", 5054);
		actuator.startActuatorSocket();
		camera = new Camera("192.168.0.111", 5001, this);
	}
	
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
	protected void controlForward(MouseEvent event) {
		actuator.speed(60,0,0);
	}
	@FXML
	protected void controlBackward(MouseEvent event) {
		actuator.speed(60,180,0);
	}
	@FXML
	protected void controlLeft(MouseEvent event) {
		actuator.speed(60,-90,0);
	}
	@FXML
	protected void controlRight(MouseEvent event) {
		actuator.speed(60,90,0);
	}
	@FXML
	protected void controlTurnLeft(MouseEvent event) {
		actuator.speed(0, 0, 60);
	}
	@FXML
	protected void controlTurnRight(MouseEvent event) {
		actuator.speed(0, 0, -60);
	}
	@FXML
	protected void controlStop(Event event) {
		actuator.stop();
		previousKey = null;
	}
	
	@FXML
	protected void controlKeyInput(KeyEvent ke) {
		
		System.out.println("Key Pressed: " + previousKey + "-> " + ke.getCode());
		if(previousKey == null || previousKey != ke.getCode()) {
			if(ke.getCode() == KeyCode.W) {
				actuator.speed(60,0,0);
			} else if(ke.getCode() == KeyCode.S) {
				actuator.speed(60,180,0);
			} else if(ke.getCode() == KeyCode.A) {
				actuator.speed(60,-90,0);
			} else if(ke.getCode() == KeyCode.D) {
				actuator.speed(60,90,0);
			} else if(ke.getCode() == KeyCode.Q) {
				actuator.speed(0,0,60);
			} else if(ke.getCode() == KeyCode.E) {
				actuator.speed(0,0,-60);
			}
			previousKey = ke.getCode();
		}
		
	}
	

	@FXML
	protected void startCamera(ActionEvent event) {
		int res = camera.startCameraSocket();
		if (res == 0) {
			this.start_btn.setText("Stop Camera");
		} 
		else if(res == 1) {
			camera.stopCameraSocket();
			this.start_btn.setText("Start Camera");
		}
	}

	@FXML
	protected void startLaser(ActionEvent event) {
		int res = lsscanner.startLaserscannerSocket();
		if (res == 0) {
			this.start_laser_btn.setText("Stop Laser");
		} 
		else if(res == 1) {
			lsscanner.stopLaserscannerSocket();
			this.start_laser_btn.setText("Start Laser");
		}
	}

	public int showServerConfirmation(String type) {

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


	public void updateCameraImageView(Image image)

	{
		Utils.onFXThread(currentFrame.imageProperty(), image);
	}

	protected void setClosed() {
		camera.stopCameraSocket();
		lsscanner.stopLaserscannerSocket();
		actuator.stopActuatorSocket();
	}

	public KOOSCanvas getKoosCanvas() {
		return koosCanvas;
	}
}
