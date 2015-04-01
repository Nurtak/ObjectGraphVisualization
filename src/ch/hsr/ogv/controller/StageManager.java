package ch.hsr.ogv.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Group;
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

import ch.hsr.ogv.controller.ThemeMenuController.Style;
import ch.hsr.ogv.model.Endpoint;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelBox.ModelBoxChange;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.ArrowLine;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;
import ch.hsr.ogv.view.SubSceneCamera;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class StageManager extends Observable implements Observer {

	private final static Logger logger = LoggerFactory.getLogger(StageManager.class);

	private String appTitle = "Object Graph Visualizer";
	private Stage primaryStage;
	private BorderPane rootLayout;
	private SubSceneAdapter subSceneAdapter;

	private ModelManager modelManager;
	
	private Map<ModelBox, PaneBox> boxes = new HashMap<ModelBox, PaneBox>();
	private Map<Relation, ArrowLine> arrows = new HashMap<Relation, ArrowLine>();

	private ThemeMenuController themeMenuController = new ThemeMenuController();
	private CameraController cameraController = new CameraController();
	private SubSceneController subSceneController = new SubSceneController();
	private SelectionController selectionController = new SelectionController();
	private TextInputController textInputController = new TextInputController();
	private DragMoveController dragMoveController = new DragMoveController();
	private DragResizeController dragResizeController = new DragResizeController();

	private static final int MIN_WIDTH = 1024;
	private static final int MIN_HEIGHT = 768;

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public String getAppTitle() {
		return appTitle;
	}

	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
		this.getPrimaryStage().setTitle(this.appTitle);
	}
	
	public SubSceneController getSubSceneController() {
		return subSceneController;
	}
	
	public SelectionController getSelectionController() {
		return selectionController;
	}
	
	public StageManager(Stage primaryStage) {
		if (primaryStage == null)
			throw new IllegalArgumentException("The primaryStage argument can not be null!");
		this.primaryStage = primaryStage;

		this.modelManager = new ModelManager();
		this.modelManager.addObserver(this);

		initRootLayoutController();
		setupStage();
		initSubSceneController();
		initCameraController();
		initPaneBoxController();
	}

	private void setupStage() {
		this.setAppTitle(this.appTitle);
		this.primaryStage.setMinWidth(MIN_WIDTH);
		this.primaryStage.setMinHeight(MIN_HEIGHT);
		this.primaryStage.getIcons().add(new Image(ResourceLocator.getResourcePath(Resource.ICON_PNG).toExternalForm())); // set the application icon

		Pane canvas = (Pane) this.rootLayout.getCenter();
		this.subSceneAdapter = new SubSceneAdapter(canvas.getWidth(), canvas.getHeight());
		SubScene subScene = this.subSceneAdapter.getSubScene();
		canvas.getChildren().add(subScene);
		subScene.widthProperty().bind(canvas.widthProperty());
		subScene.heightProperty().bind(canvas.heightProperty());

		Scene scene = new Scene(this.rootLayout);
		this.primaryStage.setScene(scene);
		this.primaryStage.show();
		this.subSceneAdapter.getSubScene().requestFocus();

		setChanged();
		notifyObservers(this); // pass StageManager to RootLayoutController

		// TODO: Remove everything below this line:
		ModelClass mcA = this.modelManager.createClass(new Point3D(100, PaneBox.INIT_DEPTH / 2, 100), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		ModelClass mcB = this.modelManager.createClass(new Point3D(300, PaneBox.INIT_DEPTH / 2, 300), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		ModelClass mcC = this.modelManager.createClass(new Point3D(400, PaneBox.INIT_DEPTH / 2, -200), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		mcA.setName("A");
		ModelObject moA = this.modelManager.createObject(mcA);
		this.modelManager.createObject(mcB);
		ModelObject moB = this.modelManager.createObject(mcB);
		this.modelManager.createObject(mcB);
	
		this.modelManager.createRelation(mcA, mcB, RelationType.GENERALIZATION);
		this.modelManager.createRelation(mcC, mcB, RelationType.DIRECTED_AGGREGATION);
		this.modelManager.createRelation(mcC, mcA, RelationType.DIRECTED_COMPOSITION);
		this.modelManager.createRelation(moA, moB, RelationType.DIRECTED_AGGREGATION);
	}

	public void onlyFloorMouseEvent(boolean value) {
		this.subSceneAdapter.onlyFloorMouseEvent(value);
	}

	public void handleCreateNewClass(Point3D mouseCoords) {
		onlyFloorMouseEvent(false);
		Point3D boxPosition = new Point3D(mouseCoords.getX(), PaneBox.INIT_DEPTH / 2, mouseCoords.getZ());
		ModelClass newClass = this.modelManager.createClass(boxPosition, PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		PaneBox newBox = this.boxes.get(newClass);
		newBox.allowTopTextInput(true);
		Platform.runLater(() -> {
			newBox.getTopTextField().requestFocus();
			newBox.getTopTextField().selectAll();
			newBox.getTopTextField().applyCss();
		});
	}
	
	public void handleCreateNewObject(PaneBox selectedPaneBox) {
		ModelBox selectedModelBox = this.getModelBoxByPaneBox(selectedPaneBox);
		ModelClass selectedModelClass = (ModelClass) selectedModelBox;
		this.modelManager.createObject(selectedModelClass);
		//TODO
		//		ModelObject newObject = 
		//		PaneBox newBox = this.boxes.get(newObject);
		//		newBox.allowTopTextInput(true);
		//		Platform.runLater(() -> {
		//			newBox.getTopTextField().requestFocus();
		//			newBox.getTopTextField().selectAll();
		//			newBox.getTopTextField().applyCss();
		//		});
	}
	
	private ModelBox getModelBoxByPaneBox(PaneBox value) {
	    for (Entry<ModelBox, PaneBox> entry : boxes.entrySet()) {
	        if (entry.getValue().equals(value)) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}

	public void handleCenterView() {
		SubSceneCamera ssCamera = this.subSceneAdapter.getSubSceneCamera();
		this.cameraController.handleCenterView(ssCamera);
	}

	public void handleLockedTopView(boolean isLockedTopView) {
		SubSceneCamera ssCamera = this.subSceneAdapter.getSubSceneCamera();
		this.cameraController.handleLockedTopView(ssCamera, isLockedTopView);
	}

	public void handleShowObjects(boolean showObjects) {
		for (ModelBox modelBox : this.boxes.keySet()) {
			if (modelBox instanceof ModelObject) {
				PaneBox paneBox = this.boxes.get(modelBox);
				paneBox.setVisible(showObjects);
				for (Endpoint endpoint : modelBox.getEndpoints()) {
					ArrowLine arrowLine = this.arrows.get(endpoint.getRelation());
					arrowLine.setVisible(showObjects);
				}
			}
		}
	}

	public void handleShowModelAxis(boolean showModelAxis) {
		Group axis = this.subSceneAdapter.getAxis();
		if (showModelAxis) {
			axis.setVisible(true);
		} else {
			axis.setVisible(false);
		}
	}

	public void handleSetTheme(Style style) {
		this.themeMenuController.handleSetTheme(this.rootLayout, style);
	}

	private void initRootLayoutController() {
		FXMLLoader loader = new FXMLLoader(); // load rootlayout from fxml file
		loader.setLocation(ResourceLocator.getResourcePath(Resource.ROOTLAYOUT_FXML));
		try {
			this.rootLayout = (BorderPane) loader.load();
			addObserver(loader.getController());
		} catch (IOException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}

	private void initCameraController() {
		this.cameraController.handleMouse(this.subSceneAdapter);
		this.cameraController.handleKeyboard(this.subSceneAdapter);
	}

	private void initSubSceneController() {
		this.subSceneController.handleSubSceneMouse(this.subSceneAdapter);
	}

	private void initPaneBoxController() {
		this.dragMoveController.addObserver(this.cameraController);
		this.dragResizeController.addObserver(this.cameraController);
		this.selectionController.addObserver(this.dragMoveController);
		this.selectionController.addObserver(this.dragResizeController);
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

	private void addRelationToSubScene(Relation relation) {
		ModelBox startModelBox = relation.getStart().getAppendant();
		ModelBox endModelBox = relation.getEnd().getAppendant();
		PaneBox startViewBox = this.boxes.get(startModelBox);
		PaneBox endViewBox = this.boxes.get(endModelBox);
		if (startViewBox != null && endViewBox != null) {
			ArrowLine arrow = new ArrowLine(startViewBox, endViewBox, relation.getType());
			addToSubScene(arrow);
			this.arrows.put(relation, arrow);
		}
	}

	private void adaptArrowAtBoxChanges(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
		Map<Endpoint, Endpoint> endpointMap = modelBox.getFriends();
		for (Endpoint endpoint : endpointMap.keySet()) {
			Endpoint friendEndpoint = endpointMap.get(endpoint);
			PaneBox friendChangedBox = this.boxes.get(friendEndpoint.getAppendant());
			Relation relation = endpoint.getRelation();
			ArrowLine changedArrow = this.arrows.get(relation);
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

	private void addClassToSubScene(ModelClass modelClass) {
		modelClass.addObserver(this);
		PaneBox paneBox = new PaneBox();
		paneBox.setCenterText(0, "TEST");
		paneBox.setDepth(PaneBox.CLASSBOX_DEPTH);
		paneBox.setColor(PaneBox.DEFAULT_COLOR);
		paneBox.setTopUnderline(false);
		addPaneBoxControls(modelClass, paneBox);
		addToSubScene(paneBox.get());
		addToSubScene(paneBox.getSelection());
		this.boxes.put(modelClass, paneBox);
	}

	private void addObjectToSubScene(ModelObject modelObject) {
		modelObject.addObserver(this);
		PaneBox paneBox = new PaneBox();
		paneBox.setDepth(PaneBox.OBJECTBOX_DEPTH);
		paneBox.setColor(modelObject.getColor());
		paneBox.setTopUnderline(true);
		// addPaneBoxControls(modelObject, paneBox);
		addToSubScene(paneBox.get());
		addToSubScene(paneBox.getSelection());
		this.boxes.put(modelObject, paneBox);
	}

	private void addPaneBoxControls(ModelClass modelClass, PaneBox paneBox) {
		this.selectionController.enableSelection(paneBox, this.subSceneAdapter);
		this.textInputController.enableTextInput(modelClass, paneBox);
		this.dragMoveController.enableDragMove(modelClass, paneBox, this.subSceneAdapter);
		this.dragResizeController.enableDragResize(modelClass, paneBox, this.subSceneAdapter);
	}

	private void adaptBoxSettings(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
		if (changedBox != null) {
			changedBox.setTopText(modelBox.getName());
			changedBox.setColor(modelBox.getColor());
			changedBox.setMinWidth(modelBox.getWidth());
			changedBox.setWidth(modelBox.getWidth());
			changedBox.setMinHeight(modelBox.getHeight());
			changedBox.setHeight(modelBox.getHeight());
			changedBox.setTranslateXYZ(modelBox.getCoordinates());
		}
		adaptArrowAtBoxChanges(modelBox);
	}

	private void adaptBoxName(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
		if (changedBox != null) {
			changedBox.setTopText(modelBox.getName());
			modelBox.setWidth(changedBox.getMinWidth());
		}
	}

	private void adaptBoxWidth(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
		if (changedBox != null) {
			changedBox.setWidth(modelBox.getWidth());
		}
		if (modelBox instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject instance : modelClass.getModelObjects()) {
				instance.setWidth(modelClass.getWidth());
			}
		}
		// adaptArrowAtBoxChanges(modelBox);
	}

	private void adaptBoxHeight(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
		if (changedBox != null) {
			changedBox.setHeight(modelBox.getHeight());
		}
		if (modelBox instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject instance : modelClass.getModelObjects()) {
				instance.setHeight(modelClass.getHeight());
			}
		}
		// adaptArrowAtBoxChanges(modelBox);
	}

	private void adaptBoxColor(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
		if (changedBox != null) {
			changedBox.setColor(modelBox.getColor());
		}
		if (modelBox instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject instance : modelClass.getModelObjects()) {
				instance.setColor(Util.brighter(modelClass.getColor(), 0.1));
			}
		}
	}

	private void adaptBoxCoordinates(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
		if (changedBox != null) {
			changedBox.setTranslateXYZ(modelBox.getCoordinates());
		}
		adaptArrowAtBoxChanges(modelBox);
		if (modelBox instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject instance : modelClass.getModelObjects()) {
				instance.setX(modelClass.getX());
				instance.setZ(modelClass.getZ());
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO
		if (o instanceof ModelManager && arg instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) arg;
			if (!this.boxes.containsKey(modelClass)) { // class is new
				addClassToSubScene(modelClass);
				adaptBoxSettings(modelClass);
			} else {
				PaneBox toDelete = this.boxes.remove(modelClass);
				removeFromSubScene(toDelete.get());
				removeFromSubScene(toDelete.getSelection());
			}
		} else if (o instanceof ModelManager && arg instanceof Relation) {
			Relation relation = (Relation) arg;
			if (!this.arrows.containsKey(relation)) { // relation is new
				addRelationToSubScene(relation);
				// adaptRelation(relation);
			} else {
				ArrowLine toDelete = this.arrows.remove(relation);
				removeFromSubScene(toDelete);
			}
		} else if (o instanceof ModelManager && arg instanceof ModelObject) {
			ModelObject instance = (ModelObject) arg;
			if (!this.boxes.containsKey(instance)) { // instance is new
				addObjectToSubScene(instance);
				adaptBoxSettings(instance);
			} else {
				PaneBox toDelete = this.boxes.remove(instance);
				removeFromSubScene(toDelete.get());
				removeFromSubScene(toDelete.getSelection());
			}
		} else if (o instanceof ModelBox && arg instanceof ModelBoxChange) {
			ModelBox modelBox = (ModelBox) o;
			ModelBoxChange modelBoxChange = (ModelBoxChange) arg;
			switch (modelBoxChange) {
			case COLOR:
				adaptBoxColor(modelBox);
				break;
			case COORDINATES:
				adaptBoxCoordinates(modelBox);
				break;
			case HEIGHT:
				adaptBoxHeight(modelBox);
				break;
			case NAME:
				adaptBoxName(modelBox);
				break;
			case WIDTH:
				adaptBoxWidth(modelBox);
				break;
			default:
				break;
			}
		}
	}

}
