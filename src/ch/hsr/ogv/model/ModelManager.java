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

	public ModelClass createClass(String classname, Point3D coordinates, double width, double heigth, Color color) {
		ModelClass theClass = null;
		if(!isNameTaken(classname)) {
			theClass = new ModelClass(classname, coordinates, width, heigth, color);
			classes.put(theClass.getName(), theClass);
			setChanged();
			notifyObservers(theClass);
		}
		return theClass;
	}
	
	public Instance createInstance(ModelClass theClass) {
		Instance instance = theClass.createInstance(theClass);
		setChanged();
		notifyObservers(theClass);
		return instance;
	}

	public ModelClass getClass(String name) {
		return classes.get(name);
	}

	public boolean isNameTaken(String name) {
		return classes.containsKey(name);
	}

	public ModelClass deleteClass(ModelClass theClass) {
		ModelClass deletedClass = classes.remove(theClass.getName());
		if(deletedClass != null) {
			setChanged();
			notifyObservers(deletedClass);
		}
		return deletedClass;
	}
	
	public boolean deleteRelation(Relation relation) {
		boolean deletedRelation = relations.remove(relation);
		if(deletedRelation) {
			Endpoint start = relation.getStart();
			Endpoint end = relation.getEnd();
			start.getAppendant().getEndpoints().remove(start);
			end.getAppendant().getEndpoints().remove(end);
			setChanged();
			notifyObservers(relation);
		}
		return deletedRelation;
	}

	public Relation createRelation(ModelClass startClass, ModelClass endClass, RelationType relationType) {
		Endpoint start = new Endpoint(relationType.getStartType(), startClass);
		Endpoint end = new Endpoint(relationType.getEndType(), endClass);
		startClass.getEndpoints().add(start);
		endClass.getEndpoints().add(end);
		Relation relation = new Relation(start, end, relationType.getLineType());
		relations.add(relation);
		setChanged();
		notifyObservers(relation);
		return relation;
	}
	
}
