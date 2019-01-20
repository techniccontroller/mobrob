package application;


import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import koos.Robot;
import myRobCon.KOOSCanvas;
import utils.Utils;

public class FXController {
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
	private Pane paneJoystick;
	@FXML
	private ImageView currentFrame;
	@FXML
	private KOOSCanvas koosCanvas;
	@FXML
	private Text joystickText;
	@FXML
	private Circle joystick;
	@FXML
	private Circle joystickBackground;
	@FXML
	private StackPane koosPane;
	@FXML
	private MenuItem menuConRobot;
	@FXML
	private MenuItem menuConLaser;
	@FXML
	private MenuItem menuConCamera;
	
	private Robot robot;

	private double mouseX, mouseY;
	private double startposX, startposY;
	
	private KeyCode previousKey = null;
	
	private int lastangle = 0;
	private int lastdist = 0;
	private enum ControlMode{TRANSLATION, ROTATION};
	private ControlMode controlMode = ControlMode.TRANSLATION;
	
	
	public void setRobot(Robot robot) {
		this.robot = robot;
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
		
		joystickBackground.setOnMouseClicked(event -> {
			if(controlMode == ControlMode.TRANSLATION) {
				controlMode = ControlMode.ROTATION;
				joystickText.setText("R");
			}else {
				controlMode = ControlMode.TRANSLATION;
				joystickText.setText("T");
			}
		});
		
		joystick.setOnMousePressed(evt -> {
			mouseX = evt.getScreenX();
			mouseY = evt.getScreenY();
		});
		joystickText.setOnMousePressed(evt -> {
			mouseX = evt.getScreenX();
			mouseY = evt.getScreenY();
		});
		
		
		EventHandler<MouseEvent> mouseeventHandlerDragged = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseevent) {
				double dx = mouseevent.getScreenX() - mouseX;
				double dy = mouseevent.getScreenY() - mouseY;
				int angle = (int)(Math.toDegrees(Math.atan2(dx, dy))/10)*10;
				int dist = (int) (Math.min(Math.sqrt(dx*dx+dy*dy), 80));
				joystick.setTranslateX(dist*Math.sin(Math.toRadians(angle)));
				joystick.setTranslateY(dist*Math.cos(Math.toRadians(angle)));
				joystickText.setTranslateX(dist*Math.sin(Math.toRadians(angle)));
				joystickText.setTranslateY(dist*Math.cos(Math.toRadians(angle)));
				switch(controlMode) {
				case TRANSLATION:
					if(angle > 0) {
						angle = angle-180;
					}else {
						angle = angle+180;
					}
					if((dist > 10 && dist != lastdist) || angle != lastangle) {
						robot.getActuator().speed(60*dist/80, -angle, 0);
					}else if (dist <= 10) {
						robot.getActuator().stop();
					}
					
					lastangle = angle;
					lastdist = dist;
					break;
				case ROTATION:
					if((dist > 10 && dist != lastdist) || angle != lastangle) {
						robot.getActuator().speed((int)(-dy), 0, (int) -dx/2);
					}else if (dist <= 10) {
						robot.getActuator().stop();
					}
					
					break;
				}
			}
		};
		joystick.setOnMouseDragged(mouseeventHandlerDragged);
		joystickText.setOnMouseDragged(mouseeventHandlerDragged);
		
		joystick.setOnMouseReleased(evt -> {
			this.joystick.setTranslateX(0);
			this.joystick.setTranslateY(0);
			this.joystickText.setTranslateX(0);
			this.joystickText.setTranslateY(0);
			robot.getActuator().stop();
		});
		joystickText.setOnMouseReleased(evt -> {
			this.joystick.setTranslateX(0);
			this.joystick.setTranslateY(0);
			this.joystickText.setTranslateX(0);
			this.joystickText.setTranslateY(0);
			robot.getActuator().stop();
		});
		
		paneControl.setVisible(false);
		paneJoystick.setVisible(false);
		
	}

	@FXML
	protected void controlForward(MouseEvent event) {
		robot.getActuator().speed(60,0,0);
	}
	@FXML
	protected void controlBackward(MouseEvent event) {
		robot.getActuator().speed(60,180,0);
	}
	@FXML
	protected void controlLeft(MouseEvent event) {
		robot.getActuator().speed(60,-90,0);
	}
	@FXML
	protected void controlRight(MouseEvent event) {
		robot.getActuator().speed(60,90,0);
	}
	@FXML
	protected void controlTurnLeft(MouseEvent event) {
		robot.getActuator().speed(0, 0, 60);
	}
	@FXML
	protected void controlTurnRight(MouseEvent event) {
		robot.getActuator().speed(0, 0, -60);
	}
	@FXML
	protected void controlStop(Event event) {
		robot.getActuator().stop();
		previousKey = null;
	}
	
	@FXML
	protected void controlKeyInput(KeyEvent ke) {
		System.out.println("Key Pressed: " + previousKey + "-> " + ke.getCode());
		if(previousKey == null || previousKey != ke.getCode()) {
			if(ke.getCode() == KeyCode.W) {
				robot.getActuator().speed(60,0,0);
			} else if(ke.getCode() == KeyCode.S) {
				robot.getActuator().speed(60,180,0);
			} else if(ke.getCode() == KeyCode.A) {
				robot.getActuator().speed(60,-90,0);
			} else if(ke.getCode() == KeyCode.D) {
				robot.getActuator().speed(60,90,0);
			} else if(ke.getCode() == KeyCode.Q) {
				robot.getActuator().speed(0,0,60);
			} else if(ke.getCode() == KeyCode.E) {
				robot.getActuator().speed(0,0,-60);
			}
			previousKey = ke.getCode();
		}
	}
	

	@FXML
	protected void startCamera(ActionEvent event) {
		int res = robot.getCamera().startCameraSocket();
		if (res == 0) {
			menuConCamera.setText("Disconnect Camera");
		} 
		else if(res == 1) {
			robot.getCamera().stopCameraSocket();
			menuConCamera.setText("Connect Camera");
		}
	}

	@FXML
	protected void startLaser(ActionEvent event) {
		int res = robot.getLsscanner().startLaserscannerSocket();
		if (res == 0) {
			menuConLaser.setText("Disconnect Laserscanner");
		} 
		else if(res == 1) {
			robot.getLsscanner().stopLaserscannerSocket();
			menuConLaser.setText("Connect Laserscanner");
		}
		koosCanvas.clear();
	}
	
	@FXML
	protected void connectRobot(ActionEvent event) {
		int res = robot.getActuator().startActuatorSocket();
		if(res == 0) {
			paneControl.setVisible(true);
			paneJoystick.setVisible(true);
			menuConRobot.setText("Disconnect Robot");
		}
		else if(res == 1) {
			robot.getActuator().stopActuatorSocket();
			paneControl.setVisible(false);
			paneJoystick.setVisible(false);
			menuConRobot.setText("Connect Robot");
		}
	}
	
	@FXML
	protected void showAbout(ActionEvent event) {
		System.out.println("About");
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
		robot.getCamera().stopCameraSocket();
		robot.getLsscanner().stopLaserscannerSocket();
		robot.getActuator().stopActuatorSocket();
	}

	public KOOSCanvas getKoosCanvas() {
		return koosCanvas;
	}
}
