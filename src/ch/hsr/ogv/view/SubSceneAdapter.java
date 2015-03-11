package ch.hsr.ogv.view;

import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class SubSceneAdapter {
	
	private static final Color BACKGROUND = Color.WHITESMOKE;
	
	private SubScene subScene;
	private SubSceneCamera subSceneCamera;
	private Axis axis;

	private final Group root = new Group();

	private final Xform world = new Xform();
    
	public SubScene getSubScene() {
		return this.subScene;
	}
    
    public Xform getWorld() {
		return this.world;
	}
        
	public SubSceneCamera getSubSceneCamera() {
		return this.subSceneCamera;
	}
	
	public Axis getAxis() {
		return this.axis;
	}

	public SubSceneAdapter(double initWidth, double initHeight) {
    	// create a new subscene that resides in the root group
    	this.root.setDepthTest(DepthTest.ENABLE);
    	this.subScene = new SubScene(this.root, initWidth, initHeight, true, SceneAntialiasing.BALANCED);
        this.subScene.setFill(BACKGROUND);
                
        // create axis and add them to the world Xform
        this.axis = new Axis();
        world.getChildren().add(axis.get());

        // add a camera for the subscene
        this.subSceneCamera = new SubSceneCamera();
    	root.getChildren().add(this.subSceneCamera.getCameraXform());
        subScene.setCamera(this.subSceneCamera.get());
        
    	// populate the root group with the world objects
    	this.root.getChildren().add(world);
    }
	
}
