package myRobCon;

public class DesRotVel extends Desire{
	
	private double valRotSpeed;
	
	public DesRotVel(double valRotSpeed, double strength) {
		super(strength);
		this.valRotSpeed = valRotSpeed;
	}

	public double getValRotSpeed() {
		return valRotSpeed;
	}
}
