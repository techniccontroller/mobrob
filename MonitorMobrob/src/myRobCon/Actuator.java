package myRobCon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Actuator {
	
	private String ipaddress;
	private int port;
	
	private Socket clientSocketCon;
	private OutputStreamWriter outToServerCon;
	private BufferedReader inFromServerCon;
	
	private boolean actuatorActive = false;
	
	public Actuator(String ip, int port) {
		this.ipaddress = ip;
		this.port = port;
	}
	
	public int initActuatorSocket() {
		if (!actuatorActive) {
			try {
				if (clientSocketCon == null || clientSocketCon.isClosed()) {
					clientSocketCon = new Socket(ipaddress, port);
					System.out.println("Create Control socket...");
					outToServerCon = new OutputStreamWriter(clientSocketCon.getOutputStream());
					inFromServerCon = new BufferedReader(new InputStreamReader(clientSocketCon.getInputStream()));
				}
				actuatorActive = true;
				return 0;
	
			} catch (IOException e) {
				System.err.println("Not able to open the control connection...");
				actuatorActive = false;
				return -1;
			}
		}else {
			return 1;
		}
	}

	public void closeActuatorSocket() {
		if (!(clientSocketCon == null) && !clientSocketCon.isClosed()) {
			try {
				clientSocketCon.shutdownOutput();
				clientSocketCon.close();
				actuatorActive = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void move(int distance, int speed, int direction) {
		try {
			if (clientSocketCon.isConnected()) {
				outToServerCon.write("move(" + distance + "," + speed + "," + direction + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending move command" + e.getMessage());
		}
	}
	
	public void turn(int angle, int speed, int radius) {
		try {
			if (clientSocketCon.isConnected()) {
				outToServerCon.write("turn(" + angle + "," + speed + "," + radius + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending turn command" + e.getMessage());
		}
	}
	
	public void speed(int speed, int direction, int rotspeed) {
		try {
			if (clientSocketCon.isConnected()) {
				outToServerCon.write("speed(" + speed + "," + direction + "," + rotspeed + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending speed command" + e.getMessage());
		}
	}
	
	public void stop() {
		try {
			if (clientSocketCon.isConnected()) {
				outToServerCon.write("stop()\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending stop command" + e.getMessage());
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
