package ch.hsr.ogv.model;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class Instance extends ModelBox {

	private Map<Attribute, String> attributeValues = new HashMap<Attribute, String>();

	public Instance(ModelClass modelClass, Point3D coordinates, double width, double heigth, Color color) {
		super(modelClass.getName(), coordinates, width, heigth, color);
		this.name = "object" + hashCode() + ':' + this.name;
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

	public void updateAttribute(Attribute attribute, String value) {
		attributeValues.replace(attribute, value);
	}
}