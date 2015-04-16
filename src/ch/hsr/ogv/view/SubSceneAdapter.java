package ch.hsr.ogv.view;

import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class SubSceneAdapter implements Selectable {
	
	public final static Color DEFAULT_COLOR = Color.LIGHTCYAN;
	private Color color = DEFAULT_COLOR;
	
	private SubScene subScene;
	private SubSceneCamera subSceneCamera;
	private Axis axis;
	private Floor floor;
	
	private volatile boolean selected = false;

	private final Group root = new Group();

	private final Xform world = new Xform();
    
	public SubScene getSubScene() {
		return this.subScene;
	}
    
	public SubSceneCamera getSubSceneCamera() {
		return this.subSceneCamera;
	}
	
	public Axis getAxis() {
		return this.axis;
	}
	
	public Floor getFloor() {
		return this.floor;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if(this.subScene != null) this.subScene.setFill(color);
		this.color = color;
	}

	public SubSceneAdapter(double initWidth, double initHeight) {
    	// create a new subscene that resides in the root group
    	this.root.setDepthTest(DepthTest.ENABLE);
    	this.subScene = new SubScene(this.root, initWidth, initHeight, true, SceneAntialiasing.BALANCED);
        this.subScene.setFill(color);
                
        // create axis and add them to the world Xform
        this.axis = new Axis();
        this.world.getChildren().add(axis);
        
        // create ground floor and add it to the world Xform
        this.floor = new Floor();
        this.world.getChildren().add(floor);

        // add a camera for the subscene
        this.subSceneCamera = new SubSceneCamera();
        this.root.getChildren().add(this.subSceneCamera.getCameraXform());
        this.subScene.setCamera(this.subSceneCamera.get());
        
    	// populate the root group with the world objects
    	this.root.getChildren().add(world);
    }
	
	/**
	 * If set to true, only the specified nodes will receive Mouse Events and every other added
	 * Node will have MouseTransparent disabled. Reversed if false. The specified nodes can be
	 * omitted to allow mouse events for all respectively no nodes in this scene.
	 * @param value
	 */
	public void receiveMouseEvents(boolean value, Node... nodes) {
		for(Node n: world.getChildren()) {
			n.setMouseTransparent(value);
		}
		for(Node n : nodes) {
			n.setMouseTransparent(!value);
		}
	}
	
	public boolean add(Node node) {
		boolean retAdd = this.world.getChildren().add(node);
		this.floor.toFront();
		return retAdd;
	}
	
	public boolean remove(Node node) {
		return this.world.getChildren().remove(node);
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public Group getSelection() {
		return null; // SubScene has no real (visible) selection
	}

	@Override
	public void requestFocus() {
		this.subScene.requestFocus();
	}
	
}
