package ch.hsr.ogv.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

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
import ch.hsr.ogv.util.TextUtil;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.Arrow;
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
	private Map<Relation, Arrow> arrows = new HashMap<Relation, Arrow>();

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
		ModelClass mcA = this.modelManager.createClass(new Point3D(0, PaneBox.INIT_DEPTH / 2, 0), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		mcA.setName("A");		
		ModelClass mcB = this.modelManager.createClass(new Point3D(300, PaneBox.INIT_DEPTH / 2, 300), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		mcB.setName("B");		
		ModelClass mcC = this.modelManager.createClass(new Point3D(400, PaneBox.INIT_DEPTH / 2, -200), PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		mcC.setName("C");
		
		ModelObject moA1 = this.modelManager.createObject(mcA);
		ModelObject moB1 = this.modelManager.createObject(mcB);		
		ModelObject moB2 = this.modelManager.createObject(mcB);
		ModelObject moB3 = this.modelManager.createObject(mcB);
	
		this.modelManager.createRelation(mcA, mcB, RelationType.GENERALIZATION);
		this.modelManager.createRelation(mcC, mcB, RelationType.DIRECTED_AGGREGATION);
		this.modelManager.createRelation(mcC, mcA, RelationType.DIRECTED_COMPOSITION);
		this.modelManager.createRelation(moA1, moB1, RelationType.OBJDIAGRAM);
		this.modelManager.createRelation(moA1, moB2, RelationType.OBJDIAGRAM);
		this.modelManager.createRelation(moA1, moB3, RelationType.OBJDIAGRAM);
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
	}

	public void onlyFloorMouseEvent(boolean value) {
		this.subSceneAdapter.onlyFloorMouseEvent(value);
	}

	public void handleCreateNewClass(Point3D mouseCoords) {
		onlyFloorMouseEvent(false);
		Point3D boxPosition = new Point3D(mouseCoords.getX(), PaneBox.INIT_DEPTH / 2, mouseCoords.getZ());
		ModelClass newClass = this.modelManager.createClass(boxPosition, PaneBox.MIN_WIDTH, PaneBox.MIN_HEIGHT, PaneBox.DEFAULT_COLOR);
		PaneBox newBox = this.boxes.get(newClass);
		if(newBox != null) {
			newBox.allowTopTextInput(true);
		}
	}
	
	public void handleCreateNewObject(PaneBox selectedPaneBox) {
		ModelBox selectedModelBox = this.getModelBoxByPaneBox(selectedPaneBox);
		ModelClass selectedModelClass = (ModelClass) selectedModelBox;
		ModelObject newObject = this.modelManager.createObject(selectedModelClass);
		PaneBox newBox = this.boxes.get(newObject);
		if(newBox != null) {
			newBox.allowTopTextInput(true);
		}
	}

	public void handleCreateNewGeneralization(PaneBox child, PaneBox parent) {
		ModelBox modelBoxChild = this.getModelBoxByPaneBox(child);		
		ModelBox modelBoxParent = this.getModelBoxByPaneBox(parent);
		this.modelManager.createRelation(modelBoxChild, modelBoxParent, RelationType.GENERALIZATION);
	}
	
	public void handleCreateNewDependency(PaneBox dependent, PaneBox supplier) {
		ModelBox modelBoxDependet = this.getModelBoxByPaneBox(dependent);
		ModelBox modelBoxSupplier = this.getModelBoxByPaneBox(supplier);
		this.modelManager.createRelation(modelBoxDependet, modelBoxSupplier, RelationType.DEPENDENCY);
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
				
				if(this.selectionController.isSelected(paneBox)) {
					this.subSceneAdapter.getSubScene().requestFocus();
				}
				
				for (Endpoint endpoint : modelBox.getEndpoints()) {
					Arrow arrow = this.arrows.get(endpoint.getRelation());
					arrow.setVisible(showObjects);
					if(this.selectionController.isSelected(arrow)) {
						this.subSceneAdapter.getSubScene().requestFocus();
					}
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
		addPaneBoxControls(modelObject, paneBox);
		addToSubScene(paneBox.get());
		addToSubScene(paneBox.getSelection());
		this.boxes.put(modelObject, paneBox);
	}
	
	private void addRelationToSubScene(Relation relation) {
		ModelBox startModelBox = relation.getStart().getAppendant();
		ModelBox endModelBox = relation.getEnd().getAppendant();
		PaneBox startViewBox = this.boxes.get(startModelBox);
		PaneBox endViewBox = this.boxes.get(endModelBox);
		if (startViewBox != null && endViewBox != null) {
			Arrow arrow = new Arrow(startViewBox, endViewBox, relation.getType());
			addArrowControls(arrow);
			addToSubScene(arrow);
			addToSubScene(arrow.getSelection());
			this.arrows.put(relation, arrow);
		}
	}

	private void addPaneBoxControls(ModelBox modelBox, PaneBox paneBox) {
		this.selectionController.enableSelection(paneBox, this.subSceneAdapter);
		this.textInputController.enableTextInput(modelBox, paneBox);
		//TODO
		if(modelBox instanceof ModelClass) {
			this.dragMoveController.enableDragMove(modelBox, paneBox, this.subSceneAdapter);
			this.dragResizeController.enableDragResize(modelBox, paneBox, this.subSceneAdapter);
		}
		else if(modelBox instanceof ModelObject) {
			
		}
	}
	
	private void addArrowControls(Arrow arrow) {
		this.selectionController.enableSelection(arrow, this.subSceneAdapter);
	}

	private void adaptBoxSettings(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
		if (changedBox != null) {
			changedBox.setMinWidth(modelBox.getWidth());
			changedBox.setMinHeight(modelBox.getHeight());
		}
		adaptBoxName(modelBox);
		adaptBoxColor(modelBox);
		adaptBoxWidth(modelBox);
		adaptBoxHeight(modelBox);
		adaptBoxCoordinates(modelBox);
	}
	
	private void adaptArrowToBox(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
		Map<Endpoint, Endpoint> endpointMap = modelBox.getFriends();
		for (Endpoint endpoint : endpointMap.keySet()) {
			Endpoint friendEndpoint = endpointMap.get(endpoint);
			PaneBox friendChangedBox = this.boxes.get(friendEndpoint.getAppendant());
			Relation relation = endpoint.getRelation();
			Arrow changedArrow = this.arrows.get(relation);
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

	private void adaptBoxName(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
		if (changedBox != null && modelBox instanceof ModelObject) {
			ModelObject modelObject = (ModelObject) modelBox;
			changedBox.getTopTextField().setText((modelObject.getName()));
			changedBox.getTopLabel().setText(modelObject.getName() + " : " + modelObject.getModelClass().getName());
			modelBox.setWidth(modelObject.getModelClass().getWidth());
		}
		else if (changedBox != null && modelBox instanceof ModelClass) {
			changedBox.setTopText(modelBox.getName());
			
			// + 70px for some additional space to compensate insets, borders etc.
			double newWidth = TextUtil.computeTextWidth(changedBox.getTopFont(), modelBox.getName(), 0.0D) + 70;
			changedBox.setMinWidth(newWidth);
			//changedBox.getTopLabel().setPrefWidth(newWidth);
			//changedBox.getTopTextField().setPrefWidth(newWidth);
			modelBox.setWidth(changedBox.getMinWidth());
			
			ModelClass modelClass = (ModelClass) modelBox;
			for (ModelObject modelObject : modelClass.getModelObjects()) {
				modelObject.setName(modelObject.getName());
			}
		}
	}

	private void adaptBoxWidth(ModelBox modelBox) {
		PaneBox changedBox = this.boxes.get(modelBox);
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
		PaneBox changedBox = this.boxes.get(modelBox);
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
		PaneBox changedBox = this.boxes.get(modelBox);
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
		PaneBox changedBox = this.boxes.get(modelBox);
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

	@Override
	public void update(Observable o, Object arg) {
		// TODO
		if (o instanceof ModelManager && arg instanceof ModelClass) {
			ModelClass modelClass = (ModelClass) arg;
			if (!this.boxes.containsKey(modelClass)) { // class is new
				addClassToSubScene(modelClass);
				adaptBoxSettings(modelClass);
				adaptArrowToBox(modelClass);
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
				Arrow toDelete = this.arrows.remove(relation);
				removeFromSubScene(toDelete);
			}
		} else if (o instanceof ModelManager && arg instanceof ModelObject) {
			ModelObject modelObject = (ModelObject) arg;
			if (!this.boxes.containsKey(modelObject)) { // instance is new
				addObjectToSubScene(modelObject);
				adaptBoxSettings(modelObject);
				adaptArrowToBox(modelObject);
			} else {
				PaneBox toDelete = this.boxes.remove(modelObject);
				removeFromSubScene(toDelete.get());
				removeFromSubScene(toDelete.getSelection());
			}
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
				adaptBoxName(modelBox);
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
	}

}
