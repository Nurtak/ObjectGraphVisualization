package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import jfxtras.labs.util.Util;

/**
 *
 * @author Adrian Rieser
 *
 */
@XmlType(propOrder = { "attributes", "modelObjects" })
public class ModelClass extends ModelBox {

	public final static double OBJECT_LEVEL_DIFF = 100;
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private List<ModelObject> modelObjects = new ArrayList<ModelObject>();
	private HashMap<ModelObject, ArrayList<ModelObject>> superObjects = new HashMap<ModelObject, ArrayList<ModelObject>>();

	public static volatile AtomicInteger modelClassCounter = new AtomicInteger(0);

	// for marshaling only
	public ModelClass() {
	}

	public ModelClass(String name, Point3D coordinates, double width, double height, Color color) {
		super(name, coordinates, width, height, color);
	}

	@XmlElementWrapper(name = "attributes")
	@XmlElement(name = "attribute")
	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	@XmlElementWrapper(name = "objects")
	@XmlElement(name = "object")
	public List<ModelObject> getModelObjects() {
		return modelObjects;
	}

	public ModelObject getModelObject(String name) {
		if (name == null || name.isEmpty()) {
			return null;
		}
		for (ModelObject modelObject : this.modelObjects) {
			if (name.equals(modelObject.getName())) {
				return modelObject;
			}
		}
		return null;
	}

	public void setModelObjects(List<ModelObject> modelObjects) {
		this.modelObjects = modelObjects;
	}

	private boolean addModelObject(ModelObject modelObject) {
		if (modelObject != null && !modelObjects.contains(modelObject)) {
			return modelObjects.add(modelObject);
		}
		return false;
	}

	public void addSuperObject(ModelObject subObject, ModelObject superObject) {
		if (subObject == null || superObject == null)
			return;
		ArrayList<ModelObject> superObjectContainer = new ArrayList<ModelObject>();
		if (this.superObjects.containsKey(subObject)) {
			superObjectContainer = this.superObjects.get(subObject);
		}
		superObjectContainer.add(superObject);
		this.superObjects.put(subObject, superObjectContainer);
	}

	public void removeSuperObject(ModelObject superObject) {
		for (ModelObject subObject : this.superObjects.keySet()) {
			this.superObjects.get(subObject).remove(superObject);
		}
	}

	protected void removeAllSuperObjects(ModelObject subModelObject) {
		this.superObjects.remove(subModelObject);
	}

	protected void removeAllSuperObjects(ModelClass superClass) {
		for (ModelObject subObject : this.superObjects.keySet()) {
			ArrayList<ModelObject> superObjectList = this.superObjects.get(subObject);
			for (ModelObject superObject : superObjectList) {
				if (superObject.getModelClass().equals(superClass)) {
					superObjectList.remove(superObject);
				}
			}
		}
	}

	public ModelObject getSubModelObject(ModelObject superObject) {
		for (ModelObject subObject : this.superObjects.keySet()) {
			if (this.superObjects.get(subObject).contains(superObject)) {
				return subObject;
			}
		}
		return null;
	}

	public List<ModelObject> getSuperObjects(ModelObject subObject) {
		ArrayList<ModelObject> emptyList = new ArrayList<ModelObject>();
		if (subObject == null) {
			return emptyList;
		}
		ArrayList<ModelObject> superObjectList = this.superObjects.get(subObject);
		if (superObjectList != null) {
			return superObjectList;
		}
		return emptyList;
	}

	public List<ModelObject> getSuperObjects(ModelClass superClass) {
		ArrayList<ModelObject> retList = new ArrayList<ModelObject>();
		if (superClass == null) {
			return retList;
		}
		for (ModelObject subObject : this.superObjects.keySet()) {
			ArrayList<ModelObject> superObjectList = this.superObjects.get(subObject);
			for (ModelObject superObject : superObjectList) {
				if (superObject.getModelClass().equals(superClass)) {
					retList.add(superObject);
				}
			}
		}
		return retList;
	}

	public List<ModelObject> getSuperObjects() {
		ArrayList<ModelObject> retList = new ArrayList<ModelObject>();
		for (ModelObject subObject : this.superObjects.keySet()) {
			retList.addAll(this.superObjects.get(subObject));
		}
		return retList;
	}

	public List<ModelObject> getInheritingObjects() {
		ArrayList<ModelObject> retList = new ArrayList<ModelObject>();
		for (ModelClass subClass : getSubClasses()) {
			retList.addAll(subClass.getSuperObjects(this));
		}
		return retList;
	}

	private boolean addAttribute(Attribute attribute) {
		if (!attributes.contains(attribute) && attributes.add(attribute)) {
			for (ModelObject modelObject : getModelObjects()) {
				modelObject.addAttributeValue(attribute, "");
			}
			for (ModelObject inheritingObject : getInheritingObjects()) {
				inheritingObject.addAttributeValue(attribute, "");
			}
			return true;
		}
		return false;
	}

	public ModelObject createModelObject(String name) {
		double levelPlus = getTopLevel() + OBJECT_LEVEL_DIFF;
		Point3D modelObjectCoordinates = new Point3D(this.getX(), levelPlus, this.getZ());
		ModelObject modelObject = new ModelObject(name, this, modelObjectCoordinates, this.getWidth(), this.getHeight(), Util.brighter(this.getColor(), 0.1));
		for (Attribute attribute : getAttributes()) {
			modelObject.addAttributeValue(attribute, "");
		}
		boolean added = addModelObject(modelObject);
		if (added) {
			return modelObject;
		}
		return null;
	}

	public void resetObjectLevel() {
		int levelCount = 1;
		for (ModelObject modelObject : this.modelObjects) {
			double level = (levelCount) * OBJECT_LEVEL_DIFF;
			Point3D modelObjectCoordinates = new Point3D(modelObject.getX(), level, this.getZ());
			modelObject.setCoordinates(modelObjectCoordinates);
			levelCount++;
		}
	}

	private double getTopLevel() {
		double y = 0.0;
		for (ModelObject modelObject : this.modelObjects) {
			if (modelObject.getY() > y) {
				y = modelObject.getY();
			}
		}
		return y;
	}

	public void deleteModelObjects() {
		this.modelObjects.clear();
	}

	public boolean deleteModelObject(ModelObject modelObject) {
		return this.modelObjects.remove(modelObject);
	}

	public void deleteSuperObjects() {
		this.superObjects.clear();
	}

	public boolean deleteSuperObject(ModelObject superObject) {
		boolean removed = false;
		for (ArrayList<ModelObject> superObjectList : this.superObjects.values()) {
			if (superObjectList.remove(superObject)) {
				removed = true;
			}
		}
		return removed;
	}

	public Attribute createAttribute() {
		return createAttribute("attr" + (this.attributes.size() + 1));
	}

	public Attribute createAttribute(String attributeName) {
		Attribute attribute = new Attribute(attributeName);
		boolean added = addAttribute(attribute);
		if (added) {
			setChanged();
			notifyObservers(attribute);
			return attribute;
		}
		return null;
	}

	public boolean deleteAttribute(int rowIndex) {
		if (getAttributes().isEmpty() || rowIndex < 0 || rowIndex >= getAttributes().size()) {
			return false;
		}
		Attribute attribute = getAttributes().get(rowIndex);
		return deleteAttribute(attribute);
	}

	public boolean moveAttributeUp(int rowIndex) {
		if (getAttributes().isEmpty() || rowIndex <= 0 || rowIndex >= getAttributes().size()) {
			return false;
		}
		Attribute thisAttribute = getAttributes().get(rowIndex);
		Attribute upperAttribute = getAttributes().set(rowIndex - 1, thisAttribute);
		getAttributes().set(rowIndex, upperAttribute);
		setChanged();
		notifyObservers(thisAttribute);
		for (ModelObject modelObject : this.modelObjects) {
			modelObject.changeAttributeName(thisAttribute, thisAttribute.getName());
			// modelObject.changeAttributeName(upperAttribute, upperAttribute.getName());
		}
		for (ModelObject inheritingObject : getInheritingObjects()) {
			inheritingObject.changeAttributeName(thisAttribute, thisAttribute.getName());
		}
		return true;
	}

	public boolean moveAttributeDown(int rowIndex) {
		if (getAttributes().isEmpty() || rowIndex < 0 || rowIndex >= getAttributes().size() - 1) {
			return false;
		}
		Attribute thisAttribute = getAttributes().get(rowIndex);
		Attribute lowerAttribute = getAttributes().set(rowIndex + 1, thisAttribute);
		getAttributes().set(rowIndex, lowerAttribute);
		setChanged();
		notifyObservers(thisAttribute);
		for (ModelObject modelObject : this.modelObjects) {
			modelObject.changeAttributeName(thisAttribute, thisAttribute.getName());
			// modelObject.changeAttributeName(lowerAttribute, lowerAttribute.getName());
		}
		for (ModelObject inheritingObject : getInheritingObjects()) {
			inheritingObject.changeAttributeName(thisAttribute, thisAttribute.getName());
		}
		return true;
	}

	public void changeAttributeName(int rowIndex, String name) throws IndexOutOfBoundsException {
		Attribute attribute = this.attributes.get(rowIndex);
		attribute.setName(name);
		setChanged();
		notifyObservers(attribute);
		for (ModelObject modelObject : this.modelObjects) {
			modelObject.changeAttributeName(attribute, name);
		}
		for (ModelObject inheritingObject : getInheritingObjects()) {
			inheritingObject.changeAttributeName(attribute, name);
		}
	}

	private boolean deleteAttribute(Attribute attribute) {
		boolean deleted = attributes.remove(attribute);
		for (ModelObject modelObject : getModelObjects()) {
			modelObject.deleteAttributeValue(attribute);
		}
		for (ModelObject inheritingObject : getInheritingObjects()) {
			inheritingObject.deleteAttributeValue(attribute);
		}
		if (deleted) {
			setChanged();
			notifyObservers(attribute);
		}
		return deleted;
	}

	public List<ModelClass> getSubClasses() {
		ArrayList<ModelClass> subClassList = new ArrayList<ModelClass>();
		for (Endpoint endpoint : getEndpoints()) {
			if (endpoint.getFriend() != null && endpoint.getType() == EndpointType.EMPTY_ARROW) {
				ModelBox modelBox = endpoint.getFriend().getAppendant();
				if (modelBox != null && modelBox instanceof ModelClass) {
					ModelClass subClass = (ModelClass) modelBox;
					if (!this.equals(subClass)) {
						subClassList.addAll(subClass.getSubClasses()); // recursively getting all sub classes
						subClassList.add(subClass);
					}
				}
			}
		}
		return subClassList;
	}
	
	private ModelClass getDirectSuperClass(Endpoint endpoint) {
		if (endpoint.getFriend() != null && endpoint.getFriend().getType() == EndpointType.EMPTY_ARROW) {
			ModelBox modelBox = endpoint.getFriend().getAppendant();
			if (modelBox != null && modelBox instanceof ModelClass) {
				return (ModelClass) modelBox;
			}
		}
		return null;
	}

	public List<ModelClass> getSuperClasses() {
		ArrayList<ModelClass> superClassList = new ArrayList<ModelClass>();
		for (Endpoint endpoint : getEndpoints()) {
			ModelClass superClass = getDirectSuperClass(endpoint);
			if(superClass != null) {
				superClassList.add(superClass);
				superClassList.addAll(superClass.getSuperClasses());
			}
		}
		return superClassList;
	}

	@Override
	public String toString() {
		return super.toString() + " - " + name;
	}

}
