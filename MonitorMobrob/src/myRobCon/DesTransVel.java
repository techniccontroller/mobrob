package myRobCon;

public class DesTransVel extends Desire{

	private double valDirection;
	private double valSpeed;
	
	public DesTransVel(double valDirection, double valSpeed, double strength) {
		super(strength);
		this.valDirection = valDirection;
		this.valSpeed = valSpeed;
	}

	public double getValDirection() {
		return valDirection;
	}

	public double getValSpeed() {
		return valSpeed;
	}

	public void setValDirection(double valDirection) {
		this.valDirection = valDirection;
	}

	public void setValSpeed(double valSpeed) {
		this.valSpeed = valSpeed;
	}
	
}
