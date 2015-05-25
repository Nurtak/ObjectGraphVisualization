package ch.hsr.ogv;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.controller.CameraController;
import ch.hsr.ogv.controller.ContextMenuController;
import ch.hsr.ogv.controller.DragMoveController;
import ch.hsr.ogv.controller.DragResizeController;
import ch.hsr.ogv.controller.ModelViewConnector;
import ch.hsr.ogv.controller.MouseMoveController;
import ch.hsr.ogv.controller.ObjectGraph;
import ch.hsr.ogv.controller.RelationCreationController;
import ch.hsr.ogv.controller.ViewController;
import ch.hsr.ogv.controller.SelectionController;
import ch.hsr.ogv.controller.ModelController;
import ch.hsr.ogv.controller.TextFieldController;
import ch.hsr.ogv.dataaccess.Persistancy;
import ch.hsr.ogv.dataaccess.UserPreferences;
import ch.hsr.ogv.util.FXMLResourceUtil;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 *
 * @author Simon Gwerder
 *
 */
public class StageBuilder {

	private final static Logger logger = LoggerFactory.getLogger(StageBuilder.class);

	private static final int MIN_WIDTH = 1024;
	private static final int MIN_HEIGHT = 768;
	
	private String appTitle = "Object Graph Visualizer";
	private Stage primaryStage;
	private BorderPane rootLayout;
	private SubSceneAdapter subSceneAdapter;

	private ModelViewConnector mvConnector;
	private ObjectGraph objectGraph;
	private Persistancy persistancy;

	private ViewController rootLayoutController = new ViewController();
	private SelectionController selectionController = new SelectionController();
	private ContextMenuController contextMenuController = new ContextMenuController();
	private TextFieldController textFieldController = new TextFieldController();
	private MouseMoveController mouseMoveController = new MouseMoveController();
	private CameraController cameraController = new CameraController();
	private DragMoveController dragMoveController = new DragMoveController();
	private DragResizeController dragResizeController = new DragResizeController();
	private RelationCreationController relationCreationController = new RelationCreationController();
	
	private ModelController stageManager = new ModelController();

	public StageBuilder(Stage primaryStage) {
		if (primaryStage == null) {
			throw new IllegalArgumentException("The primaryStage argument can not be null!");
		}
		this.primaryStage = primaryStage;

		loadRootLayoutController();
		setupStage();

		initMVConnector();
		initObjectGraph();
		initPersistancy();

		initRootLayoutController();
		initSelectionController();
		initContextMenuController();
		initMouseMoveController();
		initCameraController();
		initDragController();
		initRelationCreationController();
		
		initStageManager();

		this.selectionController.setSelected(this.subSceneAdapter, true, this.subSceneAdapter);
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
	}

	private void initObjectGraph() {
		this.objectGraph = new ObjectGraph(this.mvConnector, this.subSceneAdapter);
	}

	private void initPersistancy() {
		UserPreferences.setOGVFilePath(null); // reset user preferences of file path
		persistancy = new Persistancy(this.mvConnector.getModelManager());
	}

	private void initRootLayoutController() {
		this.rootLayoutController.setPrimaryStage(this.primaryStage);
		this.rootLayoutController.setSubSceneAdapter(this.subSceneAdapter);
		this.rootLayoutController.setMVConnector(this.mvConnector);
		this.rootLayoutController.setObjectGraph(this.objectGraph);
		this.rootLayoutController.setPersistancy(this.persistancy);
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
		this.cameraController.enableCamera(this.subSceneAdapter);
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

	private void initStageManager() {
		this.stageManager.setRootLayout(this.rootLayout);
		this.stageManager.setSubSceneAdapter(this.subSceneAdapter);
		this.stageManager.setMVConnector(this.mvConnector);
		this.stageManager.setSelectionController(this.selectionController);
		
		this.stageManager.setContextMenuController(this.contextMenuController);
		this.stageManager.setTextFieldController(this.textFieldController);
		this.stageManager.setMouseMoveController(this.mouseMoveController);
		this.stageManager.setDragMoveController(this.dragMoveController);
		this.stageManager.setDragResizeController(this.dragResizeController);
	}
	
}
