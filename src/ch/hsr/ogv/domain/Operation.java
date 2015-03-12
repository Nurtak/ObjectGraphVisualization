package ch.hsr.ogv.domain;

public class Operation {

	private String name;
	private Class theClass;
	private Type returnType;

	public Operation(String name, Class theClass, Type returnType) {
		this.name = name;
		this.theClass = theClass;
		this.returnType = returnType;
	}

}
