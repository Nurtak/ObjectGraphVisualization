package ch.hsr.ogv.domain;

import java.util.List;

public class Operation {

	private String name;
	private Type returnType;
	private List<Parameter> parameters;

	public Operation(String name, Type returnType) {
		this.name = name;
		this.returnType = returnType;
	}

}
