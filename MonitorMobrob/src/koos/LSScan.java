package koos;

import java.util.ArrayList;

public class LSScan {
	
	private ArrayList<LSScanPoint> scanpoints;
	
	private ArrayList<LSCluster> clusters;
	
	private final double DBSCAN_EPS = 100;
	private final int MIN_PTS = 5;
	
	
	public LSScan() {
		scanpoints = new ArrayList<LSScanPoint>();
		clusters = new ArrayList<LSCluster>();
	}
	
	public void addRawData(String data) {
		scanpoints.clear();
		String koordinates[] = data.split(";");
		for (int i = 0; i < koordinates.length; i++) {
			if (koordinates[i].length() > 0) {
				LSScanPoint k = new LSScanPoint(koordinates[i]);
				if (k.getDist() > 0) {
					scanpoints.add(k);
				}
			}
		}
		findClusters();
	}
	
	public ArrayList<LSScanPoint> getScanpoints(){
		return scanpoints;
	}
	
	public ArrayList<LSCluster> getClusters() {
		return clusters;
	}

	public void findClusters() {
		clusters.clear();
		LSCluster cluster = new LSCluster();
		for(int i = 0; i < scanpoints.size()-1; i++) {
			LSScanPoint currentPoint = scanpoints.get(i);
			LSScanPoint nextPoint = scanpoints.get(i+1);
	        double distBtwTwoPoints = LSScanPoint.distance(currentPoint, nextPoint);
	        if (distBtwTwoPoints < DBSCAN_EPS)
	        {
	            cluster.addPoint(nextPoint);
	        }
	        else {
	            if (cluster.size() >= MIN_PTS){
	                clusters.add(cluster);
	                cluster = new LSCluster();
	            }
	            else{
	                cluster.clear();
	            }
	        }
		}
	}
}
