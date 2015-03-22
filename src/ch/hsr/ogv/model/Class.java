package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * 
 * @author arieser
 *
 */
public class Class extends ModelBox {

	private String name;
	private LinkedHashSet<Attribute> attributes;
	private List<Instance> instances;

	public Class(String name, Point3D coordinates) {
		super(coordinates);
		this.name = name;
		attributes = new LinkedHashSet<Attribute>();
		instances = new ArrayList<Instance>();
	}

	public Class(String name, Point3D coordinates, double width, double heigth, Color color) {
		super(coordinates, width, heigth, color);
		this.name = name;
		attributes = new LinkedHashSet<Attribute>();
		instances = new ArrayList<Instance>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
			return attributes.add(attribute);
		}
		return false;
	}

	public boolean deleteAttribute(Attribute attribute) {
		return attributes.remove(attribute);
	}

	public boolean addInstance(Instance instance) {
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

	public void createInstance(String name, Point3D coordinates) {
		Instance instance = new Instance(name, coordinates);
		addInstance(instance);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Class))
			return false;
		Class other = (Class) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public boolean hasSuperClass() {
		for (Endpoint endpoint : this.getEndpoints()) {
			if (endpoint.getFriend().getType() == EndpointType.EMPTY_ARROW) {
				return true;
			}
		}
		return false;
	}
	
	public Class getSuperClass() {
		for (Endpoint endpoint : this.getEndpoints()) {
			if (endpoint.getFriend().getType() == EndpointType.EMPTY_ARROW) {
				return (Class) endpoint.getTarget();
			}
		}
		return null;
	}

}