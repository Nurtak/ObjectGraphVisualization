package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;
import jfxtras.labs.util.Util;
import ch.hsr.ogv.model.Attribute;
import ch.hsr.ogv.model.Endpoint;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.MultiplicityParser;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.ConnectorBox;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class ObjectGraph {

	private final static double ARRAYBOX_LEVEL_DIFF = 100.0;

	private List<PaneBox> boxes = new ArrayList<PaneBox>(); // contains normal and arrayBoxes
	private List<Arrow> arrows = new ArrayList<Arrow>();
	private List<ConnectorBox> connectorBoxes = new ArrayList<ConnectorBox>();

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

	private void add(PaneBox paneBox) {
		this.boxes.add(paneBox);
		this.subSceneAdapter.add(paneBox.get());
		paneBox.get().applyCss();
	}

	private void add(Arrow arrow) {
		this.arrows.add(arrow);
		this.subSceneAdapter.add(arrow);
		arrow.applyCss();
	}

	private void add(ConnectorBox connector) {
		this.connectorBoxes.add(connector);
		this.subSceneAdapter.add(connector);
		connector.applyCss();
	}

	private void remove(PaneBox paneBox) {
		this.subSceneAdapter.remove(paneBox.get());
		this.boxes.remove(paneBox);
	}

	private void remove(Arrow arrow) {
		this.subSceneAdapter.remove(arrow);
		this.arrows.remove(arrow);
	}

	private void remove(ConnectorBox connector) {
		this.subSceneAdapter.remove(connector);
		this.connectorBoxes.remove(connector);
	}

	public void setup() {
		for (ModelBox modelBox : this.mvConnector.getBoxes().keySet()) {
			if (modelBox instanceof ModelClass) {
				ModelClass modelClass = (ModelClass) modelBox;
				for (ModelObject modelObject : modelClass.getModelObjects()) {
					createBox(modelObject);
					for (ModelObject superModelObject : modelObject.getSuperObjects()) {
						createBox(superModelObject);
					}
				}
			}
		}
	}

	private int buildReferenceNames(PaneBox paneBox, ObjectGraphWrapper ogWrapper) {
		int origSize = paneBox.getCenterLabels().size();
		for (int i = 0; i < ogWrapper.getClassFriendEndpoints().size(); i++) {
			int centerLabelIndex = origSize + i;
			Endpoint friendEndpoint = ogWrapper.getClassFriendEndpoints().get(i);
			Relation relation = friendEndpoint.getRelation();
			String roleName = ogWrapper.getReferenceNames().get(relation);
			paneBox.setCenterText(centerLabelIndex, roleName + " " + MultiplicityParser.ASTERISK, "");
		}
		return origSize;
	}

	private void buildReferences(PaneBox paneBox, int origSize, ObjectGraphWrapper ogWrapper) {
		for (int i = 0; i < ogWrapper.getClassFriendEndpoints().size(); i++) {
			int centerLabelIndex = origSize + i;
			Endpoint friendEndpoint = ogWrapper.getClassFriendEndpoints().get(i);
			Relation relation = friendEndpoint.getRelation();
			ArrayList<ModelObject> modelObjects = ogWrapper.getAssociatedObjects(relation);
			String upperBoundStr = ogWrapper.getAllocates().get(relation);
			if (!modelObjects.isEmpty() && upperBoundStr != null && !upperBoundStr.isEmpty() && upperBoundStr.equals("1")) { // direct reference
				ModelObject firstRefObject = modelObjects.get(0);
				PaneBox firstRefBox = this.mvConnector.getPaneBox(firstRefObject);
				if (firstRefBox != null) {
					createBoxArrow(paneBox, firstRefBox, centerLabelIndex, relation);
				}
			}
			else if (!modelObjects.isEmpty()) { // create array object in between
				if (upperBoundStr == null) {
					upperBoundStr = MultiplicityParser.ASTERISK;
				}
				PaneBox arrayBox = createArrayBox(ogWrapper.getModelObject(), (ModelClass) friendEndpoint.getAppendant(), relation, upperBoundStr);
				createBoxArrow(paneBox, arrayBox, centerLabelIndex, relation);
				createArrayBoxAttributes(arrayBox, relation, ogWrapper);
				createArrayBoxArrows(arrayBox, relation, ogWrapper);
			}
		}
	}

	private PaneBox createBox(ModelObject modelObject) {
		PaneBox paneBox = new PaneBox();
		paneBox.setDepth(PaneBox.OBJECTBOX_DEPTH);
		paneBox.setTopText(modelObject.getName() + " : " + modelObject.getModelClass().getName());
		paneBox.setTopUnderline(true);
		paneBox.setColor(modelObject.getColor());
		paneBox.setTranslateXYZ(modelObject.getCoordinates());
		paneBox.setWidth(modelObject.getWidth());
		paneBox.setHeight(modelObject.getHeight());
		createBoxAttributes(paneBox, modelObject);
		if (!modelObject.isSuperObject()) {
			ObjectGraphWrapper ogWrapper = new ObjectGraphWrapper(modelObject);
			int origSize = buildReferenceNames(paneBox, ogWrapper);
			buildReferences(paneBox, origSize, ogWrapper);
		}
		add(paneBox);
		return paneBox;
	}

	private void createBoxAttributes(PaneBox paneBox, ModelObject modelObject) {
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

	private Arrow createBoxArrow(PaneBox startBox, PaneBox endBox, int centerLabelIndex, Relation relation) {
		Point3D labelPosition = startBox.getCenterLabelEndPos(centerLabelIndex);
		System.out.println("Z: " + startBox.getTranslateZ() + ", Index: " + centerLabelIndex + ", pos z: " + labelPosition.getZ() + ", calc min height: " + startBox.calcMinHeight() + ", height: "
				+ startBox.getHeight());
		Point3D refArrowStartpoint = new Point3D(labelPosition.getX() + PaneBox.HORIZONTAL_BORDER_GAP, labelPosition.getY() + startBox.getDepth() - 1, labelPosition.getZ());
		Arrow refArrow = new Arrow(refArrowStartpoint, endBox, RelationType.OBJGRAPH);
		refArrow.setColor(relation.getColor());
		add(refArrow);
		add(new ConnectorBox(startBox, labelPosition, refArrow));
		return refArrow;
	}

	private PaneBox createArrayBox(ModelObject modelObject, ModelClass modelClass, Relation relation, String upperBoundStr) {
		PaneBox paneBox = new PaneBox();
		paneBox.setDepth(PaneBox.OBJECTBOX_DEPTH);
		paneBox.setTopText(" : " + modelClass.getName() + "[" + upperBoundStr + "]");
		paneBox.setTopUnderline(true);
		paneBox.setIndexCenterGrid(0);
		paneBox.setColor(Util.brighter(modelClass.getColor(), 0.1));
		Point3D newPosition = modelObject.getCoordinates().midpoint(modelClass.getCoordinates());
		// Point3D newPosition = modelObject.getCoordinates().midpoint(newPosition);
		newPosition = new Point3D(newPosition.getX(), modelObject.getY(), newPosition.getZ());
		while (hasArrayBoxAtPos(newPosition)) {
			newPosition = new Point3D(newPosition.getX(), newPosition.getY() + ARRAYBOX_LEVEL_DIFF, newPosition.getZ());
		}
		paneBox.setTranslateXYZ(newPosition);
		paneBox.setWidth(PaneBox.MIN_WIDTH);
		paneBox.setMinHeight(PaneBox.MIN_HEIGHT);
		add(paneBox);
		return paneBox;
	}

	private void createArrayBoxAttributes(PaneBox arrayBox, Relation relation, ObjectGraphWrapper ogWrapper) {
		ArrayList<ModelObject> modelObjects = ogWrapper.getAssociatedObjects(relation);
		Integer upperBound = MultiplicityParser.toInteger(ogWrapper.getAllocates().get(relation));
		if (upperBound == null) {
			upperBound = modelObjects.size();
		}
		for (int i = 0; i < upperBound; i++) {
			String arrayIndexRef = "[" + i + "] " + MultiplicityParser.ASTERISK;
			arrayBox.setCenterText(i, arrayIndexRef, arrayIndexRef);
		}
	}

	private void createArrayBoxArrows(PaneBox arrayBox, Relation relation, ObjectGraphWrapper ogWrapper) {
		ArrayList<ModelObject> modelObjects = ogWrapper.getAssociatedObjects(relation);
		for (int i = 0; i < arrayBox.getCenterLabels().size(); i++) {
			if (i < modelObjects.size()) {
				ModelObject modelObject = modelObjects.get(i);
				PaneBox refBox = this.mvConnector.getPaneBox(modelObject);
				if (refBox != null) {
					createBoxArrow(arrayBox, refBox, i, relation);
				}
			}
		}
	}

	private boolean hasArrayBoxAtPos(Point3D coords) {
		for (PaneBox paneBox : this.boxes) {
			boolean approxEqualX = Math.floor(paneBox.getCenterPoint().getX()) == Math.floor(coords.getX());
			boolean approxEqualY = Math.floor(paneBox.getCenterPoint().getY() + paneBox.getDepth() / 2) == Math.floor(coords.getY());
			boolean approxEqualZ = Math.floor(paneBox.getCenterPoint().getZ()) == Math.floor(coords.getZ());
			if (approxEqualX && approxEqualY && approxEqualZ) {
				return true;
			}
		}
		return false;
	}

	public void tearDown() {
		for (PaneBox paneBox : new ArrayList<PaneBox>(this.boxes)) {
			remove(paneBox);
		}
		for (Arrow arrow : new ArrayList<Arrow>(this.arrows)) {
			remove(arrow);
		}
		for (ConnectorBox connectorBox : new ArrayList<ConnectorBox>(this.connectorBoxes)) {
			remove(connectorBox);
		}

		this.boxes.clear();
		this.arrows.clear();
	}
}
