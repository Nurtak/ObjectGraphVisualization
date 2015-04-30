package ch.hsr.ogv.model;

import java.util.ArrayList;
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
	public static volatile AtomicInteger modelClassCounter = new AtomicInteger(0);

	// For marshaling only
	public ModelClass(){
	}

	public ModelClass(Point3D coordinates, double width, double heigth, Color color) {
		super("Class" + modelClassCounter.addAndGet(1), coordinates, width, heigth, color);
	}

	@XmlElementWrapper (name = "attributes")
	@XmlElement (name = "attribute")
	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	@XmlElementWrapper (name = "objects")
	@XmlElement (name = "object")
	public List<ModelObject> getModelObjects() {
		return modelObjects;
	}

	public void setModelObjects(List<ModelObject> modelObjects) {
		this.modelObjects = modelObjects;
	}

	private boolean addModelObject(ModelObject modelObject) {
		if (!modelObjects.contains(modelObject)) {
			return modelObjects.add(modelObject);
		}
		return false;
	}

	private boolean addAttribute(Attribute attribute) {
		if (!attributes.contains(attribute) && attributes.add(attribute)) {
			for (ModelObject modelObject : getModelObjects()) {
				modelObject.addAttributeValue(attribute, "");
			}
			return true;
		}
		return false;
	}

	public ModelObject createModelObject() {
		double levelPlus = getTopLevel() + OBJECT_LEVEL_DIFF;
		Point3D modelObjectCoordinates = new Point3D(this.getX(), levelPlus, this.getZ());
		ModelObject modelObject = new ModelObject(this, modelObjectCoordinates, this.getWidth(), this.getHeight(), Util.brighter(this.getColor(), 0.1));
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
		for (int i = 0; i < this.modelObjects.size(); i++) {
			ModelObject modelObject = this.modelObjects.get(i);
			double level = (i + 1.0) * OBJECT_LEVEL_DIFF;
			Point3D modelObjectCoordinates = new Point3D(modelObject.getX(), level, this.getZ());
			modelObject.setCoordinates(modelObjectCoordinates);
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
		// ModelObject.modelObjectCounter.decrementAndGet();
		modelObjects.clear();
	}

	public boolean deleteModelObject(ModelObject modelObject) {
		// ModelObject.modelObjectCounter.decrementAndGet();
		boolean removed = modelObjects.remove(modelObject);
		if (removed) {
			double level = getTopLevel() + OBJECT_LEVEL_DIFF;
			Point3D modelObjectCoordinates = new Point3D(modelObject.getX(), level, this.getZ());
			modelObject.setCoordinates(modelObjectCoordinates);
		}
		return removed;
	}

	public Attribute createAttribute() {
		Attribute attribute = new Attribute("field" + (this.attributes.size() + 1));
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
		for (ModelObject modelObject : getModelObjects()) {
			modelObject.changeAttributeName(thisAttribute, thisAttribute.getName());
			// modelObject.changeAttributeName(upperAttribute, upperAttribute.getName());
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
		for (ModelObject modelObject : getModelObjects()) {
			modelObject.changeAttributeName(thisAttribute, thisAttribute.getName());
			// modelObject.changeAttributeName(lowerAttribute, lowerAttribute.getName());
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
	}

	private boolean deleteAttribute(Attribute attribute) {
		boolean deleted = attributes.remove(attribute);
		for (ModelObject modelObject : getModelObjects()) {
			modelObject.deleteAttributeValue(attribute);
		}
		if (deleted) {
			setChanged();
			notifyObservers(attribute);
		}
		return deleted;
	}

	public List<ModelClass> getSubClasses() {
		ArrayList<ModelClass> subClassList = new ArrayList<ModelClass>();
		for (Endpoint endpoint : this.getEndpoints()) {
			if (endpoint.getType() == EndpointType.EMPTY_ARROW) {
				ModelBox modelBox = endpoint.getFriend().getAppendant();
				if (modelBox instanceof ModelClass) {
					subClassList.add((ModelClass) modelBox);
				}
			}
		}
		return subClassList;
	}

	public ModelClass getSuperClass() {
		for (Endpoint endpoint : this.getEndpoints()) {
			if (endpoint.getFriend().getType() == EndpointType.EMPTY_ARROW) {
				return (ModelClass) endpoint.getAppendant();
			}
		}
		return null;
	}

}
