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

	private Map<String, ModelClass> classes;

	public ClassManager() {
		classes = new HashMap<String, ModelClass>();
	}

	public Collection<ModelClass> getClasses() {
		return classes.values();
	}

	private void addClass(ModelClass theClass) {
		classes.put(theClass.getName(), theClass);
		setChanged();
		notifyObservers(theClass);
	}

	public void createClass(String name, Point3D coordinates, double width, double heigth, Color color) {
		if (!classes.containsKey(name)) {
			ModelClass theClass = new ModelClass(name, coordinates, width, heigth, color);
			addClass(theClass);
		}
	}

	public void createClass(String name, Point3D coordinates) {
		createClass(name, coordinates, ModelBox.DEFAULT_WIDTH, ModelBox.DEFAULT_HEIGHT, ModelBox.DEFAULT_COLOR);
	}

	public ModelClass getClass(String name) {
		return classes.get(name);
	}

	public boolean isNameTaken(String name) {
		return classes.containsKey(name);
	}
	
	public void deleteClass(ModelClass theClass){
		classes.remove(theClass.getName());
	}
}
