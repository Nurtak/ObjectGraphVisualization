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
public class ClassManager {

	private List<Class> classes;
	private Point3D defaultCoordinates;
	private double defaultWidth, defaultHeight;
	private Color defaultColor;
	
	public ClassManager() {
		classes = new ArrayList<Class>();
		defaultCoordinates = new Point3D(-100, 0, 100);
		defaultWidth = 100;
		defaultHeight = 100;
		defaultColor = Color.BLUE;
	}

	public List<Class> getClasses() {
		return classes;
	}

	public void setClasses(List<Class> classes) {
		this.classes = classes;
	}

	public boolean addClass(Class theClass) {
		return classes.add(theClass);
	}
	
	public void createClass(String name, Point3D coordinates, double width, double heigth, Color color) {
		Class theClass = new Class(name, coordinates, width, heigth, color);
		addClass(theClass);
	}

	public void createClass(String name) {
		createClass(name, defaultCoordinates, defaultWidth, defaultHeight, defaultColor);
	}
}
