package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * 
 * @author arieser
 *
 */
public class ModelBox extends Observable {

	public static double DEFAULT_WIDTH = 100;
	public static double DEFAULT_HEIGHT = 100;
	public static final Color DEFAULT_COLOR = Color.WHITE;
	
	private Point3D coordinates;
	private double width;
	private double height;
	private Color color;	
	private List<Endpoint> endpoints = new ArrayList<Endpoint>();
	
	public ModelBox(Point3D coordinates) {
		this(coordinates, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_COLOR);
	}

	public ModelBox(Point3D coordinates, double width, double heigth, Color color) {
		this.coordinates = coordinates;
		this.width = width;
		this.height = heigth;
		this.color = color;
	}

	public Point3D getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point3D coordinates) {
		this.coordinates = coordinates;
		setChanged();
		notifyObservers(coordinates);
	}
	
	public void setX(double x) {
		Point3D coords = new Point3D(x, this.coordinates.getY(), this.coordinates.getZ());
		setCoordinates(coords);
	}
	
	public void setY(double y) {
		Point3D coords = new Point3D(this.coordinates.getX(), y, this.coordinates.getZ());
		setCoordinates(coords);
	}
	
	public void setZ(double z) {
		Point3D coords = new Point3D(this.coordinates.getX(), this.coordinates.getY(), z);
		setCoordinates(coords);
	}
	
	public double getX() {
		return this.coordinates.getX();
	}
	
	public double getY() {
		return this.coordinates.getY();
	}
	
	public double getZ() {
		return this.coordinates.getZ();
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
		setChanged();
		notifyObservers(width);
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
		setChanged();
		notifyObservers(height);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		setChanged();
		notifyObservers(color);
	}

	public List<Endpoint> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}

}
