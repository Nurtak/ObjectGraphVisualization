package ch.hsr.ogv.view;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import jfxtras.labs.util.Util;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.GeometryUtil;

/**
 *
 * @author Simon Gwerder, Adrian Rieser
 *
 */
public class Arrow extends Group implements Selectable {

	protected static final int INIT_WIDTH = 2;
	protected static final int SELECTION_HELPER_WIDTH = 20;
	protected double width = INIT_WIDTH;
	protected static final Color SELECTION_COLOR = Color.DODGERBLUE;
	protected static final double EDGE_SPACING = 3;
	protected static final double LABEL_SPACING = 30;
	protected double startEndDistance;
	protected double rotateYAngle;
	protected double rotateXAngle;

	protected int arrowNumber = 1;
	protected int totalArrowNumber = 1;

	protected Point3D startPoint;
	protected Point3D endPoint;

	protected ArrowEdge arrowStart;
	protected ArrowEdge arrowEnd;

	protected ArrowLabel labelStartRight; // start multiplicity
	protected ArrowLabel labelStartLeft; // start role
	protected ArrowLabel labelEndRight; // end multiplicity
	protected ArrowLabel labelEndLeft; // end role

	protected RelationType type = RelationType.BIDIRECTED_ASSOCIATION;

	protected Box line;

	public static final Color DEFAULT_COLOR = Color.BLACK;
	protected Color color = DEFAULT_COLOR;
	protected ArrowSelection selection = null;
	protected List<Box> lineSelectionHelpers = new ArrayList<Box>();
	protected Box startSelectionHelper;
	protected Box endSelectionHelper;

	public Arrow(Point3D startPoint, Point3D endPoint, RelationType type) {
		setPoints(startPoint, endPoint);
		this.type = type;
		buildArrow();
		drawArrow();
	}
	
	public Arrow(PaneBox startBox, Point3D endPoint, RelationType type) {
		setPoints(startBox, endPoint);
		this.type = type;
		buildArrow();
		drawArrow();
	}

	public Arrow(Point3D startPoint, PaneBox endBox, RelationType type) {
		setPoints(startPoint, endBox);
		this.type = type;
		buildArrow();
		drawArrow();
	}
	
	public Arrow(PaneBox startBox, PaneBox endBox, RelationType type) {
		setPoints(startBox, endBox);
		this.type = type;
		buildArrow();
		drawArrow();
	}

	public Point3D getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point3D startPoint) {
		this.startPoint = startPoint;
	}

	public Point3D getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Point3D endPoint) {
		this.endPoint = endPoint;
	}

	public ArrowEdge getArrowStart() {
		return arrowStart;
	}

	public ArrowEdge getArrowEnd() {
		return arrowEnd;
	}

	public ArrowLabel getLabelStartRight() {
		return labelStartRight;
	}

	public ArrowLabel getLabelStartLeft() {
		return labelStartLeft;
	}

	public ArrowLabel getLabelEndRight() {
		return labelEndRight;
	}

	public ArrowLabel getLabelEndLeft() {
		return labelEndLeft;
	}

	public RelationType getRelationType() {
		return type;
	}

	public void setType(RelationType type) {
		this.type = type;
		this.arrowStart.setEndpointType(type.getStartType());
		this.arrowEnd.setEndpointType(type.getEndType());
		drawArrow();
	}

	public List<Box> getLineSelectionHelpers() {
		return lineSelectionHelpers;
	}

	public Box getStartSelectionHelper() {
		return startSelectionHelper;
	}

	public Box getEndSelectionHelper() {
		return endSelectionHelper;
	}

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
	
	protected void prepareArrowLineEdge() {
		this.arrowStart = new ArrowEdge(this.type.getStartType(), this.color);
		this.arrowEnd = new ArrowEdge(this.type.getEndType(), this.color);
	}

	protected void prepareArrowLabel() {
		this.labelStartRight = new ArrowLabel();
		this.labelStartLeft = new ArrowLabel();
		this.labelEndRight = new ArrowLabel();
		this.labelEndLeft = new ArrowLabel();
	}

	protected void prepareLines() {
		this.line = new Box(this.width, this.width, this.startEndDistance);
	}

	protected void buildSelectionHelpers() {
		double lineSelectionGap = 100;
		double endGap = this.arrowEnd.getAdditionalGap();
		double startGap = this.arrowStart.getAdditionalGap();
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(Color.DODGERBLUE);
		Box lineSelectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH, this.line.getDepth() - lineSelectionGap);
		lineSelectionHelper.depthProperty().bind(this.line.depthProperty().subtract(lineSelectionGap).add((endGap + startGap) / 2));
		lineSelectionHelper.translateXProperty().bind(this.line.translateXProperty());
		lineSelectionHelper.translateYProperty().bind(this.line.translateYProperty());
		lineSelectionHelper.translateZProperty().bind(this.line.translateZProperty().subtract((-endGap + startGap) / 4));
		lineSelectionHelper.rotateProperty().bind(this.line.rotateProperty());
		lineSelectionHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events
		this.lineSelectionHelpers.add(lineSelectionHelper);
		
		this.startSelectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH / 2, lineSelectionGap / 2);
		this.startSelectionHelper.setMaterial(material); // for debugging
		this.startSelectionHelper.translateXProperty().bind(this.line.translateXProperty());
		this.startSelectionHelper.translateYProperty().bind(this.line.translateYProperty());
		this.startSelectionHelper.translateZProperty().bind(this.line.translateZProperty().subtract(this.line.depthProperty().divide(2)).subtract(startGap / 2).add(lineSelectionGap / 4));
		this.startSelectionHelper.rotateProperty().bind(this.line.rotateProperty());
		this.startSelectionHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events

		this.endSelectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH / 2, lineSelectionGap / 2);
		this.endSelectionHelper.setMaterial(material); // for debugging
		this.endSelectionHelper.translateXProperty().bind(this.line.translateXProperty());
		this.endSelectionHelper.translateYProperty().bind(this.line.translateYProperty());
		this.endSelectionHelper.translateZProperty().bind(this.line.translateZProperty().add(this.line.depthProperty().divide(2)).add(endGap / 2).subtract(lineSelectionGap / 4));
		this.endSelectionHelper.rotateProperty().bind(this.line.rotateProperty());
		this.endSelectionHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events
	}
	
	protected void addElementsToGroup() {
		getChildren().clear();
		getChildren().addAll(this.line, this.arrowStart, this.arrowEnd);
		getChildren().addAll(this.lineSelectionHelpers);
		getChildren().addAll(this.startSelectionHelper, this.endSelectionHelper);
		getChildren().addAll(this.labelStartRight, this.labelStartLeft, this.labelEndRight, this.labelEndLeft);
	}
	
	public void setPoints(Point3D startPoint, Point3D endPoint) {
		setStartPoint(startPoint);
		setEndPoint(endPoint);
		this.startEndDistance = this.startPoint.distance(this.endPoint);
	}

	public void setPoints(PaneBox startBox, Point3D endPoint) {
		setStartPoint(startBox.getCenterPoint());
		setEndPoint(endPoint);
		Point2D startIntersection = lineBoxIntersection(this.startPoint, startBox, this.endPoint);
		if (startIntersection != null) {
			setStartPoint(new Point3D(startIntersection.getX(), this.startPoint.getY(), startIntersection.getY()));
		}
		this.startEndDistance = this.startPoint.distance(this.endPoint);
	}

	public void setPoints(Point3D startPoint, PaneBox endBox) {
		setStartPoint(startPoint);
		setEndPoint(endBox.getCenterPoint());
		Point2D endIntersection = lineBoxIntersection(this.endPoint, endBox, this.startPoint);
		if (endIntersection != null) {
			setEndPoint(new Point3D(endIntersection.getX(), this.endPoint.getY(), endIntersection.getY()));
		}
		this.startEndDistance = this.startPoint.distance(this.endPoint);
	}

	public void setPoints(PaneBox startBox, PaneBox endBox) {
		setStartPoint(startBox.getCenterPoint());
		setEndPoint(endBox.getCenterPoint());

		if (totalArrowNumber > 1) {
			calculateArrangement(startBox, endBox);
		}

		Point2D startIntersection = lineBoxIntersection(startPoint, startBox, endPoint);
		Point2D endIntersection = lineBoxIntersection(endPoint, endBox, startPoint);
		if (startIntersection != null) {
			setStartPoint(new Point3D(startIntersection.getX(), this.startPoint.getY(), startIntersection.getY()));
		}
		if (endIntersection != null) {
			setEndPoint(new Point3D(endIntersection.getX(), endPoint.getY(), endIntersection.getY()));
		}
		this.startEndDistance = this.startPoint.distance(this.endPoint);
	}
	
	protected void calculateArrangement(PaneBox startBox, PaneBox endBox) {
		double startBoxRadius = startBox.getWidth() <= startBox.getHeight() ? startBox.getWidth() / 2 : startBox.getHeight() / 2;
		double endBoxRadius = endBox.getWidth() <= endBox.getHeight() ? endBox.getWidth() / 2 : endBox.getHeight() / 2;

		double alphaStart = GeometryUtil.rotateYAngle(startBox.getCenterPoint(), endBox.getCenterPoint());
		if (alphaStart < 0.0) {
			alphaStart += 180.0;
		}
		double alphaEnd = GeometryUtil.rotateYAngle(endBox.getCenterPoint(), startBox.getCenterPoint());
		if (alphaEnd < 0.0) {
			alphaEnd += 180.0;
		}

		double betaStart = 90.0 - alphaStart;
		double betaEnd = 90.0 - alphaEnd;

		double diffXStart = startBoxRadius * Math.sin(Math.toRadians(betaStart));
		double diffZStart = startBoxRadius * Math.cos(Math.toRadians(betaStart));
		double diffXEnd = endBoxRadius * Math.sin(Math.toRadians(betaEnd));
		double diffZEnd = endBoxRadius * Math.cos(Math.toRadians(betaEnd));

		Point3D lineStartPointStart = new Point3D(startBox.getCenterPoint().getX() + diffXStart, startBox.getCenterPoint().getY(), startBox.getCenterPoint().getZ() - diffZStart);
		Point3D lineEndPointStart = new Point3D(startBox.getCenterPoint().getX() - diffXStart, startBox.getCenterPoint().getY(), startBox.getCenterPoint().getZ() + diffZStart);
		Point3D lineStartPointEnd = new Point3D(endBox.getCenterPoint().getX() + diffXEnd, endBox.getCenterPoint().getY(), endBox.getCenterPoint().getZ() - diffZEnd);
		Point3D lineEndPointEnd = new Point3D(endBox.getCenterPoint().getX() - diffXEnd, endBox.getCenterPoint().getY(), endBox.getCenterPoint().getZ() + diffZEnd);

		setStartPoint(GeometryUtil.divideLineFraction(lineStartPointStart, lineEndPointStart, (arrowNumber) / (totalArrowNumber + 1.0)));
		setEndPoint(GeometryUtil.divideLineFraction(lineStartPointEnd, lineEndPointEnd, (arrowNumber) / (totalArrowNumber + 1.0)));
	}
	
	public final void drawArrow() {
		getTransforms().clear();
		setLineVisibility();
		setArrowLineEdge();
		setArrowLabels();
		setSingleElements();
		moveRotateGroup();
	}
	
	protected void setLineVisibility() {
		this.line.setVisible(true);
	}
	
	protected void setSingleElements() {
		double endGap = this.arrowEnd.getAdditionalGap();
		double startGap = this.arrowStart.getAdditionalGap();
		double gapDistance = this.startEndDistance - (endGap + startGap) / 2;

		this.line.setDepth(gapDistance);
		this.line.setTranslateZ((-endGap + startGap) / 4);
	}
	
	protected void moveRotateGroup() {
		this.rotateYAngle = GeometryUtil.rotateYAngle(this.startPoint, this.endPoint);
		this.rotateXAngle = -GeometryUtil.rotateXAngle(this.startPoint, this.endPoint);

		Point3D midPoint = this.startPoint.midpoint(this.endPoint);
		setTranslateXYZ(midPoint);
		addRotateYAxis(this.rotateYAngle);
		addRotateXAxis(this.rotateXAngle);

		this.labelStartLeft.setRotateYAxis(-this.rotateYAngle);
		this.labelStartRight.setRotateYAxis(-this.rotateYAngle);
		this.labelEndLeft.setRotateYAxis(-this.rotateYAngle);
		this.labelEndRight.setRotateYAxis(-this.rotateYAngle);

		this.selection.setStartEndXYZ(this.startPoint, this.endPoint);
	}
	
	private Point2D lineRectangleIntersection(Point3D internalPoint, Point3D externalPoint, Point3D centerPoint, double width, double height) {
		double halfWidth = width / 2;
		double halfHeight = height / 2;

		Point2D lineStart = new Point2D(externalPoint.getX(), externalPoint.getZ());
		Point2D lineEnd = new Point2D(internalPoint.getX(), internalPoint.getZ());

		Point2D northEast = new Point2D(centerPoint.getX() - halfWidth, centerPoint.getZ() + halfHeight);
		Point2D southEast = new Point2D(centerPoint.getX() - halfWidth, centerPoint.getZ() - halfHeight);
		Point2D southWest = new Point2D(centerPoint.getX() + halfWidth, centerPoint.getZ() - halfHeight);
		Point2D northWest = new Point2D(centerPoint.getX() + halfWidth, centerPoint.getZ() + halfHeight);

		Point2D interEastHeight = GeometryUtil.lineIntersect(lineStart, lineEnd, northEast, southEast);
		Point2D interWestHeight = GeometryUtil.lineIntersect(lineStart, lineEnd, northWest, southWest);
		Point2D interNorthWidth = GeometryUtil.lineIntersect(lineStart, lineEnd, northEast, northWest);
		Point2D interSouthWidth = GeometryUtil.lineIntersect(lineStart, lineEnd, southEast, southWest);

		if (interEastHeight != null) {
			return interEastHeight;
		}
		if (interWestHeight != null) {
			return interWestHeight;
		}
		if (interNorthWidth != null) {
			return interNorthWidth;
		}
		if (interSouthWidth != null) {
			return interSouthWidth;
		}
		return null;
	}

	private Point2D lineBoxIntersection(Point3D internalPoint, PaneBox box, Point3D externalPoint) {
		return lineRectangleIntersection(externalPoint, internalPoint, box.getCenterPoint(), box.getWidth(), box.getHeight());
	}

	protected void setArrowLineEdge() {
		this.arrowStart.setTranslateX(0);
		this.arrowStart.setTranslateY(0);
		this.arrowStart.setTranslateZ(0);
		this.arrowStart.getTransforms().clear();
		this.arrowStart.getTransforms().add(new Rotate(180, arrowStart.getTranslateX(), arrowStart.getTranslateY(), arrowStart.getTranslateZ(), Rotate.Y_AXIS));
		this.arrowStart.setTranslateZ(-this.startEndDistance / 2 - EDGE_SPACING);
		this.arrowEnd.setTranslateZ(this.startEndDistance / 2 + EDGE_SPACING);
	}

	protected void setArrowLabels() {
		this.labelStartRight.setTranslateXYZ(-LABEL_SPACING / 3 - 1, this.startPoint.getY(), -this.startEndDistance / 2 + LABEL_SPACING + 15);

		double startLeftWidth = this.labelStartLeft.calcMinWidth();
		startLeftWidth = startLeftWidth < 20 ? 20 : startLeftWidth;
		this.labelStartLeft.setTranslateXYZ(startLeftWidth + LABEL_SPACING / 3, this.startPoint.getY(), -this.startEndDistance / 2 + LABEL_SPACING + 15);

		this.labelEndRight.setTranslateXYZ(-LABEL_SPACING / 3 - 1, this.endPoint.getY(), this.startEndDistance / 2 - LABEL_SPACING + 10);

		double endLeftWidth = this.labelEndLeft.calcMinWidth();
		endLeftWidth = endLeftWidth < 20 ? 20 : endLeftWidth;
		this.labelEndLeft.setTranslateXYZ(endLeftWidth + LABEL_SPACING / 3, this.endPoint.getY(), this.startEndDistance / 2 - LABEL_SPACING + 10);
	}

	public void setColor(Color color) {
		this.color = color;
		applyColor(this.line, this.color);
		this.arrowStart.setColor(color);
		this.arrowEnd.setColor(color);
		this.labelStartRight.setColor(color);
		this.labelStartLeft.setColor(color);
		this.labelEndRight.setColor(color);
		this.labelEndLeft.setColor(color);
	}

	protected void applyColor(Box box, Color color) {
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(color);
		material.setSpecularColor(color.brighter());
		box.setMaterial(material);
	}

	public Color getColor() {
		return this.color;
	}

	public void setAllLabelSelected(boolean selected) {
		this.labelStartLeft.setLabelSelected(selected);
		this.labelStartRight.setLabelSelected(selected);
		this.labelEndLeft.setLabelSelected(selected);
		this.labelEndRight.setLabelSelected(selected);
	}

	public ArrowLabel getSelectedLabel() {
		if (this.labelStartLeft.isLabelSelected()) {
			return this.labelStartLeft;
		}
		else if (this.labelStartRight.isLabelSelected()) {
			return this.labelStartRight;
		}
		else if (this.labelEndLeft.isLabelSelected()) {
			return this.labelEndLeft;
		}
		else if (this.labelEndRight.isLabelSelected()) {
			return this.labelEndRight;
		}
		return null;
	}

	public boolean isStart(ArrowLabel arrowLabel) {
		return this.labelStartLeft.equals(arrowLabel) || this.labelStartRight.equals(arrowLabel);
	}

	public boolean isLeft(ArrowLabel arrowLabel) {
		return this.labelStartLeft.equals(arrowLabel) || this.labelEndLeft.equals(arrowLabel);
	}

	public boolean hasLeftText(boolean atStart) {
		String leftText = null;
		if (atStart) {
			leftText = getLabelStartLeft().getArrowText().getText();
		}
		else {
			leftText = getLabelEndLeft().getArrowText().getText();
		}
		return leftText != null && !leftText.isEmpty();
	}

	public boolean hasRightText(boolean atStart) {
		String rightText = null;
		if (atStart) {
			rightText = getLabelStartRight().getArrowText().getText();
		}
		else {
			rightText = getLabelEndRight().getArrowText().getText();
		}
		return rightText != null && !rightText.isEmpty();
	}

	@Override
	public void setSelected(boolean selected) {
		Color colorToApply = colorToApply(selected);
		applyColor(this.line, colorToApply);
		this.arrowStart.setColor(colorToApply);
		this.arrowEnd.setColor(colorToApply);
		this.labelStartRight.setColor(Util.darker(colorToApply, 0.3));
		this.labelStartLeft.setColor(Util.darker(colorToApply, 0.3));
		this.labelEndRight.setColor(Util.darker(colorToApply, 0.3));
		this.labelEndLeft.setColor(Util.darker(colorToApply, 0.3));
		this.selection.setVisible(selected);
		if (!selected) {
			setAllLabelSelected(false);
		}
	}
	
	protected Color colorToApply(boolean selected) {
		Color colorToApply = getColor();
		if (selected) {
			colorToApply = SELECTION_COLOR;
		}
		return colorToApply;
	}

	@Override
	public boolean isSelected() {
		return this.selection.isVisible();
	}

	@Override
	public ArrowSelection getSelection() {
		return this.selection;
	}

	public void setTranslateXYZ(Point3D point) {
		setTranslateXYZ(point.getX(), point.getY(), point.getZ());
	}

	public void setTranslateXYZ(double x, double y, double z) {
		getTransforms().add(new Translate(x, y, z));
	}

	public void addRotateYAxis(double degree) {
		getTransforms().add(new Rotate(degree, getTranslateX(), getTranslateY(), getTranslateZ(), Rotate.Y_AXIS));
	}

	public void addRotateZAxis(double degree) {
		getTransforms().add(new Rotate(degree, getTranslateX(), getTranslateY(), getTranslateZ(), Rotate.Z_AXIS));
	}

	public void addRotateXAxis(double degree) {
		getTransforms().add(new Rotate(degree, getTranslateX(), getTranslateY(), getTranslateZ(), Rotate.X_AXIS));
	}

	public double getWidth() {
		return this.width;
	}

	public double getBoxDistance() {
		return this.startEndDistance;
	}

	public double getRotateYAngle() {
		return this.rotateYAngle;
	}

	public double getRotateXAngle() {
		return this.rotateXAngle;
	}

	public void arrangeEndpoints(PaneBox startBox, PaneBox endBox, int actualArrowNumber, int totalArrowNumber) {
		this.arrowNumber = actualArrowNumber;
		this.totalArrowNumber = totalArrowNumber;
		this.setPoints(startBox, endBox);
		drawArrow();
	}

}
