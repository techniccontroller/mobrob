package myRobCon.behaviours;

import myRobCon.DesRotVel;

public class BehConstRotVel extends Behaviour {
	protected int tolerance, rotVel;

	public BehConstRotVel(String name, int tolerance, int rotVel) {
		super(name);
		this.tolerance = tolerance;
		this.rotVel = rotVel;
	}

	@Override
	public void fire() {
		addDesire(new DesRotVel(rotVel, 0.5));
	}

}
