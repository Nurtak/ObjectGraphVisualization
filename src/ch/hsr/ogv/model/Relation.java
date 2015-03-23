package ch.hsr.ogv.model;

/**
 * 
 * @author Adrian Rieser
 *
 */
public abstract class Relation {

	private Endpoint first;
	private Endpoint second;
	private LineType type;

	public Relation(Endpoint first, Endpoint second, LineType type) {
		this.first = first;
		this.second = second;
		this.type = type;
	}

	public Endpoint getFirst() {
		return first;
	}

	public void setFirst(Endpoint first) {
		this.first = first;
	}

	public Endpoint getSecond() {
		return second;
	}

	public void setSecond(Endpoint second) {
		this.second = second;
	}

	public LineType getType() {
		return type;
	}

	public void setType(LineType type) {
		this.type = type;
	}

	public Endpoint getFriend(Endpoint other) {
		if (other.equals(first)) {
			return second;
		}
		return first;
	}

}
