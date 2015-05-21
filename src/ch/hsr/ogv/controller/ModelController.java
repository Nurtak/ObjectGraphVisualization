package ch.hsr.ogv.controller;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import jfxtras.labs.util.Util;
import ch.hsr.ogv.model.Attribute;
import ch.hsr.ogv.model.Endpoint;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.ModelBox.ModelBoxChange;
import ch.hsr.ogv.model.Relation.RelationChange;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

public class ModelController implements Observer {

	private BorderPane rootLayout;
	private SubSceneAdapter subSceneAdapter;
	
	private ModelViewConnector mvConnector;
	
	private SelectionController selectionController;
	private ContextMenuController contextMenuController;
	private TextFieldController textFieldController;
	private MouseMoveController mouseMoveController;
	private DragMoveController dragMoveController;
	private DragResizeController dragResizeController;
	
	public void setRootLayout(BorderPane rootLayout) {
		this.rootLayout = rootLayout;
	}

	public void setSubSceneAdapter(SubSceneAdapter subSceneAdapter) {
		this.subSceneAdapter = subSceneAdapter;
	}

	public void setMVConnector(ModelViewConnector mvConnector) {
		this.mvConnector = mvConnector;
		this.mvConnector.getModelManager().addObserver(this); // observing ModelManager immediately after setting it
	}

	public void setSelectionController(SelectionController selectionController) {
		this.selectionController = selectionController;
	}

	public void setContextMenuController(ContextMenuController contextMenuController) {
		this.contextMenuController = contextMenuController;
	}

	public void setTextFieldController(TextFieldController textFieldController) {
		this.textFieldController = textFieldController;
	}

	public void setMouseMoveController(MouseMoveController mouseMoveController) {
		this.mouseMoveController = mouseMoveController;
	}

	public void setDragMoveController(DragMoveController dragMoveController) {
		this.dragMoveController = dragMoveController;
	}

	public void setDragResizeController(DragResizeController dragResizeController) {
		this.dragResizeController = dragResizeController;
	}

	/**
	 * Adds node to the subscene of the primary stage.
	 *
	 * @param node
	 *            node.
	 */
	private void addToSubScene(Node node) {
		this.subSceneAdapter.add(node);
		this.rootLayout.applyCss();
	}

	/**
	 * Removes node from the subscene of the primary stage.
	 *
	 * @param node
	 *            node.
	 */
	private void removeFromView(Node node) {
		this.subSceneAdapter.remove(node);
		this.rootLayout.applyCss();
	}

	private void showModelClassInView(ModelClass modelClass) {
		modelClass.addObserver(this);
		PaneBox paneBox = new PaneBox();
		paneBox.setDepth(PaneBox.CLASSBOX_DEPTH);
		paneBox.setColor(modelClass.getColor());
		paneBox.setTopUnderline(false);
		paneBox.setAllCenterGrid(false);
		addPaneBoxControls(modelClass, paneBox);
		addToSubScene(paneBox.get());
		addToSubScene(paneBox.getSelection());
		this.mvConnector.putBoxes(modelClass, paneBox);
	}

	private void showModelObjectInView(ModelObject modelObject) {
		modelObject.addObserver(this);
		PaneBox paneBox = new PaneBox();
		paneBox.setDepth(PaneBox.OBJECTBOX_DEPTH);
		paneBox.setColor(modelObject.getColor());
		paneBox.setTopUnderline(true);
		paneBox.setAllCenterGrid(true);
		addPaneBoxControls(modelObject, paneBox);
		addToSubScene(paneBox.get());
		addToSubScene(paneBox.getSelection());
		this.mvConnector.putBoxes(modelObject, paneBox);
	}

	private void showArrowInView(Relation relation) {
		relation.addObserver(this);
		ModelBox startModelBox = relation.getStart().getAppendant();
		ModelBox endModelBox = relation.getEnd().getAppendant();
		PaneBox startViewBox = this.mvConnector.getPaneBox(startModelBox);
		PaneBox endViewBox = this.mvConnector.getPaneBox(endModelBox);
		if (startViewBox != null && endViewBox != null) {
			Arrow arrow = new Arrow(startViewBox, endViewBox, relation.getRelationType());
			addArrowControls(arrow, relation);
			addToSubScene(arrow);
			addToSubScene(arrow.getSelection());

			this.mvConnector.putArrows(relation, arrow);
			this.contextMenuController.enableContextMenu(arrow, relation);

			this.mvConnector.arrangeArrowNumbers(startModelBox, endModelBox);
		}
	}

	private void addPaneBoxControls(ModelBox modelBox, PaneBox paneBox) {
		if (modelBox instanceof ModelClass) {
			this.selectionController.enablePaneBoxSelection(paneBox, this.subSceneAdapter, true);
			this.dragMoveController.enableDragMove(modelBox, paneBox, this.subSceneAdapter);
			this.dragResizeController.enableDragResize(modelBox, paneBox, this.subSceneAdapter);
		}
		else if (modelBox instanceof ModelObject) {
			ModelObject modelObject = (ModelObject) modelBox;
			if (!modelObject.isSuperObject()) {
				this.selectionController.enablePaneBoxSelection(paneBox, this.subSceneAdapter, true);
				this.dragMoveController.enableDragMove(modelBox, paneBox, this.subSceneAdapter);
			}
			else {
				this.selectionController.enablePaneBoxSelection(paneBox, this.subSceneAdapter, false);
			}
		}
		this.selectionController.enableCenterLabelSelection(paneBox, subSceneAdapter);
		this.textFieldController.enableTopTextInput(modelBox, paneBox, this.mvConnector);
		this.textFieldController.enableCenterTextInput(modelBox, paneBox, this.mvConnector);
		this.contextMenuController.enablePaneBoxContextMenu(modelBox, paneBox, this.subSceneAdapter);
		this.contextMenuController.enableCenterFieldContextMenu(modelBox, paneBox, this.subSceneAdapter);
		this.mouseMoveController.enableMouseMove(paneBox);
	}

	private void addArrowControls(Arrow arrow, Relation relation) {
		this.selectionController.enableArrowSelection(arrow, this.subSceneAdapter);
		this.selectionController.enableArrowLabelSelection(arrow, this.subSceneAdapter);
		this.textFieldController.enableArrowLabelTextInput(arrow, relation, this.mvConnector);
	}

	private void adaptBoxSettings(ModelBox modelBox) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelBox);
		if (changedBox != null && modelBox instanceof ModelClass) {
			changedBox.setMinWidth(modelBox.getWidth());
			changedBox.setMinHeight(modelBox.getHeight());
			adaptCenterFields((ModelClass) modelBox);
		}
		else if (changedBox != null && modelBox instanceof ModelObject) {
			ModelObject modelObject = (ModelObject) modelBox;
			ModelClass modelClass = modelObject.getModelClass();
			PaneBox paneClassBox = this.mvConnector.getPaneBox(modelClass);
			if (paneClassBox != null && !modelObject.isSuperObject()) {
				changedBox.setMinWidth(paneClassBox.getMinWidth());
				changedBox.setMinHeight(paneClassBox.getMinHeight());
			}
			else if (paneClassBox != null && modelObject.isSuperObject()) {
				for (ModelClass subClass : modelClass.getSubClasses()) {
					if (subClass.getSubModelObject(modelObject) != null) {
						changedBox.setWidth(subClass.getWidth());
					}
				}
			}
			adaptCenterFields(modelObject);
		}
		adaptBoxTopField(modelBox);
		adaptBoxColor(modelBox);
		adaptBoxWidth(modelBox);
		adaptBoxHeight(modelBox);
		adaptBoxCoordinates(modelBox);
	}

	private void adaptArrowColor(Relation relation) {
		Arrow changedArrow = this.mvConnector.getArrow(relation);
		if (changedArrow == null) {
			return;
		}
		changedArrow.setColor(relation.getColor());
	}

	private void adaptArrowDirection(Relation relation) {
		Arrow changedArrow = this.mvConnector.getArrow(relation);
		if (changedArrow == null) {
			return;
		}
		ModelBox startModelBox = relation.getStart().getAppendant();
		ModelBox endModelBox = relation.getEnd().getAppendant();
		PaneBox startPaneBox = this.mvConnector.getPaneBox(startModelBox);
		PaneBox endPaneBox = this.mvConnector.getPaneBox(endModelBox);

		changedArrow.setType(relation.getRelationType());
		changedArrow.setPointsBasedOnBoxes(startPaneBox, endPaneBox);
		changedArrow.drawArrow();
		this.selectionController.setSelected(changedArrow, true, this.subSceneAdapter);
	}

	private void adaptArrowLabel(Relation relation) {
		Arrow changedArrow = this.mvConnector.getArrow(relation);
		if (changedArrow != null) {
			changedArrow.getLabelStartLeft().setText(relation.getStart().getRoleName());
			changedArrow.getLabelStartRight().setText(relation.getStart().getMultiplicity());
			changedArrow.getLabelEndLeft().setText(relation.getEnd().getRoleName());
			changedArrow.getLabelEndRight().setText(relation.getEnd().getMultiplicity());
			changedArrow.drawArrow();
		}
	}

	private void adaptArrowToBox(ModelBox modelBox) {
		if (modelBox.getEndpoints().isEmpty()) {
			return;
		}
		PaneBox changedBox = this.mvConnector.getPaneBox(modelBox);
		Map<Endpoint, Endpoint> endpointMap = modelBox.getFriends();
		for (Endpoint endpoint : endpointMap.keySet()) {
			Endpoint friendEndpoint = endpointMap.get(endpoint);
			PaneBox friendChangedBox = this.mvConnector.getPaneBox(friendEndpoint.getAppendant());
			Relation relation = endpoint.getRelation();
			Arrow changedArrow = this.mvConnector.getArrow(relation);
			if (changedArrow != null && changedBox != null && friendChangedBox != null) {
				if (endpoint.isStart()) {
					changedArrow.setPointsBasedOnBoxes(changedBox, friendChangedBox);
					endpoint.setCoordinates(changedArrow.getStartPoint());
					friendEndpoint.setCoordinates(changedArrow.getEndPoint());
				}
				else {
					changedArrow.setPointsBasedOnBoxes(friendChangedBox, changedBox);
					friendEndpoint.setCoordinates(changedArrow.getStartPoint());
					endpoint.setCoordinates(changedArrow.getEndPoint());
				}
				changedArrow.drawArrow();
			}
		}
	}

	private void adaptBoxTopField(ModelBox modelBox) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelBox);
		if (changedBox != null && modelBox instanceof ModelClass) {
			changedBox.setTopText(modelBox.getName());

			double newWidth = changedBox.calcMinWidth();
			changedBox.setMinWidth(newWidth);
			if (newWidth > changedBox.getWidth()) {
				modelBox.setWidth(changedBox.getMinWidth());
			}

			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject modelObject : modelClass.getModelObjects()) {
				modelObject.setName(modelObject.getName());
			}

			for (ModelObject inheritingObject : modelClass.getInheritingObjects()) {
				inheritingObject.setName(inheritingObject.getName());
			}
		}
		else if (changedBox != null && modelBox instanceof ModelObject) {
			ModelObject modelObject = (ModelObject) modelBox;
			changedBox.getTopTextField().setText((modelObject.getName()));
			changedBox.getTopLabel().setText(modelObject.getName() + " : " + modelObject.getModelClass().getName());
			if (!modelObject.isSuperObject()) {
				modelBox.setWidth(modelObject.getModelClass().getWidth());
			}
		}
	}

	private void adaptBoxWidth(ModelBox modelBox) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelBox);
		if (changedBox != null) {
			changedBox.setWidth(modelBox.getWidth());
		}
		if (modelBox instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject modelObject : modelClass.getModelObjects()) {
				modelObject.setWidth(modelClass.getWidth());
			}
			for (ModelObject superObject : modelClass.getSuperObjects()) {
				superObject.setWidth(modelClass.getWidth());
			}
		}
	}

	private void adaptBoxHeight(ModelBox modelBox) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelBox);
		if (changedBox != null) {
			changedBox.setHeight(modelBox.getHeight());
		}
		if (modelBox instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject modelObject : modelClass.getModelObjects()) {
				modelObject.setHeight(modelClass.getHeight());
			}
			for (ModelClass subClass : modelClass.getSubClasses()) {
				for (ModelObject subModelObject : subClass.getModelObjects()) {
					double cascadingHeight = 0.0;
					for (ModelObject subSuperObject : subClass.getSuperObjects(subModelObject)) {
						if (subSuperObject.getModelClass().equals(modelClass)) {
							subSuperObject.setHeight(modelClass.getHeight());
						}
						subSuperObject.setX(subClass.getX());
						subSuperObject.setZ(subClass.getZ() + subClass.getHeight() / 2 + cascadingHeight + subSuperObject.getHeight() / 2);
						cascadingHeight += subSuperObject.getHeight();
					}
				}
			}
		}
	}

	private void adaptBoxColor(ModelBox modelBox) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelBox);
		if (changedBox != null) {
			changedBox.setColor(modelBox.getColor());
		}
		if (modelBox instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject modelObject : modelClass.getModelObjects()) {
				modelObject.setColor(Util.brighter(modelClass.getColor(), 0.1));
			}
			for (ModelObject inheritingObject : modelClass.getInheritingObjects()) {
				inheritingObject.setColor(Util.brighter(modelClass.getColor(), 0.1));
			}
		}
	}

	private void adaptBoxCoordinates(ModelBox modelBox) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelBox);
		if (changedBox == null) {
			return;
		}
		if (modelBox instanceof ModelClass) {
			changedBox.setTranslateXYZ(modelBox.getCoordinates());
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject modelObject : modelClass.getModelObjects()) {
				modelObject.setX(modelClass.getX());
				modelObject.setZ(modelClass.getZ());
				double cascadingHeight = 0.0;
				for (ModelObject superObject : modelClass.getSuperObjects(modelObject)) {
					superObject.setX(modelClass.getX());
					superObject.setZ(modelClass.getZ() + modelClass.getHeight() / 2 + cascadingHeight + superObject.getHeight() / 2);
					cascadingHeight += superObject.getHeight();
				}
			}
		}
		else if (modelBox instanceof ModelObject) {
			changedBox.setTranslateXYZ(modelBox.getCoordinates());
			ModelObject modelObject = (ModelObject) modelBox;
			for (ModelObject superObjects : modelObject.getSuperObjects()) {
				superObjects.setY(modelObject.getY());
			}
		}
	}

	private void adaptCenterFields(ModelClass modelClass) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelClass);
		if (changedBox != null) {
			int prevSelectionIndex = changedBox.getCenterLabels().indexOf(changedBox.getSelectedLabel());
			changedBox.clearCenterFields();
			for (int i = 0; i < modelClass.getAttributes().size(); i++) {
				if (i < PaneBox.MAX_CENTER_LABELS) {
					Attribute attribute = modelClass.getAttributes().get(i);
					changedBox.setCenterText(i, attribute.getName(), attribute.getName());
				}
			}
			changedBox.setLabelSelected(prevSelectionIndex, true);
			// center labels were cleared and recreated, need controls again
			this.selectionController.enableCenterLabelSelection(changedBox, this.subSceneAdapter);
			this.textFieldController.enableCenterTextInput(modelClass, changedBox, this.mvConnector);
			this.contextMenuController.enableCenterFieldContextMenu(modelClass, changedBox, this.subSceneAdapter);

			double newWidth = changedBox.calcMinWidth();
			changedBox.setMinWidth(newWidth);
			if (newWidth > changedBox.getWidth()) {
				modelClass.setWidth(changedBox.getMinWidth());
			}
			double newHeight = changedBox.calcMinHeight();
			changedBox.setMinHeight(newHeight);
			if (newHeight > changedBox.getHeight()) {
				modelClass.setHeight(changedBox.getMinHeight());
			}
		}
	}

	private void adaptCenterFields(ModelObject modelObject) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelObject);
		if (changedBox != null) {
			int prevSelectionIndex = changedBox.getCenterLabels().indexOf(changedBox.getSelectedLabel());
			changedBox.clearCenterFields();
			// using attribute list of this objects class, to get same order.
			for (int i = 0; i < modelObject.getModelClass().getAttributes().size(); i++) {
				if (i < PaneBox.MAX_CENTER_LABELS) {
					Attribute attribute = modelObject.getModelClass().getAttributes().get(i);
					String attributeName = attribute.getName();
					String attributeValue = modelObject.getAttributeValues().get(attribute);
					if (attributeValue != null && !attributeValue.isEmpty()) {
						changedBox.setCenterText(i, attributeName + " = " + attributeValue, attributeValue);
					}
					else {
						changedBox.setCenterText(i, attributeName, attributeValue);
					}
				}
			}
			changedBox.recalcHasCenterGrid();
			changedBox.setLabelSelected(prevSelectionIndex, true);
			// center labels were cleared and recreated, need controls again
			this.selectionController.enableCenterLabelSelection(changedBox, this.subSceneAdapter);
			this.textFieldController.enableCenterTextInput(modelObject, changedBox, this.mvConnector);
			this.contextMenuController.enableCenterFieldContextMenu(modelObject, changedBox, this.subSceneAdapter);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof ModelManager && arg instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) arg;
			if (!this.mvConnector.containsModelBox(modelClass)) { // class is new
				showModelClassInView(modelClass);
				adaptBoxSettings(modelClass);
				adaptArrowToBox(modelClass);
			}
			else {
				PaneBox toDelete = this.mvConnector.removeBoxes(modelClass);
				removeFromView(toDelete.get());
				removeFromView(toDelete.getSelection());
			}
		}
		else if (o instanceof ModelManager && arg instanceof ModelObject) {
			ModelObject modelObject = (ModelObject) arg;
			if (!this.mvConnector.containsModelBox(modelObject)) { // object is new
				showModelObjectInView(modelObject);
				adaptBoxSettings(modelObject);
				adaptArrowToBox(modelObject);
			}
			else {
				PaneBox toDelete = this.mvConnector.removeBoxes(modelObject);
				removeFromView(toDelete.get());
				removeFromView(toDelete.getSelection());
			}
		}
		else if (o instanceof ModelManager && arg instanceof Relation) {
			Relation relation = (Relation) arg;
			if (!this.mvConnector.containsRelation(relation)) { // relation is new
				showArrowInView(relation);
				adaptArrowColor(relation);
			}
			else {
				ModelBox startModelBox = relation.getStart().getAppendant();
				ModelBox endModelBox = relation.getEnd().getAppendant();
				Arrow toDelete = this.mvConnector.removeArrows(relation);
				this.mvConnector.arrangeArrowNumbers(startModelBox, endModelBox);
				removeFromView(toDelete);
				removeFromView(toDelete.getSelection());
			}
		}
		else if (o instanceof ModelClass && arg instanceof Attribute) {
			ModelClass modelClass = (ModelClass) o;
			adaptCenterFields(modelClass);
		}
		else if (o instanceof ModelObject && arg instanceof Attribute) {
			ModelObject modelObject = (ModelObject) o;
			adaptCenterFields(modelObject);
		}
		else if (o instanceof ModelBox && arg instanceof ModelBoxChange) {
			ModelBox modelBox = (ModelBox) o;
			ModelBoxChange modelBoxChange = (ModelBoxChange) arg;
			switch (modelBoxChange) {
			case COLOR:
				adaptBoxColor(modelBox);
				adaptArrowToBox(modelBox);
				break;
			case COORDINATES:
				adaptBoxCoordinates(modelBox);
				adaptArrowToBox(modelBox);
				break;
			case HEIGHT:
				adaptBoxHeight(modelBox);
				adaptArrowToBox(modelBox);
				break;
			case NAME:
				adaptBoxTopField(modelBox);
				adaptArrowToBox(modelBox);
				break;
			case WIDTH:
				adaptBoxWidth(modelBox);
				adaptArrowToBox(modelBox);
				break;
			default:
				break;
			}
		}
		else if (o instanceof Relation && arg instanceof RelationChange) {
			Relation relation = (Relation) o;
			RelationChange relationChange = (RelationChange) arg;
			switch (relationChange) {
			case COLOR:
				adaptArrowColor(relation);
				break;
			case DIRECTION:
				adaptArrowDirection(relation);
				break;
			case MULTIPLCITY_ROLE:
				adaptArrowLabel(relation);
				break;
			default:
				break;
			}
		}
		this.rootLayout.applyCss();
	}
	
}
