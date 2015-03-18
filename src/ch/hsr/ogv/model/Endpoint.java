package ch.hsr.ogv.model;

import javafx.geometry.Point3D;

public class Endpoint {

	private EndpointType type;
	private Point3D coordinates;
	private String roleName;
	private String multiplicity;

	private Relation relation;

	public Endpoint(EndpointType type, Point3D coordinates, String roleName, String multiplicity) {
		this.type = type;
		this.coordinates = coordinates;
		this.roleName = roleName;
		this.multiplicity = multiplicity;
		this.relation = null;
	}

	public EndpointType getType() {
		return type;
	}

	public void setType(EndpointType type) {
		this.type = type;
	}
	
	
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}

	public Point3D getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point3D coordinates) {
		this.coordinates = coordinates;
	}


	
	public Endpoint getFriend(){
		return relation.getFriend(this);
	}
}
