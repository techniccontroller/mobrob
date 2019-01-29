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
	private LinkedList<DesTransDir> lstDesTransDir;
	private LinkedList<DesRotVel> lstDesRotVel;
	private LinkedList<Behaviour> lstBehaviours;
	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> task;
	private Object lock = new Object();
	
	private DesTransVel lastDesTransVel;
	
	public Resolver() {
		this.lstBehaviours = new LinkedList<Behaviour>();
		this.lstDesRotVel = new LinkedList<DesRotVel>();
		this.lstDesTransVel = new LinkedList<DesTransVel>();
		this.lstDesTransDir = new LinkedList<DesTransDir>();
		this.lastDesTransVel = new DesTransVel(0, 0);
	}
	
	public void addDesire(Desire<?> desire) {
		if(desire instanceof DesTransVel) {
			lstDesTransVel.add((DesTransVel) desire);
		}
		else if(desire instanceof DesTransDir) {
			lstDesTransDir.add((DesTransDir) desire);
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
				robot.getLsscanner().drawRawScanPoints();
				
				lstDesRotVel.clear();
				lstDesTransVel.clear();
				
				// Run fire() Methode of all active Behaviours
				lstBehaviours.stream().forEach(beh -> beh.fire());
				
				lstDesTransVel.sort(Comparator.comparingDouble(DesTransVel::getPriority).reversed());
				System.out.println("Desire-Values (TransVel):");
				lstDesTransVel.forEach(d -> System.out.println(d.getValue()));
				System.out.println("Size of DesireList (TransVel): " + lstDesTransVel.size());
				
				DesTransVel resultDes;
				resultDes = new DesTransVel(0, 0);
				int lastNumDes = 0;
				
				for(int i = 0; i < lstDesTransVel.size() && resultDes.getStrength() < 1 ;) {
					lastNumDes = 0;
					DesTransVel tempDes = new DesTransVel(0, 0);
					for(int k = 0; k < lstDesTransVel.size(); k++) {
						if(lstDesTransVel.get(k).getPriority() == lstDesTransVel.get(i).getPriority()) {
							tempDes.setValue((int)(lstDesTransVel.get(k).getValue() * lstDesTransVel.get(k).getStrength()));
							tempDes.setStrength(lstDesTransVel.get(k).getStrength());
							lastNumDes++;
						}
					}
					tempDes.setStrength(tempDes.getStrength()/lastNumDes);
					resultDes.setValue(resultDes.getValue() + tempDes.getValue());
					resultDes.setStrength(resultDes.getStrength() + tempDes.getStrength());
					i = i + lastNumDes;
				}
				System.out.println("Output to Robot (TransVel): " + resultDes.getValue()/resultDes.getStrength());
				if(lastDesTransVel.getValue() != resultDes.getValue() || lastDesTransVel.getStrength() != resultDes.getStrength()) {
					robot.getActuator().speed((int)(resultDes.getValue()/resultDes.getStrength()), 0, 0);
				}
				lastDesTransVel.setValue(resultDes.getValue());
				lastDesTransVel.setStrength(resultDes.getStrength());
			}
		};
		
		task = pool.scheduleAtFixedRate(resolverTask, 0, 100, TimeUnit.MILLISECONDS);
	}
	
	public void stopWorking() {
		if (this.task != null && !this.task.isDone()) {
			// stop the timer
			this.task.cancel(true);
		}
	}

	public void setRobot(MyRob robot) {
		this.robot = robot;
	}
}
