package myRobCon;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class BehaviourGroup {
	private String name;
	private LinkedList<BehaviourCapsula> lstBehaviours;
	private Resolver resolver;
	private boolean success;
	private boolean error;

	public BehaviourGroup(String name) {
		this.name = name;
		this.lstBehaviours = new LinkedList<BehaviourGroup.BehaviourCapsula>();
		this.success = false;
		this.error = false;
	}

	public String getName() {
		return name;
	}

	public void setResolver(Resolver resolver) {
		this.resolver = resolver;
		lstBehaviours.stream().forEach(behCap -> behCap.getBehaviour().setResolver(resolver));
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public void add(Behaviour behaviour, double priority) {
		lstBehaviours.add(new BehaviourCapsula(behaviour, priority));
	}

	public void activateExclusive() {
		success = false;
		error = false;
		lstBehaviours.stream().forEach(behCap -> {
			behCap.getBehaviour().setPriority(behCap.getPriority());
			behCap.getBehaviour().setCurrentBehaviourGroup(this);
		});
		resolver.setNewBehaviourList(lstBehaviours.stream().map(beh -> beh.getBehaviour())
				.collect(Collectors.toCollection(LinkedList::new)));
	}

	private class BehaviourCapsula {
		private Behaviour behaviour;
		private double priority;

		public BehaviourCapsula(Behaviour beh, double prio) {
			this.behaviour = beh;
			this.priority = prio;
		}

		public Behaviour getBehaviour() {
			return behaviour;
		}

		public double getPriority() {
			return priority;
		}
	}
}
