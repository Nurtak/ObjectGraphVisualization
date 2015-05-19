package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.HashMap;

import ch.hsr.ogv.model.Endpoint;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.MultiplicityParser;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class ObjectGraphWrapper {

	private ModelObject modelObject; // the model object we gather all info for
	private ArrayList<Relation> classRelations = new ArrayList<Relation>(); // helper list with all class relations
	private ArrayList<Endpoint> classFriendEndpoints = new ArrayList<Endpoint>(); // helper list with all class relations friend endpoints
	private HashMap<Relation, String> allocates = new HashMap<Relation, String>(); // k: class relation, v: upper multiplicity bound
	private HashMap<Relation, String> referenceNames = new HashMap<Relation, String>(); // k: class relation, v: role name / mClass
	private HashMap<Relation, ArrayList<ModelObject>> references = new HashMap<Relation, ArrayList<ModelObject>>(); // k: class relation, v: list of referenced objects.
	
	public ModelObject getModelObject() {
		return modelObject;
	}
	
	public ArrayList<Relation> getClassRelations() {
		return classRelations;
	}

	public HashMap<Relation, String> getAllocates() {
		return allocates;
	}

	public HashMap<Relation, String> getReferenceNames() {
		return referenceNames;
	}

	public HashMap<Relation, ArrayList<ModelObject>> getReferences() {
		return references;
	}

	public ObjectGraphWrapper(ModelObject modelObject) {
		this.modelObject = modelObject;
		setHelpers();
		setAllocates();
		setReferenceNames();
		setReferences();
	}

	private void setHelpers() {
		for (Endpoint endpoint : this.modelObject.getModelClass().getEndpoints()) {
			Endpoint friendEndpoint = endpoint.getFriend();
			Relation relation = endpoint.getRelation();
			if (!RelationType.DEPENDENCY.equals(relation.getRelationType())
				&& !RelationType.GENERALIZATION.equals(relation.getRelationType())
				&& (endpoint.getFriend().isEnd() || RelationType.BIDIRECTED_ASSOCIATION.equals(relation.getRelationType()))) {
				this.classRelations.add(relation);
				this.classFriendEndpoints.add(friendEndpoint);
			}
		}
		for(Relation relation : this.classRelations) {
			this.references.put(relation, new ArrayList<ModelObject>());
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
				this.referenceNames.put(friendEndpoint.getRelation(), "m" + friendEndpoint.getAppendant().getName());
			}
		}
	}
	
	private void setReferences() {
		HashMap<ModelClass, Endpoint> appendantClasses = new HashMap<ModelClass, Endpoint>();
		for (Endpoint friendEndpoint : this.classFriendEndpoints) {
			ModelBox modelBox = friendEndpoint.getAppendant();
			if(modelBox instanceof ModelClass) {
				ModelClass modelClass = (ModelClass) modelBox;
				appendantClasses.put(modelClass, friendEndpoint);
			}
		}
		for (ModelObject connectedObject : getConnectedObjects()) {
			Endpoint classFriendEndpoint = appendantClasses.get(connectedObject.getModelClass());
			if(classFriendEndpoint != null) {
				ArrayList<ModelObject> temp = this.references.get(classFriendEndpoint.getRelation());
				temp.add(connectedObject);
				this.references.put(classFriendEndpoint.getRelation(), temp);
			}
		}
	}
	
	private ArrayList<ModelObject> getConnectedObjects() {
		ArrayList<ModelObject> retList = new ArrayList<ModelObject>();
		for (Endpoint endpoint : this.modelObject.getEndpoints()) {
			ModelBox modelBox = endpoint.getFriend().getAppendant();
			if(modelBox instanceof ModelObject) {
				retList.add((ModelObject) modelBox);
			}
		}
		return retList;
	}
}
