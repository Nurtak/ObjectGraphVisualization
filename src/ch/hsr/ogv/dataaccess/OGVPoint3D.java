package ch.hsr.ogv.dataaccess;

import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Adrian Rieser
 * @version OGV 3.1, May 2015
 *
 */
@XmlType(propOrder = { "x", "y", "z" })
public class OGVPoint3D {

	private double x = 0.0;
	private double y = 0.0;
	private double z = 0.0;

	public OGVPoint3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public OGVPoint3D() {

	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

}
