package ch.hsr.ogv.domain;

public class AttributeValue {

	private Attribute attribute;
	private Instance instance;

	public AttributeValue(Attribute attribute, Instance instance) {
		this.attribute = attribute;
		this.instance = instance;
	}
}
