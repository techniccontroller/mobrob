package koos;

public class LSScanPoint{
	private float angle;
	private float dist;
	
	public LSScanPoint(String value){
		this.angle = -1 * Float.valueOf(value.split(",")[0]);
		this.dist = Float.valueOf(value.split(",")[1]);
	}

	public float getAngle() {
		return angle;
	}

	public float getDist() {
		return dist;
	}
	
	public float getX(){
		return (float) (dist * Math.cos(Math.toRadians(angle)));
	}
	
	public float getY(){
		return (float) (dist * Math.sin(Math.toRadians(angle)));
	}
}