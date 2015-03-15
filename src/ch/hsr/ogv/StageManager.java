package ch.hsr.ogv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class StageManager extends Observable {
	
private final static Logger logger = LoggerFactory.getLogger(StageManager.class);
	
	private String appTitle = "Object Graph Visualizer";
	private Stage primaryStage;
	private BorderPane rootLayout;
	private SubSceneAdapter subSceneAdpater;
	private List<PaneBox> classes = new ArrayList<PaneBox>();

	public List<PaneBox> getClasses() {
		return classes;
	}

	private static final int MIN_WIDTH = 1024;
	private static final int MIN_HEIGHT = 768;
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public BorderPane getRootLayout() {
		return rootLayout;
	}
	
	public SubSceneAdapter getSubSceneAdpater() {
		return subSceneAdpater;
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
		setupStage();
	}
	
	private void setupStage() {
		this.setAppTitle(this.appTitle);
        this.primaryStage.setMinWidth(MIN_WIDTH);
        this.primaryStage.setMinHeight(MIN_HEIGHT);
        
        this.primaryStage.getIcons().add(new Image(ResourceLocator.getResourcePath(Resource.ICON_PNG).toExternalForm())); // set the application icon
        
        initRootLayout();
        setLightTheme();
        
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
        notifyObservers(this);
        
        //TODO: Remove everything below this line:
        
        Arrow arrow = new Arrow();
        addToSubScene(arrow);
        
        arrow.setTranslateX(20);
        arrow.setTranslateZ(60);
                
	    PaneBox paneBox = new PaneBox(Color.ALICEBLUE);
	    addClassToSubScene(paneBox);
	    
	    PaneBox paneBox1 = new PaneBox(Color.AQUA);
	    addClassToSubScene(paneBox1);
	    paneBox1.setTranslateZ(222);
	    
	    PaneBox paneBox2 = new PaneBox(Color.CHARTREUSE);
	    addClassToSubScene(paneBox2);
	    paneBox2.setTranslateX(360);
	}
	
	/**
	 * Adds node to the subscene of the primary stage.
	 * @param node node.
	 */
	private void addToSubScene(Node node) {
		this.subSceneAdpater.add(node);
		this.rootLayout.applyCss();
	}
	
	public void addClassToSubScene(PaneBox classBox) {
		this.classes.add(classBox);
		setChanged();
		notifyObservers(classBox);
		addToSubScene(classBox.get());
		addToSubScene(classBox.getSelection().get());
	}
	
	public void setLightTheme() {
		String lightTheme = ResourceLocator.getResourcePath(Resource.LIGHTHEME_CSS).toExternalForm();
		this.rootLayout.getStylesheets().clear();
		this.rootLayout.getStylesheets().add(lightTheme);
		this.rootLayout.applyCss();
	}

	public void setDarkTheme() {
		String darkTheme = ResourceLocator.getResourcePath(Resource.DARKTHEME_CSS).toExternalForm();
		this.rootLayout.getStylesheets().clear();
		this.rootLayout.getStylesheets().add(darkTheme);
		this.rootLayout.applyCss();
	}
	
	/**
     * Initializes the root layout.
     */
    private void initRootLayout() {
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
	
}
