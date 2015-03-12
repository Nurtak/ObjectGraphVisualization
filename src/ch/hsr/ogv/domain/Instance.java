package ch.hsr.ogv.domain;

import java.util.List;

public class Instance {

	private String name;
	private Class theClass;
	private List<AttributeValue> attributeValues;

	public Instance(String name, Class theClass) {
		this.name = name;
		this.theClass = theClass;
	}

}
