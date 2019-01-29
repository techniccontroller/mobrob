package myRobCon;

public class Desire<T> {
	private double priority;		// 0   -> 100
	private double strength;		// 0.0 -> 1.0
	private T value;
	
	public Desire(T value, double strength) {
		this.value = value;
		this.strength = strength;
		this.priority = 0;
	}

	public double getPriority() {
		return priority;
	}

	public double getStrength() {
		return strength;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}

}
