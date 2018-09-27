package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import ch.hsr.ogv.model.Endpoint;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.MultiplicityParser;
import ch.hsr.ogv.util.TextUtil;

/**
 * 
 * @author Simon Gwerder
 * @version OGV 3.1, May 2015
 *
 */
public class ObjectGraphCollector {

	private ModelObject modelObject; // the model object we gather all info for
	private ArrayList<Endpoint> classFriendEndpoints = new ArrayList<Endpoint>(); // helper list with all class relations friend endpoints
	private Map<Endpoint, String> allocates = new LinkedHashMap<Endpoint, String>(); // k: class endpoint, v: upper multiplicity bound
	private Map<Endpoint, String> referenceNames = new LinkedHashMap<Endpoint, String>(); // k: class endpoint, v: role name / mClass
	private Map<Endpoint, ArrayList<Relation>> classObjectRelations = new LinkedHashMap<Endpoint, ArrayList<Relation>>(); // k: class endpoint, v: list of associated object relations.
	private ArrayList<Relation> objectRelations = new ArrayList<Relation>(); // helper list of object relations (to keep order)
	private Map<Relation, ArrayList<ModelObject>> objectReferences = new LinkedHashMap<Relation, ArrayList<ModelObject>>(); // k: object relation, v: list of referenced objects.

	public ModelObject getModelObject() {
		return modelObject;
	}

	public ArrayList<Endpoint> getClassFriendEndpoints() {
		return classFriendEndpoints;
	}

	public Map<Endpoint, String> getAllocates() {
		return allocates;
	}

	public Map<Endpoint, String> getReferenceNames() {
		return referenceNames;
	}

	public ArrayList<ModelObject> getAssociatedObjects(Endpoint classEndpoint) {
		ArrayList<ModelObject> retList = new ArrayList<ModelObject>();
		ArrayList<Relation> objectRelations = this.classObjectRelations.get(classEndpoint);
		if (objectRelations == null) {
			return retList;
		}
		for (Relation objectRelation : objectRelations) {
			ArrayList<ModelObject> referencedObjects = this.objectReferences.get(objectRelation);
			if (referencedObjects == null) {
				continue;
			}
			retList.addAll(referencedObjects);
		}
		return retList;
	}

	public ObjectGraphCollector(ModelObject modelObject) {
		this.modelObject = modelObject;
		perpareHelpers();
		setAllocates();
		setReferenceNames();
		setObjectReferences();
		setClassObjectRelations();
	}

	private void perpareHelpers() {
		for (Endpoint endpoint : this.modelObject.getModelClass().getEndpoints()) {
			Endpoint friendEndpoint = endpoint.getFriend();
			Relation relation = endpoint.getRelation();
			if (!RelationType.DEPENDENCY.equals(relation.getRelationType()) && !RelationType.GENERALIZATION.equals(relation.getRelationType())) {
				if ((endpoint.getFriend().isEnd())
					|| (endpoint.getFriend().isStart() && RelationType.BIDIRECTED_ASSOCIATION.equals(relation.getRelationType()))) {
					this.classFriendEndpoints.add(friendEndpoint);
				}
			}
		}
		for (Endpoint endpoint : this.classFriendEndpoints) {
			this.classObjectRelations.put(endpoint, new ArrayList<Relation>());
		}
	}

	private void setAllocates() {
		for (Endpoint friendEndpoint : this.classFriendEndpoints) {
			String multiString = friendEndpoint.getMultiplicity();
			String upperBound = MultiplicityParser.getUppermostBound(multiString);
			if (upperBound != null && !upperBound.isEmpty()) {
				allocates.put(friendEndpoint, upperBound);
			}
		}
	}

	private void setReferenceNames() {
		for (Endpoint friendEndpoint : this.classFriendEndpoints) {
			String roleName = friendEndpoint.getRoleName();
			if (roleName != null && !roleName.isEmpty()) {
				this.referenceNames.put(friendEndpoint, roleName);
			}
			else {
				String className = friendEndpoint.getAppendant().getName();
				String referenceName = "m" + className;
				String nextReferenceName = "m" + "1" + className;
				if(this.referenceNames.values().contains(referenceName)) {
					Endpoint prevEndpoint = getFirstEndpoint(referenceName);
					this.referenceNames.remove(prevEndpoint); // replace old reference name
					this.referenceNames.put(prevEndpoint, nextReferenceName);
				}
				if(this.referenceNames.values().contains(nextReferenceName)) {
					referenceName = "m" + "1" + className;
					while (this.referenceNames.values().contains(referenceName)) {
						String partRefName = TextUtil.replaceLast(referenceName, className, "");
						referenceName = TextUtil.countUpTrailing(partRefName, 1);
						referenceName += className;
					}
				}
				this.referenceNames.put(friendEndpoint, referenceName);
			}
		}
	}
	
	private Endpoint getFirstEndpoint(String referenceName) {
		for (Entry<Endpoint, String> entry : this.referenceNames.entrySet()) {
			if (entry.getValue().equals(referenceName)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private void setClassObjectRelations() {
		for (Endpoint classEndpoint : this.classFriendEndpoints) {
			for (Relation objectRelation : this.objectRelations) {
				Relation classRelation = classEndpoint.getRelation();
				ModelClass friendClass = (ModelClass) classRelation.getEnd().getAppendant();
				ModelObject friendStartObject = (ModelObject) objectRelation.getStart().getAppendant();
				ModelObject friendEndObject = (ModelObject) objectRelation.getEnd().getAppendant();
				if (classRelation.getColor().equals(objectRelation.getColor()) && (friendClass.equals(friendStartObject.getModelClass()) || friendClass.equals(friendEndObject.getModelClass()))) {
					ArrayList<Relation> objectRelations = this.classObjectRelations.get(classEndpoint);
					objectRelations.add(objectRelation);
					this.classObjectRelations.put(classEndpoint, objectRelations);
				}
			}
		}
	}

	private void setObjectReferences() {
		for (Endpoint endpoint : this.modelObject.getEndpoints()) {
			ModelBox modelBox = endpoint.getFriend().getAppendant();
			if (modelBox instanceof ModelObject) {
				Relation relation = endpoint.getRelation();
				boolean isObjectReflexive = isObjectReflexive(relation);
				if((!relation.isReflexive() && !isObjectReflexive) || endpoint.isStart()) {
					ArrayList<ModelObject> temp = this.objectReferences.get(relation);
					if (temp == null) {
						temp = new ArrayList<ModelObject>();
					}
					temp.add((ModelObject) modelBox);
					this.objectRelations.add(relation);
					this.objectReferences.put(relation, temp);
				}
			}
		}
	}
	
	private boolean isObjectReflexive(Relation relation) {
		ModelBox startBox = relation.getStart().getAppendant();
		ModelBox endBox = relation.getEnd().getAppendant();
		if(!(startBox instanceof ModelObject) || !(endBox instanceof ModelObject)) {
			return false;
		}
		ModelObject startObject = (ModelObject) startBox;
		ModelObject endObject = (ModelObject) endBox;
		if(startObject.equals(endObject)) { // direct reflexive
			return false;
		}
		if(startObject.equals(this.modelObject) && endObject.getModelClass().getModelObjects().contains(startObject)) {
			return true;
		}
		if(endBox.equals(this.modelObject) && startObject.getModelClass().getModelObjects().contains(endObject)) {
			return true;
		}
		return false;
	}
}
