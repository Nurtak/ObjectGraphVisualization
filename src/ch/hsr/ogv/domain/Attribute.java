package ch.hsr.ogv.domain;

public class Attribute {

	private String name;
	private Class theClass;
	private Type type;

	public Attribute(String name, Class theClass, Type type) {
		this.name = name;
		this.theClass = theClass;
		this.type = type;
	}
}
