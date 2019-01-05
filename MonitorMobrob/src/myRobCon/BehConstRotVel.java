package myRobCon;

public class BehConstRotVel extends Behaviour {
	protected int tolerance, rotVel;

	public BehConstRotVel(String name, int tolerance, int rotVel) {
		super(name);
		this.tolerance = tolerance;
		this.rotVel = rotVel;
	}

	@Override
	public void fire() {
		System.out.println("new Desire Rotation: " + rotVel);
		addDesire(new DesRotVel(rotVel, 0.5));
	}

}
