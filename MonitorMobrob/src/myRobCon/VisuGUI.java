package myRobCon;

import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import koos.KOOSCanvas;
import utils.Utils;

public class VisuGUI extends Application {
	
	private MyRob robot;
	private VisuMenuBar menuBar;
	private ControlPanel controlPanel;
	private KeyCode previousKey = null;
	private TextArea textArea;
	private ImageView imageView;
	private KOOSCanvas koosCanvas;
	
	private static VisuGUI appInstance;
	private static final Lock lock = new ReentrantLock();
	private static final Condition appStarted = lock.newCondition();
	private Stage priStage;
	
	
	@Override
	public void start(Stage primaryStage) {
		lock.lock();
		
        try {
        	/*Sets the JavaFX platform not to exit implicitly. 
            (e.g. an explicit call to Platform.exit() is required
            to exit the JavaFX Platform).*/
        	Platform.setImplicitExit(false);
        	//records the instance
            appInstance = this;
            
            appStarted.signalAll();
        } catch(Exception e){
        	e.printStackTrace();
        }finally {
            lock.unlock();
        }
		
		VBox windowPanel = new VBox();
		BorderPane mainPanel = new BorderPane();
		
		/**
         * MenuBar
         * */
        menuBar = new VisuMenuBar(this);
        windowPanel.getChildren().add(menuBar);
		
		/**
		 * Left Panel
		 * */
        VBox leftPane = new VBox();
        leftPane.setPadding(new Insets(10));
        leftPane.setSpacing(5);
        leftPane.getChildren().add(new Label("Console:"));
        textArea = new TextArea();
        textArea.setPrefHeight(300);
        textArea.setEditable(false);
        leftPane.getChildren().add(textArea);
        controlPanel = new ControlPanel(robot);
        controlPanel.setVisible(false);
        leftPane.getChildren().add(controlPanel);
        mainPanel.setLeft(leftPane);
        
        /**
         * Right Panel
         * */
        VBox rightPane = new VBox();
        rightPane.setPadding(new Insets(10));
        rightPane.setSpacing(5);
    	Image image = SwingFXUtils.toFXImage(robot.getCamera().getBlankImage(640, 480), null);
		imageView = new ImageView(image);
		imageView.setFitHeight(240);
		imageView.setFitWidth(320);
        rightPane.getChildren().add(imageView);
        koosCanvas = new KOOSCanvas();
        koosCanvas.setHeight(480);
        koosCanvas.setWidth(640);
        koosCanvas.clear();
        rightPane.getChildren().add(koosCanvas);
        mainPanel.setRight(rightPane);
        
        /**
         * Bottom Panel
         * */        
        BorderPane bottomPane = new BorderPane();
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        Button btnStartStopResolver = new Button("Start");
        btnStartStopResolver.setPrefWidth(300);
        btnStartStopResolver.setPrefHeight(30);
        Font bigFont = Font.font("Tahoma", FontWeight.NORMAL, 15);
        btnStartStopResolver.setFont(bigFont);
        btnStartStopResolver.setStyle("-fx-background-color: #00ff00");
        btnStartStopResolver.setOnAction(e -> {
        	if(btnStartStopResolver.getText() == "Start") {
        		robot.run();
        		btnStartStopResolver.setStyle("-fx-background-color: #ff0000");
        		btnStartStopResolver.setText("Stop");
        	}else {
        		robot.stop();
        		btnStartStopResolver.setStyle("-fx-background-color: #00ff00");
        		btnStartStopResolver.setText("Start");
        	}
        });
        bottomPane.setCenter(btnStartStopResolver);
        mainPanel.setBottom(bottomPane);
        
        windowPanel.getChildren().add(mainPanel);
        windowPanel.setOnKeyPressed(this::controlKeyInput);
        windowPanel.setOnKeyReleased(this::controlStop);
        
		Scene scene = new Scene(windowPanel);
		primaryStage.setTitle("MyRobCon");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				robot.shutDown();
				Platform.exit();
			}
		});
		primaryStage.hide();
		this.priStage = primaryStage;
	}
	
	/**
     * Get an instance of the application.
     * If the application has not already been launched it will be launched.
     * This method will block the calling thread until the
     * start method of the application has been invoked and the instance set. 
     * @return application instance (will not return null).
     */
    public static VisuGUI getInstance() throws InterruptedException {
        lock.lock();

        try {
            if (appInstance == null) {
                Thread launchThread = new Thread(
                        () -> launch(VisuGUI.class), 
                        "visu-launcher"
                );
                launchThread.setDaemon(true);
                launchThread.start();
            	System.out.println("Lauch Visu...");
            	//launch(VisuGUI.class);
                appStarted.await();
            }
        } finally {
            lock.unlock();
        }

        return appInstance;
    }
	
	
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
	
	protected void controlStop(Event event) {
		robot.getActuator().stop();
		previousKey = null;
	}
	
	public void log(String text) {
		textArea.setText(textArea.getText() + text);
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
	
	public MyRob getRobot() {
		return robot;
	}	
	
	public void setRobot(MyRob robot) {
		this.robot = robot;
	}
	
	public VisuMenuBar getMenuBar() {
		return menuBar;
	}

	public ControlPanel getControlPanel() {
		return controlPanel;
	}
	
	public void updateCameraImageView(Image image)	{
		Utils.onFXThread(imageView.imageProperty(), image);
	}
	
	public KOOSCanvas getKoosCanvas() {
		return koosCanvas;
	}

	public Stage getStage() {
		return priStage;
	}
	
	/*public static void main(String[] args) {
		launch(args);
	}*/
}