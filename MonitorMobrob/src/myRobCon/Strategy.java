package myRobCon;

import javafx.application.Platform;

public abstract class Strategy {
	
	private MyRob robot;
	private boolean finish;
	
	public Strategy() {
		this.finish = false;
	}
	
	public void setRobot(MyRob robot) {
		this.robot = robot;
	}
	
	protected void stopRunning(){
		finish = true;
		robot.getVisu().setStartButtonRunning(false);
		robot.logOnVisu("Strategy finished!\n");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				robot.stop();
			}
		});
		
	}

	public boolean isFinish() {
		return finish;
	}
	
	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public abstract void plan();
}
