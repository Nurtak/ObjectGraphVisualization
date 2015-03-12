package ch.hsr.ogv.domain;

public class Parameter extends Attribute {

	private Operation operation;
	
	public Parameter(String name, Class theClass, Type type, Operation operation){
		super(name, theClass, type);
		this.operation = operation;
	}
}
