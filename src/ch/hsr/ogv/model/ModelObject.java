package ch.hsr.ogv.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class ModelObject extends ModelBox {

	private Map<Attribute, String> attributeValues = new HashMap<Attribute, String>();

	private ModelClass modelClass;
	
	public static volatile AtomicInteger modelObjectCounter = new AtomicInteger(0);
	
	public ModelObject(ModelClass modelClass, Point3D coordinates, double width, double heigth, Color color) {
		super("obj" + modelObjectCounter.addAndGet(1), coordinates, width, heigth, color);
		this.modelClass = modelClass;
	}

	public Map<Attribute, String> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(Map<Attribute, String> attributeValues) {
		this.attributeValues = attributeValues;
	}

	public boolean addAttributeValue(Attribute attribute, String attributeValue) {
		if (attributeValues.containsKey(attribute)) {
			return false;
		}
		attributeValues.put(attribute, attributeValue);
		return true;
	}
	
	public String removeAttributeValue(Attribute attribute) {
		return attributeValues.remove(attribute);
	}

	public ModelClass getModelClass() {
		return modelClass;
	}

	public void setModelClass(ModelClass modelClass) {
		this.modelClass = modelClass;
	}
	
	public void updateAttribute(Attribute attribute, String value) {
		attributeValues.replace(attribute, value);
	}
	
}