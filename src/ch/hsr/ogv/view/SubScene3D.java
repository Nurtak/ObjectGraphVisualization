package ch.hsr.ogv.view;


import ch.hsr.ogv.controller.CameraController;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

public class SubScene3D {
	
	private static final Color BACKGROUND = Color.WHITESMOKE;
	
	private SubScene subScene = null;
	private final Group root = new Group();

	private final Xform world = new Xform();
    
	public SubScene getSubScene() {
		return subScene;
	}
    
    public Xform getWorld() {
		return world;
	}
    
    public Group getRoot() {
		return root;
	}

	public SubScene3D(double initWidth, double initHeight) {
    	// create a new subscene that resides in the root group
    	this.root.setDepthTest(DepthTest.ENABLE);
    	this.subScene = new SubScene(this.root, initWidth, initHeight, true, SceneAntialiasing.BALANCED);
        this.subScene.setFill(BACKGROUND);
        
        // create axis and add them to the world Xform
        Axis axis = new Axis();
        world.getChildren().add(axis.getAxisGroup());

        // add a camera for the subscene
    	SubSceneCamera ssCamera = new SubSceneCamera();
    	root.getChildren().add(ssCamera.getCameraXform());
        subScene.setCamera(ssCamera.getPerspectiveCamera());
        
        // add a camera controller
        CameraController cameraController = new CameraController();
        cameraController.handleMouse(this.root, subScene, ssCamera);
        cameraController.handleKeyboard(this.root, subScene, ssCamera);
        
    	// populate the root group with the world objects
    	this.root.getChildren().add(world);
    }
	
}
