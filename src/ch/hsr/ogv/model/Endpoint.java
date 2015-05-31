package ch.hsr.ogv.model;

import java.util.UUID;

import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Adrian Rieser
 * @version OGV 3.1, May 2015
 *
 */
public class Endpoint {

	// for un/marshaling only
	private String uniqueID = UUID.randomUUID().toString();

	private EndpointType endpointType;
	private String roleName;
	private String multiplicity;
	private Relation relation;
	private ModelBox appendant;

	// for un/marshaling only
	public Endpoint() {
	}

	public Endpoint(EndpointType type, ModelBox appendant) {
		this.endpointType = type;
		this.appendant = appendant;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public EndpointType getEndpointType() {
		return endpointType;
	}

	public void setEndpointType(EndpointType endpointType) {
		this.endpointType = endpointType;
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

	public boolean isStart() {
		if (relation != null) {
			return relation.isStart(this);
		}
		return false;
	}

	public boolean isEnd() {
		if (relation != null) {
			return relation.isEnd(this);
		}
		return false;
	}

}
