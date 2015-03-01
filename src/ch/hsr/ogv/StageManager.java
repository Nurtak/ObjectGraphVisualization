package ch.hsr.ogv;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.controller.RootLayoutController;
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
	
	private String appTitle = "Object Graph Visualization";
	private Stage primaryStage;
	private BorderPane rootLayout;

	private static final int MIN_WIDTH = 1024;
	private static final int MIN_HEIGHT = 768;
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	private SubScene3D subScene3D;
		
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

        this.primaryStage.getIcons().add(new Image("file:resources/images/dummy_icon.png")); // set the application icon
        
        initRootLayout();
        
        Pane canvas = (Pane) this.rootLayout.getCenter();
        
        this.subScene3D = new SubScene3D(canvas.getWidth(), canvas.getHeight());
        SubScene subScene = this.subScene3D.getSubScene();
        
        canvas.getChildren().add(subScene);
        subScene.heightProperty().bind(canvas.heightProperty());
        subScene.widthProperty().bind(canvas.widthProperty());
        
        Scene scene = new Scene(this.rootLayout);

        this.primaryStage.setScene(scene);
        this.primaryStage.show();
        
        //TODO: Remove test paneBox3D
	    PaneBox3D paneBox3D = new PaneBox3D(Color.ALICEBLUE);
	    showInSubScene(paneBox3D.getNode());
	}
	
	/**
	 * Adds node to the subscene of the primary stage.
	 * @param node node.
	 */
	public void showInSubScene(Node node) {
		this.subScene3D.getWorld().getChildren().add(node);
		this.rootLayout.applyCss();
	}

	/**
     * Initializes the root layout.
     */
    private void initRootLayout() {
    	FXMLLoader loader = new FXMLLoader(); // load rootlayout from fxml file
        loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
    	try {
            this.rootLayout = (BorderPane) loader.load();
            RootLayoutController controller = loader.getController();
            controller.setStageManager(this);
        } catch (IOException e) {
        	logger.debug(e.getMessage());
            e.printStackTrace();
        }
    }
	
}
