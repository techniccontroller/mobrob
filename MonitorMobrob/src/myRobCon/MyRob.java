package myRobCon;

import java.util.LinkedList;

import javafx.application.Platform;
import myRobCon.behaviours.BehaviourGroup;

public class MyRob {
	private String id;
	private Resolver resolver;
	private LinkedList<BehaviourGroup> behGroups;
	private Strategy strategy;
	
	private String ipaddress;
	private LSScanner lsscanner;
	private Camera camera;
	private Actuator actuator;
	private EGOPoseSensor egoSensor;
	private Gripper gripper;
	private VisuGUI visu;
	private boolean shutdown = false;
	
	private Object lock = new Object();
	
	public MyRob(String id, String ip) {
		this.id = id;
		this.ipaddress = ip;
		this.resolver = new Resolver();
		resolver.setRobot(this);
		this.behGroups = new LinkedList<BehaviourGroup>();
		this.strategy = null;
		
		//https://stackoverflow.com/questions/30335165/handle-on-launched-javafx-application
		// Create VisuGUI
		try {
			this.visu = VisuGUI.getInstance();
			this.visu.setRobot(this);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		addLaserscanner(1234);
		addActuator(5054);
		addCamera(5001);
		addEGOPoseSensor(5053);
		addGripper(5044);
	}
	
	public void setVisu(VisuGUI visu) {
		this.visu = visu;
	}
	
	public VisuGUI getVisu() {
		return visu;
	}
	
	public String getId() {
		return id;
	}
	
	public int addLaserscanner(int port) {
		lsscanner = new LSScanner(ipaddress, port);
		lsscanner.setVisu(visu);
		return 0;
	}
	
	public int addCamera(int port) {
		camera = new Camera(ipaddress, port);
		camera.setVisu(visu);
		return 0;
	}
	
	public int addActuator(int port) {
		actuator = new Actuator(ipaddress, port);
		return 0;
	}
	
	public int addEGOPoseSensor(int port) {
		egoSensor = new EGOPoseSensor(ipaddress, port);
		egoSensor.setVisu(visu);
		return 0;
	}
	
	public int addGripper(int port) {
		gripper = new Gripper(ipaddress, port);
		return 0;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public LSScanner getLsscanner() {
		return lsscanner;
	}

	public Camera getCamera() {
		return camera;
	}

	public Actuator getActuator() {
		return actuator;
	}

	public EGOPoseSensor getEgoSensor() {
		return egoSensor;
	}
	
	public Gripper getGripper() {
		return gripper;
	}

	public void add(BehaviourGroup behGroup) {
		behGroup.setResolver(resolver);
		behGroup.setRobot(this);
		this.behGroups.add(behGroup);
	}
	
	public void add(Strategy strategy) {
		strategy.setRobot(this);
		this.strategy = strategy;
	}
	
	public void run() {
		if(camera != null) {
			logOnVisu("Connecting Camera ...");
			if(camera.initCameraSocket() == 0) {
				logOnVisu("connected\n");
			}else {
				logOnVisu("not connected\n");
			}
		}
		if(lsscanner != null) {
			logOnVisu("Connecting Laserscanner ..." );
			if(lsscanner.initLaserscannerSocket() == 0) {
				logOnVisu("connected\n");
				lsscanner.startLaserscannerThread();
			}else {
				logOnVisu("not connected\n");
			}
		}
		if(actuator != null) {
			logOnVisu("Connecting Actuator ..." );
			if(actuator.initActuatorSocket() == 0) {
				logOnVisu("connected\n");
			}else {
				logOnVisu("not connected\n");
			}
		}
		resolver.setStrategy(strategy);
		if(strategy == null && behGroups.size() > 0) {
			behGroups.get(0).activateExclusive();
		}else {
			strategy.setFinish(false);
		}
		resolver.startWorking();
	}
	
	public void stop() {
		resolver.stopWorking();
		behGroups.stream().forEach(bg -> bg.setSuccess(false));
		if(actuator != null) {
			actuator.closeActuatorSocket();
			logOnVisu("Actuator Socket closed!\n");
			System.out.println("Actuator Socket closed!");
		}
		if(camera != null) {
			camera.closeCameraSocket();
			logOnVisu("Camera Socket closed!\n");
			System.out.println("Camera Socket closed!");
		}
		if(lsscanner != null) {
			lsscanner.stopLaserscannerThread();
			System.out.println("Laserscanner Thread canceled!");
			lsscanner.closeLaserSocket();
			logOnVisu("Laserscanner Socket closed!\n");
			System.out.println("Laserscanner Socket closed!");
		}
	}
	
	public void logOnVisu(String text) {
		visu.log(text);
	}
	
	public void shutDown() {
		synchronized (lock) {
			shutdown = true;
			lock.notifyAll();
		}
	}
	
	public void showGUI() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				visu.getStage().show();
			}
		});
		synchronized (lock) {
			while(!shutdown) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Shutdown Main");
	}
}
