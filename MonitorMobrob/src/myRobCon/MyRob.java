package myRobCon;

import java.util.LinkedList;

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
	private VisuGUI visu;
	
	public MyRob(String id, String ip) {
		this.id = id;
		this.ipaddress = ip;
		this.resolver = new Resolver();
		this.behGroups = new LinkedList<BehaviourGroup>();
		this.strategy = null;
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
	
	public int initLaserscanner(int port) {
		lsscanner = new LSScanner(ipaddress, port);
		lsscanner.setVisu(visu);
		return 0;
	}
	
	public int initCamera(int port) {
		camera = new Camera(ipaddress, port);
		camera.setVisu(visu);
		return 0;
	}
	
	public int initActuator(int port) {
		actuator = new Actuator(ipaddress, port);
		return 0;
	}
	
	public int initEGOPoseSensor(int port) {
		egoSensor = new EGOPoseSensor(ipaddress, port);
		egoSensor.setVisu(visu);
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

	public void add(BehaviourGroup behGroup) {
		behGroup.setResolver(resolver);
		behGroup.setRobot(this);
		this.behGroups.add(behGroup);
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
		
		if(strategy == null && behGroups.size() > 0) {
			behGroups.get(0).activateExclusive();
		}
		resolver.startWorking();
	}
	
	public void stop() {
		if(camera != null) {
			camera.closeCameraSocket();
			logOnVisu("Camera Socket closed!\n");
		}
		if(lsscanner != null) {
			lsscanner.closeLaserSocket();
			logOnVisu("Laserscanner Socket closed!\n");	
		}
		if(actuator != null) {
			actuator.closeActuatorSocket();
			logOnVisu("Actuator Socket closed!\n");
		}
		resolver.stopWorking();
	}
	
	public void logOnVisu(String text) {
		visu.log(text);
	}
}
