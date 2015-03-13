package ch.hsr.ogv.model;

public class Parameter {

	private String name;
	private Type returnType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	public Parameter(String name, Type returnType) {
		this.name = name;
		this.returnType = returnType;
	}
}
