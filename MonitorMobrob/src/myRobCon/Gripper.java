package myRobCon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Gripper {
	
	private String ipaddress;
	private int port;
	
	private Socket clientSocketCon;
	private OutputStreamWriter outToServerCon;
	private BufferedReader inFromServerCon;
	
	private boolean gripperActive = false;
	
	public Gripper(String ip, int port) {
		this.ipaddress = ip;
		this.port = port;
	}
	
	public int initGripperSocket() {
		if (!gripperActive) {
			try {
				if (clientSocketCon == null || clientSocketCon.isClosed()) {
					clientSocketCon = new Socket(ipaddress, port);
					System.out.println("Create Control socket...");
					outToServerCon = new OutputStreamWriter(clientSocketCon.getOutputStream());
					inFromServerCon = new BufferedReader(new InputStreamReader(clientSocketCon.getInputStream()));
				}
				gripperActive = true;
				return 0;
	
			} catch (IOException e) {
				System.err.println("Not able to open the control connection...");
				gripperActive = false;
				return -1;
			}
		}else {
			return 1;
		}
	}

	public void closeGripperSocket() {
		if (!(clientSocketCon == null) && !clientSocketCon.isClosed()) {
			try {
				stopAll();
				clientSocketCon.shutdownOutput();
				clientSocketCon.close();
				gripperActive = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setActivServo(boolean value) {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("sv_ac(" + (value ? 1:0) + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending activateServo command" + e.getMessage());
		}
	}
	
	public void writeServo(int angle) {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("sv_wr(" + angle + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending writeServo command" + e.getMessage());
		}
	}
	
	public void refreshServo() {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("sv_rf(1)\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending refreshServo command" + e.getMessage());
		}
	}
	
	public void initGRIP() {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("gr_it(1)\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending initGRIP command" + e.getMessage());
		}
	}
	
	public void initVERT() {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("vt_it(1)\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending initVERT command" + e.getMessage());
		}
	}
	
	public void setSpeedGRIP(int value) {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("gr_sp(" + Math.abs(value) + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending setSpeedGRIP command" + e.getMessage());
		}
	}
	
	public void setSpeedVERT(int value) {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("vt_sp(" + Math.abs(value) + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending setSpeedVERT command" + e.getMessage());
		}
	}
	
	public void moveAbsGRIP(int value) {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("gr_ma(" + value + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending moveAbsGRIP command" + e.getMessage());
		}
	}
	
	public void moveAbsVERT(int value) {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("vt_ma(" + value + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending moveAbsVERT command" + e.getMessage());
		}
	}
	
	public void moveRelGRIP(int value) {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("gr_mr(" + value + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending moveRelGRIP command" + e.getMessage());
		}
	}
	
	public void moveRelVERT(int value) {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("vt_mr(" + value + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending moveRelVERT command" + e.getMessage());
		}
	}
	
	public void stopGRIP() {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("gr_st(1)\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending stopGRIP command" + e.getMessage());
		}
	}
	
	public void stopVERT() {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("vt_st(1)\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending stopVERT command" + e.getMessage());
		}
	}
	
	public void stopAll() {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("st_st(1)\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending stopAll command" + e.getMessage());
		}
	}
	
	public void setStayActivStepper(boolean value) {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("st_ac(" + (value?1:0) + ")\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending stop command" + e.getMessage());
		}
	}
	
	public void getPosVERT() {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("vt_gp(1)\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending getPosVERT command" + e.getMessage());
		}
	}
	
	public void getPosGRIP() {
		try {
			if (clientSocketCon != null && clientSocketCon.isConnected()) {
				outToServerCon.write("gr_gp(1)\n");
				outToServerCon.flush();
				System.out.println(inFromServerCon.readLine());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while sending getPosGRIP command" + e.getMessage());
		}
	}
	

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
