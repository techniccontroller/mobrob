package myRobCon;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import myRobCon.behaviours.Behaviour;

public class Resolver {
	private MyRob robot;
	private LinkedList<DesTransVel> lstDesTransVel;
	private LinkedList<DesTransDir> lstDesTransDir;
	private LinkedList<DesRotVel> lstDesRotVel;
	private LinkedList<Behaviour> lstBehaviours;
	private ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> task;
	private Object lock = new Object();
	
	private int lastTransVel;
	private int lastTransDir;
	private int lastRotVel;
	
	public Resolver() {
		this.lstBehaviours = new LinkedList<Behaviour>();
		this.lstDesRotVel = new LinkedList<DesRotVel>();
		this.lstDesTransVel = new LinkedList<DesTransVel>();
		this.lstDesTransDir = new LinkedList<DesTransDir>();
		
		this.lastTransVel = 0;
		this.lastTransDir = 0;
		this.lastRotVel = 0;
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
				lstDesTransDir.clear();
				
				// Run fire() Methode of all active Behaviours
				lstBehaviours.stream().forEach(beh -> beh.fire());
				
				
				if(lstDesTransVel.size() > 0 || lstDesTransDir.size() > 0 || lstDesRotVel.size() > 0) {
					int outputTransVel = resolveNumber(lstDesTransVel).intValue();
					int outputTransDir = resolveNumber(lstDesTransDir).intValue();
					int outputRotVel = resolveNumber(lstDesRotVel).intValue();
					
					if(lastTransVel != outputTransVel || lastTransDir != outputTransDir || lastRotVel != outputRotVel) {
						robot.getActuator().speed(outputTransVel, outputTransDir, outputRotVel);
					}
					
					lastTransVel = outputTransVel;
					lastTransDir = outputTransDir;
					lastRotVel = outputRotVel;
				}
			}
		};
		
		task = pool.scheduleAtFixedRate(resolverTask, 0, 100, TimeUnit.MILLISECONDS);
	}
	
	void test(Desire<?> test) {
		
	}
	
	static Number resolveNumber(LinkedList<? extends Desire<? extends Number>> lstDesire){
		Number outputValue = 0;
		
		if(lstDesire.size() > 0) {
			lstDesire.sort(Comparator.comparingDouble(Desire<? extends Number>::getPriority).reversed());
			System.out.println("Desire: " + lstDesire.getLast().getClass().getName());
			System.out.print("\tList: [ ");
			lstDesire.forEach(d -> System.out.print(d.getValue() + " "));
			System.out.println("]");
			
			
			Desire<Number> resultDes;
			resultDes = new Desire<Number>(0, 0);
			int lastNumDes = 0;
			
			for(int i = 0; i < lstDesire.size() && resultDes.getStrength() < 1 ;) {
				lastNumDes = 0;
				Desire<Number> tempDes = new Desire<Number>(0, 0);
				for(int k = 0; k < lstDesire.size(); k++) {
					if(lstDesire.get(k).getPriority() == lstDesire.get(i).getPriority()) {
						tempDes.setValue((int)(lstDesire.get(k).getValue().doubleValue() * lstDesire.get(k).getStrength()));
						tempDes.setStrength(lstDesire.get(k).getStrength());
						lastNumDes++;
					}
				}
				tempDes.setStrength(tempDes.getStrength()/lastNumDes);
				resultDes.setValue(resultDes.getValue().doubleValue() + tempDes.getValue().doubleValue());
				resultDes.setStrength(resultDes.getStrength() + tempDes.getStrength());
				i = i + lastNumDes;
			}
			
			outputValue = resultDes.getValue().doubleValue()/resultDes.getStrength();
			System.out.println("\tOutput: " + outputValue);
		}
		return outputValue;
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
