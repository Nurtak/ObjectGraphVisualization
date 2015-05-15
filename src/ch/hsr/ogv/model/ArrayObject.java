package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;

public class ArrayObject extends ModelBox {
	
	private String allocate = "";
	private ModelClass baseModelClass;
	private ModelObject referencingObject;

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
	
	public ModelObject getReferencingObject() {
		return referencingObject;
	}

	public void setReferencingObject(ModelObject referencingObject) {
		this.referencingObject = referencingObject;
	}

	
	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public ArrayObject(String name, ModelObject referencingObject, ModelClass baseModelClass, String allocate) {
		super(name, baseModelClass.getCoordinates(), baseModelClass.getWidth(), baseModelClass.getHeight(), baseModelClass.getColor());
		Point3D midpoint = referencingObject.getCoordinates().midpoint(baseModelClass.getCoordinates());
		Point3D coordinates = new Point3D(midpoint.getX(), referencingObject.getY(), midpoint.getZ());
		this.coordinates = coordinates;
		this.baseModelClass = baseModelClass;
		this.referencingObject = referencingObject;
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
	
	public Relation getBaseRelation() {
		return this.baseModelClass.getRelationWith(this.referencingObject.getModelClass());
	}
	
}
