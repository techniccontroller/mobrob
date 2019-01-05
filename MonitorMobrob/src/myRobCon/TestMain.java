package myRobCon;

public class TestMain {
	
	public static void main(String[] args) {
		myRobSim robot = new myRobSim("MOBROB");
		
		BehaviourGroup dock = new BehaviourGroup("Dock");
		BehConstTransVel cv = new BehConstTransVel("ConstVel", 40);
		dock.add(cv, 50);
		robot.add(dock);
		
		robot.run();
		
		try {
			Thread.sleep(10000);
			robot.stop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
