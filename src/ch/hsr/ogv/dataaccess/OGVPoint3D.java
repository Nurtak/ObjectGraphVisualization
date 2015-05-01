package ch.hsr.ogv.dataaccess;

public class OGVPoint3D {

	private double x, y, z;

	public OGVPoint3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public OGVPoint3D() {
		this(0, 0, 0);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
	
}
