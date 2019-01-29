package myRobCon.behaviours;

import myRobCon.Desire;
import myRobCon.MyRob;
import myRobCon.Resolver;

public abstract class Behaviour {
	private String name;
	protected MyRob robot;
	private Resolver resolver;
	private double priority;
	private BehaviourGroup currentBehaviourGroup;
	
	public Behaviour(String name) {
		this.name = name;
	}
	
	public abstract void fire();
	
	public void setResolver(Resolver resolver) {
		this.resolver = resolver;
	}

	public void setRobot(MyRob robot) {
		this.robot = robot;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}
	
	public String getName() {
		return name;
	}
	
	public void setCurrentBehaviourGroup(BehaviourGroup currentBehaviourGroup) {
		this.currentBehaviourGroup = currentBehaviourGroup;
	}

	protected void addDesire(Desire<?> desire) {
		desire.setPriority(priority);
		resolver.addDesire(desire);
	}
	
	protected void success() {
		currentBehaviourGroup.setSuccess(true);
	}
	
	protected void error() {
		currentBehaviourGroup.setError(true);
	}
}
