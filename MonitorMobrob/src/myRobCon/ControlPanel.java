package myRobCon;


import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ControlPanel extends BorderPane{
	private Button btnLeft;
	private Button btnRight;
	private Button btnUp;
	private Button btnDown;
	private Button btnLeftUp;
	private Button btnRightUp;
	private GridPane gridPane;
	
	private double mouseX, mouseY;
	//private double startposX, startposY;
	
	private enum ControlMode{TRANSLATION, ROTATION};
	
	private MyRob robot;
	
	public ControlPanel(MyRob myrob) {
		this.robot = myrob;
		gridPane = new GridPane();
		
		btnLeftUp = new Button("Turn Left");
		btnLeftUp.setOnMousePressed(this::controlTurnLeft);
		btnLeftUp.setOnMouseReleased(this::controlStop);
		presetButton(btnLeftUp, 0, 0);
		
		btnUp = new Button("For");
		btnUp.setOnMousePressed(this::controlForward);
		btnUp.setOnMouseReleased(this::controlStop);
		presetButton(btnUp, 1, 0);
		
		btnLeft = new Button("Left");
		btnLeft.setOnMousePressed(this::controlLeft);
		btnLeft.setOnMouseReleased(this::controlStop);
		presetButton(btnLeft, 2, 0);
		
		btnRight = new Button("Right");
		btnRight.setOnMousePressed(this::controlRight);
		btnRight.setOnMouseReleased(this::controlStop);
		presetButton(btnRight, 0, 1);
		
		btnDown = new Button("Back");
		btnDown.setOnMousePressed(this::controlBackward);
		btnDown.setOnMouseReleased(this::controlStop);
		presetButton(btnDown, 1, 1);
		
		btnRightUp = new Button("Turn Right");
		btnRightUp.setOnMousePressed(this::controlTurnRight);
		btnRightUp.setOnMouseReleased(this::controlStop);
		presetButton(btnRightUp, 2, 1);
		
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		gridPane.setPadding(new Insets(10));
	    
		gridPane.setBackground(
				new Background(new BackgroundFill(new Color(0, 0, 1, 0.5), new CornerRadii(10), new Insets(0))));

		/*setOnMousePressed(evt -> {
			startposX = getTranslateX();
			startposY = getTranslateY();

			mouseX = evt.getScreenX();
			mouseY = evt.getScreenY();
		});
		setOnMouseDragged(mouseevent -> {
			setTranslateX(startposX + mouseevent.getScreenX() - mouseX);
			setTranslateY(startposY + mouseevent.getScreenY() - mouseY);
		});*/
	    setLeft(gridPane);
	    JoystickPanel joystickPane = new JoystickPanel();
	    setRight(joystickPane);
	    Region dummyRegion = new Region();
	    dummyRegion.setMinWidth(10);
	    setCenter(dummyRegion);
	}
	
	public void presetButton(Button btn, int col, int row) {
		btn.setPrefHeight(50.0);
		btn.setPrefWidth(50.0);
		btn.setMinHeight(50.0);
		btn.setMinWidth(50.0);
		btn.setTextAlignment(TextAlignment.CENTER);
		btn.setWrapText(true);
		gridPane.add(btn, col, row);
	}
	protected void controlForward(MouseEvent event) {
		robot.getActuator().speed(60,0,0);
	}
	
	protected void controlBackward(MouseEvent event) {
		robot.getActuator().speed(60,180,0);
	}
	
	protected void controlLeft(MouseEvent event) {
		robot.getActuator().speed(60,-90,0);
	}
	
	protected void controlRight(MouseEvent event) {
		robot.getActuator().speed(60,90,0);
	}
	
	protected void controlTurnLeft(MouseEvent event) {
		robot.getActuator().speed(0, 0, 60);
	}
	
	protected void controlTurnRight(MouseEvent event) {
		robot.getActuator().speed(0, 0, -60);
	}

	protected void controlStop(Event event) {
		robot.getActuator().stop();
	}
	
	class JoystickPanel extends Pane{
		
		private int lastangle = 0;
		private int lastdist = 0;
		
		private ControlMode controlMode = ControlMode.TRANSLATION;
		
		public JoystickPanel() {
			setPrefHeight(200.0);
			setPrefWidth(200.0);
			setMinHeight(200.0);
			setMinWidth(200.0);
			Circle background = new Circle(100, 100, 100);
			background.setStrokeWidth(0);
			Stop[] stops = new Stop[] { new Stop(0, Color.web("#7a70ff9a")), new Stop(1, Color.web("#002bff99"))};
			RadialGradient radialGradient = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.REPEAT, stops);
			background.setFill(radialGradient);
			Circle joystick = new Circle(100, 100, 30);
			Text textLabel = new Text(93, 109, "T");
			textLabel.setFill(Color.web("#48ff00"));
			textLabel.setFont(Font.font(24));
			setPadding(new Insets(10));
			getChildren().add(background);
			getChildren().add(joystick);
			getChildren().add(textLabel);
			
			background.setOnMouseClicked(event -> {
				if(controlMode == ControlMode.TRANSLATION) {
					controlMode = ControlMode.ROTATION;
					textLabel.setText("R");
				}else {
					controlMode = ControlMode.TRANSLATION;
					textLabel.setText("T");
				}
			});
			
			joystick.setOnMousePressed(evt -> {
				mouseX = evt.getScreenX();
				mouseY = evt.getScreenY();
			});
			textLabel.setOnMousePressed(evt -> {
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
					textLabel.setTranslateX(dist*Math.sin(Math.toRadians(angle)));
					textLabel.setTranslateY(dist*Math.cos(Math.toRadians(angle)));
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
			textLabel.setOnMouseDragged(mouseeventHandlerDragged);
			
			joystick.setOnMouseReleased(evt -> {
				joystick.setTranslateX(0);
				joystick.setTranslateY(0);
				textLabel.setTranslateX(0);
				textLabel.setTranslateY(0);
				robot.getActuator().stop();
			});
			textLabel.setOnMouseReleased(evt -> {
				joystick.setTranslateX(0);
				joystick.setTranslateY(0);
				textLabel.setTranslateX(0);
				textLabel.setTranslateY(0);
				robot.getActuator().stop();
			});
		}
	}
}
