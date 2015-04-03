package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import jfxtras.labs.util.Util;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class ModelClass extends ModelBox {
	
	private final static double OBJECT_LEVEL_DIFF = 100;

	private List<Attribute> attributes = new ArrayList<Attribute>();
	private List<ModelObject> modelObjects = new ArrayList<ModelObject>();
	
	public static volatile AtomicInteger modelClassCounter = new AtomicInteger(0);

	public ModelClass(Point3D coordinates, double width, double heigth, Color color) {
		super("Class" + modelClassCounter.addAndGet(1), coordinates, width, heigth, color);
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public List<ModelObject> getModelObjects() {
		return modelObjects;
	}

	public void setModelObjects(List<ModelObject> modelObjects) {
		this.modelObjects = modelObjects;
	}

	public boolean addAttribute(Attribute attribute) {
		if (!attributes.contains(attribute)) {
			if(attributes.add(attribute)) {
				for(ModelObject modelObject : getModelObjects()) {
					modelObject.addAttributeValue(attribute, "");
				}	
			}
		}
		return false;
	}

	public boolean deleteAttribute(Attribute attribute) {
		for(ModelObject modelObject : getModelObjects()) {
			modelObject.removeAttributeValue(attribute);
		}
		return attributes.remove(attribute);
	}

	private boolean addModelObject(ModelObject modelObject) {
		if (!modelObjects.contains(modelObject)) {
			return modelObjects.add(modelObject);
		}
		return false;
	}

	public boolean deleteModelObject(ModelObject modelObject) {
		//ModelObject.modelObjectCounter.decrementAndGet();
		return modelObjects.remove(modelObject);
	}

	public void createAttribute(String name) {
		Attribute attribute = new Attribute(name);
		addAttribute(attribute);
	}

	public ModelObject createModelObject() {
		double levelPlus = (this.getModelObjects().size() + 1.0) * OBJECT_LEVEL_DIFF;
		Point3D modelObjectCoordinates = new Point3D(this.getX(), this.getY() + levelPlus, this.getZ());
		ModelObject modelObject = new ModelObject(this, modelObjectCoordinates, this.getWidth(), this.getHeight(), Util.brighter(this.getColor(), 0.1));
		for(Attribute attribute : getAttributes()) {
			modelObject.addAttributeValue(attribute, "");
		}
		boolean added = addModelObject(modelObject);
		if(added) {
			return modelObject;
		}
		return null;
	}

	public boolean hasSuperClass() {
		for (Endpoint endpoint : this.getEndpoints()) {
			if (endpoint.getFriend().getType() == EndpointType.EMPTY_ARROW) {
				return true;
			}
		}
		return false;
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
