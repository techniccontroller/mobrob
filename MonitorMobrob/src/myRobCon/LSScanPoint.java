package myRobCon;

import java.awt.geom.Point2D;

public class LSScanPoint extends Point2D{
	private double angle;
	private double dist;
	private double x;
	private double y;
	
	public LSScanPoint(String value){
		this.angle = -1 * java.lang.Float.valueOf(value.split(",")[0]);
		this.dist = java.lang.Float.valueOf(value.split(",")[1]);
		this.x = dist * Math.cos(Math.toRadians(angle));
		this.y = dist * Math.sin(Math.toRadians(angle));
		this.angle = normalizeAngle(this.angle);
	}
	
	public LSScanPoint(double x, double y) {
		this.x = x;
		this.y = y;
		this.angle = Math.toDegrees(Math.atan2(y, x));
		this.dist = Math.sqrt(this.x*this.x + this.y + this.y);
		this.angle = normalizeAngle(this.angle);
	}

	public double getAngle() {
		return angle;
	}

	public double getDist() {
		return dist;
	}
	
	public static double distance(LSScanPoint p1, LSScanPoint p2) {
		return Math.sqrt((p1.getX()-p2.getX())*(p1.getX()-p2.getX()) + (p1.getY()-p2.getY())*(p1.getY()-p2.getY()));
	}
	
	public static double normalizeAngle(double angle) {
		double newAngle = angle;
	    while (newAngle <= -180) newAngle += 360;
	    while (newAngle > 180) newAngle -= 360;
	    return newAngle;
	}

	@Override
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public double getX(){
		return this.x;
	}
	
	@Override
	public double getY(){
		return this.y;
	}
}