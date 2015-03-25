package ch.hsr.ogv.model;

import java.util.Observable;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class Relation extends Observable {

	private Endpoint start;
	private Endpoint end;
	private RelationType type;

	public Relation(ModelClass startClass, ModelClass endClass, RelationType relationType) {
		this.start = new Endpoint(relationType.getStartType(), startClass);
		this.end = new Endpoint(relationType.getEndType(), endClass);
		this.type = relationType;
		this.start.setRelation(this);
		this.end.setRelation(this);
	}

	public Endpoint getStart() {
		return start;
	}

	public void setStart(Endpoint start) {
		this.start = start;
	}

	public Endpoint getEnd() {
		return end;
	}

	public void setEnd(Endpoint end) {
		this.end = end;
	}

	public RelationType getType() {
		return type;
	}

	public void setType(RelationType type) {
		this.type = type;
	}

	public Endpoint getFriend(Endpoint endpoint) {
		if (endpoint.equals(start)) {
			return end;
		}
		return start;
	}

	public boolean isStart(Endpoint endpoint) {
		if (start.equals(endpoint)) {
			return true;
		}
		return false;
	}
	
	public boolean isEnd(Endpoint endpoint) {
		if (end.equals(endpoint)) {
			return true;
		}
		return false;
	}
}
