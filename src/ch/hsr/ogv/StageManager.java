package ch.hsr.ogv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.controller.RootLayoutController;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.PaneBox3D;
import ch.hsr.ogv.view.SubScene3D;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StageManager {
	
private final static Logger logger = LoggerFactory.getLogger(StageManager.class);
	
	private String appTitle = "Object Graph Visualizer";
	private Stage primaryStage;
	private BorderPane rootLayout;
	private RootLayoutController rootLayoutController;
	private SubScene3D subScene3D;
	private List<PaneBox3D> classes = new ArrayList<PaneBox3D>();

	public List<PaneBox3D> getClasses() {
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
	
	public SubScene3D getSubScene3D() {
		return subScene3D;
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
        
        this.subScene3D = new SubScene3D(canvas.getWidth(), canvas.getHeight());
        SubScene subScene = this.subScene3D.getSubScene();
        
        canvas.getChildren().add(subScene);
        subScene.heightProperty().bind(canvas.heightProperty());
        subScene.widthProperty().bind(canvas.widthProperty());
        
        Scene scene = new Scene(this.rootLayout);

        this.rootLayoutController.initController(this);
        
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
        
        this.subScene3D.getSubScene().requestFocus();
        
        //TODO: Remove test paneBox3D
	    PaneBox3D paneBox3D = new PaneBox3D(Color.ALICEBLUE);
	    showClassInSubScene(paneBox3D);
	}
	
	/**
	 * Adds node to the subscene of the primary stage.
	 * @param node node.
	 */
	public void showInSubScene(Node node) {
		this.subScene3D.getWorld().getChildren().add(node);
		this.rootLayout.applyCss();
	}
	
	public void showClassInSubScene(PaneBox3D classBox) {
		this.classes.add(classBox);
		this.rootLayoutController.addPaneBox3DControls(classBox);
		showInSubScene(classBox.getNode());
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
            this.rootLayoutController = loader.getController();
        } catch (IOException e) {
        	logger.debug(e.getMessage());
            e.printStackTrace();
        }
    }
	
}
