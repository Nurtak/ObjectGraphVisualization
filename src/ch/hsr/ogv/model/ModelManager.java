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
		ModelClass modelClass = new ModelClass(coordinates, width, heigth, color);
		classes.add(modelClass);
		setChanged();
		notifyObservers(modelClass);
		return modelClass;
	}

	public ModelObject createObject(ModelClass modelClass) {
		ModelObject modelObject = modelClass.createModelObject();
		setChanged();
		notifyObservers(modelObject);
		return modelObject;
	}

	public ModelClass getClass(String name) {
		if (name == null)
			return null;
		for (ModelClass modelClass : this.classes) {
			if (name.equals(modelClass.getName())) {
				return modelClass;
			}
		}
		return null;
	}

	public boolean isNameTaken(String name) {
		for (ModelClass modelClass : this.classes) {
			if (name != null && name.equals(modelClass.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean deleteClass(ModelClass modelClass) {
		boolean deletedClass = classes.remove(modelClass);
		if (deletedClass) {
			setChanged();
			notifyObservers(deletedClass);
		}
		//ModelClass.modelClassCounter.decrementAndGet();
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

	public Relation createRelation(ModelBox startBox, ModelBox endBox, RelationType relationType) {
		Relation relation = new Relation(startBox, endBox, relationType);
		startBox.getEndpoints().add(relation.getStart());
		endBox.getEndpoints().add(relation.getEnd());
		relations.add(relation);
		setChanged();
		notifyObservers(relation);
		return relation;
	}

}
