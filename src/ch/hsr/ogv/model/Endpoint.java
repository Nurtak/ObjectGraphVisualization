package ch.hsr.ogv.model;

import java.util.UUID;

import javafx.geometry.Point3D;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.hsr.ogv.dataaccess.Point3DAdapter;

/**
 *
 * @author Adrian Rieser
 *
 */
public class Endpoint {

	// for un/marshaling only
	private String uniqueID = UUID.randomUUID().toString();

	private EndpointType type;
	private Point3D coordinates;
	private String roleName;
	private String multiplicity;
	private Relation relation;
	private ModelBox appendant;
	
	// for un/marshaling only
	public Endpoint() {
	}

	public Endpoint(EndpointType type, ModelBox appendant) {
		this.type = type;
		this.appendant = appendant;
		this.coordinates = appendant.getCoordinates();
	}
	
	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
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

	@XmlJavaTypeAdapter(Point3DAdapter.class)
	public Point3D getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Point3D coordinates) {
		this.coordinates = coordinates;
	}

	@XmlTransient
	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	@XmlTransient
	public ModelBox getAppendant() {
		return appendant;
	}

	public void setAppendant(ModelBox appendant) {
		this.appendant = appendant;
	}

	@XmlTransient
	public Endpoint getFriend() {
		if (relation != null) {
			return relation.getFriend(this);
		}
		return null;
	}

	public boolean isStart(){
		if (relation != null) {
			return relation.isStart(this);
		}
		return false;
	}

}
