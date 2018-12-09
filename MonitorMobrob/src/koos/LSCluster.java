package koos;

import java.util.ArrayList;

public class LSCluster {
	
	private ArrayList<LSScanPoint> scanpoints;
	private LSScanPoint middlePoint;
	
	public LSCluster() {
		scanpoints = new ArrayList<LSScanPoint>();
	}
	
	
	public void addPoint(LSScanPoint p) {
		scanpoints.add(p);
	}
	
	public int size() {
		return scanpoints.size();
	}
	
	public void clear() {
		scanpoints.clear();
		middlePoint = null;
	}
	
	public LSScanPoint getPoint(int index) {
		return scanpoints.get(index);
	}
	
	public LSScanPoint getMiddlePoint() {
		//scanpoints.sort((p1, p2) -> Double.compare(p1.getAngle(), p2.getAngle()));
		//middlePoint = scanpoints.get(scanpoints.size()/2);
		double sumX = scanpoints.stream().mapToDouble(p -> p.getX()).sum();
		double sumY = scanpoints.stream().mapToDouble(p -> p.getY()).sum();
		middlePoint = new LSScanPoint(sumX/scanpoints.size(), sumY/scanpoints.size());
		return middlePoint;
	}
}
