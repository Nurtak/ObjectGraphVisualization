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

	private String appTitle = "Object Graph Visualizer v.1.5";
	private Stage primaryStage;
	private BorderPane rootLayout;
	private SubSceneAdapter subSceneAdapter;

	private ModelViewConnector mvConnector;

	private RootLayoutController rootLayoutController = new RootLayoutController();
	private CameraController cameraController = new CameraController();
	private SelectionController selectionController = new SelectionController();
	private MouseMoveController mouseMoveController = new MouseMoveController();
	private TextFieldController textFieldController = new TextFieldController();
	private DragMoveController dragMoveController = new DragMoveController();
	private DragResizeController dragResizeController = new DragResizeController();
	private ContextMenuController contextMenuController = new ContextMenuController();

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
		initRootLayoutController();
		initContextMenuController();
		initSelectionController();
		initMouseMoveController();
		initCameraController();
		initDragController();

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
		} catch (IOException | ClassCastException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}

	private void initMVConnector() {
		this.mvConnector = new ModelViewConnector();
		this.mvConnector.getModelManager().addObserver(this);
	}

	private void initRootLayoutController() {
		this.rootLayoutController.setPrimaryStage(this.primaryStage);
		this.rootLayoutController.setMVConnector(this.mvConnector);
		this.rootLayoutController.setSubSceneAdapter(this.subSceneAdapter);
		this.rootLayoutController.setSelectionController(this.selectionController);
		this.rootLayoutController.setMouseMoveController(this.mouseMoveController);
		this.rootLayoutController.setCameraController(this.cameraController);
	}

	private void initContextMenuController() {
		this.contextMenuController.setMVConnector(this.mvConnector);
		this.contextMenuController.enableContextMenu(this.subSceneAdapter);
	}

	private void initSelectionController() {
		this.selectionController.enableSubSceneSelection(this.subSceneAdapter);
		this.selectionController.addObserver(this.rootLayoutController);
		this.selectionController.addObserver(this.contextMenuController);
	}

	private void initMouseMoveController() {
		this.mouseMoveController.enableMouseMove(this.subSceneAdapter.getFloor());
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
	private void removeFromSubScene(Node node) {
		this.subSceneAdapter.remove(node);
		this.rootLayout.applyCss();
	}

	private void addClassToSubScene(ModelClass modelClass) {
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

	private void addObjectToSubScene(ModelObject modelObject) {
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

	private void addRelationToSubScene(Relation relation) {
		relation.addObserver(this);
		ModelBox startModelBox = relation.getStart().getAppendant();
		ModelBox endModelBox = relation.getEnd().getAppendant();
		PaneBox startViewBox = this.mvConnector.getPaneBox(startModelBox);
		PaneBox endViewBox = this.mvConnector.getPaneBox(endModelBox);
		if (startViewBox != null && endViewBox != null) {
			Arrow arrow = new Arrow(startViewBox, endViewBox, relation.getType());
			addArrowControls(arrow);
			addToSubScene(arrow);
			addToSubScene(arrow.getSelection());
			this.mvConnector.putArrows(relation, arrow);
			this.contextMenuController.enableContextMenu(relation, arrow);
		}
	}

	private void addPaneBoxControls(ModelBox modelBox, PaneBox paneBox) {
		this.selectionController.enablePaneBoxSelection(paneBox, this.subSceneAdapter);
		this.textFieldController.enableTextInput(modelBox, paneBox);
		this.contextMenuController.enableContextMenu(modelBox, paneBox, this.subSceneAdapter);
		this.mouseMoveController.enableMouseMove(paneBox);
		this.dragMoveController.enableDragMove(modelBox, paneBox, this.subSceneAdapter);
		if (modelBox instanceof ModelClass) {
			this.dragResizeController.enableDragResize(modelBox, paneBox, this.subSceneAdapter);
		}
	}

	private void addArrowControls(Arrow arrow) {
		this.selectionController.enableArrowSelection(arrow, this.subSceneAdapter);
	}

	private void adaptBoxSettings(ModelBox modelBox) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelBox);
		if (changedBox != null && modelBox instanceof ModelClass) {
			changedBox.setMinWidth(modelBox.getWidth());
			changedBox.setMinHeight(modelBox.getHeight());
			adaptCenterFields((ModelClass) modelBox);
		} else if (changedBox != null && modelBox instanceof ModelObject) {
			ModelObject modelObject = (ModelObject) modelBox;
			ModelClass modelClass = modelObject.getModelClass();
			PaneBox paneClassBox = this.mvConnector.getPaneBox(modelClass);
			if (paneClassBox != null) {
				changedBox.setMinWidth(paneClassBox.getMinWidth());
				changedBox.setMinHeight(paneClassBox.getMinHeight());
			}
			adaptCenterFields((ModelObject) modelBox);
		}
		adaptBoxTopField(modelBox);
		adaptBoxColor(modelBox);
		adaptBoxWidth(modelBox);
		adaptBoxHeight(modelBox);
		adaptBoxCoordinates(modelBox);
	}

	private void adaptArrowColor(Relation relation) {
		Arrow changedArrow = this.mvConnector.getArrow(relation);
		if (changedArrow != null) {
			changedArrow.setColor(relation.getColor());
		}
	}
	
	private void adaptArrowDirection(Relation relation) {
		Arrow changedArrow = this.mvConnector.getArrow(relation);
		if (changedArrow != null) {
			ModelBox startModelBox = relation.getStart().getAppendant();
			ModelBox endModelBox = relation.getEnd().getAppendant();
			System.out.println("Relation getStart(): " + relation.getStart() + " has class: " + relation.getStart().getAppendant());
			System.out.println("Relation getEnd(): " + relation.getEnd() + " has class: " + relation.getEnd().getAppendant());
			PaneBox startPaneBox = this.mvConnector.getPaneBox(startModelBox);
			PaneBox endPaneBox = this.mvConnector.getPaneBox(endModelBox);
			changedArrow.setPointsBasedOnBoxes(startPaneBox, endPaneBox);
			changedArrow.drawArrow();
			this.selectionController.setSelected(changedArrow, true, this.subSceneAdapter);
		}
	}

	private void adaptArrowToBox(ModelBox modelBox) {
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
				} else {
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
			modelBox.setWidth(modelObject.getModelClass().getWidth());
		} else if (changedBox != null && modelBox instanceof ModelClass) {
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
		}
	}

	private void adaptBoxCoordinates(ModelBox modelBox) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelBox);
		if (changedBox != null) {
			changedBox.setTranslateXYZ(modelBox.getCoordinates());
		}
		if (modelBox instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject modelObject : modelClass.getModelObjects()) {
				modelObject.setX(modelClass.getX());
				modelObject.setZ(modelClass.getZ());
			}
		}
	}

	private void adaptCenterFields(ModelClass modelClass) {
		PaneBox changedBox = this.mvConnector.getPaneBox(modelClass);
		if (changedBox != null) {
			changedBox.showAllCenterLabels(false);
			for (int i = 0; i < modelClass.getAttributes().size(); i++) {
				if (i < PaneBox.MAX_CENTER_LABELS) {
					Attribute attribute = modelClass.getAttributes().get(i);
					changedBox.showCenterLabel(i, true);
					changedBox.setCenterText(i, attribute.getName(), attribute.getName());
				}
			}
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
		PaneBox paneObjectBox = this.mvConnector.getPaneBox(modelObject);
		if (paneObjectBox != null) {
			paneObjectBox.showAllCenterLabels(false);
			for (int i = 0; i < modelObject.getModelClass().getAttributes().size(); i++) { // using attribute list of this objects class, to get same order.
				if (i < PaneBox.MAX_CENTER_LABELS) {
					Attribute attribute = modelObject.getModelClass().getAttributes().get(i);
					String attributeName = attribute.getName();
					String attributeValue = modelObject.getAttributeValues().get(attribute);
					paneObjectBox.showCenterLabel(i, true);
					if (attributeValue != null && !attributeValue.isEmpty()) {
						paneObjectBox.setCenterText(i, attributeName + " = " + attributeValue, attributeValue);
					} else {
						paneObjectBox.setCenterText(i, attributeName, attributeValue);
					}
				}
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof ModelManager && arg instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) arg;
			if (!this.mvConnector.containsModelBox(modelClass)) { // class is new
				addClassToSubScene(modelClass);
				adaptBoxSettings(modelClass);
				adaptArrowToBox(modelClass);
			} else {
				PaneBox toDelete = this.mvConnector.removeBoxes(modelClass);
				removeFromSubScene(toDelete.get());
				removeFromSubScene(toDelete.getSelection());
			}
		} else if (o instanceof ModelManager && arg instanceof Relation) {
			Relation relation = (Relation) arg;
			if (!this.mvConnector.containsRelation(relation)) { // relation is new
				addRelationToSubScene(relation);
				adaptArrowColor(relation);
				// adaptRelation(relation);
			} else {
				Arrow toDelete = this.mvConnector.removeArrows(relation);
				removeFromSubScene(toDelete);
				removeFromSubScene(toDelete.getSelection());
			}
		} else if (o instanceof ModelManager && arg instanceof ModelObject) {
			ModelObject modelObject = (ModelObject) arg;
			if (!this.mvConnector.containsModelBox(modelObject)) { // instance is new
				addObjectToSubScene(modelObject);
				adaptBoxSettings(modelObject);
				adaptArrowToBox(modelObject);
			} else {
				PaneBox toDelete = this.mvConnector.removeBoxes(modelObject);
				removeFromSubScene(toDelete.get());
				removeFromSubScene(toDelete.getSelection());
			}
		} else if (o instanceof ModelClass && arg instanceof Attribute) {
			ModelClass modelClass = (ModelClass) o;
			adaptCenterFields(modelClass);
		} else if (o instanceof ModelObject && arg instanceof Attribute) {
			ModelObject modelObject = (ModelObject) o;
			adaptCenterFields(modelObject);
		} else if (o instanceof ModelBox && arg instanceof ModelBoxChange) {
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
		} else if (o instanceof Relation && arg instanceof RelationChange) {
			Relation relation = (Relation) o;
			RelationChange relationChange = (RelationChange) arg;
			switch (relationChange) {
			case COLOR:
				adaptArrowColor(relation);
			case DIRECTION:
				adaptArrowDirection(relation);
			default:
				break;
			}
		}
		this.rootLayout.applyCss();
	}
}
