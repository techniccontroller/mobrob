package myRobCon.behaviours;

import myRobCon.DesRotVel;

public class BehAlign extends Behaviour {

	private double tolerance;
	private int rotVel;
	
	public BehAlign(String name, double tolerance, int rotVel) {
		super(name);
		this.tolerance = tolerance;
		this.rotVel = rotVel;
	}

	@Override
	public void fire() {
		double leftDist = robot.getLsscanner().checkBox(100, 100, 800, 200);
		double rightDist = robot.getLsscanner().checkBox(100, -100, 800, -200);
		if(leftDist != 0 && rightDist != 0) {
			if(leftDist-rightDist > tolerance) {
				addDesire(new DesRotVel(-rotVel, 1.0));
			}
			else if(rightDist-leftDist > tolerance) {
				addDesire(new DesRotVel(rotVel, 1.0));
			}
			else {
				addDesire(new DesRotVel(0, 0.5));
			}
		}
		else {
			addDesire(new DesRotVel(0, 0.5));
		}
	}

}
