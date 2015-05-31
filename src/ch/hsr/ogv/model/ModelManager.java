package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import jfxtras.labs.util.Util;
import ch.hsr.ogv.util.TextUtil;

/**
 *
 * @author Adrian Rieser
 * @version OGV 3.1, May 2015
 *
 */
public class ModelManager extends Observable {

	private Set<ModelClass> classes = new LinkedHashSet<ModelClass>();
	private Set<Relation> relations = new LinkedHashSet<Relation>();
	
	public Set<ModelClass> getClasses() {
		return this.classes;
	}

	public void setClasses(Set<ModelClass> classes) {
		this.classes = classes;
	}

	public Set<Relation> getRelations() {
		return this.relations;
	}

	public void setRelations(Set<Relation> relations) {
		this.relations = relations;
	}
	
	public ModelClass createClass(Point3D coordinates, double width, double heigth, Color color) {
		int classCount = ModelClass.modelClassCounter.addAndGet(1);
		String newClassName = "Class" + classCount;
		while (isClassNameTaken(newClassName)) {
			newClassName = TextUtil.countUpTrailing(newClassName, classCount);
			if (isClassNameTaken(newClassName)) {
				classCount = ModelClass.modelClassCounter.addAndGet(1);
			}
		}
		ModelClass modelClass = new ModelClass(newClassName, coordinates, width, heigth, color);
		this.classes.add(modelClass);
		setChanged();
		notifyObservers(modelClass);
		return modelClass;
	}

	public ModelObject createObject(ModelClass modelClass) {
		int objectCount = ModelObject.modelObjectCounter.addAndGet(1);
		String newObjectName = "obj" + objectCount;
		while (isObjectNameTaken(modelClass, newObjectName)) {
			newObjectName = TextUtil.countUpTrailing(newObjectName, objectCount);
			if (isClassNameTaken(newObjectName)) {
				objectCount = ModelObject.modelObjectCounter.addAndGet(1);
			}
		}
		ModelObject modelObject = modelClass.createModelObject(newObjectName);

		buildGeneralizationObjects(modelClass);

		setChanged();
		notifyObservers(modelObject);
		return modelObject;
	}

	private void createSuperObjects(List<ModelClass> superClasses, ModelClass subClass) {
		for (ModelClass superClass : superClasses) {
			ArrayList<ModelObject> uncoveredObjects = new ArrayList<ModelObject>(subClass.getModelObjects());
			List<ModelObject> superObjectList = subClass.getSuperObjects(superClass);
			for (ModelObject superObject : superObjectList) {
				uncoveredObjects.remove(subClass.getSubModelObject(superObject));
			}
			for (ModelObject subObject : uncoveredObjects) {
				createSuperObject(superClass, subObject);
			}
		}
	}

	private ModelObject createSuperObject(ModelClass superClass, ModelObject subObject) {
		ModelObject.modelObjectCounter.addAndGet(1);
		ModelClass subClass = subObject.getModelClass();
		double newZ = subObject.getModelClass().getZ();
		newZ += subObject.getModelClass().getHeight() / 2;
		for (ModelObject superObject : subObject.getSuperObjects()) {
			newZ += superObject.getHeight();
		}
		newZ += superClass.getHeight() / 2;
		Point3D modelObjectCoordinates = new Point3D(subObject.getX(), subObject.getY(), newZ);
		ModelObject superObject = new ModelObject("", superClass, modelObjectCoordinates, subClass.getWidth(), superClass.getHeight(), Util.brighter(superClass.getColor(), 0.1));
		for (Attribute attribute : superClass.getAttributes()) {
			superObject.addAttributeValue(attribute, "");
		}
		subObject.addSuperObject(superObject);
		setChanged();
		notifyObservers(superObject);
		return superObject;
	}

	public Relation createRelation(ModelBox start, ModelBox end, RelationType relationType, Color color) {
		if (start != null && end != null) {
			Relation relation = new Relation(start, end, relationType, color);
			start.getEndpoints().add(relation.getStart());
			end.getEndpoints().add(relation.getEnd());
			relations.add(relation);

			if (RelationType.GENERALIZATION.equals(relation.getRelationType()) && start instanceof ModelClass) {
				buildGeneralizationObjects((ModelClass) start);
			}

			setChanged();
			notifyObservers(relation);
			return relation;
		}
		return null;
	}

	public void clearClasses() {
		for (ModelClass modelClass : new ArrayList<ModelClass>(classes)) {
			deleteClass(modelClass);
		}
		ModelClass.modelClassCounter.set(0);
		ModelObject.modelObjectCounter.set(0);
	}
	
	public boolean deleteClass(ModelClass modelClass) {
		for (ModelClass subClass : modelClass.getSubClasses()) {
			for (ModelObject subSuperObject : subClass.getSuperObjects(modelClass)) {
				deleteSuperObject(subClass, subSuperObject);
			}
		}

		for (ModelObject superObject : modelClass.getSuperObjects()) {
			deleteSuperObject(modelClass, superObject);
		}

		for (ModelObject modelObject : new ArrayList<ModelObject>(modelClass.getModelObjects())) {
			deleteObject(modelObject);
		}

		ArrayList<Endpoint> classesEndPoints = new ArrayList<Endpoint>(modelClass.getEndpoints());
		for (Endpoint endPoint : classesEndPoints) {
			deleteRelation(endPoint.getRelation());
		}

		boolean deletedClass = classes.remove(modelClass);
		if (deletedClass) {
			setChanged();
			notifyObservers(modelClass);
		}
		return deletedClass;
	}

	private boolean deleteSuperObject(ModelClass subClass, ModelObject superObject) {
		ArrayList<Endpoint> objectsEndPoints = new ArrayList<Endpoint>(superObject.getEndpoints());
		for (Endpoint endPoint : objectsEndPoints) {
			deleteRelation(endPoint.getRelation());
		}
		boolean deletedObject = subClass.deleteSuperObject(superObject);
		if (deletedObject) {
			setChanged();
			notifyObservers(superObject);
			subClass.setCoordinates(subClass.getCoordinates()); // triggers repositioning
		}
		return deletedObject;
	}

	public boolean deleteObject(ModelObject modelObject) {
		ArrayList<Endpoint> objectsEndPoints = new ArrayList<Endpoint>(modelObject.getEndpoints());
		for (Endpoint endPoint : objectsEndPoints) {
			deleteRelation(endPoint.getRelation());
		}

		for (ModelObject superObject : new ArrayList<ModelObject>(modelObject.getSuperObjects())) {
			deleteSuperObject(modelObject.getModelClass(), superObject);
		}

		boolean deletedObject = modelObject.getModelClass().deleteModelObject(modelObject);
		if (deletedObject) {
			setChanged();
			notifyObservers(modelObject);
		}
		return deletedObject;
	}

	private void buildGeneralizationObjects(ModelClass start) {
		ModelClass startClass = start;
		List<ModelClass> superClasses = startClass.getSuperClasses();
		List<ModelClass> subClasses = startClass.getSubClasses();
		subClasses.add(startClass);
		for (ModelClass subClass : subClasses) {
			createSuperObjects(superClasses, subClass);
		}
	}

	private void cleanupGeneralizationObjects(Relation relation, List<ModelClass> beforeSuperClasses) {
		ModelBox startModelBox = relation.getStart().getAppendant();
		ModelBox endModelBox = relation.getEnd().getAppendant();
		if (!(startModelBox instanceof ModelClass) || !(endModelBox instanceof ModelClass)) {
			return;
		}
		ModelClass startClass = (ModelClass) startModelBox;
		List<ModelClass> subClasses = new ArrayList<ModelClass>(startClass.getSubClasses());
		subClasses.add(startClass);
		for (ModelClass subClass : subClasses) {
			for (ModelClass superClass : beforeSuperClasses) {
				List<ModelClass> afterSuperClasses = new ArrayList<ModelClass>(subClass.getSuperClasses());
				if(!afterSuperClasses.contains(superClass)) {
					for (ModelObject subSuperObject : subClass.getSuperObjects(superClass)) {
						deleteSuperObject(subClass, subSuperObject);
					}
				}
			}
		}
	}

	private int countSameColored(List<Relation> relationList, Relation toCheck) {
		int count = 0;
		relationList.remove(toCheck);
		for(Relation relation : relationList) {
			if(relation.getColor().equals(toCheck.getColor())) {
				count++;
			}
		}
		return count;
	}
	
	private void deleteObjectRelations(Relation relation, ModelBox startBox, ModelBox endBox) {
		if (startBox instanceof ModelClass && endBox instanceof ModelClass) {
			ModelClass startClass = (ModelClass) startBox;
			ModelClass endClass = (ModelClass) endBox;
			List<Relation> classRelations = getRelationsBetween(startClass, endClass); // relation we delete still contained
			boolean deleteAllObjRel = classRelations.size() <= 1;
			boolean hasOtherWithColor = countSameColored(classRelations, relation) > 0;
			List<ModelObject> startObjects = startClass.getModelObjects();
			for (ModelObject startObject : startObjects) {
				for (ModelObject endObject : endClass.getModelObjects()) {
					List<Relation> objectRelations = getRelationsBetween(startObject, endObject);
					for(Relation objectRelation : objectRelations) {
						if(deleteAllObjRel || (!hasOtherWithColor && objectRelation.getColor().equals(relation.getColor()))) {
							deleteRelation(objectRelation);
						}
					}
				}
			}
		}
	}

	public void clearRelations() {
		for (Relation relation : new ArrayList<Relation>(relations)) {
			deleteRelation(relation);
		}
	}
	
	public boolean deleteRelation(Relation relation) {
		List<ModelClass> superClasses = new ArrayList<ModelClass>();
		ModelBox startModelBox = relation.getStart().getAppendant();
		if(RelationType.GENERALIZATION.equals(relation.getRelationType()) && startModelBox instanceof ModelClass) {
			ModelClass startClass = (ModelClass) startModelBox;
			superClasses = startClass.getSuperClasses();
		}
		boolean deletedRelation = relations.remove(relation);
		if (deletedRelation) {
			Endpoint start = relation.getStart();
			Endpoint end = relation.getEnd();
			ModelBox startBox = start.getAppendant();
			ModelBox endBox = end.getAppendant();

			deleteObjectRelations(relation, startBox, endBox);

			startBox.getEndpoints().remove(start);
			endBox.getEndpoints().remove(end);
			
			if (RelationType.GENERALIZATION.equals(relation.getRelationType())) {
				cleanupGeneralizationObjects(relation, superClasses);
			}
			
			setChanged();
			notifyObservers(relation);
		}
		
		return deletedRelation;
	}

	public ModelClass getModelClass(String name) {
		if (name == null || name.isEmpty()) {
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
			if (name != null && !name.isEmpty() && name.equals(modelObject.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isAttributeNameTaken(ModelClass modelClass, String name) {
		for (Attribute attribute : modelClass.getAttributes()) {
			if (name != null && !name.isEmpty() && name.equals(attribute.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isRoleNameTaken(ModelClass modelClass, String name) {
		for (Endpoint endpoint : modelClass.getEndpoints()) {
			Endpoint friend = endpoint.getFriend();
			if (name != null && !name.isEmpty() && friend != null && name.equals(friend.getRoleName())) {
				return true;
			}
		}
		return false;
	}
	
	public List<Relation> getRelationsBetween(ModelBox thisModelBox, ModelBox otherModelBox) {
		List<Relation> relationList = new ArrayList<Relation>();
		if(thisModelBox == null || otherModelBox == null) {
			return relationList;
		}
		// List<ModelClass> superClasses = new ArrayList<ModelClass>();
		// if(thisModelBox instanceof ModelClass && otherModelBox instanceof ModelClass) {
		// ModelClass otherClass = (ModelClass) otherModelBox;
		// superClasses = otherClass.getSubClasses();
		// }
		
		for (Endpoint endpoint : thisModelBox.getEndpoints()) {
			boolean directlyConnected = endpoint.getFriend() != null && endpoint.getFriend().getAppendant() != null && endpoint.getFriend().getAppendant().equals(otherModelBox);
			// boolean superConnected = endpoint.getFriend() != null && endpoint.getFriend().getAppendant() != null && superClasses.contains(endpoint.getFriend().getAppendant());
			if (directlyConnected) { // || superConnected
				Relation relation = endpoint.getRelation();
				if(relation.isReflexive() && endpoint.isStart()) { // otherwise reflexive relations end up twice in the list
					continue;
				}
				relationList.add(endpoint.getRelation());
			}
		}
		return relationList;
	}

}
