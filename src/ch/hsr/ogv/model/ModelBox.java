package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.hsr.ogv.dataaccess.ColorAdapter;
import ch.hsr.ogv.dataaccess.Point3DAdapter;

/**
 *
 * @author Adrian Rieser
 *
 */
@XmlType(propOrder = { "name", "coordinates", "width", "height", "color", "endpoints" })
public class ModelBox extends Observable {

	protected String name = "";
	protected Point3D coordinates = new Point3D(0, 0, 0);
	protected double width = 100.0;
	protected double height = 100.0;
	protected Color color = Color.CORNSILK;
	protected List<Endpoint> endpoints = new ArrayList<Endpoint>();

	// for un/marshaling only
	public ModelBox() {
	}

	public ModelBox(String name, Point3D coordinates, double width, double height, Color color) {
		this.name = name;
		this.coordinates = coordinates;
		this.width = width;
		this.height = height;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		// not doing the equals check here for when ModelObject needs a name change event
		if (this.name != null) {
			this.name = name;
			setChanged();
			notifyObservers(ModelBoxChange.NAME);
		}
	}

	@XmlJavaTypeAdapter(Point3DAdapter.class)
	public Point3D getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point3D coordinates) {
		if (this.coordinates != null) {
			this.coordinates = coordinates;
			setChanged();
			notifyObservers(ModelBoxChange.COORDINATES);
		}
	}

	public void setX(double x) {
		Point3D coords = new Point3D(x, 0, 0);
		if (coordinates != null) {
			coords = new Point3D(x, this.coordinates.getY(), this.coordinates.getZ());
		}
		setCoordinates(coords);
	}

	public void setY(double y) {
		Point3D coords = new Point3D(0, y, 0);
		if (coordinates != null) {
			coords = new Point3D(this.coordinates.getX(), y, this.coordinates.getZ());
		}
		setCoordinates(coords);
	}

	public void setZ(double z) {
		Point3D coords = new Point3D(0, 0, z);
		if (coordinates != null) {
			coords = new Point3D(this.coordinates.getX(), this.coordinates.getY(), z);
		}
		setCoordinates(coords);
	}

	@XmlTransient
	public double getX() {
		return this.coordinates.getX();
	}

	@XmlTransient
	public double getY() {
		return this.coordinates.getY();
	}

	@XmlTransient
	public double getZ() {
		return this.coordinates.getZ();
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		if (this.width != width) {
			this.width = width;
			setChanged();
			notifyObservers(ModelBoxChange.WIDTH);
		}
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		if (this.height != height) {
			this.height = height;
			setChanged();
			notifyObservers(ModelBoxChange.HEIGHT);
		}
	}

	@XmlJavaTypeAdapter(ColorAdapter.class)
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if (this.color != null && !this.color.equals(color)) {
			this.color = color;
			setChanged();
			notifyObservers(ModelBoxChange.COLOR);
		}
	}

	@XmlElementWrapper(name = "endpoints")
	@XmlElement(name = "endpoint")
	public List<Endpoint> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}

	@XmlTransient
	public Map<Endpoint, Endpoint> getFriends() {
		Map<Endpoint, Endpoint> result = new LinkedHashMap<Endpoint, Endpoint>(endpoints.size());
		for (Endpoint endpoint : endpoints) {
			Endpoint friend = endpoint.getFriend();
			if (friend != null) {
				result.put(endpoint, endpoint.getFriend());
			}
		}
		return result;
	}

	public boolean replaceEndpoint(Endpoint toReplace, Endpoint replacement) {
		int index = this.endpoints.indexOf(toReplace);
		if (index >= 0) {
			this.endpoints.set(index, replacement);
			return true;
		}
		return false;
	}

	public enum ModelBoxChange {
		COORDINATES, HEIGHT, WIDTH, NAME, COLOR;
	}
}
