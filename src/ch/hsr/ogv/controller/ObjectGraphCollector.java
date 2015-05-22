package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
 *
 */
public class ObjectGraphCollector {

	private ModelObject modelObject; // the model object we gather all info for
	private ArrayList<Relation> classRelations = new ArrayList<Relation>(); // helper list with all class relations
	private ArrayList<Endpoint> classFriendEndpoints = new ArrayList<Endpoint>(); // helper list with all class relations friend endpoints
	private HashMap<Relation, String> allocates = new HashMap<Relation, String>(); // k: class relation, v: upper multiplicity bound
	private HashMap<Relation, String> referenceNames = new HashMap<Relation, String>(); // k: class relation, v: role name / mClass
	private HashMap<Relation, ArrayList<Relation>> classObjectRelations = new HashMap<Relation, ArrayList<Relation>>(); // k: class relation, v: list of associated object relations.
	private HashMap<Relation, ArrayList<ModelObject>> objectReferences = new HashMap<Relation, ArrayList<ModelObject>>(); // k: object relation, v: list of referenced objects.

	public ModelObject getModelObject() {
		return modelObject;
	}

	public ArrayList<Relation> getClassRelations() {
		return classRelations;
	}

	public ArrayList<Endpoint> getClassFriendEndpoints() {
		return classFriendEndpoints;
	}

	public HashMap<Relation, String> getAllocates() {
		return allocates;
	}

	public HashMap<Relation, String> getReferenceNames() {
		return referenceNames;
	}

	public ArrayList<ModelObject> getAssociatedObjects(Relation classRelation) {
		ArrayList<ModelObject> retList = new ArrayList<ModelObject>();
		ArrayList<Relation> objectRelations = this.classObjectRelations.get(classRelation);
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
				if ((endpoint.getFriend().isEnd())) {
					this.classRelations.add(relation);
					this.classFriendEndpoints.add(friendEndpoint);
				}
				else if ((endpoint.getFriend().isStart() && RelationType.BIDIRECTED_ASSOCIATION.equals(relation.getRelationType()))) {
					this.classRelations.add(relation);
					this.classFriendEndpoints.add(friendEndpoint);
				}
			}
		}
		for (Relation relation : this.classRelations) {
			this.classObjectRelations.put(relation, new ArrayList<Relation>());
		}
	}

	private void setAllocates() {
		for (Endpoint friendEndpoint : this.classFriendEndpoints) {
			String multiString = friendEndpoint.getMultiplicity();
			String upperBound = MultiplicityParser.getUppermostBound(multiString);
			if (upperBound != null && !upperBound.isEmpty()) {
				allocates.put(friendEndpoint.getRelation(), upperBound);
			}
		}
	}

	private void setReferenceNames() {
		for (Endpoint friendEndpoint : this.classFriendEndpoints) {
			String roleName = friendEndpoint.getRoleName();
			if (roleName != null && !roleName.isEmpty()) {
				this.referenceNames.put(friendEndpoint.getRelation(), roleName);
			}
			else {
				String className = friendEndpoint.getAppendant().getName();
				String referenceName = "m" + className;
				String nextReferenceName = "m" + "1" + className;
				if(this.referenceNames.values().contains(referenceName)) {
					Relation prevRelation = getFirstRelation(referenceName);
					this.referenceNames.remove(prevRelation); // replace old reference name
					this.referenceNames.put(prevRelation, nextReferenceName);
				}
				if(this.referenceNames.values().contains(nextReferenceName)) {
					referenceName = "m" + "1" + className;
					while (this.referenceNames.values().contains(referenceName)) {
						String partRefName = TextUtil.replaceLast(referenceName, className, "");
						referenceName = TextUtil.countUpTrailing(partRefName, 1);
						referenceName += className;
					}
				}
				this.referenceNames.put(friendEndpoint.getRelation(), referenceName);
			}
		}
	}
	
	private Relation getFirstRelation(String referenceName) {
		for (Entry<Relation, String> entry : this.referenceNames.entrySet()) {
			if (entry.getValue().equals(referenceName)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private void setClassObjectRelations() {
		ArrayList<Relation> allObjectRelations = new ArrayList<Relation>();
		for (Endpoint endpoint : this.modelObject.getEndpoints()) {
			allObjectRelations.add(endpoint.getRelation());
		}
		for (Relation classRelation : this.classRelations) {
			for (Relation objectRelation : allObjectRelations) {
				ModelClass friendClass = (ModelClass) classRelation.getEnd().getAppendant();
				ModelObject friendStartObject = (ModelObject) objectRelation.getStart().getAppendant();
				ModelObject friendEndObject = (ModelObject) objectRelation.getEnd().getAppendant();
				if (classRelation.getColor().equals(objectRelation.getColor()) && (friendClass.equals(friendStartObject.getModelClass()) || friendClass.equals(friendEndObject.getModelClass()))) {
					ArrayList<Relation> objectRelations = this.classObjectRelations.get(classRelation);
					objectRelations.add(objectRelation);
					this.classObjectRelations.put(classRelation, objectRelations);
				}
			}
		}
	}

	private void setObjectReferences() {
		for (Endpoint endpoint : this.modelObject.getEndpoints()) {
			ModelBox modelBox = endpoint.getFriend().getAppendant();
			if (modelBox instanceof ModelObject) {
				ArrayList<ModelObject> temp = this.objectReferences.get(endpoint.getRelation());
				if (temp == null) {
					temp = new ArrayList<ModelObject>();
				}
				temp.add((ModelObject) modelBox);
				this.objectReferences.put(endpoint.getRelation(), temp);
			}
		}
	}
}
