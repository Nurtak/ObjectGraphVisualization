package ch.hsr.ogv.domain;

public class ClassRelation {

	private ClassRelationEndpoint start;
	private ClassRelationEndpoint end;

	public ClassRelation(ClassRelationEndpoint start, ClassRelationEndpoint end) {
		this.start = start;
		this.end = end;
	}
}
