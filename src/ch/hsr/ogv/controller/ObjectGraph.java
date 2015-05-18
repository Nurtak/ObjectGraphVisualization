package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.ogv.model.Attribute;
import ch.hsr.ogv.model.Endpoint;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.MultiplicityParser;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class ObjectGraph {

	private List<PaneBox> boxes = new ArrayList<PaneBox>();
	private List<Arrow> arrows = new ArrayList<Arrow>();

	private ModelViewConnector mvConnector;
	private SubSceneAdapter subSceneAdapter;

	public List<PaneBox> getBoxes() {
		return boxes;
	}

	public List<Arrow> getArrows() {
		return arrows;
	}

	public ObjectGraph(ModelViewConnector mvConnector, SubSceneAdapter subSceneAdapter) {
		this.mvConnector = mvConnector;
		this.subSceneAdapter = subSceneAdapter;
	}

	private void addGraphBox(PaneBox paneBox) {
		this.boxes.add(paneBox);
		this.subSceneAdapter.add(paneBox.get());
		paneBox.get().applyCss();
	}

	private void addGraphArrow(Arrow arrow) {
		this.arrows.add(arrow);
		this.subSceneAdapter.add(arrow);
		arrow.applyCss();
	}

	private void removeGraphBox(PaneBox paneBox) {
		this.subSceneAdapter.remove(paneBox.get());
		this.boxes.remove(paneBox);
	}

	private void removeGraphArrow(Arrow arrow) {
		this.subSceneAdapter.remove(arrow);
		this.arrows.remove(arrow);
	}

	public void setup() {
		for (ModelBox modelBox : this.mvConnector.getBoxes().keySet()) {
			if (modelBox instanceof ModelClass) {
				ModelClass modelClass = (ModelClass) modelBox;
				for (ModelObject modelObject : modelClass.getModelObjects()) {
					buildGraphObject(modelObject);
				}
			}
		}
	}

	private void buildGraphObject(ModelObject modelObject) {
		PaneBox paneBox = new PaneBox();
		paneBox.setDepth(PaneBox.OBJECTBOX_DEPTH);
		paneBox.setTopText(modelObject.getName() + " : " + modelObject.getModelClass().getName());
		paneBox.setTopUnderline(true);
		paneBox.setColor(modelObject.getColor());
		paneBox.setTranslateXYZ(modelObject.getCoordinates());
		paneBox.setWidth(modelObject.getWidth());
		paneBox.setMinHeight(modelObject.getHeight());
		
		buildGraphAttributes(paneBox, modelObject);
		
		ObjectGraphWrapper ogWrapper = new ObjectGraphWrapper(modelObject);
		buildGraphReferences(paneBox, ogWrapper);

		addGraphBox(paneBox);
	}

	private void buildGraphAttributes(PaneBox paneBox, ModelObject modelObject) {
		ModelClass modelClass = modelObject.getModelClass();
		paneBox.setIndexCenterGrid(modelClass.getAttributes().size());
		for (int i = 0; i < modelClass.getAttributes().size(); i++) {
			if (i < PaneBox.MAX_CENTER_LABELS) {
				Attribute attribute = modelClass.getAttributes().get(i);
				String attributeName = attribute.getName();
				String attributeValue = modelObject.getAttributeValues().get(attribute);
				if (attributeValue != null && !attributeValue.isEmpty()) {
					paneBox.setCenterText(i, attributeName + " = " + attributeValue, attributeValue);
				}
				else {
					paneBox.setCenterText(i, attributeName, attributeValue);
				}
			}
		}
	}

	private void buildGraphReferences(PaneBox paneBox, ObjectGraphWrapper ogWrapper) {
		int origSize = paneBox.getCenterLabels().size();
		for (int i = 0; i < ogWrapper.getClassRelations().size(); i++) {
			Relation relation = ogWrapper.getClassRelations().get(i);
			String roleName = ogWrapper.getReferenceNames().get(relation);
			paneBox.setCenterText(origSize + i, roleName + " " + MultiplicityParser.ASTERISK, "");
			
			String upperBound = ogWrapper.getAllocates().get(relation);
			if(upperBound != null && !upperBound.isEmpty() && upperBound.equals("1")) { // direct reference
				ArrayList<ModelObject> modelObjects = ogWrapper.getReferences().get(relation);
				if (!modelObjects.isEmpty()) {
					ModelObject firstRefObject = modelObjects.get(0);
					PaneBox firstRefBox = this.mvConnector.getPaneBox(firstRefObject);
					if(firstRefBox != null) {
						Arrow refArrow = new Arrow(paneBox, firstRefBox, RelationType.OBJGRAPH);
						addGraphArrow(refArrow);
					}
				}
			}
			else { // create array object in between
				
			}
		}
		paneBox.recalcHasCenterGrid();
		paneBox.setHeight(paneBox.calcMinHeight());
	}

	public void buildGraphRelation(Relation relation) {

	}

	public void tearDown() {
		for (PaneBox paneBox : new ArrayList<PaneBox>(this.boxes)) {
			removeGraphBox(paneBox);
		}
		for (Arrow arrow : new ArrayList<Arrow>(this.arrows)) {
			removeGraphArrow(arrow);
		}
		this.boxes.clear();
		this.arrows.clear();
	}
}
