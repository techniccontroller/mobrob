package koos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Actuator {
	
	private String ipaddress;
	private int port;
	
	private Socket clientSocketCon;
	private OutputStreamWriter outToServerCon;
	private BufferedReader inFromServerCon;
	
	public Actuator(String ip, int port) {
		this.ipaddress = ip;
		this.port = port;
	}
	
	public void startActuatorSocket() {
		try {
			if (clientSocketCon == null || clientSocketCon.isClosed()) {
				clientSocketCon = new Socket(ipaddress, port);
				System.out.println("Create Control socket...");
				outToServerCon = new OutputStreamWriter(clientSocketCon.getOutputStream());
				inFromServerCon = new BufferedReader(new InputStreamReader(clientSocketCon.getInputStream()));
			}

		} catch (IOException e) {
			System.err.println("Not able to open the control connection...");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Control Connection");
			alert.setContentText("Not able to open the control connection.");
			alert.showAndWait();
		}
	}

	public void stopActuatorSocket() {
		if (!(clientSocketCon == null) && !clientSocketCon.isClosed()) {
			try {
				clientSocketCon.shutdownOutput();
				clientSocketCon.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
}
