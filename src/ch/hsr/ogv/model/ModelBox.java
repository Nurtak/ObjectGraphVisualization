package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * 
 * @author arieser
 *
 */
public class ModelBox {

	private static double defaultWidth = 100;
	private static double defaultHeight = 100;
	private static Color defaultColor = Color.BLUE;
	
	private Point3D coordinates;
	private double width;
	private double height;
	private Color color;	
	private List<Endpoint> endpoints;
	
	public ModelBox(Point3D coordinates) {
		this(coordinates, defaultWidth, defaultHeight, defaultColor);
	}

	public ModelBox(Point3D coordinates, double width, double heigth, Color color) {
		this.coordinates = coordinates;
		this.width = width;
		this.height = heigth;
		this.color = color;
		endpoints = new ArrayList<Endpoint>();
	}

	public Point3D getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point3D coordinates) {
		this.coordinates = coordinates;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public List<Endpoint> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}

}
