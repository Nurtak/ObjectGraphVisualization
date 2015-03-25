package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import jfxtras.labs.util.Util;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class ModelClass extends ModelBox {

	private LinkedHashSet<Attribute> attributes = new LinkedHashSet<Attribute>();
	private List<Instance> instances = new ArrayList<Instance>();

	public ModelClass(String name, Point3D coordinates, double width, double heigth, Color color) {
		super(name, coordinates, width, heigth, color);
	}

	public LinkedHashSet<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(LinkedHashSet<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	public boolean addAttribute(Attribute attribute) {
		if (!attributes.contains(attribute)) {
			if(attributes.add(attribute)) {
				for(Instance instance : getInstances()) {
					instance.addAttributeValue(attribute, "");
				}	
			}
		}
		return false;
	}

	public boolean deleteAttribute(Attribute attribute) {
		for(Instance instance : getInstances()) {
			instance.removeAttributeValue(attribute);
		}
		return attributes.remove(attribute);
	}

	private boolean addInstance(Instance instance) {
		if (!instances.contains(instance)) {
			return instances.add(instance);
		}
		return false;
	}

	public boolean deleteInstance(Instance instance) {
		return instances.remove(instance);
	}

	public void createAttribute(String name) {
		Attribute attribute = new Attribute(name);
		addAttribute(attribute);
	}

	public Instance createInstance(ModelClass theClass) {
		int levelPlus = (theClass.getInstances().size() + 1) * 100;
		Point3D instanceCoordinates = new Point3D(theClass.getX(), theClass.getY() + levelPlus, theClass.getZ());
		Instance instance = new Instance(theClass, instanceCoordinates, theClass.getWidth(), theClass.getHeight(), Util.brighter(theClass.getColor(), 0.1));
		for(Attribute attribute : getAttributes()) {
			instance.addAttributeValue(attribute, "");
		}
		boolean added = addInstance(instance);
		if(added) {
			return instance;
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
