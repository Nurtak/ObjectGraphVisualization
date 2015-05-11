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
	private VerticalHelper verticalHelper;

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

	public VerticalHelper getVerticalHelper() {
		return this.verticalHelper;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		if (this.subScene != null)
			this.subScene.setFill(color);
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

		this.verticalHelper = new VerticalHelper();
		this.world.getChildren().add(this.verticalHelper);

		// add a camera for the subscene
		this.subSceneCamera = new SubSceneCamera();
		this.root.getChildren().add(this.subSceneCamera.getCameraXform());
		this.subScene.setCamera(this.subSceneCamera.get());

		// populate the root group with the world objects
		this.root.getChildren().add(world);
	}

	public void receiveMouseEvents(Node... nodes) {
		for (Node n : nodes) {
			n.setMouseTransparent(false);
		}
	}

	public void worldReceiveMouseEvents() {
		receiveMouseEvents(world.getChildren().toArray(new Node[world.getChildren().size()]));
	}

	public void restrictMouseEvents(Node... nodes) {
		for (Node n : nodes) {
			n.setMouseTransparent(true);
		}
	}

	public void worldRestrictMouseEvents() {
		restrictMouseEvents(world.getChildren().toArray(new Node[world.getChildren().size()]));
	}

	public boolean add(Node node) {
		boolean retAdd = this.world.getChildren().add(node);
		this.floor.toFront();
		this.verticalHelper.toFront();
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
