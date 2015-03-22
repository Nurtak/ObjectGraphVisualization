package ch.hsr.ogv.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.controller.ThemeMenuController.Style;
import ch.hsr.ogv.model.Class;
import ch.hsr.ogv.model.ClassManager;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;
import ch.hsr.ogv.view.SubSceneCamera;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
	private SubSceneAdapter subSceneAdpater;
	
	private ClassManager classManager;
	private Map<Class, PaneBox> boxes = new HashMap<Class, PaneBox>();
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
	
	public StageManager(Stage primaryStage) {
		if(primaryStage == null) throw new IllegalArgumentException("The primaryStage argument can not be null!");
		this.primaryStage = primaryStage;
		
		this.classManager = new ClassManager();
		this.classManager.addObserver(this);
		
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
        this.subSceneAdpater = new SubSceneAdapter(canvas.getWidth(), canvas.getHeight());
        SubScene subScene = this.subSceneAdpater.getSubScene();
        canvas.getChildren().add(subScene);
        subScene.heightProperty().bind(canvas.heightProperty());
        subScene.widthProperty().bind(canvas.widthProperty());
        
        Scene scene = new Scene(this.rootLayout);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
        this.subSceneAdpater.getSubScene().requestFocus();
        
        setChanged();
        notifyObservers(this); // pass StageManager to RootLayoutController
        
        //TODO: Remove everything below this line:
        this.classManager.createClass("A", new Point3D(100, 5, 100));
        this.classManager.createClass("B", new Point3D(500, 5, 500));
	    
//        Arrow arrow = new Arrow(paneBoxA, paneBoxB);
//        addArrowToSubScene(arrow);
	}
	
	public void handle2DClassView() {
		SubSceneCamera ssCamera = this.subSceneAdpater.getSubSceneCamera();
		this.cameraController.handle2DClassView(ssCamera);
	}
	
	public void handleSetTheme(CheckMenuItem choosenMenu, Style style) {
		this.themeMenuController.handleSetTheme(this.rootLayout, choosenMenu, style);
	}
	
    private void initRootLayoutController() {
    	FXMLLoader loader = new FXMLLoader(); // load rootlayout from fxml file
        loader.setLocation(ResourceLocator.getResourcePath(Resource.ROOTLAYOUT_FXML));
    	try {
            this.rootLayout = (BorderPane) loader.load();
            addObserver(loader.getController()); // its the RootLayoutController
        } catch (IOException e) {
        	logger.debug(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initCameraController() {
        this.cameraController.handleMouse(this.subSceneAdpater);
        this.cameraController.handleKeyboard(this.subSceneAdpater);
	}
	
	private void initSubSceneController() {
		this.subSceneController.handleMouse(this.subSceneAdpater);
	}
	
	private void initPaneBoxController() {
		this.dragMoveController.addObserver(this.cameraController);
		this.dragResizeController.addObserver(this.cameraController);
		this.selectionController.addObserver(this.dragMoveController);
		this.selectionController.addObserver(this.dragResizeController);
	}
	
	private void addClassToSubScene(Class theClass) {
		PaneBox classBox = new PaneBox();
		classBox.setTopText(theClass.getName());
		classBox.setColor(theClass.getColor());
		classBox.setTranslateXYZ(theClass.getCoordinates());
		addPaneBoxControls(classBox);
		addToSubScene(classBox.get());
		addToSubScene(classBox.getSelection().get());
		this.boxes.put(theClass, classBox);
	}
	
	private void addPaneBoxControls(PaneBox paneBox) {
		this.selectionController.enableSelection(paneBox);
		this.textInputController.enableTextInput(paneBox);
		this.dragMoveController.enableDragMove(paneBox, this.subSceneAdpater);
		this.dragResizeController.enableDragResize(paneBox, this.subSceneAdpater);
	}
	
	private void addArrowToSubScene(Relation relation) {
//		Arrow arrow = new Arrow();
//		this.dragMoveController.addObserver(arrow);
//		this.dragResizeController.addObserver(arrow);
//		addToSubScene(arrow);
//		this.arrows.put(relation, arrow);
	}
	
	/**
	 * Adds node to the subscene of the primary stage.
	 * @param node node.
	 */
	private void addToSubScene(Node node) {
		this.subSceneAdpater.add(node);
		this.rootLayout.applyCss();
	}
	
	/**
	 * Removes node from the subscene of the primary stage.
	 * @param node node.
	 */
	private void removeFromSubScene(Node node) {
		this.subSceneAdpater.remove(node);
		this.rootLayout.applyCss();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof ClassManager && arg instanceof Class) {
			//TODO: Provisorisch
			Class theClass = (Class) arg;
			if(!this.boxes.containsKey(theClass)) { // class is new
				addClassToSubScene(theClass);
			}
			else {
				PaneBox toDelete = this.boxes.remove(theClass);
				removeFromSubScene(toDelete.get());
				removeFromSubScene(toDelete.getSelection().get());
			}
		}
	}
	
}
