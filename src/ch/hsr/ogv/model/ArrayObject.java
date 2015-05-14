package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

public class ArrayObject extends ModelBox {
	
	private String allocate = "";
	private ModelClass baseModelClass;
	private List<Attribute> attributes = new ArrayList<Attribute>();
	
	public String getAllocate() {
		return allocate;
	}

	public void setAllocate(String allocate) {
		this.allocate = allocate;
	}

	public ModelClass getBaseModelClass() {
		return baseModelClass;
	}

	public void setBaseModelClass(ModelClass baseModelClass) {
		this.baseModelClass = baseModelClass;
	}

	
	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public ArrayObject(String name, ModelClass baseModelClass, Point3D coordinates, double width, double height, Color color, String allocate) {
		super(name, coordinates, width, height, color);
		this.baseModelClass = baseModelClass;
		this.allocate = allocate;
	}
	
	private boolean addAttribute(Attribute attribute) {
		if (attributes.add(attribute)) { // !attributes.contains(attribute) && 
			return true;
		}
		return false;
	}
	
	public Attribute createAttribute() {
		return createAttribute("m" + this.baseModelClass.getName() + (this.attributes.size() + 1));
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
	
}
