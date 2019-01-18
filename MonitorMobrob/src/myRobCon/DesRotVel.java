package myRobCon;

public class DesRotVel extends Desire{
	
	private int valRotSpeed;
	
	public DesRotVel(int valRotSpeed, double strength) {
		super(strength);
		this.valRotSpeed = valRotSpeed;
	}

	public int getValRotSpeed() {
		return valRotSpeed;
	}

	public void setValRotSpeed(int valRotSpeed) {
		this.valRotSpeed = valRotSpeed;
	}
}
