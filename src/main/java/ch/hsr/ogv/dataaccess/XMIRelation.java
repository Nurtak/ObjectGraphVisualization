package ch.hsr.ogv.dataaccess;

import ch.hsr.ogv.model.RelationType;

public class XMIRelation {

    private String name = "";
    private RelationType type = RelationType.UNDIRECTED_ASSOCIATION;
    private String sourceID;
    private String targetID;
    private String sourceRoleName;
    private String targetRoleName;
    private String sourceMultiplicity;
    private String targetMultiplicity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RelationType getType() {
        return type;
    }

    public void setType(RelationType type) {
        this.type = type;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public String getSourceRoleName() {
        return sourceRoleName;
    }

    public void setSourceRoleName(String sourceRoleName) {
        this.sourceRoleName = sourceRoleName;
    }

    public String getTargetRoleName() {
        return targetRoleName;
    }

    public void setTargetRoleName(String targetRoleName) {
        this.targetRoleName = targetRoleName;
    }

    public String getSourceMultiplicity() {
        return sourceMultiplicity;
    }

    public void setSourceMultiplicity(String sourceMultiplicity) {
        this.sourceMultiplicity = sourceMultiplicity;
    }

    public String getTargetMultiplicity() {
        return targetMultiplicity;
    }

    public void setTargetMultiplicity(String targetMultiplicity) {
        this.targetMultiplicity = targetMultiplicity;
    }

}
