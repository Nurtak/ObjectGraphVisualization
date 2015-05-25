package ch.hsr.ogv.model;

import java.util.Observable;

import javafx.scene.paint.Color;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.hsr.ogv.dataaccess.ColorAdapter;

/**
 *
 * @author Adrian Rieser
 *
 */
public class Relation extends Observable {

	private String name = "";
	private Endpoint start;
	private Endpoint end;
	private RelationType relationType = RelationType.UNDIRECTED_ASSOCIATION;
	private Color color = Color.BLACK;

	// for un/marshaling only
	public Relation() {
	}

	public Relation(ModelBox startBox, ModelBox endBox, RelationType relationType) {
		this(startBox, endBox, relationType, Color.BLACK);
	}

	public Relation(ModelBox startBox, ModelBox endBox, RelationType relationType, Color color) {
		this.start = new Endpoint(relationType.getStartType(), startBox);
		this.end = new Endpoint(relationType.getEndType(), endBox);
		this.relationType = relationType;
		this.start.setRelation(this);
		this.end.setRelation(this);
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		// setChanged();
		// notifyObservers(RelationChange.NAME);
		this.name = name;
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

	public RelationType getRelationType() {
		return relationType;
	}

	public void setRelationType(RelationType type) {
		this.relationType = type;
	}

	@XmlJavaTypeAdapter(ColorAdapter.class)
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
		if (start != null && start.equals(endpoint)) {
			return true;
		}
		return false;
	}

	public boolean isEnd(Endpoint endpoint) {
		if (end != null && end.equals(endpoint)) {
			return true;
		}
		return false;
	}
	
	public boolean isReflexive() {
		if (start != null && end != null && start.getAppendant() != null && start.getAppendant().equals(end.getAppendant())) {
			return true;
		}
		return false;
	}

	public void changeDirection() {
		if (this.start == null || this.end == null)
			return;
		this.start.getAppendant().replaceEndpoint(this.start, this.end);
		this.end.getAppendant().replaceEndpoint(this.end, this.start);
		ModelBox tempModelBox = this.end.getAppendant();
		this.end.setAppendant(this.start.getAppendant());
		this.start.setAppendant(tempModelBox);
		setChanged();
		notifyObservers(RelationChange.DIRECTION);
	}

	public void setStartMultiplicity(String multiplicity) {
		if (this.start == null)
			return;
		this.start.setMultiplicity(multiplicity);
		setChanged();
		notifyObservers(RelationChange.MULTIPLCITY_ROLE);
	}

	public void setEndMultiplicity(String multiplicity) {
		if (this.end == null)
			return;
		this.end.setMultiplicity(multiplicity);
		setChanged();
		notifyObservers(RelationChange.MULTIPLCITY_ROLE);
	}

	public void setStartRoleName(String roleName) {
		if (this.start == null)
			return;
		this.start.setRoleName(roleName);
		setChanged();
		notifyObservers(RelationChange.MULTIPLCITY_ROLE);
	}

	public void setEndRoleName(String roleName) {
		if (this.end == null)
			return;
		this.end.setRoleName(roleName);
		setChanged();
		notifyObservers(RelationChange.MULTIPLCITY_ROLE);
	}

	public enum RelationChange {
		COLOR, DIRECTION, MULTIPLCITY_ROLE;
	}
}
