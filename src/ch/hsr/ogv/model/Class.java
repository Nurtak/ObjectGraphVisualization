package ch.hsr.ogv.model;

import java.util.List;

public class Class extends Type {

	private List<Attribute> attributes;
	private List<ClassRelationEndpoint> classRelationEndpoints;
	private List<Instance> instances;

	public Class(String name) {
		super(name);
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<ClassRelationEndpoint> getClassRelationEndpoints() {
		return classRelationEndpoints;
	}

	public void setClassRelationEndpoints(List<ClassRelationEndpoint> classRelationEndpoints) {
		this.classRelationEndpoints = classRelationEndpoints;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

	public void addClassRelationEndpoint(ClassRelationEndpoint classRelationEndpoint) {
		classRelationEndpoints.add(classRelationEndpoint);
	}

	public void addInstance(Instance instance) {
		instances.add(instance);
	}
}
