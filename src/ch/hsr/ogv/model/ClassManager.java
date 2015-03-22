package ch.hsr.ogv.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * 
 * @author arieser
 *
 */
public class ClassManager extends Observable {

	private Map<String, Class> classes;

	public ClassManager() {
		classes = new HashMap<String, Class>();
	}

	public Collection<Class> getClasses() {
		return classes.values();
	}

	private void addClass(Class theClass) {
		classes.put(theClass.getName(), theClass);
		setChanged();
		notifyObservers(theClass);
	}

	public void createClass(String name, Point3D coordinates, double width, double heigth, Color color) {
		if (!classes.containsKey(name)) {
			Class theClass = new Class(name, coordinates, width, heigth, color);
			addClass(theClass);
		}
	}

	public void createClass(String name, Point3D coordinates) {
		createClass(name, coordinates, ModelBox.DEFAULT_WIDTH, ModelBox.DEFAULT_HEIGHT, ModelBox.DEFAULT_COLOR);
	}

	public Class getClass(String name) {
		return classes.get(name);
	}

	public boolean isNameTaken(String name) {
		return classes.containsKey(name);
	}
	
	public void deleteClass(Class theClass){
		classes.remove(theClass.getName());
	}
}
