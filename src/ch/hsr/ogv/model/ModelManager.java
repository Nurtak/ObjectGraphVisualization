package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Adrian Rieser
 *
 */
@XmlRootElement(name = "modelmanager")
@XmlType(propOrder = { "classes", "relations"})
public class ModelManager extends Observable {

	private Set<ModelClass> classes = new HashSet<ModelClass>();
	private Set<Relation> relations = new HashSet<Relation>();

	public ModelClass createClass(Point3D coordinates, double width, double heigth, Color color) {
		ModelClass modelClass = new ModelClass(coordinates, width, heigth, color);
		this.classes.add(modelClass);
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

	public Relation createRelation(ModelBox start, ModelBox end, RelationType relationType, Color color) {
		if (isRelationAllowed(start, end, relationType)) {
			Relation relation = new Relation(start, end, relationType, color);
			start.getEndpoints().add(relation.getStart());
			end.getEndpoints().add(relation.getEnd());
			relations.add(relation);
			setChanged();
			notifyObservers(relation);
			return relation;
		}
		return null;
	}

	public boolean deleteClass(ModelClass modelClass) {
		ArrayList<Endpoint> classesEndPoints = new ArrayList<Endpoint>(modelClass.getEndpoints());
		for (Endpoint endPoint : classesEndPoints) {
			deleteRelation(endPoint.getRelation());
		}
		ArrayList<ModelObject> classesObjects = new ArrayList<ModelObject>(modelClass.getModelObjects());
		for(ModelObject modelObject : classesObjects) {
			deleteObject(modelObject);
		}
		modelClass.deleteModelObjects();
		boolean deletedClass = classes.remove(modelClass);
		if (deletedClass) {
			setChanged();
			notifyObservers(modelClass);
		}
		// ModelClass.modelClassCounter.decrementAndGet();
		return deletedClass;
	}

	public boolean deleteObject(ModelObject modelObject) {
		ArrayList<Endpoint> objectsEndPoints = new ArrayList<Endpoint>(modelObject.getEndpoints());
		for (Endpoint endPoint : objectsEndPoints) {
			deleteRelation(endPoint.getRelation());
		}
		boolean deletedObject = modelObject.getModelClass().deleteModelObject(modelObject);
		if (deletedObject) {
			setChanged();
			notifyObservers(modelObject);
		}
		return deletedObject;
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

	public ModelClass getClass(String name) {
		if (name == null) {
			return null;
		}
		for (ModelClass modelClass : this.classes) {
			if (name.equals(modelClass.getName())) {
				return modelClass;
			}
		}
		return null;
	}

	public boolean isClassNameTaken(String name) {
		for (ModelClass modelClass : this.classes) {
			if (name != null && name.equals(modelClass.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isObjectNameTaken(ModelClass modelClass, String name) {
		for (ModelObject modelObject : modelClass.getModelObjects()) {
			if (name != null && name.equals(modelObject.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isAttributeNameTaken(ModelClass modelClass, String name) {
		for (Attribute attribute : modelClass.getAttributes()) {
			if (name != null && name.equals(attribute.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isRoleNameTaken(ModelClass modelClass, String name) {
		for (Endpoint endpoint : modelClass.getEndpoints()) {
			Endpoint friend = endpoint.getFriend();
			if (name != null && name.equals(friend.getRoleName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isRelationAllowed(ModelBox start, ModelBox end, RelationType relationType) {
		switch (relationType) {
		case GENERALIZATION:
			if ((start instanceof ModelClass) && (end instanceof ModelClass) && !start.equals(end)) {
				return true;
			}
			return false;
		case UNDIRECTED_ASSOCIATION:
		case DIRECTED_ASSOCIATION:
		case BIDIRECTED_ASSOCIATION:
		case UNDIRECTED_AGGREGATION:
		case DIRECTED_AGGREGATION:
		case UNDIRECTED_COMPOSITION:
		case DIRECTED_COMPOSITION:
		case DEPENDENCY:
			if ((start instanceof ModelClass) && (end instanceof ModelClass)) {
				return true;
			}
			return false;
		case OBJDIAGRAM:
		case OBJGRAPH:
			if ((start instanceof ModelObject) && (end instanceof ModelObject)) {
				return true;
			}
			return false;
		default:
			return false;
		}
	}

	@XmlElementWrapper (name = "classes")
	@XmlElement (name = "class")
	public Set<ModelClass> getClasses() {
		return this.classes;
	}

	public void setClasses(Set<ModelClass> classes) {
		this.classes = classes;
	}

	public void clearClasses() {
		// Copy for concurrent modification
		List<ModelClass> copy = new ArrayList<ModelClass>(classes);
		for (ModelClass modelClass : copy) {
			deleteClass(modelClass);
		}
	}

	@XmlElementWrapper (name = "relations")
	@XmlElement (name = "relation")
	public Set<Relation> getRelations() {
		return this.relations;
	}

	public void setRelations(Set<Relation> relations) {
		this.relations = relations;
	}

	public void clearRelations() {
		for (Relation relation : relations) {
			deleteRelation(relation);
		}
	}
}
