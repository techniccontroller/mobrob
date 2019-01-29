package myRobCon;

public class BehLimFor extends Behaviour{
	private double stopdistance;
	private double slowdistance;
	private int slowspeed;
	
	public BehLimFor(String name, double stopdistance, double slowdistance, int slowspeed) {
		super(name);
		this.stopdistance = stopdistance;
		this.slowdistance = slowdistance;
		this.slowspeed = slowspeed;
	}
	
	@Override
	public void fire() {
		double distanceFront = robot.getLsscanner().checkBox(100, 200, 1000, -200);
		//double distanceFront = robot.getLsscanner().checkPolar(-45, 45, 1000);
		
		if(distanceFront > 0) {
			if(distanceFront < stopdistance) {
				addDesire(new DesTransVel(0, 1.0));
			}
			else if(distanceFront < slowdistance) {
				addDesire(new DesTransVel(slowspeed, 1.0));
			}
		}
	}
}
