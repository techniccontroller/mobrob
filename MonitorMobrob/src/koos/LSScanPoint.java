package koos;

public class LSScanPoint{
	private double angle;
	private double dist;
	private double x;
	private double y;
	
	public LSScanPoint(String value){
		this.angle = -1 * Float.valueOf(value.split(",")[0]);
		this.dist = Float.valueOf(value.split(",")[1]);
		this.x = dist * Math.cos(Math.toRadians(angle));
		this.y = dist * Math.sin(Math.toRadians(angle));
	}
	
	public LSScanPoint(double x, double y) {
		this.x = x;
		this.y = y;
		this.angle = Math.toDegrees(Math.atan2(y, x));
		this.dist = Math.sqrt(this.x*this.x + this.y + this.y);
	}

	public double getAngle() {
		return angle;
	}

	public double getDist() {
		return dist;
	}
	
	public double getX(){
		return this.x;
	}
	
	public double getY(){
		return this.y;
	}
	
	public static double distance(LSScanPoint p1, LSScanPoint p2) {
		return Math.sqrt((p1.getX()-p2.getX())*(p1.getX()-p2.getX()) + (p1.getY()-p2.getY())*(p1.getY()-p2.getY()));
	}
}