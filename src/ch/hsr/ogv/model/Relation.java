package ch.hsr.ogv.model;

import java.util.Observable;

import javafx.scene.paint.Color;

/**
 *
 * @author Adrian Rieser
 *
 */
public class Relation extends Observable {

	private Endpoint start;
	private Endpoint end;
	private RelationType type;

	protected Color color;

	public Relation(ModelBox startBox, ModelBox endBox, RelationType relationType, Color color) {
		this.start = new Endpoint(relationType.getStartType(), startBox);
		this.end = new Endpoint(relationType.getEndType(), endBox);
		this.type = relationType;
		this.start.setRelation(this);
		this.end.setRelation(this);
		this.color = color;
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

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		setChanged();
		notifyObservers(RelationChange.COLOR);
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

	public void changeDirection() {
		Endpoint tempEndpoint = this.end;
		this.end = this.start;
		this.start = tempEndpoint;
		setChanged();
		notifyObservers(RelationChange.DIRECTION);
	}

	public void setStartMultiplicity(String multiplicity) {
		this.start.setMultiplicity(multiplicity);
		setChanged();
		notifyObservers(RelationChange.MULTIPLCITY_ROLE);
	}

	public void setEndMultiplicity(String multiplicity) {
		this.end.setMultiplicity(multiplicity);
		setChanged();
		notifyObservers(RelationChange.MULTIPLCITY_ROLE);
	}

	public void setStartRoleName(String roleName) {
		this.start.setRoleName(roleName);
		setChanged();
		notifyObservers(RelationChange.MULTIPLCITY_ROLE);
	}

	public void setEndRoleName(String roleName) {
		this.end.setRoleName(roleName);
		setChanged();
		notifyObservers(RelationChange.MULTIPLCITY_ROLE);
	}

	public enum RelationChange {
		COLOR, DIRECTION, MULTIPLCITY_ROLE;
	}
}
