package ch.hsr.ogv.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class Selection3D {
	
	private final static int INIT_SELECT_SIZE = 4;
	private final static Color SELECTION_COLOR = Color.DODGERBLUE;
	
	private Group selection3D = new Group();
	private Cuboid3D box; 
	
	private Group pointNE = new Group();
	private Group pointNW = new Group();
	private Group pointSE = new Group();
	private Group pointSW = new Group();
	
	private Group lineN = new Group();
	private Group lineE = new Group();
	private Group lineS = new Group();
	private Group lineW = new Group();
	
	public Group getNode() {
		return selection3D;
	}

	public Group getPointNE() {
		return pointNE;
	}

	public Group getPointNW() {
		return pointNW;
	}

	public Group getPointSE() {
		return pointSE;
	}

	public Group getPointSW() {
		return pointSW;
	}
	
	public Group getLineN() {
		return lineN;
	}

	public Group getLineE() {
		return lineE;
	}

	public Group getLineS() {
		return lineS;
	}

	public Group getLineW() {
		return lineW;
	}

	public Selection3D(PaneBox3D paneBox3D) {
		this.box = paneBox3D.getBox();
		buildSelection();
	}
	
	private void buildSelection() {
		
		for(int i = 0; i < 8; i++) {
			Sphere3D sphere3D = new Sphere3D(SELECTION_COLOR, INIT_SELECT_SIZE);
			
			Cylinder3D cylinderV3D = new Cylinder3D(SELECTION_COLOR, INIT_SELECT_SIZE - 2, 10);
			cylinderV3D.heightProperty().bind(this.box.depthProperty());
			cylinderV3D.translateYProperty().bind(this.box.translateYProperty());
			
			Cylinder3D cylinderH3D = new Cylinder3D(SELECTION_COLOR, INIT_SELECT_SIZE - 2, 10);
			cylinderH3D.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
			
			if(i == 0 || i == 1 || i == 4 || i == 5) {
				sphere3D.translateZProperty().bind(this.box.translateZProperty().subtract(this.box.heightProperty().divide(2)));
				cylinderV3D.translateZProperty().bind(this.box.translateZProperty().subtract(this.box.heightProperty().divide(2)));
				if(i == 0 || i == 1) {
					cylinderH3D.translateZProperty().bind(this.box.translateZProperty().subtract(this.box.heightProperty().divide(2)));
					cylinderH3D.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));
					cylinderH3D.heightProperty().bind(this.box.widthProperty());
					this.lineS.getChildren().add(cylinderH3D.getNode());
					this.pointSE.getChildren().addAll(sphere3D.getNode(), cylinderV3D.getNode());
				}
			}
			
			if(i == 0 || i == 1 || i == 6 || i == 7) {
				sphere3D.translateXProperty().bind(this.box.translateXProperty().subtract(this.box.widthProperty().divide(2)));
				cylinderV3D.translateXProperty().bind(this.box.translateXProperty().subtract(this.box.widthProperty().divide(2)));
				if(i == 6 || i == 7) {
					cylinderH3D.translateXProperty().bind(this.box.translateXProperty().add(this.box.widthProperty().divide(2)));
					cylinderH3D.heightProperty().bind(this.box.heightProperty());
					this.lineW.getChildren().add(cylinderH3D.getNode());
					this.pointNE.getChildren().addAll(sphere3D.getNode(), cylinderV3D.getNode());
				}
			}
			
			if(i == 2 || i == 3 || i == 6 || i == 7) {
				sphere3D.translateZProperty().bind(this.box.translateZProperty().add(this.box.heightProperty().divide(2)));
				cylinderV3D.translateZProperty().bind(this.box.translateZProperty().add(this.box.heightProperty().divide(2)));
				if(i == 2 || i == 3) {
					cylinderH3D.translateXProperty().bind(this.box.translateXProperty());
					cylinderH3D.translateZProperty().bind(this.box.translateZProperty().add(this.box.heightProperty().divide(2)));
					cylinderH3D.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));
					cylinderH3D.heightProperty().bind(this.box.widthProperty());
					this.lineN.getChildren().add(cylinderH3D.getNode());
					this.pointNW.getChildren().addAll(sphere3D.getNode(), cylinderV3D.getNode());
				}
			}
			
			if(i == 2 || i == 3 || i == 4 || i == 5) {
				sphere3D.translateXProperty().bind(this.box.translateXProperty().add(this.box.widthProperty().divide(2)));
				cylinderV3D.translateXProperty().bind(this.box.translateXProperty().add(this.box.widthProperty().divide(2)));
				if(i == 4 || i == 5) {
					cylinderH3D.translateXProperty().bind(this.box.translateXProperty().subtract(this.box.widthProperty().divide(2)));
					cylinderH3D.translateZProperty().bind(this.box.translateZProperty());
					cylinderH3D.heightProperty().bind(this.box.heightProperty());
					this.lineE.getChildren().add(cylinderH3D.getNode());
					this.pointSW.getChildren().addAll(sphere3D.getNode(), cylinderV3D.getNode());
				}
			}
			
			if(i % 2 == 1) {
				sphere3D.translateYProperty().bind(this.box.translateZProperty().subtract(this.box.depthProperty()));
				cylinderH3D.translateYProperty().bind(this.box.translateZProperty().subtract(this.box.depthProperty()));
				this.selection3D.getChildren().add(cylinderV3D.getNode());
			}
		}
		this.selection3D.getChildren().addAll(this.pointNE, this.pointSE, this.pointNW, this.pointSW);
		this.selection3D.getChildren().addAll(this.lineE, this.lineN, this.lineS, this.lineW);
	}
	
}
