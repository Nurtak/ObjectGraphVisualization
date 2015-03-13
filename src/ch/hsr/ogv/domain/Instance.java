package ch.hsr.ogv.domain;

import java.util.List;

public class Instance {

	private String name;
	private List<AttributeValue> attributeValues;
	private List<InstanceRelationEndpoint> instanceRelationEndpoint;

	public Instance(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AttributeValue> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(List<AttributeValue> attributeValues) {
		this.attributeValues = attributeValues;
	}

	public List<InstanceRelationEndpoint> getInstanceRelationEndpoint() {
		return instanceRelationEndpoint;
	}

	public void setInstanceRelationEndpoint(List<InstanceRelationEndpoint> instanceRelationEndpoint) {
		this.instanceRelationEndpoint = instanceRelationEndpoint;
	}

}
