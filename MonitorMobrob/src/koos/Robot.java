package koos;

import application.FXController;

public class Robot {
	
	private String ipaddress;
	private LSScanner lsscanner;
	private Camera camera;
	private Actuator actuator;
	private FXController fxcontroller;

	
	public Robot(String ip, FXController fxcontroller) {
		this.ipaddress = ip;
		this.fxcontroller = fxcontroller;
		this.fxcontroller.setRobot(this);
	}
	
	public int initLaserscanner(int port) {
		lsscanner = new LSScanner(ipaddress, port, fxcontroller);
		return 0;
	}
	
	public int initCamera(int port) {
		camera = new Camera(ipaddress, port, fxcontroller);
		return 0;
	}
	
	public int initActuator(int port) {
		actuator = new Actuator(ipaddress, port);
		actuator.startActuatorSocket();
		return 0;
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
	
	
}
