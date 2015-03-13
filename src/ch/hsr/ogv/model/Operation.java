package ch.hsr.ogv.model;

import java.util.List;

public class Operation {

	private String name;
	private Type returnType;
	private List<Parameter> parameters;

	public Operation(String name, Type returnType) {
		this.name = name;
		this.returnType = returnType;
	}

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

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}
}
