package ch.hsr.ogv.view;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Box;

public class VerticalHelper extends Group {
	
	public final static double DEPTH = 1000;
	
	private PaneBox basePaneBox;
	private Box verticalHelper;
	
	public VerticalHelper(PaneBox basePaneBox) {
		this.basePaneBox = basePaneBox;
		this.verticalHelper = new Box(this.basePaneBox.getWidth(), DEPTH, this.basePaneBox.getHeight());
		//this.verticalHelper.setTranslateY(DEPTH / 2);
		this.verticalHelper.translateXProperty().bind(this.basePaneBox.get().translateXProperty());
		this.verticalHelper.translateYProperty().bind(this.basePaneBox.get().translateYProperty());
		this.verticalHelper.translateZProperty().bind(this.basePaneBox.get().translateZProperty());
		this.verticalHelper.widthProperty().bind(this.basePaneBox.getBox().widthProperty().subtract(1));
		this.verticalHelper.depthProperty().bind(this.basePaneBox.getBox().heightProperty().subtract(1));
		//this.verticalHelper.setMouseTransparent(false);
		//this.verticalHelper.setOpacity(0.0);
		this.getChildren().add(verticalHelper);
		
		
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
