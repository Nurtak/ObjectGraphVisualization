package ch.hsr.ogv.view;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import ch.hsr.ogv.model.ModelClass;

public class VerticalHelper extends Group {

	public final static double DEPTH = 5000;
	public final static double OPACITY = 0.1;

	private final static double PLUS_SIZE = 2;

	private PaneBox basePaneBox;

	public PaneBox getBasePaneBox() {
		return basePaneBox;
	}

	private Rectangle northRectangle = new Rectangle(PLUS_SIZE, DEPTH, DEFAULT_COLOR);
	private Rectangle southRectangle = new Rectangle(PLUS_SIZE, DEPTH, DEFAULT_COLOR);
	private Rectangle eastRectangle = new Rectangle(PLUS_SIZE, DEPTH, DEFAULT_COLOR);
	private Rectangle westRectangle = new Rectangle(PLUS_SIZE, DEPTH, DEFAULT_COLOR);

	public final static Color DEFAULT_COLOR = Color.DODGERBLUE;

	public VerticalHelper() {
		this.northRectangle.setOpacity(OPACITY);
		this.northRectangle.setTranslateY(ModelClass.OBJECT_LEVEL_DIFF);

		this.southRectangle.setOpacity(OPACITY);
		this.southRectangle.setTranslateY(ModelClass.OBJECT_LEVEL_DIFF);

		this.eastRectangle.setOpacity(OPACITY);
		this.eastRectangle.setTranslateY(ModelClass.OBJECT_LEVEL_DIFF);
		this.eastRectangle.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));

		this.westRectangle.setOpacity(OPACITY);
		this.westRectangle.setTranslateY(ModelClass.OBJECT_LEVEL_DIFF);
		this.westRectangle.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));

		getChildren().addAll(this.northRectangle, this.southRectangle, this.eastRectangle, this.westRectangle);
		setMouseTransparent(true);
		setVisible(false);
	}

	public void setBasePaneBox(PaneBox paneBox) {
		this.basePaneBox = paneBox;
		if (paneBox == null) {
			return;
		}
		this.northRectangle.setWidth(paneBox.getWidth() + PLUS_SIZE);
		this.northRectangle.setTranslateX(paneBox.getTranslateX() - paneBox.getWidth() / 2 - PLUS_SIZE / 2);
		this.northRectangle.setTranslateZ(paneBox.getTranslateZ() + paneBox.getHeight() / 2 + PLUS_SIZE / 2);

		this.southRectangle.setWidth(paneBox.getWidth() + PLUS_SIZE);
		this.southRectangle.setTranslateX(paneBox.getTranslateX() - paneBox.getWidth() / 2 - PLUS_SIZE / 2);
		this.southRectangle.setTranslateZ(paneBox.getTranslateZ() - paneBox.getHeight() / 2 - PLUS_SIZE / 2);

		this.eastRectangle.setWidth(paneBox.getHeight() + PLUS_SIZE);
		this.eastRectangle.setTranslateX(paneBox.getTranslateX() + paneBox.getWidth() / 2 + PLUS_SIZE / 2);
		this.eastRectangle.setTranslateZ(paneBox.getTranslateZ() + paneBox.getHeight() / 2 + PLUS_SIZE / 2);

		this.westRectangle.setWidth(paneBox.getHeight() + PLUS_SIZE);
		this.westRectangle.setTranslateX(paneBox.getTranslateX() - paneBox.getWidth() / 2 - PLUS_SIZE / 2);
		this.westRectangle.setTranslateZ(paneBox.getTranslateZ() + paneBox.getHeight() / 2 + PLUS_SIZE / 2);
	}

	private boolean isVerticalHelper(Rectangle rectangle) {
		if (this.northRectangle.equals(rectangle)) {
			return true;
		}
		if (this.southRectangle.equals(rectangle)) {
			return true;
		}
		if (this.eastRectangle.equals(rectangle)) {
			return true;
		}
		if (this.westRectangle.equals(rectangle)) {
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
