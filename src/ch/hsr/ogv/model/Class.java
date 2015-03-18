package ch.hsr.ogv.model;

import java.util.ArrayList;
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
	private List<Attribute> attributes;
	private List<Instance> instances;

	public Class(String name, Point3D coordinates, double width, double heigth, Color color) {
		super(coordinates, width, heigth, color);
		this.name = name;
		attributes = new ArrayList<Attribute>();
		instances = new ArrayList<Instance>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	public boolean addAttribute(Attribute attribute) {
		return attributes.add(attribute);
	}

	public boolean addInstance(Instance instance) {
		return instances.add(instance);
	}

	public void createAttribute(String name) {
		Attribute attribute = new Attribute(name);
		addAttribute(attribute);
	}

	public void createInstance(String name, Point3D coordinates, double width, double height, Color color) {
		Instance instance = new Instance(name, coordinates, width, height, color);
		addInstance(instance);
	}
}