package ch.hsr.ogv.view;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.GeometryUtil;

public class ReflexiveArrow extends Arrow {

	Point3D firstPartPoint;
	Point3D secondPartPoint;
	Point3D thirdPartPoint;

	public ReflexiveArrow(PaneBox box, RelationType type) {
		super(box, box, type);
		calcucaltePartPoints(box);
	}

	@Override
	protected void buildArrow() {
		prepareArrowLineEdge();
		prepareArrowLabel();
		prepareLines();
		buildSelectionHelpers();
		selection = new ArrowSelection();
		selection.setVisible(false);
		setColor(color);
		drawArrow();
		getChildren().addAll(line, arrowStart, arrowEnd);
		getChildren().addAll(lineSelectionHelper, startSelectionHelper, endSelectionHelper);
		getChildren().addAll(labelStartRight, labelStartLeft, labelEndRight, labelEndLeft);
	}

	@Override
	protected void buildSelectionHelpers() {
		double lineSelectionGap = 100;
		double endGap = arrowEnd.getAdditionalGap();
		double startGap = arrowStart.getAdditionalGap();
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(Color.DODGERBLUE);
		lineSelectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH, line.getDepth() - lineSelectionGap);
		lineSelectionHelper.depthProperty().bind(this.line.depthProperty().subtract(lineSelectionGap).add((endGap + startGap) / 2));
		lineSelectionHelper.translateXProperty().bind(line.translateXProperty());
		lineSelectionHelper.translateYProperty().bind(line.translateYProperty());
		lineSelectionHelper.translateZProperty().bind(line.translateZProperty().subtract((-endGap + startGap) / 4));
		lineSelectionHelper.rotateProperty().bind(line.rotateProperty());
		lineSelectionHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events

		startSelectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH / 2, lineSelectionGap / 2);
		startSelectionHelper.setMaterial(material); // for debugging
		startSelectionHelper.translateXProperty().bind(line.translateXProperty());
		startSelectionHelper.translateYProperty().bind(line.translateYProperty());
		startSelectionHelper.translateZProperty().bind(line.translateZProperty().subtract(this.line.depthProperty().divide(2)).subtract(startGap / 2).add(lineSelectionGap / 4));
		startSelectionHelper.rotateProperty().bind(line.rotateProperty());
		startSelectionHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events

		endSelectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH / 2, lineSelectionGap / 2);
		endSelectionHelper.setMaterial(material); // for debugging
		endSelectionHelper.translateXProperty().bind(line.translateXProperty());
		endSelectionHelper.translateYProperty().bind(line.translateYProperty());
		endSelectionHelper.translateZProperty().bind(line.translateZProperty().add(this.line.depthProperty().divide(2)).add(endGap / 2).subtract(lineSelectionGap / 4));
		endSelectionHelper.rotateProperty().bind(line.rotateProperty());
		endSelectionHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events
	}

	@Override
	public void drawArrow() {
		getTransforms().clear();
		line.setVisible(true);
		setArrowLineEdge();
		setArrowLabels();

		drawSmallHorizontalPart();
		drawLargeVerticalPart();
		drawLargeHorizontalPart();
		drawSmallVerticalPart();

		labelStartLeft.setRotateYAxis(-rotateYAngle);
		labelStartRight.setRotateYAxis(-rotateYAngle);
		labelEndLeft.setRotateYAxis(-rotateYAngle);
		labelEndRight.setRotateYAxis(-rotateYAngle);

		selection.setStartEndXYZ(startPoint, endPoint);
	}

	private void calcucaltePartPoints(PaneBox box) {
		firstPartPoint = new Point3D(startPoint.getX() - (box.getWidth() / 2) + 200, startPoint.getY(), startPoint.getZ());
		thirdPartPoint = new Point3D(startPoint.getX(), startPoint.getY(), startPoint.getZ() + (box.getHeight() / 2) + 200);
		secondPartPoint = new Point3D(firstPartPoint.getX(), startPoint.getY(), thirdPartPoint.getZ());
	}

	public void drawArrowBody(Point3D start, Point3D end) {
		double endGap = arrowEnd.getAdditionalGap();
		double startGap = arrowStart.getAdditionalGap();
		double gapDistance = boxDistance - (endGap + startGap) / 2;

		line.setDepth(gapDistance);
		line.setTranslateZ((-endGap + startGap) / 4);

		rotateYAngle = GeometryUtil.rotateYAngle(start, end);
		rotateXAngle = -GeometryUtil.rotateXAngle(start, end);

		Point3D midPoint = start.midpoint(end);
		setTranslateXYZ(midPoint);
		addRotateYAxis(rotateYAngle);
		addRotateXAxis(rotateXAngle);

	}

	public void drawSmallHorizontalPart() {
		drawArrowBody(startPoint, firstPartPoint);
	}

	public void drawLargeVerticalPart() {
		drawArrowBody(firstPartPoint, secondPartPoint);
	}

	public void drawLargeHorizontalPart() {
		drawArrowBody(secondPartPoint, thirdPartPoint);
	}

	public void drawSmallVerticalPart() {
		drawArrowBody(thirdPartPoint, startPoint);
	}

}
