package myRobCon;

public class Desire {
	private double priority;
	private double strength;

	public Desire(double strength) {
		this.strength = strength;
		this.priority = 0;
	}

	public double getPriority() {
		return priority;
	}

	public double getStrength() {
		return strength;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}

}