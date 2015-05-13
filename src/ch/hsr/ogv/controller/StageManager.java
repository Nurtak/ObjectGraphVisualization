package ch.hsr.ogv.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfxtras.labs.util.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.dataaccess.Persistancy;
import ch.hsr.ogv.dataaccess.UserPreferences;
import ch.hsr.ogv.model.Attribute;
import ch.hsr.ogv.model.Endpoint;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelBox.ModelBoxChange;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.Relation.RelationChange;
import ch.hsr.ogv.util.FXMLResourceUtil;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 *
 * @author Simon Gwerder
 *
 */
public class StageManager implements Observer {

	private final static Logger logger = LoggerFactory.getLogger(StageManager.class);

	private String appTitle = "Object Graph Visualizer v.2.0";
	private Stage primaryStage;
	private BorderPane rootLayout;
	private SubSceneAdapter subSceneAdapter;

	private ModelViewConnector mvConnector;
	private Persistancy persistancy;

	private RootLayoutController rootLayoutController = new RootLayoutController();
	private SelectionController selectionController = new SelectionController();
	private ContextMenuController contextMenuController = new ContextMenuController();
	private TextFieldController textFieldController = new TextFieldController();
	private MouseMoveController mouseMoveController = new MouseMoveController();
	private CameraController cameraController = new CameraController();
	private DragMoveController dragMoveController = new DragMoveController();
	private DragResizeController dragResizeController = new DragResizeController();
	private RelationCreationController relationCreationController = new RelationCreationController();

	private static final int MIN_WIDTH = 1024;
	private static final int MIN_HEIGHT = 768;

	public StageManager(Stage primaryStage) {
		if (primaryStage == null) {
			throw new IllegalArgumentException("The primaryStage argument can not be null!");
		}
		this.primaryStage = primaryStage;

		loadRootLayoutController();
		setupStage();

		initMVConnector();
		initPersistancy();

		initRootLayoutController();
		initSelectionController();
		initContextMenuController();
		initMouseMoveController();
		initCameraController();
		initDragController();
		initRelationCreationController();

		this.selectionController.setSelected(this.subSceneAdapter, true, this.subSceneAdapter);

		// TODO: Remove everything below this line:
		mvConnector.createDummyContent();
	}

	private void setupStage() {
		this.primaryStage.setTitle(this.appTitle);
		this.primaryStage.setMinWidth(MIN_WIDTH);
		this.primaryStage.setMinHeight(MIN_HEIGHT);
		this.primaryStage.getIcons().add(new Image(ResourceLocator.getResourcePath(Resource.ICON_GIF).toExternalForm())); // set the application icon

		Pane canvas = (Pane) this.rootLayout.getCenter();
		this.subSceneAdapter = new SubSceneAdapter(canvas.getWidth(), canvas.getHeight());
		SubScene subScene = this.subSceneAdapter.getSubScene();
		canvas.getChildren().add(subScene);
		subScene.widthProperty().bind(canvas.widthProperty());
		subScene.heightProperty().bind(canvas.heightProperty());

		Scene scene = new Scene(this.rootLayout);
		String sceneCSS = ResourceLocator.getResourcePath(Resource.SCENE_CSS).toExternalForm();
		scene.getStylesheets().add(sceneCSS);
		this.primaryStage.setScene(scene);
		this.primaryStage.show();
		this.subSceneAdapter.getSubScene().requestFocus();
	}

	private void loadRootLayoutController() {
		FXMLLoader loader = FXMLResourceUtil.prepareLoader(Resource.ROOTLAYOUT_FXML); // load rootlayout from fxml file
		try {
			loader.setController(rootLayoutController);
			this.rootLayout = (BorderPane) loader.load();
		}
		catch (IOException | ClassCastException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}

	private void initMVConnector() {
		this.mvConnector = new ModelViewConnector();
		this.mvConnector.getModelManager().addObserver(this);
	}

	private void initPersistancy() {
		UserPreferences.setOGVFilePath(null); // reset user preferences of file path
		persistancy = new Persistancy(this.mvConnector.getModelManager());
		rootLayoutController.setPersistancy(this.persistancy);
	}

	private void initRootLayoutController() {
		this.rootLayoutController.setPrimaryStage(this.primaryStage);
		this.rootLayoutController.setMVConnector(this.mvConnector);
		this.rootLayoutController.setSubSceneAdapter(this.subSceneAdapter);
		this.rootLayoutController.setSelectionController(this.selectionController);
		this.rootLayoutController.setCameraController(this.cameraController);
		this.rootLayoutController.setRelationCreationController(this.relationCreationController);
	}

	private void initSelectionController() {
		this.selectionController.enableSubSceneSelection(this.subSceneAdapter);
		this.selectionController.addObserver(this.rootLayoutController);
		this.selectionController.addObserver(this.contextMenuController);
	}

	private void initContextMenuController() {
		this.contextMenuController.enableActionEvents(this.selectionController, this.subSceneAdapter);
		this.contextMenuController.setMVConnector(this.mvConnector);
		this.contextMenuController.setRelationCreationController(this.relationCreationController);
		this.contextMenuController.enableContextMenu(this.subSceneAdapter);
	}

	private void initMouseMoveController() {
		this.mouseMoveController.enableMouseMove(this.subSceneAdapter.getFloor());
		this.mouseMoveController.addObserver(relationCreationController);
	}

	private void initCameraController() {
		this.cameraController.handleMouse(this.subSceneAdapter);
		this.cameraController.handleKeyboard(this.subSceneAdapter);
	}

	private void initDragController() {
		this.dragMoveController.addObserver(this.cameraController);
		this.dragMoveController.addObserver(this.rootLayoutController);
		this.dragMoveController.addObserver(this.selectionController);
		this.dragResizeController.addObserver(this.cameraController);
		this.dragResizeController.addObserver(this.rootLayoutController);
		this.dragResizeController.addObserver(this.selectionController);
	}

	private void initRelationCreationController() {
		relationCreationController.setSelectionController(selectionController);
		relationCreationController.setSubSceneAdapter(subSceneAdapter);
		relationCreationController.setMvConnector(mvConnector);
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

	private void showClassInView(ModelClass modelClass) {
		modelClass.addObserver(this);
		PaneBox paneBox = new PaneBox();
		paneBox.setDepth(PaneBox.CLASSBOX_DEPTH);
		paneBox.setColor(PaneBox.DEFAULT_COLOR);
		paneBox.setTopUnderline(false);
		paneBox.showCenterGrid(false);
		addPaneBoxControls(modelClass, paneBox);
		addToSubScene(paneBox.get());
		addToSubScene(paneBox.getSelection());
		this.mvConnector.putBoxes(modelClass, paneBox);
	}

	private void showObjectInView(ModelObject modelObject) {
		modelObject.addObserver(this);
		PaneBox paneBox = new PaneBox();
		paneBox.setDepth(PaneBox.OBJECTBOX_DEPTH);
		paneBox.setColor(modelObject.getColor());
		paneBox.setTopUnderline(true);
		paneBox.showCenterGrid(true);
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
			Arrow arrow = new Arrow(startViewBox, endViewBox, relation.getType());
			addArrowControls(arrow, relation);
			addToSubScene(arrow);
			addToSubScene(arrow.getSelection());
			this.mvConnector.putArrows(relation, arrow);
			this.contextMenuController.enableContextMenu(arrow, relation);
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
						changedBox.setMinWidth(subClass.getWidth());
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

		changedArrow.setType(relation.getType());
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
		if (changedBox != null && modelBox instanceof ModelObject) {
			ModelObject modelObject = (ModelObject) modelBox;
			changedBox.getTopTextField().setText((modelObject.getName()));
			changedBox.getTopLabel().setText(modelObject.getName() + " : " + modelObject.getModelClass().getName());
			if (!modelObject.isSuperObject()) {
				modelBox.setWidth(modelObject.getModelClass().getWidth());
			}
		}
		else if (changedBox != null && modelBox instanceof ModelClass) {
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
	}

	private void adaptBoxWidth(ModelBox modelBox) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelBox);
		if (changedBox == null) {
			return;
		}
		if (modelBox instanceof ModelClass) {
			changedBox.setWidth(modelBox.getWidth());
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject modelObject : modelClass.getModelObjects()) {
				modelObject.setWidth(modelClass.getWidth());
			}
			for (ModelObject superObject : modelClass.getSuperObjects()) {
				superObject.setWidth(modelClass.getWidth());
				// PaneBox superPaneBox = this.mvConnector.getPaneBox(modelBox);
				// if(superPaneBox != null) {
				// superPaneBox.setMinWidth(changedBox.getMinWidth());
				// }
			}
		}
		else if (modelBox instanceof ModelObject) {
			changedBox.setWidth(modelBox.getWidth());
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
				showClassInView(modelClass);
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
				showObjectInView(modelObject);
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
				// adaptRelation(relation);
			}
			else {
				Arrow toDelete = this.mvConnector.removeArrows(relation);
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
