package ch.hsr.ogv.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class ModelManager extends Observable {

	private Map<String, ModelClass> classes = new HashMap<String, ModelClass>();
	private Set<Relation> relations = new HashSet<Relation>();
	
	public Collection<ModelClass> getClasses() {
		return classes.values();
	}

	private void addClass(ModelClass theClass) {
		classes.put(theClass.getName(), theClass);
		setChanged();
		notifyObservers(theClass);
	}

	public void createClass(String name, Point3D coordinates, double width, double heigth, Color color) {
		if (!isNameTaken(name)) {
			ModelClass theClass = new ModelClass(name, coordinates, width, heigth, color);
			addClass(theClass);
		}
	}

	public ModelClass getClass(String name) {
		return classes.get(name);
	}

	public boolean isNameTaken(String name) {
		return classes.containsKey(name);
	}

	public ModelClass deleteClass(ModelClass theClass) {
		return classes.remove(theClass.getName());
	}

	public void createRelation(ModelClass start, Point3D endpointCoords, RelationType relationType) {
		//TODO
	}
	
}
