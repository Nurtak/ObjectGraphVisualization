package ch.hsr.ogv.model;

import java.util.Collection;
import java.util.HashSet;
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

	private Set<ModelClass> classes = new HashSet<ModelClass>();
	private Set<Relation> relations = new HashSet<Relation>();

	public Collection<ModelClass> getClasses() {
		return this.classes;
	}

	public ModelClass createClass(Point3D coordinates, double width, double heigth, Color color) {
		ModelClass theClass = null;
		theClass = new ModelClass(coordinates, width, heigth, color);
		classes.add(theClass);
		setChanged();
		notifyObservers(theClass);
		return theClass;
	}

	public ModelObject createInstance(ModelClass theClass) {
		ModelObject modelObject = theClass.createModelObject(theClass);
		setChanged();
		notifyObservers(modelObject);
		return modelObject;
	}

	public ModelClass getClass(String name) {
		if (name == null)
			return null;
		for (ModelClass theClass : this.classes) {
			if (name.equals(theClass.getName())) {
				return theClass;
			}
		}
		return null;
	}

	public boolean isNameTaken(String name) {
		for (ModelClass theClass : this.classes) {
			if (name != null && name.equals(theClass.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean deleteClass(ModelClass theClass) {
		boolean deletedClass = classes.remove(theClass);
		if (deletedClass) {
			setChanged();
			notifyObservers(deletedClass);
		}
		ModelClass.modelClassCounter.decrementAndGet();
		return deletedClass;
	}

	public boolean deleteRelation(Relation relation) {
		boolean deletedRelation = relations.remove(relation);
		if (deletedRelation) {
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
		Relation relation = new Relation(startClass, endClass, relationType);
		startClass.getEndpoints().add(relation.getStart());
		endClass.getEndpoints().add(relation.getEnd());
		relations.add(relation);
		setChanged();
		notifyObservers(relation);
		return relation;
	}

}
