package myRobCon;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VisuMenuBar extends MenuBar {

	private MyRob robot;
	private VisuGUI visu;
	private MenuItem menuConRobot;
	private MenuItem menuConLaser;
	private MenuItem menuConCamera;
	private MenuItem menuConEGO;
	private MenuItem menuSettings;

	public VisuMenuBar(VisuGUI visu) {
		this.robot = visu.getRobot();
		this.visu = visu;

		Menu menu = new Menu("Connection");
		menuConRobot = new MenuItem("Connect Actuators");
		menuConRobot.setOnAction(this::connectActuator);
		menuConLaser = new MenuItem("Connect Laserscanner");
		menuConLaser.setOnAction(this::startLaser);
		menuConCamera = new MenuItem("Connect Camera");
		menuConCamera.setOnAction(this::startCamera);
		menuConEGO = new MenuItem("Connect EGOPose Sensor");
		menuConEGO.setOnAction(this::startEGOPoseSensor);
		menuSettings = new MenuItem("Settings");
		menuSettings.setOnAction(this::openSettings);

		menu.getItems().add(menuConRobot);
		menu.getItems().add(menuConLaser);
		menu.getItems().add(menuConCamera);
		menu.getItems().add(menuConEGO);
		menu.getItems().add(menuSettings);

		getMenus().add(menu);
	}

	protected void startCamera(ActionEvent event) {
		int res = robot.getCamera().initCameraSocket();
		if (res == 0) {
			robot.getCamera().startCameraThread();
			menuConCamera.setText("Disconnect Camera");
		} else if (res == 1) {
			robot.getCamera().stopCameraThread();
			robot.getCamera().closeCameraSocket();
			menuConCamera.setText("Connect Camera");
		}
	}

	protected void startLaser(ActionEvent event) {
		int res = robot.getLsscanner().initLaserscannerSocket();
		if (res == 0) {
			robot.getLsscanner().startLaserscannerThread();
			robot.getLsscanner().startDisplayThread();
			menuConLaser.setText("Disconnect Laserscanner");
		} else if (res == 1) {
			robot.getLsscanner().stopDisplayThread();
			robot.getLsscanner().stopLaserscannerThread();
			robot.getLsscanner().closeLaserSocket();
			menuConLaser.setText("Connect Laserscanner");
		}
	}

	protected void connectActuator(ActionEvent event) {
		int res = robot.getActuator().initActuatorSocket();
		if (res == 0) {
			visu.getControlPanel().setVisible(true);
			menuConRobot.setText("Disconnect Actuator");
		} else if (res == 1) {
			robot.getActuator().closeActuatorSocket();
			visu.getControlPanel().setVisible(false);
			menuConRobot.setText("Connect Actuator");
		}
	}
	
	protected void startEGOPoseSensor(ActionEvent event) {
		int res = robot.getEgoSensor().initSensorSocket();
		if (res == 0) {
			robot.getEgoSensor().startSensorThread();
			menuConEGO.setText("Disconnect EGOPose Sensor");
		} else if (res == 1) {
			robot.getEgoSensor().stopSensorThread();
			robot.getEgoSensor().closeSensorSocket();
			menuConEGO.setText("Connect EGOPose Sensor");
		}
	}

	protected void openSettings(ActionEvent event) {
		Stage popupwindow = new Stage();

		popupwindow.initModality(Modality.APPLICATION_MODAL);
		popupwindow.setTitle("Settings");

		VisuSettings settingsPane = new VisuSettings(popupwindow, robot);

		Scene scene1 = new Scene(settingsPane, 250, 400);

		popupwindow.setScene(scene1);

		popupwindow.showAndWait();
	}
}
