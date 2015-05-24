package ch.hsr.ogv.view;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.GeometryUtil;

public class ReflexiveArrow extends Arrow {

	/**         w
	 *      1-------2/3 
	 *  m ->|		|
	 *  +---s---+	| h
	 *  |       |   |
	 *  |	    e---4
	 *  |       |
	 *  +-------+
	 * 
	 *  s: startPoint
	 *  e: endPoint
	 *  1: firstPartPoint
	 *  2: secondPartPoint
	 *  3: thirdPartPoint
	 *  4: fourthPartPoint
	 *  m: smallVertical, SMALL_PART_LENGTH
	 *  w: largeHorizontal, box width
	 *  h: largeVertical, box height
	 */
	
	private final double SMALL_PART_LENGTH = 50;
	private Point3D firstPartPoint;
	private Point3D secondPartPoint;
	private Point3D thirdPartPoint;
	private Point3D fourthPartPoint;
	
	private Box smallVertical;
	private Box largeHorizontal;
	private Box depthLine;
	private Box largeVertical;
	private Box smallHorizontal;
	
	public ReflexiveArrow(PaneBox startBox, PaneBox endBox, RelationType type) {
		super(startBox, endBox, type);
		setPoints(startBox, endBox);
		this.type = type;
		buildArrow();
		drawArrow();
	}
	
	@Override
	public void setPoints(PaneBox startBox, PaneBox endBox) { // startBox and endBox can be the same
		Point3D startBoxCenter = startBox.getCenterPoint();
		Point3D endBoxCenter = endBox.getCenterPoint();
		setStartPoint(new Point3D(startBoxCenter.getX(), startBoxCenter.getY(), startBoxCenter.getZ() + startBox.getHeight() / 2));
		setEndPoint(new Point3D(endBoxCenter.getX() - endBox.getWidth() / 2, endBoxCenter.getY(), endBoxCenter.getZ()));
		if (totalArrowNumber > 1) {
			calculateArrangement(startBox, endBox);
		}
		this.startEndDistance = this.startPoint.distance(this.endPoint);
		calculatePartPoints();
	}
	
	private void calculatePartPoints() {
		this.firstPartPoint = new Point3D(this.startPoint.getX(), this.startPoint.getY(), this.startPoint.getZ() + SMALL_PART_LENGTH);
		this.fourthPartPoint = new Point3D(this.endPoint.getX() - SMALL_PART_LENGTH, this.endPoint.getY(), this.endPoint.getZ());
		this.secondPartPoint = new Point3D(this.fourthPartPoint.getX(), this.startPoint.getY(), this.firstPartPoint.getZ());
		this.thirdPartPoint = new Point3D(this.secondPartPoint.getX(), this.endPoint.getY(), this.firstPartPoint.getZ());
	}
	
	@Override
	protected void buildArrow() {
		prepareArrowLineEdge();
		prepareArrowLabel();
		prepareLines();
		buildSelectionHelpers();
		this.selection = new ArrowSelection();
		this.selection.setVisible(false);
		setColor(this.color);
		drawArrow();
		addElementsToGroup();
	}
	
	@Override
	protected void prepareLines() {
		this.line = new Box(this.width, this.width, this.width);
		if(this.firstPartPoint != null && this.secondPartPoint != null && this.fourthPartPoint != null) {
			this.smallVertical = new Box(this.width, this.width, this.startPoint.distance(this.firstPartPoint));
			this.largeHorizontal = new Box(this.width, this.width, this.firstPartPoint.distance(this.secondPartPoint));
			this.depthLine = new Box(this.width, this.width, this.secondPartPoint.distance(this.thirdPartPoint));
			this.largeVertical = new Box(this.width, this.width, this.thirdPartPoint.distance(this.fourthPartPoint));
			this.smallHorizontal = new Box(this.width, this.width, this.fourthPartPoint.distance(this.endPoint));
		}
	}
	
	@Override
	protected void buildSelectionHelpers() {
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(Color.DODGERBLUE);
		this.startSelectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH / 2, SMALL_PART_LENGTH);
		this.startSelectionHelper.setMaterial(material); // for debugging
		this.startSelectionHelper.translateXProperty().bind(this.smallVertical.translateXProperty());
		this.startSelectionHelper.translateYProperty().bind(this.smallVertical.translateYProperty());
		this.startSelectionHelper.translateZProperty().bind(this.smallVertical.translateZProperty());
		this.startSelectionHelper.rotateProperty().bind(this.smallVertical.rotateProperty());
		this.startSelectionHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events

		this.lineSelectionHelpers.add(createBindLineHelper(this.largeHorizontal));
		this.lineSelectionHelpers.add(createBindLineHelper(this.depthLine));
		this.lineSelectionHelpers.add(createBindLineHelper(this.largeVertical));
		
		this.endSelectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH / 2, SMALL_PART_LENGTH);
		this.endSelectionHelper.setMaterial(material); // for debugging
		this.endSelectionHelper.translateXProperty().bind(this.smallHorizontal.translateXProperty());
		this.endSelectionHelper.translateYProperty().bind(this.smallHorizontal.translateYProperty());
		this.endSelectionHelper.translateZProperty().bind(this.smallHorizontal.translateZProperty());
		this.endSelectionHelper.rotationAxisProperty().bind(this.smallHorizontal.rotationAxisProperty());
		this.endSelectionHelper.rotateProperty().bind(this.smallHorizontal.rotateProperty());
		this.endSelectionHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events
	}
	
	private Box createBindLineHelper(Box origin) {
		Box lineHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH, origin.getDepth());
		lineHelper.depthProperty().bind(origin.depthProperty().add((SELECTION_HELPER_WIDTH)));
		lineHelper.translateXProperty().bind(origin.translateXProperty());
		lineHelper.translateYProperty().bind(origin.translateYProperty());
		lineHelper.translateZProperty().bind(origin.translateZProperty());
		lineHelper.rotationAxisProperty().bind(origin.rotationAxisProperty());
		lineHelper.rotateProperty().bind(origin.rotateProperty());
		lineHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events
		return lineHelper;
	}
	
	@Override
	protected void setLineVisibility() {
		this.line.setVisible(false);
		this.smallVertical.setVisible(true);
		this.largeHorizontal.setVisible(true);
		this.depthLine.setVisible(true);
		this.largeVertical.setVisible(true);
		this.smallHorizontal.setVisible(true);
	}
	
	@Override
	protected void setArrowLineEdge() {
		setArrowStart();
		setArrowEnd();
	}
	
	private void setArrowStart() {
		this.arrowStart.setTranslateX(0);
		this.arrowStart.setTranslateY(0);
		this.arrowStart.setTranslateZ(0);
		this.arrowStart.getTransforms().clear();
		this.arrowStart.getTransforms().add(new Rotate(-180, arrowStart.getTranslateX(), arrowStart.getTranslateY(), arrowStart.getTranslateZ(), Rotate.Y_AXIS));
		this.arrowStart.setTranslateX(this.startPoint.getX());
		this.arrowStart.setTranslateY(this.startPoint.getY());
		this.arrowStart.setTranslateZ(this.startPoint.getZ() - EDGE_SPACING);
	}
	
	private void setArrowEnd() {
		this.arrowEnd.setTranslateX(0);
		this.arrowEnd.setTranslateY(0);
		this.arrowEnd.setTranslateZ(0);
		this.arrowEnd.getTransforms().clear();
		this.arrowEnd.getTransforms().add(new Rotate(90, arrowEnd.getTranslateX(), arrowEnd.getTranslateY(), arrowEnd.getTranslateZ(), Rotate.Y_AXIS));
		this.arrowEnd.setTranslateX(this.endPoint.getX() + EDGE_SPACING);
		this.arrowEnd.setTranslateY(this.endPoint.getY());
		this.arrowEnd.setTranslateZ(this.endPoint.getZ());
	}
	
	@Override
	protected void setArrowLabels() {
		this.labelStartRight.setTranslateXYZ(this.startPoint.getX() - LABEL_SPACING / 3 - 1, this.startPoint.getY(), this.startPoint.getZ() + LABEL_SPACING + 15);

		double startLeftWidth = this.labelStartLeft.calcMinWidth();
		startLeftWidth = startLeftWidth < 20 ? 20 : startLeftWidth;
		this.labelStartLeft.setTranslateXYZ(this.startPoint.getX() + startLeftWidth + LABEL_SPACING / 3, this.startPoint.getY(), this.startPoint.getZ() + LABEL_SPACING + 15);

		this.labelEndRight.setTranslateXYZ(this.endPoint.getX() - LABEL_SPACING / 3, this.endPoint.getY(), this.endPoint.getZ() + LABEL_SPACING);

		double endLeftWidth = this.labelEndLeft.calcMinWidth();
		endLeftWidth = endLeftWidth < 20 ? 20 : endLeftWidth;
		this.labelEndLeft.setTranslateXYZ(this.endPoint.getX() - LABEL_SPACING / 3, this.endPoint.getY(), this.endPoint.getZ() - LABEL_SPACING + 20);
	}

	protected void setSingleElements() {
		setSmallVertical();
		setLargeHorizontal();
		setDepthLine();
		setLargeVertical();
		setSmallHorizontal();
	}
	
	private void setSmallVertical() {
		double startGap = this.arrowStart.getAdditionalGap();
		double smallVerticalDist = this.startPoint.distance(this.firstPartPoint) - (startGap / 2);
		this.smallVertical.setDepth(smallVerticalDist);
		this.smallVertical.setTranslateX(this.startPoint.getX());
		this.smallVertical.setTranslateY(this.startPoint.getY());
		this.smallVertical.setTranslateZ(this.startPoint.getZ() + (smallVerticalDist / 2) + (startGap / 2));
	}
	
	private void setLargeHorizontal() {
		double largeHorizontalDist = this.firstPartPoint.distance(this.secondPartPoint) + this.width;
		this.largeHorizontal.setDepth(largeHorizontalDist);
		this.largeHorizontal.setTranslateX(this.firstPartPoint.getX() - (largeHorizontalDist / 2) + (this.width / 2));
		this.largeHorizontal.setTranslateY(this.firstPartPoint.getY());
		this.largeHorizontal.setTranslateZ(this.firstPartPoint.getZ());
		this.largeHorizontal.setRotationAxis(Rotate.Y_AXIS);
		this.largeHorizontal.setRotate(90);
	}
	
	private void setDepthLine() {
		double depthLineDist = this.secondPartPoint.distance(this.thirdPartPoint);
		this.depthLine.setDepth(depthLineDist);
		this.depthLine.setTranslateX(this.thirdPartPoint.getX());
		this.depthLine.setTranslateY(this.thirdPartPoint.getY()  + (depthLineDist / 2) + (this.width / 2));
		this.depthLine.setTranslateZ(this.thirdPartPoint.getZ());
		this.depthLine.setRotationAxis(Rotate.X_AXIS);
		this.depthLine.setRotate(90);
	}
	
	private void setLargeVertical() {
		double largeVerticalDist = this.thirdPartPoint.distance(this.fourthPartPoint) + this.width;
		this.largeVertical.setDepth(largeVerticalDist);
		this.largeVertical.setTranslateX(this.thirdPartPoint.getX());
		this.largeVertical.setTranslateY(this.thirdPartPoint.getY());
		this.largeVertical.setTranslateZ(this.thirdPartPoint.getZ() - (largeVerticalDist / 2) + (this.width / 2));
	}
	
	private void setSmallHorizontal() {
		double endGap = this.arrowEnd.getAdditionalGap();
		double smallHorizontalDist = this.fourthPartPoint.distance(this.endPoint) - (endGap / 2);
		this.smallHorizontal.setDepth(smallHorizontalDist);
		this.smallHorizontal.setTranslateX(this.endPoint.getX() - (smallHorizontalDist / 2) + (endGap / 4));
		this.smallHorizontal.setTranslateY(this.endPoint.getY());
		this.smallHorizontal.setTranslateZ(this.endPoint.getZ());
		this.smallHorizontal.setRotationAxis(Rotate.Y_AXIS);
		this.smallHorizontal.setRotate(90);
	}
	
	@Override
	protected void moveRotateGroup() {
		this.rotateYAngle = GeometryUtil.rotateYAngle(this.startPoint, this.endPoint);
		this.rotateXAngle = -GeometryUtil.rotateXAngle(this.startPoint, this.endPoint);
		this.selection.setStartEndXYZ(this.startPoint, this.endPoint);
	}
	
	@Override
	protected void addElementsToGroup() {
		getChildren().clear();
		getChildren().addAll(this.smallVertical, this.largeHorizontal, this.depthLine, this.largeVertical, this.smallHorizontal);
		getChildren().addAll(this.arrowStart, this.arrowEnd);
		getChildren().addAll(this.lineSelectionHelpers);
		getChildren().addAll(this.startSelectionHelper, this.endSelectionHelper);
		getChildren().addAll(this.labelStartRight, this.labelStartLeft, this.labelEndRight, this.labelEndLeft);
	}
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		applyColor(this.smallVertical, this.color);
		applyColor(this.largeHorizontal, this.color);
		applyColor(this.depthLine, this.color);
		applyColor(this.largeVertical, this.color);
		applyColor(this.smallHorizontal, this.color);
	}
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		Color colorToApply = colorToApply(selected);
		applyColor(this.smallVertical, colorToApply);
		applyColor(this.largeHorizontal, colorToApply);
		applyColor(this.depthLine, colorToApply);
		applyColor(this.largeVertical, colorToApply);
		applyColor(this.smallHorizontal, colorToApply);
	}
	
}
