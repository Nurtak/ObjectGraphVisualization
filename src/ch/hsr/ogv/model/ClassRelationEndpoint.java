package ch.hsr.ogv.model;

public class ClassRelationEndpoint {

	private String roleName;
	private char multiplicity;

	public ClassRelationEndpoint(String roleName, char multiplicity) {
		this.roleName = roleName;
		this.multiplicity = multiplicity;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public char getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(char multiplicity) {
		this.multiplicity = multiplicity;
	}
}
