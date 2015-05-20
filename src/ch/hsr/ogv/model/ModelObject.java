package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Adrian Rieser
 *
 */
@XmlType(propOrder = { "uniqueID", "attributeValues" })
public class ModelObject extends ModelBox {

	// for un/marshaling only
	private String uniqueID = UUID.randomUUID().toString();
	
	private Map<Attribute, String> attributeValues = new HashMap<Attribute, String>();
	private ModelClass modelClass;

	public static volatile AtomicInteger modelObjectCounter = new AtomicInteger(0);

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	
	public Map<Attribute, String> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(Map<Attribute, String> attributeValues) {
		this.attributeValues = attributeValues;
	}

	public String getAttributeValue(String attributeName) {
		for (Attribute attribute : attributeValues.keySet()) {
			if (attribute.getName().equals(attributeName)) {
				return attributeValues.get(attribute);
			}
		}
		return null;
	}

	@XmlTransient
	public ModelClass getModelClass() {
		return modelClass;
	}

	public void setModelClass(ModelClass modelClass) {
		this.modelClass = modelClass;
	}

	@XmlTransient
	public List<ModelObject> getSuperObjects() {
		if (this.modelClass == null)
			return new ArrayList<ModelObject>();
		return this.modelClass.getSuperObjects(this);
	}

	public void addSuperObject(ModelObject superObject) {
		if (this.modelClass == null)
			return;
		this.modelClass.addSuperObject(this, superObject);
	}

	public boolean isSuperObject() {
		if (this.modelClass == null)
			return false;
		return !this.modelClass.getModelObjects().contains(this);
	}

	// for un/marshaling only
	public ModelObject() {
	}

	public ModelObject(String name, ModelClass modelClass, Point3D coordinates, double width, double heigth, Color color) {
		super(name, coordinates, width, heigth, color);
		this.modelClass = modelClass;
	}

	public void changeAttributeName(Attribute attribute, String name) {
		attribute.setName(name);
		setChanged();
		notifyObservers(attribute);
	}

	public void changeAttributeValue(Attribute attribute, String value) {
		String oldValue = this.attributeValues.put(attribute, value);
		if (oldValue != null) {
			setChanged();
			notifyObservers(attribute);
		}
	}

	public void changeAttributeValue(String attributeName, String value) {
		for (Attribute attribute : attributeValues.keySet()) {
			if (attribute.getName().equals(attributeName)) {
				changeAttributeValue(attribute, value);
			}
		}
	}

	public boolean addAttributeValue(Attribute attribute, String attributeValue) {
		if (attributeValues.containsKey(attribute)) {
			return false;
		}
		attributeValues.put(attribute, attributeValue);
		setChanged();
		notifyObservers(attribute);
		return true;
	}

	public String deleteAttributeValue(Attribute attribute) {
		String deleted = attributeValues.remove(attribute);
		if (deleted != null) {
			setChanged();
			notifyObservers(attribute);
		}
		return deleted;
	}

	public void updateAttribute(Attribute attribute, String value) {
		attributeValues.replace(attribute, value);
	}
	
	@Override
	public String toString() {
		return super.toString() + " - " + name;
	}

}