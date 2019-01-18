package myRobCon;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Resolver {
	private MyRob robot;
	private LinkedList<DesTransVel> lstDesTransVel;
	private LinkedList<DesRotVel> lstDesRotVel;
	private LinkedList<Behaviour> lstBehaviours;
	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> task;
	private Object lock = new Object();
	
	public Resolver() {
		this.lstBehaviours = new LinkedList<Behaviour>();
		this.lstDesRotVel = new LinkedList<DesRotVel>();
		this.lstDesTransVel = new LinkedList<DesTransVel>();
	}
	
	public void addDesire(Desire desire) {
		if(desire instanceof DesTransVel) {
			lstDesTransVel.add((DesTransVel) desire);
		}
		else if(desire instanceof DesRotVel) {
			lstDesRotVel.add((DesRotVel) desire);
		}
		else {
			System.out.println("Type of desire not known: " + desire.getClass().getName());
		}
	}
	
	public void clearActiveBehaviours() {
		lstBehaviours.clear();
		lstDesRotVel.clear();
		lstDesTransVel.clear();
	}
	
	public void setNewBehaviourList(LinkedList<Behaviour> behaviours) {
		synchronized (lock) {
			clearActiveBehaviours();
			lstBehaviours = behaviours;
		}
	}
	
	public void startWorking() {
		Runnable resolverTask = new Runnable() {
			
			@Override
			public void run() {
				lstDesRotVel.clear();
				lstDesTransVel.clear();
				
				System.out.println(lstBehaviours.size());
				
				// Run fire() Methode of all active Behaviours
				lstBehaviours.stream().forEach(beh -> beh.fire());
				
				lstDesTransVel.sort(Comparator.comparingDouble(Desire::getPriority));
				
				System.out.println(lstDesTransVel.size());
				
				DesTransVel resultDes;
				resultDes = new DesTransVel(0, 0, 0);
				int lastNumDes = 0;
				
				for(int i = 0; i < lstDesTransVel.size() && resultDes.getStrength() < 1 ;) {
					lastNumDes = 0;
					DesTransVel tempDes = new DesTransVel(0, 0, 0);
					for(int k = 0; k < lstDesTransVel.size(); k++) {
						if(lstDesTransVel.get(k).getPriority() == lstDesTransVel.get(i).getPriority()) {
							tempDes.setValDirection(lstDesTransVel.get(k).getValDirection() * lstDesTransVel.get(k).getStrength());
							tempDes.setValSpeed(lstDesTransVel.get(k).getValSpeed() * lstDesTransVel.get(k).getStrength());
							tempDes.setStrength(lstDesTransVel.get(k).getStrength());
							lastNumDes++;
						}
					}
					tempDes.setStrength(tempDes.getStrength()/lastNumDes);
					resultDes.setValDirection(resultDes.getValDirection() + tempDes.getValDirection());
					resultDes.setValSpeed(resultDes.getValSpeed() + tempDes.getValSpeed());
					resultDes.setStrength(resultDes.getStrength() + tempDes.getStrength());
					i = i + lastNumDes;
				}
				System.out.println("Output to Robot for DesTransVel -> dir:" + resultDes.getValDirection() + ", s: " + resultDes.getValSpeed());
			}
		};
		
		task = pool.scheduleAtFixedRate(resolverTask, 0, 1000, TimeUnit.MILLISECONDS);
	}
	
	public void stopWorking() {
		if (this.task != null && !this.task.isDone()) {
			// stop the timer
			this.task.cancel(true);
		}
	}
}
