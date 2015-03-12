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

}
