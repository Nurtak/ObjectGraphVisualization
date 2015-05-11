package ch.hsr.ogv.view;

import java.util.ArrayList;
import java.util.HashSet;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import ch.hsr.ogv.model.ModelClass;

public class VerticalHelper extends Group {

	private HashSet<ArrayList<Rectangle>> helperAreas = new HashSet<ArrayList<Rectangle>>();
	public final static double DEPTH_SIZE = 2000;
	public final static double DEPTH_DIMENSION = 3;
	public final static double OPACITY = 0.1;

	private final static double PLUS_SIZE = 2;

	public final static Color DEFAULT_COLOR = Color.DODGERBLUE;

	public VerticalHelper() {
		for (int i = 0; i < DEPTH_DIMENSION; i++) {
			buildHelperArea(i);
		}

		for (ArrayList<Rectangle> oneDimension : this.helperAreas) {
			getChildren().addAll(oneDimension);
		}
		setMouseTransparent(true);
		setVisible(false);
	}

	private void buildHelperArea(int depthDimension) {
		Rectangle northRectangle = new Rectangle(PLUS_SIZE, DEPTH_SIZE, DEFAULT_COLOR);
		Rectangle southRectangle = new Rectangle(PLUS_SIZE, DEPTH_SIZE, DEFAULT_COLOR);
		Rectangle eastRectangle = new Rectangle(PLUS_SIZE, DEPTH_SIZE, DEFAULT_COLOR);
		Rectangle westRectangle = new Rectangle(PLUS_SIZE, DEPTH_SIZE, DEFAULT_COLOR);

		northRectangle.setOpacity(OPACITY);
		northRectangle.setTranslateY(ModelClass.OBJECT_LEVEL_DIFF + depthDimension * DEPTH_SIZE);

		southRectangle.setOpacity(OPACITY);
		southRectangle.setTranslateY(ModelClass.OBJECT_LEVEL_DIFF + depthDimension * DEPTH_SIZE);

		eastRectangle.setOpacity(OPACITY);
		eastRectangle.setTranslateY(ModelClass.OBJECT_LEVEL_DIFF + depthDimension * DEPTH_SIZE);
		eastRectangle.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));

		westRectangle.setOpacity(OPACITY);
		westRectangle.setTranslateY(ModelClass.OBJECT_LEVEL_DIFF + depthDimension * DEPTH_SIZE);
		westRectangle.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));

		ArrayList<Rectangle> oneDimension = new ArrayList<Rectangle>();
		oneDimension.add(northRectangle);
		oneDimension.add(southRectangle);
		oneDimension.add(eastRectangle);
		oneDimension.add(westRectangle);
		this.helperAreas.add(oneDimension);
	}

	public void setDimension(PaneBox paneBox) {
		setDimension(paneBox.getTranslateX(), paneBox.getTranslateZ(), paneBox.getWidth(), paneBox.getHeight());
	}

	public void setDimension(double x, double z, double width, double height) {
		for (ArrayList<Rectangle> oneDimension : this.helperAreas) {
			Rectangle northRectangle = oneDimension.get(0);
			Rectangle southRectangle = oneDimension.get(1);
			Rectangle eastRectangle = oneDimension.get(2);
			Rectangle westRectangle = oneDimension.get(3);
			
			northRectangle.setWidth(width + PLUS_SIZE);
			northRectangle.setTranslateX(x - width / 2 - PLUS_SIZE / 2);
			northRectangle.setTranslateZ(z + height / 2 + PLUS_SIZE / 2);

			southRectangle.setWidth(width + PLUS_SIZE);
			southRectangle.setTranslateX(x - width / 2 - PLUS_SIZE / 2);
			southRectangle.setTranslateZ(z - height / 2 - PLUS_SIZE / 2);

			eastRectangle.setWidth(height + PLUS_SIZE);
			eastRectangle.setTranslateX(x + width / 2 + PLUS_SIZE / 2);
			eastRectangle.setTranslateZ(z + height / 2 + PLUS_SIZE / 2);

			westRectangle.setWidth(height + PLUS_SIZE);
			westRectangle.setTranslateX(x - width / 2 - PLUS_SIZE / 2);
			westRectangle.setTranslateZ(z + height / 2 + PLUS_SIZE / 2);
		}
	}

	private boolean isVerticalHelper(Rectangle rectangle) {
		HashSet<Rectangle> allRectangles = new HashSet<Rectangle>();
		for (ArrayList<Rectangle> oneDimension : this.helperAreas) {
			allRectangles.addAll(oneDimension);
		}
		if (allRectangles.contains(rectangle)) {
			return true;
		}
		return false;
	}

	public boolean isVerticalHelper(Node node) {
		if (node == null || !(node instanceof Rectangle)) {
			return false;
		}
		return isVerticalHelper((Rectangle) node);
	}

}
