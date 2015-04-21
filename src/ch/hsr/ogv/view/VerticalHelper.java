package ch.hsr.ogv.view;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class VerticalHelper extends Group {
	
	public final static double DEPTH = 1000;
	
	private PaneBox basePaneBox;
	private Box verticalHelperBox;
	
	public final static Color DEFAULT_COLOR = Color.DODGERBLUE;
	
	public VerticalHelper(PaneBox basePaneBox) {
		this.basePaneBox = basePaneBox;
		this.verticalHelperBox = new Box(this.basePaneBox.getWidth(), DEPTH, this.basePaneBox.getHeight());
		//this.verticalHelper.setTranslateY(DEPTH / 2);
		this.verticalHelperBox.translateXProperty().bind(this.basePaneBox.get().translateXProperty());
		this.verticalHelperBox.translateYProperty().bind(this.basePaneBox.get().translateYProperty());
		this.verticalHelperBox.translateZProperty().bind(this.basePaneBox.get().translateZProperty());
		this.verticalHelperBox.widthProperty().bind(this.basePaneBox.getBox().widthProperty());
		this.verticalHelperBox.depthProperty().bind(this.basePaneBox.getBox().heightProperty());
		//this.verticalHelper.setMouseTransparent(false);
		this.verticalHelperBox.setOpacity(0.0);
		
		Rectangle northRectangle = new Rectangle(basePaneBox.getWidth(), DEPTH, DEFAULT_COLOR);
		northRectangle.setOpacity(0.1);
		northRectangle.setMouseTransparent(true);
		northRectangle.translateXProperty().bind(this.basePaneBox.get().translateXProperty().subtract(basePaneBox.getBox().widthProperty().divide(2)));
		northRectangle.translateYProperty().bind(this.basePaneBox.get().translateYProperty().subtract(DEPTH / 2));
		northRectangle.translateZProperty().bind(this.basePaneBox.get().translateZProperty().add(basePaneBox.getBox().heightProperty().divide(2)));
		northRectangle.widthProperty().bind(this.basePaneBox.getBox().widthProperty());
		
		Rectangle southRectangle = new Rectangle(basePaneBox.getWidth(), DEPTH, DEFAULT_COLOR);
		southRectangle.setOpacity(0.1);
		northRectangle.setMouseTransparent(true);
		southRectangle.translateXProperty().bind(this.basePaneBox.get().translateXProperty().subtract(basePaneBox.getBox().widthProperty().divide(2)));
		southRectangle.translateYProperty().bind(this.basePaneBox.get().translateYProperty().subtract(DEPTH / 2));
		southRectangle.translateZProperty().bind(this.basePaneBox.get().translateZProperty().subtract(basePaneBox.getBox().heightProperty().divide(2)));
		southRectangle.widthProperty().bind(this.basePaneBox.getBox().widthProperty());
		
		Rectangle eastRectangle = new Rectangle(basePaneBox.getHeight(), DEPTH, DEFAULT_COLOR);
		eastRectangle.setOpacity(0.1);
		eastRectangle.setMouseTransparent(true);
		eastRectangle.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
		eastRectangle.translateXProperty().bind(this.basePaneBox.get().translateXProperty().add(basePaneBox.getBox().widthProperty().divide(2)));
		eastRectangle.translateYProperty().bind(this.basePaneBox.get().translateYProperty().subtract(DEPTH / 2));
		eastRectangle.translateZProperty().bind(this.basePaneBox.get().translateZProperty().add(basePaneBox.getBox().heightProperty().divide(2)));
		eastRectangle.widthProperty().bind(this.basePaneBox.getBox().heightProperty());
			
		Rectangle westRectangle = new Rectangle(basePaneBox.getHeight(), DEPTH, DEFAULT_COLOR);
		westRectangle.setOpacity(0.1);
		westRectangle.setMouseTransparent(true);
		westRectangle.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
		westRectangle.translateXProperty().bind(this.basePaneBox.get().translateXProperty().subtract(basePaneBox.getBox().widthProperty().divide(2)));
		westRectangle.translateYProperty().bind(this.basePaneBox.get().translateYProperty().subtract(DEPTH / 2));
		westRectangle.translateZProperty().bind(this.basePaneBox.get().translateZProperty().add(basePaneBox.getBox().heightProperty().divide(2)));
		westRectangle.widthProperty().bind(this.basePaneBox.getBox().heightProperty());
		
		this.getChildren().addAll(verticalHelperBox, northRectangle, southRectangle, eastRectangle, westRectangle);
	}
	
	public boolean isBasePaneBox(PaneBox basePaneBox) {
		return this.basePaneBox.equals(basePaneBox);
	}
	
	public boolean isVerticalHelper(PaneBox paneBox, Node node) {
		if (node == null || !(node instanceof Box))
			return false;
		return isBasePaneBox(paneBox);
	}

}
