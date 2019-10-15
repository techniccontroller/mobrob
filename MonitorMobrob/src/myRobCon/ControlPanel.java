package myRobCon;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
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
import myRobCon.visu.panels.ActuatorPanel;
import myRobCon.visu.panels.GripperPanel;
import javafx.scene.control.TextField;

public class ControlPanel extends BorderPane{
	
	private ActuatorPanel actuatorPane;
	private GripperPanel gripperPane;
	
	//private double mouseX, mouseY;
	//private double startposX, startposY;
	
	private MyRob robot;
	
	public ControlPanel(MyRob myrob) {
		this.robot = myrob;
		
		setBorder(new Border(new BorderStroke(Color.LIGHTBLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		// Move ControlPanel with mouse
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

	    //Region dummyRegion = new Region();
	    //dummyRegion.setMinWidth(10);
	    //setCenter(dummyRegion);
	    
	    actuatorPane = new ActuatorPanel(robot);
	    setLeft(actuatorPane);
	    
	    gripperPane = new GripperPanel(robot);
	    setRight(gripperPane);
	}
		
	public void showActuatorPane(boolean value) {
		actuatorPane.setVisible(value);
	}
	
	public void showGripperPane(boolean value) {
		gripperPane.setVisible(value);
	}
	
	
}
