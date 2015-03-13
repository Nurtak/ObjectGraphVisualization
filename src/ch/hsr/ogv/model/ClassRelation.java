package ch.hsr.ogv.model;

public abstract class ClassRelation {

	private ClassRelationEndpoint start;
	private ClassRelationEndpoint end;

	public ClassRelation(ClassRelationEndpoint start, ClassRelationEndpoint end) {
		this.start = start;
		this.end = end;
	}

	public ClassRelationEndpoint getStart() {
		return start;
	}

	public void setStart(ClassRelationEndpoint start) {
		this.start = start;
	}

	public ClassRelationEndpoint getEnd() {
		return end;
	}

	public void setEnd(ClassRelationEndpoint end) {
		this.end = end;
	}
}
