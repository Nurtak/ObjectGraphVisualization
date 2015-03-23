package ch.hsr.ogv.model;

import javafx.geometry.Point3D;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class ModelCamera {
	private Point3D coordinates;
	private double xAngle, yAngle;

	public Point3D getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point3D coordinates) {
		this.coordinates = coordinates;
	}

	public double getxAngle() {
		return xAngle;
	}

	public void setxAngle(double xAngle) {
		this.xAngle = xAngle;
	}

	public double getyAngle() {
		return yAngle;
	}

	public void setyAngle(double yAngle) {
		this.yAngle = yAngle;
	}

}
