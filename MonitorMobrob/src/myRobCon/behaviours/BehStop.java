package myRobCon.behaviours;

public class BehStop extends Behaviour {

	private double stopDistance;
	
	public BehStop(String name, double stopDistance) {
		super(name);
		this.stopDistance = stopDistance;
	}

	@Override
	public void fire() {
		double dist = robot.getLsscanner().checkBox(100, 200, 800, -200);
		System.out.println("Stop: " + dist);
		if(dist != 0 && dist <= stopDistance) {
			System.out.println("Stop success on distance of " + dist + "!");
			success();
		}
	}

}
