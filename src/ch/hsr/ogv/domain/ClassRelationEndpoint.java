package ch.hsr.ogv.domain;

public class ClassRelationEndpoint {

	private Class theClass;
	private String roleName;
	private char multiplicity;

	public ClassRelationEndpoint(Class theClass, String roleName, char multiplicity) {
		this.theClass = theClass;
		this.roleName = roleName;
		this.multiplicity = multiplicity;
	}
}
