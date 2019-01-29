package myRobCon.behaviours;

import myRobCon.DesTransDir;
import myRobCon.DesTransVel;

public class BehConstTransVel extends Behaviour {
	protected int transVel;

	public BehConstTransVel(String name, int transVel) {
		super(name);
		this.transVel = transVel;
	}

	@Override
	public void fire() {
		addDesire(new DesTransVel(transVel, 0.5));
		addDesire(new DesTransDir(0, 0.5));
	}
	
}
