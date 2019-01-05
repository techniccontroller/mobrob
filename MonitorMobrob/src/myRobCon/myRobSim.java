package myRobCon;

import java.util.LinkedList;

public class myRobSim {
	private String id;
	private Resolver resolver;
	private LinkedList<BehaviourGroup> behGroups;
	private Strategy strategy;
	
	
	public myRobSim(String id) {
		this.id = id;
		this.resolver = new Resolver();
		this.behGroups = new LinkedList<BehaviourGroup>();
		this.strategy = null;
	}
	
	public String getId() {
		return id;
	}

	public void add(BehaviourGroup behGroup) {
		behGroup.setResolver(resolver);
		this.behGroups.add(behGroup);
	}
	
	public void run() {
		if(strategy == null && behGroups.size() > 0) {
			behGroups.get(0).activateExclusive();
		}
		resolver.startWorking();
	}
	
	public void stop() {
		resolver.stopWorking();
	}
}
