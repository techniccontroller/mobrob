package myRobCon;

public abstract class Strategy {
	
	private MyRob robot;
	
	public Strategy() {
		
	}
	
	public void setRobot(MyRob robot) {
		this.robot = robot;
	}
	
	protected void stopRunning(){
		robot.stop();
	}

	public abstract void plan();
}
