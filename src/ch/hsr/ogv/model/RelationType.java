package ch.hsr.ogv.model;

/**
 * 
 * @author Adrian Rieser
 *
 */
public enum RelationType {
	
	GENERALIZATION(EndpointType.NONE, EndpointType.EMPTY_ARROW, LineType.CONTINUOUS_LINE),
	
	UNDIRECTED_ASSOZIATION(EndpointType.NONE, EndpointType.NONE, LineType.CONTINUOUS_LINE),
	DIRECTED_ASSOZIATION(EndpointType.NONE, EndpointType.OPEN_ARROW, LineType.CONTINUOUS_LINE),
	BIDIRECTED_ASSOZIATION(EndpointType.OPEN_ARROW, EndpointType.OPEN_ARROW, LineType.CONTINUOUS_LINE),
	
	UNDIRECTED_DEPENDENCY(EndpointType.NONE, EndpointType.NONE, LineType.DASHED_LINE),
	DIRECTED_DEPENDENCY(EndpointType.NONE, EndpointType.OPEN_ARROW, LineType.DASHED_LINE),
	BIDIRECTED_DEPENDENCY(EndpointType.OPEN_ARROW, EndpointType.OPEN_ARROW, LineType.DASHED_LINE),
	
	UNDIRECTED_AGGREGATION(EndpointType.EMPTY_DIAMOND, EndpointType.NONE, LineType.CONTINUOUS_LINE),
	DIRECTED_AGGREGATION(EndpointType.EMPTY_DIAMOND, EndpointType.OPEN_ARROW, LineType.CONTINUOUS_LINE),
	
	UNDIRECTED_COMPOSITION(EndpointType.FILLED_DIAMOND, EndpointType.NONE, LineType.CONTINUOUS_LINE),
	DIRECTED_COMPOSITION(EndpointType.FILLED_DIAMOND, EndpointType.OPEN_ARROW, LineType.CONTINUOUS_LINE),
	
	OBJDIAGRAM(EndpointType.NONE, EndpointType.NONE, LineType.CONTINUOUS_LINE),
	OBJGRAPH(EndpointType.NONE, EndpointType.FILLED_ARROW, LineType.CONTINUOUS_LINE);
	
    private final EndpointType startType;
	private final EndpointType endType;
	private final LineType lineType;
	
	private RelationType(EndpointType startType, EndpointType endType, LineType lineType) {
		this.startType = startType;
		this.endType = endType;
		this.lineType = lineType;
	}

	public EndpointType getStartType() {
		return startType;
	}
	
	public EndpointType getEndType() {
		return endType;
	}

	public LineType getLineType() {
		return lineType;
	}

}