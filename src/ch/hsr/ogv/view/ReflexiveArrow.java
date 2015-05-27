package ch.hsr.ogv.view;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.GeometryUtil;

public class ReflexiveArrow extends Arrow {

	/**         
	 *  +-------+
	 *  |       |
	 *  |	    s---1
	 *  |       |   |
	 *  +---e---+   | h
	 *  m ->|       |
	 *      4-------2/3
	 *          w
	 *          
	 *  s: startPoint
	 *  e: endPoint
	 *  1: firstPartPoint
	 *  2: secondPartPoint
	 *  3: thirdPartPoint
	 *  4: fourthPartPoint
	 *  m: smallVertical, smallPartLength
	 *  w: largeHorizontal, box width
	 *  h: largeVertical, box height
	 */
	
	private final double SMALL_PART_LENGTH = 50;
	private double smallPartLength = SMALL_PART_LENGTH;
	private Point3D firstPartPoint;
	private Point3D secondPartPoint;
	private Point3D thirdPartPoint;
	private Point3D fourthPartPoint;
	
	private Box smallHorizontal;
	private Box largeVertical;
	private Box depthLine;
	private Box largeHorizontal;
	private Box smallVertical;
	
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
		setStartPoint(new Point3D(startBoxCenter.getX() - startBox.getWidth() / 2, startBoxCenter.getY(), startBoxCenter.getZ()));
		setEndPoint(new Point3D(endBoxCenter.getX(), endBoxCenter.getY(), endBoxCenter.getZ() - endBox.getHeight() / 2));
		if (totalArrowNumber > 1) {
			calculateArrangement(startBox, endBox);
		}
		this.startEndDistance = this.startPoint.distance(this.endPoint);
		calculatePartPoints();
	}
	
	protected void calculateArrangement(PaneBox startBox, PaneBox endBox) {
		Point3D startCenter = startBox.getCenterPoint();
		Point3D endCenter = endBox.getCenterPoint();
		Point3D lineStartPointStart = new Point3D(startCenter.getX() - (startBox.getWidth() / 2), startCenter.getY(), startCenter.getZ() - (startBox.getHeight() / 2));
		Point3D lineEndPointStart = new Point3D(startCenter.getX() - (startBox.getWidth() / 2), startCenter.getY(), startCenter.getZ() + (startBox.getHeight() / 2));
		Point3D lineStartPointEnd = new Point3D(endCenter.getX() - (endBox.getWidth() / 2), endCenter.getY(), endCenter.getZ() - (startBox.getHeight() / 2));
		Point3D lineEndPointEnd = new Point3D(endCenter.getX() + (endBox.getWidth() / 2), endCenter.getY(), endCenter.getZ() - (startBox.getHeight() / 2));
		setStartPoint(GeometryUtil.divideLineFraction(lineStartPointStart, lineEndPointStart, (arrowNumber) / (totalArrowNumber + 1.0)));
		setEndPoint(GeometryUtil.divideLineFraction(lineStartPointEnd, lineEndPointEnd, (arrowNumber) / (totalArrowNumber + 1.0)));
		setSmallPartLength();
	}
	
	private void setSmallPartLength() {
		double fractionSize =  (SMALL_PART_LENGTH * 2) / totalArrowNumber;
		smallPartLength = SMALL_PART_LENGTH + (fractionSize * (arrowNumber - 1));
		this.startSelectionHelper.setDepth(this.smallPartLength);
		this.endSelectionHelper.setDepth(this.smallPartLength);
	}
	
	private void calculatePartPoints() {
		this.firstPartPoint = new Point3D(this.startPoint.getX() - this.smallPartLength, this.startPoint.getY(), this.startPoint.getZ());
		this.fourthPartPoint = new Point3D(this.endPoint.getX(), this.endPoint.getY(), this.endPoint.getZ() - this.smallPartLength);
		this.secondPartPoint = new Point3D(this.firstPartPoint.getX(), this.startPoint.getY(), this.fourthPartPoint.getZ());
		this.thirdPartPoint = new Point3D(this.firstPartPoint.getX(), this.endPoint.getY(), this.fourthPartPoint.getZ());
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
			this.smallHorizontal = new Box(this.width, this.width, this.startPoint.distance(this.firstPartPoint));
			this.largeVertical = new Box(this.width, this.width, this.firstPartPoint.distance(this.secondPartPoint));
			this.depthLine = new Box(this.width, this.width, this.secondPartPoint.distance(this.thirdPartPoint));
			this.largeHorizontal = new Box(this.width, this.width, this.thirdPartPoint.distance(this.fourthPartPoint));
			this.smallVertical = new Box(this.width, this.width, this.fourthPartPoint.distance(this.endPoint));
		}
	}
	
	@Override
	protected void buildSelectionHelpers() {
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(Color.DODGERBLUE);
		this.startSelectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH / 2, this.smallPartLength);
		this.startSelectionHelper.setMaterial(material); // for debugging
		this.startSelectionHelper.translateXProperty().bind(this.smallHorizontal.translateXProperty());
		this.startSelectionHelper.translateYProperty().bind(this.smallHorizontal.translateYProperty());
		this.startSelectionHelper.translateZProperty().bind(this.smallHorizontal.translateZProperty());
		this.startSelectionHelper.rotationAxisProperty().bind(this.smallHorizontal.rotationAxisProperty());
		this.startSelectionHelper.rotateProperty().bind(this.smallHorizontal.rotateProperty());
		this.startSelectionHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events

		this.lineSelectionHelpers.clear();
		this.lineSelectionHelpers.add(createBindLineHelper(this.largeVertical));
		this.lineSelectionHelpers.add(createBindLineHelper(this.depthLine));
		this.lineSelectionHelpers.add(createBindLineHelper(this.largeHorizontal));
		
		this.endSelectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH / 2, this.smallPartLength);
		this.endSelectionHelper.setMaterial(material); // for debugging
		this.endSelectionHelper.translateXProperty().bind(this.smallVertical.translateXProperty());
		this.endSelectionHelper.translateYProperty().bind(this.smallVertical.translateYProperty());
		this.endSelectionHelper.translateZProperty().bind(this.smallVertical.translateZProperty());
		this.endSelectionHelper.rotationAxisProperty().bind(this.smallVertical.rotationAxisProperty());
		this.endSelectionHelper.rotateProperty().bind(this.smallVertical.rotateProperty());
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
		this.smallHorizontal.setVisible(true);
		this.largeVertical.setVisible(true);
		this.depthLine.setVisible(true);
		this.largeHorizontal.setVisible(true);
		this.smallVertical.setVisible(true);
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
		this.arrowStart.getTransforms().add(new Rotate(90, arrowStart.getTranslateX(), arrowStart.getTranslateY(), arrowStart.getTranslateZ(), Rotate.Y_AXIS));
		this.arrowStart.setTranslateX(this.startPoint.getX() + EDGE_SPACING);
		this.arrowStart.setTranslateY(this.startPoint.getY());
		this.arrowStart.setTranslateZ(this.startPoint.getZ());
	}
	
	private void setArrowEnd() {
		this.arrowEnd.setTranslateX(0);
		this.arrowEnd.setTranslateY(0);
		this.arrowEnd.setTranslateZ(0);
		this.arrowEnd.getTransforms().clear();
		this.arrowEnd.setTranslateX(this.endPoint.getX());
		this.arrowEnd.setTranslateY(this.endPoint.getY());
		this.arrowEnd.setTranslateZ(this.endPoint.getZ() + EDGE_SPACING);
	}
	
	@Override
	protected void setArrowLabels() {
		this.labelStartRight.setTranslateXYZ(this.startPoint.getX() - LABEL_SPACING / 3 - 5, this.startPoint.getY(), this.startPoint.getZ() - LABEL_SPACING + 20);
		this.labelStartLeft.setTranslateXYZ(this.startPoint.getX() - LABEL_SPACING / 3 - 5, this.startPoint.getY(), this.startPoint.getZ() + LABEL_SPACING + 3);
		
		this.labelEndRight.setTranslateXYZ(this.endPoint.getX() - LABEL_SPACING / 3 + 2, this.endPoint.getY(), this.endPoint.getZ() - LABEL_SPACING + 15);
		double endLeftWidth = this.labelEndLeft.calcMinWidth();
		endLeftWidth = endLeftWidth < 20 ? 20 : endLeftWidth;
		this.labelEndLeft.setTranslateXYZ(this.endPoint.getX() + LABEL_SPACING / 3 + endLeftWidth, this.endPoint.getY(), this.endPoint.getZ() - LABEL_SPACING + 15);
	}

	protected void setSingleElements() {
		setSmallHorizontal();
		setLargeVertical();
		setDepthLine();
		setLargeHorizontal();
		setSmallVertical();
	}
	
	private void setSmallHorizontal() {
		double startGap = this.arrowStart.getAdditionalGap();
		double length = this.startPoint.distance(this.firstPartPoint) - (startGap / 2);
		this.smallHorizontal.setDepth(length);
		this.smallHorizontal.setTranslateX(this.startPoint.getX() - (length / 2) - (startGap / 2));
		this.smallHorizontal.setTranslateY(this.startPoint.getY());
		this.smallHorizontal.setTranslateZ(this.startPoint.getZ());
		this.smallHorizontal.setRotationAxis(Rotate.Y_AXIS);
		this.smallHorizontal.setRotate(90);
	}
	
	private void setLargeVertical() {
		double length = this.firstPartPoint.distance(this.secondPartPoint) + this.width;
		this.largeVertical.setDepth(length);
		this.largeVertical.setTranslateX(this.firstPartPoint.getX());
		this.largeVertical.setTranslateY(this.firstPartPoint.getY());
		this.largeVertical.setTranslateZ(this.firstPartPoint.getZ() - (length / 2) + (this.width / 2));
	}
	
	private void setDepthLine() {
		double length = this.secondPartPoint.distance(this.thirdPartPoint);
		this.depthLine.setDepth(length);
		this.depthLine.setTranslateX(this.thirdPartPoint.getX());
		this.depthLine.setTranslateY(this.thirdPartPoint.getY() + (length / 2) + (this.width / 2));
		this.depthLine.setTranslateZ(this.thirdPartPoint.getZ());
		this.depthLine.setRotationAxis(Rotate.X_AXIS);
		this.depthLine.setRotate(90);
	}
	
	private void setLargeHorizontal() {
		double length = this.thirdPartPoint.distance(this.fourthPartPoint) + this.width;
		this.largeHorizontal.setDepth(length);
		this.largeHorizontal.setTranslateX(this.thirdPartPoint.getX() + (length / 2) - (this.width / 2));
		this.largeHorizontal.setTranslateY(this.thirdPartPoint.getY());
		this.largeHorizontal.setTranslateZ(this.thirdPartPoint.getZ());
		this.largeHorizontal.setRotationAxis(Rotate.Y_AXIS);
		this.largeHorizontal.setRotate(90);
	}
	
	private void setSmallVertical() {
		double endGap = this.arrowEnd.getAdditionalGap();
		double length = this.fourthPartPoint.distance(this.endPoint) - (endGap / 2);
		this.smallVertical.setDepth(length);
		this.smallVertical.setTranslateX(this.endPoint.getX());
		this.smallVertical.setTranslateY(this.endPoint.getY());
		this.smallVertical.setTranslateZ(this.endPoint.getZ() - (length / 2) + (endGap / 4));
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
		getChildren().addAll(this.smallHorizontal, this.largeVertical, this.depthLine, this.largeHorizontal, this.smallVertical);
		getChildren().addAll(this.arrowStart, this.arrowEnd);
		getChildren().addAll(this.lineSelectionHelpers);
		getChildren().addAll(this.startSelectionHelper, this.endSelectionHelper);
		getChildren().addAll(this.labelStartRight, this.labelStartLeft, this.labelEndRight, this.labelEndLeft);
	}
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		applyColor(this.smallHorizontal, this.color);
		applyColor(this.largeVertical, this.color);
		applyColor(this.depthLine, this.color);
		applyColor(this.largeHorizontal, this.color);
		applyColor(this.smallVertical, this.color);
	}
	
	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		Color colorToApply = colorToApply(selected);
		applyColor(this.smallHorizontal, colorToApply);
		applyColor(this.largeVertical, colorToApply);
		applyColor(this.depthLine, colorToApply);
		applyColor(this.largeHorizontal, colorToApply);
		applyColor(this.smallVertical, colorToApply);
	}
	
}
