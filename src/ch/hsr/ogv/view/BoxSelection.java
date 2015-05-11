package ch.hsr.ogv.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class BoxSelection extends Group {

	public final static int INIT_SELECT_SIZE = 4;
	private final static Color SELECTION_COLOR = Color.DODGERBLUE;

	private Group selection = new Group();
	private Cuboid box;

	private Group pointNE = new Group();
	private Group pointNW = new Group();
	private Group pointSE = new Group();
	private Group pointSW = new Group();

	private Group lineN = new Group();
	private Group lineE = new Group();
	private Group lineS = new Group();
	private Group lineW = new Group();

	public Group getPointNE() {
		return this.pointNE;
	}

	public Group getPointNW() {
		return this.pointNW;
	}

	public Group getPointSE() {
		return this.pointSE;
	}

	public Group getPointSW() {
		return this.pointSW;
	}

	public Group getLineN() {
		return this.lineN;
	}

	public Group getLineE() {
		return this.lineE;
	}

	public Group getLineS() {
		return this.lineS;
	}

	public Group getLineW() {
		return this.lineW;
	}

	public BoxSelection(Cuboid box) {
		this.box = box;
		buildSelection();
		getChildren().add(selection);
	}

	private void buildSelection() {

		for (int i = 0; i < 8; i++) {
			SphereAdapter sphere = new SphereAdapter(SELECTION_COLOR, INIT_SELECT_SIZE);

			CylinderAdapter cylinderV = new CylinderAdapter(SELECTION_COLOR, INIT_SELECT_SIZE - 2, 10);
			cylinderV.heightProperty().bind(this.box.depthProperty());
			cylinderV.translateYProperty().bind(this.box.translateYProperty());

			CylinderAdapter cylinderH = new CylinderAdapter(SELECTION_COLOR, INIT_SELECT_SIZE - 2, 10);
			cylinderH.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

			if (i == 0 || i == 1 || i == 4 || i == 5) {
				sphere.translateZProperty().bind(this.box.translateZProperty().subtract(this.box.heightProperty().divide(2)));
				cylinderV.translateZProperty().bind(this.box.translateZProperty().subtract(this.box.heightProperty().divide(2)));
				if (i == 0 || i == 1) {
					cylinderH.translateZProperty().bind(this.box.translateZProperty().subtract(this.box.heightProperty().divide(2)));
					cylinderH.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));
					cylinderH.heightProperty().bind(this.box.widthProperty());
					this.lineS.getChildren().add(cylinderH);
					this.pointSE.getChildren().addAll(sphere, cylinderV);
				}
			}

			if (i == 0 || i == 1 || i == 6 || i == 7) {
				sphere.translateXProperty().bind(this.box.translateXProperty().subtract(this.box.widthProperty().divide(2)));
				cylinderV.translateXProperty().bind(this.box.translateXProperty().subtract(this.box.widthProperty().divide(2)));
				if (i == 6 || i == 7) {
					cylinderH.translateXProperty().bind(this.box.translateXProperty().add(this.box.widthProperty().divide(2)));
					cylinderH.heightProperty().bind(this.box.heightProperty());
					this.lineW.getChildren().add(cylinderH);
					this.pointNE.getChildren().addAll(sphere, cylinderV);
				}
			}

			if (i == 2 || i == 3 || i == 6 || i == 7) {
				sphere.translateZProperty().bind(this.box.translateZProperty().add(this.box.heightProperty().divide(2)));
				cylinderV.translateZProperty().bind(this.box.translateZProperty().add(this.box.heightProperty().divide(2)));
				if (i == 2 || i == 3) {
					cylinderH.translateXProperty().bind(this.box.translateXProperty());
					cylinderH.translateZProperty().bind(this.box.translateZProperty().add(this.box.heightProperty().divide(2)));
					cylinderH.getTransforms().add(new Rotate(90, Rotate.Z_AXIS));
					cylinderH.heightProperty().bind(this.box.widthProperty());
					this.lineN.getChildren().add(cylinderH);
					this.pointNW.getChildren().addAll(sphere, cylinderV);
				}
			}

			if (i == 2 || i == 3 || i == 4 || i == 5) {
				sphere.translateXProperty().bind(this.box.translateXProperty().add(this.box.widthProperty().divide(2)));
				cylinderV.translateXProperty().bind(this.box.translateXProperty().add(this.box.widthProperty().divide(2)));
				if (i == 4 || i == 5) {
					cylinderH.translateXProperty().bind(this.box.translateXProperty().subtract(this.box.widthProperty().divide(2)));
					cylinderH.translateZProperty().bind(this.box.translateZProperty());
					cylinderH.heightProperty().bind(this.box.heightProperty());
					this.lineE.getChildren().add(cylinderH);
					this.pointSW.getChildren().addAll(sphere, cylinderV);
				}
			}

			if (i % 2 == 1) {
				sphere.translateYProperty().bind(this.box.translateZProperty().subtract(this.box.depthProperty()));
				cylinderH.translateYProperty().bind(this.box.translateZProperty().subtract(this.box.depthProperty()));
				this.selection.getChildren().add(cylinderV);
			}
		}
		this.selection.getChildren().addAll(this.pointNE, this.pointSE, this.pointNW, this.pointSW);
		this.selection.getChildren().addAll(this.lineE, this.lineN, this.lineS, this.lineW);
	}

}
