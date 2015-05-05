package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Adrian Rieser
 *
 */
@XmlType(propOrder = { "attributeValues", "isSuperObject", "superObjects" })
public class ModelObject extends ModelBox {

	private Map<Attribute, String> attributeValues = new HashMap<Attribute, String>();
	private boolean isSuperObject = false;
	private List<ModelObject> superObjects = new ArrayList<ModelObject>();
	private ModelClass modelClass;

	public static volatile AtomicInteger modelObjectCounter = new AtomicInteger(0);

	public Map<Attribute, String> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(Map<Attribute, String> attributeValues) {
		this.attributeValues = attributeValues;
	}

	public String getAttributeValue(String attributeName) {
		for(Attribute attribute : attributeValues.keySet()) {
			if(attribute.getName().equals(attributeName)) {
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
	
	public boolean getIsSuperObject() {
		return isSuperObject;
	}

	public void setIsSuperObject(boolean isSuperObject) {
		this.isSuperObject = isSuperObject;
	}
	
	@XmlElementWrapper (name = "superObjects")
	@XmlElement (name = "superObject")
	public List<ModelObject> getSuperObjects() {
		return superObjects;
	}

	public void setSuperObjects(List<ModelObject> superObjects) {
		this.superObjects = superObjects;
	}
	
	@XmlTransient
	public ModelObject getSubObject() {
		if(!this.isSuperObject || getModelClass() == null) return null;
		for(ModelClass subClasses : getModelClass().getSubClasses()) {
			for(ModelObject subObject : subClasses.getModelObjects()) {
				if(subObject.getSuperObjects().contains(this)) {
					return subObject;
				}
			}
		}
		return null;
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
		if(oldValue != null) {
			setChanged();
			notifyObservers(attribute);
		}
	}
	
	public void changeAttributeValue(String attributeName, String value) {
		for(Attribute attribute : attributeValues.keySet()) {
			if(attribute.getName().equals(attributeName)) {
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
		if(deleted != null) {
			setChanged();
			notifyObservers(attribute);
		}
		return deleted;
	}

	public void updateAttribute(Attribute attribute, String value) {
		attributeValues.replace(attribute, value);
	}

}