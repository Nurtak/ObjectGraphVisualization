package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class ModelBox extends Observable {

	protected String name;
	protected Point3D coordinates;
	protected double width;
	protected double height;
	protected Color color;
	protected List<Endpoint> endpoints = new ArrayList<Endpoint>();

	public ModelBox(String name, Point3D coordinates, double width, double heigth, Color color) {
		this.name = name;
		this.coordinates = coordinates;
		this.width = width;
		this.height = heigth;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setChanged();
		notifyObservers(ModelBoxChange.NAME);
	}

	public Point3D getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point3D coordinates) {
		this.coordinates = coordinates;
		setChanged();
		notifyObservers(ModelBoxChange.COORDINATES);
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
		notifyObservers(ModelBoxChange.WIDTH);
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
		setChanged();
		notifyObservers(ModelBoxChange.HEIGHT);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		setChanged();
		notifyObservers(ModelBoxChange.COLOR);
	}

	public List<Endpoint> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}

	public enum ModelBoxChange {
		COORDINATES, HEIGHT, WIDTH, NAME, COLOR;
	}

	public Map<Endpoint, Endpoint> getFriends(){
		Map<Endpoint, Endpoint> result = new HashMap<Endpoint, Endpoint>(endpoints.size());
		for (Endpoint endpoint : endpoints) {
			result.put(endpoint, endpoint.getFriend());
		}
		return result;
	}
}
