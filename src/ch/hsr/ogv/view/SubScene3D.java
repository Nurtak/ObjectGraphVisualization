package ch.hsr.ogv.view;

import ch.hsr.ogv.controller.CameraController;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

public class SubScene3D {
	
	private static final Color BACKGROUND = Color.WHITE;
	
	private SubScene subScene = null;

	public SubScene getSubScene() {
		return subScene;
	}

	private final Group root = new Group();
    private final Xform world = new Xform();
    
    public SubScene3D(double initWidth, double initHeight) {
    	// create a new subscene that resides in the root group
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
        cameraController.handleMouse(root, subScene, ssCamera);
        cameraController.handleKeyboard(root, subScene, ssCamera);
        
        //TODO just for tests
        Object3D object3D = new Object3D();
        object3D.setColor(Color.GOLD);
        object3D.setSize(50, 50, 10);
        object3D.setPosition(100, 200, 50);
        world.getChildren().add(object3D.getBox());
    	
    	// populate the root group with the world objects
    	root.getChildren().add(world);
    }
	
}
