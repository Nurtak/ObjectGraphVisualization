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
		// not doing the equals check here for when ModelObject needs a name change event
		if(this.name != null) {
			this.name = name;
			setChanged();
			notifyObservers(ModelBoxChange.NAME);
		}
	}

	public Point3D getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point3D coordinates) {
		if(this.coordinates != null && !this.coordinates.equals(coordinates)) {
			this.coordinates = coordinates;
			setChanged();
			notifyObservers(ModelBoxChange.COORDINATES);
		}
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
		if(this.width != width) {
			this.width = width;
			setChanged();
			notifyObservers(ModelBoxChange.WIDTH);
		}
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		if(this.height != height) {
			this.height = height;
			setChanged();
			notifyObservers(ModelBoxChange.HEIGHT);
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if(this.color != null && !this.color.equals(color)) {
			this.color = color;
			setChanged();
			notifyObservers(ModelBoxChange.COLOR);
		}
	}

	public List<Endpoint> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}

	public void changeEndpoint(Endpoint oldEP, Endpoint newEP) {
		//System.out.println("Before change in class: " + this.getName() + ", oldEP: " + oldEP + ", newEP: " + newEP + ", LIST: " + this.endpoints);
		int index = endpoints.indexOf(oldEP);
		endpoints.set(index, newEP);
		//System.out.println("After change in class: " + this.getName() + ", oldEP: " + oldEP + ", newEP: " + newEP + ", LIST: " + this.endpoints);
		setChanged();
		notifyObservers(ModelBoxChange.ENDPOINTS);
	}

	public Map<Endpoint, Endpoint> getFriends(){
		Map<Endpoint, Endpoint> result = new HashMap<Endpoint, Endpoint>(endpoints.size());
		for (Endpoint endpoint : endpoints) {
			Endpoint friend = endpoint.getFriend();
			if(friend != null) {
				result.put(endpoint, endpoint.getFriend());
			}
		}
		return result;
	}

	public enum ModelBoxChange {
		COORDINATES, HEIGHT, WIDTH, NAME, COLOR, ENDPOINTS;
	}
}
