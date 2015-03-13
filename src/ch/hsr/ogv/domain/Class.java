package ch.hsr.ogv.domain;

import java.util.List;

public class Class extends Type {

	private List<Attribute> attributes;
	private List<Operation> operations;
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

	public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
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

	public void addOperation(Operation operation) {
		operations.add(operation);
	}

	public void addClassRelationEndpoint(ClassRelationEndpoint classRelationEndpoint) {
		classRelationEndpoints.add(classRelationEndpoint);
	}

	public void addInstance(Instance instance) {
		instances.add(instance);
	}
}
