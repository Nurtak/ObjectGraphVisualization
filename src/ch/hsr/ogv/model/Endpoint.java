package ch.hsr.ogv.model;

import javafx.geometry.Point3D;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class Endpoint {

	private EndpointType type;
	private Point3D coordinates;
	private String roleName;
	private String multiplicity;
	private Relation relation;
	private ModelBox appendant;

	public Endpoint(EndpointType type, ModelBox appendant) {
		this.type = type;
		this.appendant = appendant;
		this.coordinates = appendant.getCoordinates();
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

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	
	public ModelBox getAppendant() {
		return appendant;
	}

	public void setAppendant(ModelBox appendant) {
		this.appendant = appendant;
	}

	public Endpoint getFriend() {
		return relation.getFriend(this);
	}
	
}
