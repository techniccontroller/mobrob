package myRobCon;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class VisuSettings extends BorderPane{
	
	public VisuSettings(Stage stage, MyRob robot) {

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		Font titleFont = Font.font("Tahoma", FontWeight.NORMAL, 15);
		
		Text titleRobot = new Text("Robot");
		titleRobot.setFont(titleFont);
		grid.add(titleRobot, 0, 0, 2, 1);

		Label lblIPRobot = new Label("IP:");
		grid.add(lblIPRobot, 0, 1);

		TextField txtIPRobot = new TextField();
		txtIPRobot.setPrefWidth(150);
		txtIPRobot.setText(robot.getIpaddress());
		grid.add(txtIPRobot, 1, 1, 2, 1);
		
		
		Text titleCamera = new Text("Camera Server");
		titleCamera.setFont(titleFont);
		grid.add(titleCamera, 0, 2, 2, 1);

		Label lblPortCamera = new Label("Port:");
		grid.add(lblPortCamera, 0, 3);

		TextField txtPortCamera = new TextField();
		txtPortCamera.setPrefWidth(70);
		txtPortCamera.setText("" + robot.getCamera().getPort());
		grid.add(txtPortCamera, 1, 3);

		
		Text titleLaser = new Text("Laserscanner Server");
		titleLaser.setFont(titleFont);
		grid.add(titleLaser, 0, 4, 2, 1);

		Label lblPortLaser = new Label("Port:");
		grid.add(lblPortLaser, 0, 5);

		TextField txtPortLaser = new TextField();
		txtPortLaser.setPrefWidth(70);
		txtPortLaser.setText("" + robot.getLsscanner().getPort());
		grid.add(txtPortLaser, 1, 5);
		
		
		Text titleActuator = new Text("Actuator Server");
		titleActuator.setFont(titleFont);
		grid.add(titleActuator, 0, 6, 2, 1);

		Label lblPortActuator = new Label("Port:");
		grid.add(lblPortActuator, 0, 7);

		TextField txtPortActuator = new TextField();
		txtPortActuator.setPrefWidth(70);
		txtPortActuator.setText("" + robot.getActuator().getPort());
		grid.add(txtPortActuator, 1, 7);
		
		Text titleEGO = new Text("EGOPose Server");
		titleEGO.setFont(titleFont);
		grid.add(titleEGO, 0, 8, 2, 1);

		Label lblPortEGO = new Label("Port:");
		grid.add(lblPortEGO, 0, 9);

		TextField txtPortEGO = new TextField();
		txtPortEGO.setPrefWidth(70);
		txtPortEGO.setText("" + robot.getEgoSensor().getPort());
		grid.add(txtPortEGO, 1, 9);
		
		setTop(grid);
		
		BorderPane bottomPane = new BorderPane();
		bottomPane.setPadding(new Insets(10, 10, 10, 10));
		
		Button btnOk = new Button("OK");
		btnOk.setPrefWidth(100);
		btnOk.setOnAction(e -> {
			robot.setIpaddress(txtIPRobot.getText());
			robot.getCamera().setPort(Integer.valueOf(txtPortCamera.getText()));
			robot.getLsscanner().setPort(Integer.valueOf(txtPortLaser.getText()));
			robot.getActuator().setPort(Integer.valueOf(txtPortActuator.getText()));
			robot.getEgoSensor().setPort(Integer.valueOf(txtPortEGO.getText()));
			System.out.println("Settings saved");
			stage.close();
		});
		bottomPane.setRight(btnOk);
		
		Button btnCancel = new Button("Cancel");
		btnCancel.setPrefWidth(100);
		btnCancel.setOnAction(e -> stage.close());
		bottomPane.setLeft(btnCancel);
		
		setBottom(bottomPane);
	}
}
