package myRobCon.visu.panels;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import myRobCon.MyRob;

public class GripperPanel extends BorderPane{
	private MyRob robot;
	private Button btnInitGRIP;
	private Button btnInitVERT;
	private Button btnUp;
	private Button btnDown;
	private Button btnOpen;
	private Button btnClose;
	private Button btnMoveToGRIP;
	private Button btnMoveToVERT;
	private Button btnActive;
	private Button btnDeactive;
	private Button btnGetPosVERT;
	private Button btnGetPosGRIP;
	private Button btnServoActive;
	private Button btnServoWrite;
	private Button btnServoRefresh;
	private TextField txtPosGRIP;
	private TextField txtPosVERT;
	private TextField txtPosServo;
	private GridPane gridPane;
	
	public GripperPanel(MyRob myrob) {
		this.robot = myrob;
		gridPane = new GridPane();
		
		btnInitGRIP = new Button("Init GRIP");
		btnInitGRIP.setOnAction(this::initGRIP);
		presetButton(btnInitGRIP, 0, 0);
		
		btnInitVERT = new Button("Init VERT");
		btnInitVERT.setOnAction(this::initVERT);
		presetButton(btnInitVERT, 1, 0);
		
		// Vertical
		btnUp = new Button("Up");
		btnUp.setOnMousePressed(this::controlUp);
		btnUp.setOnMouseReleased(this::controlStop);
		presetButton(btnUp, 0, 1);
		
		btnDown = new Button("Down");
		btnDown.setOnMousePressed(this::controlDown);
		btnDown.setOnMouseReleased(this::controlStop);
		presetButton(btnDown, 1, 1);
		
		btnMoveToVERT = new Button("Set");
		btnMoveToVERT.setOnAction(this::controlVertAbs);
		presetButton(btnMoveToVERT, 2, 1);
		
		txtPosVERT = new TextField("0");
		presetTextField(txtPosVERT, 3, 1);
		
		btnGetPosVERT = new Button("getPos");
		btnGetPosVERT.setOnAction(this::getPosVERT);
		presetButton(btnGetPosVERT, 4, 1);
		
		//Gripper
		btnOpen = new Button("Open");
		btnOpen.setOnMousePressed(this::controlOpen);
		btnOpen.setOnMouseReleased(this::controlStop);
		presetButton(btnOpen, 0, 2);
		
		btnClose = new Button("Close");
		btnClose.setOnMousePressed(this::controlClose);
		btnClose.setOnMouseReleased(this::controlStop);
		presetButton(btnClose, 1, 2);
		
		btnMoveToGRIP = new Button("Set");
		btnMoveToGRIP.setOnAction(this::controlGripAbs);
		presetButton(btnMoveToGRIP, 2, 2);
		
		txtPosGRIP = new TextField("0");
		presetTextField(txtPosGRIP, 3, 2);
		
		btnGetPosGRIP = new Button("getPos");
		btnGetPosGRIP.setOnAction(this::getPosGRIP);
		presetButton(btnGetPosGRIP, 4, 2);
		
		btnActive = new Button("active");
		btnActive.setOnAction(this::activeStepper);
		presetButton(btnActive, 0, 3);
		
		btnDeactive = new Button("deactive");
		btnDeactive.setOnAction(this::deactiveStepper);
		presetButton(btnDeactive, 1, 3);
		
		
		btnServoActive = new Button("deactive Servo");
		btnServoActive.setOnAction(this::deactiveServo);
		presetButton(btnServoActive, 0, 4);
		
		btnServoWrite = new Button("Servo write");
		btnServoWrite.setOnAction(this::writeServo);
		presetButton(btnServoWrite, 1, 4);
		
		txtPosServo = new TextField("50");
		presetTextField(txtPosServo, 2, 4);
		
		btnServoRefresh = new Button("Servo refresh");
		btnServoRefresh.setOnAction(this::refreshServo);
		presetButton(btnServoRefresh, 3, 4);
		
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		gridPane.setPadding(new Insets(10));
	    
		gridPane.setBackground(
				new Background(new BackgroundFill(new Color(0, 0, 1, 0.5), new CornerRadii(10), new Insets(0))));
		
	    setTop(gridPane);
	    BorderPane.setMargin(gridPane, new Insets(10));
	    setVisible(false);
	}
	
	public void presetButton(Button btn, int col, int row) {
		btn.setPrefHeight(55.0);
		btn.setPrefWidth(55.0);
		btn.setMinHeight(55.0);
		btn.setMinWidth(55.0);
		btn.setTextAlignment(TextAlignment.CENTER);
		btn.setWrapText(true);
		gridPane.add(btn, col, row);
	}
	
	public void presetTextField(TextField txt, int col, int row) {
		txt.setPrefHeight(55.0);
		txt.setPrefWidth(55.0);
		txt.setMinHeight(55.0);
		txt.setMinWidth(55.0);
		gridPane.add(txt, col, row);
	}
	
	protected void initGRIP(ActionEvent event) {
		robot.getGripper().initGRIP();
	}
	
	protected void initVERT(ActionEvent event) {
		robot.getGripper().initVERT();
	}
	
	protected void controlUp(MouseEvent event) {
		robot.getGripper().moveRelVERT(-900);
	}
	
	protected void controlDown(MouseEvent event) {
		robot.getGripper().moveRelVERT(900);
	}
	
	protected void controlOpen(MouseEvent event) {
		robot.getGripper().moveRelGRIP(900);
	}
	
	protected void controlClose(MouseEvent event) {
		robot.getGripper().moveRelGRIP(-900);
	}
	
	protected void controlGripAbs(ActionEvent event) {
		robot.getGripper().moveAbsGRIP(Integer.parseInt(txtPosGRIP.getText()));
	}
	
	protected void controlVertAbs(ActionEvent event) {
		robot.getGripper().moveAbsVERT(Integer.parseInt(txtPosVERT.getText()));
	}
	
	protected void activeStepper(ActionEvent event) {
		robot.getGripper().setStayActivStepper(true);
	}
	
	protected void deactiveStepper(ActionEvent event) {
		robot.getGripper().setStayActivStepper(false);
	}
	
	protected void getPosVERT(ActionEvent event) {
		robot.getGripper().getPosVERT();
	}
	
	protected void getPosGRIP(ActionEvent event) {
		robot.getGripper().getPosGRIP();
	}
	
	protected void deactiveServo(ActionEvent event) {
		robot.getGripper().setActivServo(false);
	}
	
	protected void writeServo(ActionEvent event) {
		robot.getGripper().writeServo(Integer.parseInt(txtPosServo.getText()));
	}
	
	protected void refreshServo(ActionEvent event) {
		robot.getGripper().refreshServo();
	}

	protected void controlStop(Event event) {
		robot.getGripper().stopAll();
	}
}