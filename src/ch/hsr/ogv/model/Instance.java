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
public class Instance extends ModelBox {

	private Map<Attribute, String> attributeValues = new HashMap<Attribute, String>();

	private ModelClass modelClass;
	
	private static volatile AtomicInteger instanceCounter = new AtomicInteger(0);
	
	public Instance(ModelClass modelClass, Point3D coordinates, double width, double heigth, Color color) {
		super(modelClass.getName(), coordinates, width, heigth, color);
		instanceCounter.addAndGet(1);
		this.modelClass = modelClass;
		this.name = "obj" + instanceCounter + ':' + this.name;
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