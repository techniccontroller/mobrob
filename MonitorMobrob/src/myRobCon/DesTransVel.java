package myRobCon;

public class DesTransVel extends Desire{

	private int valDirection;
	private int valSpeed;
	
	public DesTransVel(int valDirection, int valSpeed, double strength) {
		super(strength);
		this.valDirection = valDirection;
		this.valSpeed = valSpeed;
	}

	public int getValDirection() {
		return valDirection;
	}

	public int getValSpeed() {
		return valSpeed;
	}

	public void setValDirection(int valDirection) {
		this.valDirection = valDirection;
	}

	public void setValSpeed(int valSpeed) {
		this.valSpeed = valSpeed;
	}
	
}
