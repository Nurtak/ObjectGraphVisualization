package ch.hsr.ogv.view;

import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

public class SubSceneBuilder {
	
	private static final Color BACKGROUND = Color.DARKGRAY;
	
	private SubScene subScene = null;

	public SubScene getSubScene() {
		return subScene;
	}

	private final Group root = new Group();
    private final Xform world = new Xform();
    
    public SubSceneBuilder(double initWidth, double initHeight) {
    	this.subScene = new SubScene(this.root, initWidth, initHeight, true, SceneAntialiasing.BALANCED);
        this.subScene.setFill(BACKGROUND);
        
        new Axis(this.world);
    	new SubSceneCamera(this.root, subScene);
    	
    	root.getChildren().add(world);
    }
	
}
