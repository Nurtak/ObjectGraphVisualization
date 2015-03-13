package ch.hsr.ogv.model;

public abstract class InstanceRelation {

	private InstanceRelationEndpoint start;
	private InstanceRelationEndpoint end;

	public InstanceRelation(InstanceRelationEndpoint start, InstanceRelationEndpoint end) {
		this.start = start;
		this.end = end;
	}

	public InstanceRelationEndpoint getStart() {
		return start;
	}

	public void setStart(InstanceRelationEndpoint start) {
		this.start = start;
	}

	public InstanceRelationEndpoint getEnd() {
		return end;
	}

	public void setEnd(InstanceRelationEndpoint end) {
		this.end = end;
	}

}
