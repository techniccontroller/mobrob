package myRobCon;

import myRobCon.behaviours.BehAlign;
import myRobCon.behaviours.BehConstTransVel;
import myRobCon.behaviours.BehLimFor;
import myRobCon.behaviours.BehaviourGroup;

public class TestMain {
	
	public static void main(String[] args) {
		
		MyRob robot = new MyRob("Mob1", "192.168.0.111");
		
		BehaviourGroup dock = new BehaviourGroup("Dock");
		BehConstTransVel cv = new BehConstTransVel("ConstVel", 40);
		BehLimFor lv = new BehLimFor("limit", 300, 500, 20);
		BehAlign al = new BehAlign("align", 20, 30);
		dock.add(al, 50);
		dock.add(cv, 50);
		dock.add(lv, 90);
		robot.add(dock);
		
		//robot.run();
		robot.showGUI();
		
	}

}
