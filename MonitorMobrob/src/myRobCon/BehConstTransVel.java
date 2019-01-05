package myRobCon;

public class BehConstTransVel extends Behaviour {
	protected int transVel;

	public BehConstTransVel(String name, int transVel) {
		super(name);
		this.transVel = transVel;
	}

	@Override
	public void fire() {
		System.out.println("new Desire Translation: " + transVel);
		addDesire(new DesTransVel(0, transVel, 1.0));
	}
	
}
