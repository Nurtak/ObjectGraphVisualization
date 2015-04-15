package ch.hsr.ogv.view;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import ch.hsr.ogv.model.LineType;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.GeometryUtil;

/**
 *
 * @author Simon Gwerder, Adrian Rieser
 *
 */
public class Arrow extends Group implements Selectable {

	private static final int INIT_WIDTH = 2;
	private static final int SELECTION_HELPER_WIDTH = 20;
	private static final int DASHED_ELEMENT_COUNT = 20;
	private double width = INIT_WIDTH;
	private static final Color SELECTION_COLOR = Color.DODGERBLUE;
	private double length;
	private double rotateZAngle;
	private double rotateXAngle;

	private static final double EDGE_SPACING = 3;

	private Point3D startPoint;
	private Point3D endPoint;

	private ArrowEdge arrowStart;
	private ArrowEdge arrowEnd;

	private RelationType type = RelationType.BIDIRECTED_ASSOCIATION;

	private Box line;
	private ArrayList<Box> dashedLines = new ArrayList<Box>();
	
	public static final Color DEFAULT_COLOR = Color.BLACK;
	private Color color = DEFAULT_COLOR;
	private ArrowSelection selection = null;
	private Box selectionHelper;

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

	public RelationType getType() {
		return type;
	}

	public void setType(RelationType type) {
		this.type = type;
		this.arrowStart.setEndpointType(type.getStartType());
		this.arrowEnd.setEndpointType(type.getEndType());
		drawArrow();
	}

	public Arrow(Point3D startPoint, Point3D endPoint, RelationType type) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.type = type;
		this.line = new Box(this.width, this.width, this.length);
		for (int i = 0; i < DASHED_ELEMENT_COUNT; i++) {
			Box dashedLine = new Box(this.width, this.width, this.length);
			dashedLine.setVisible(false);
			this.dashedLines.add(dashedLine);
		}
		this.arrowStart = new ArrowEdge(this.type.getStartType(), this.color);
		this.arrowEnd = new ArrowEdge(this.type.getEndType(), this.color);
		buildSelectionHelper();
		this.selection = new ArrowSelection();
		this.selection.setVisible(false);
		setColor(this.color);
		drawArrow();
		getChildren().addAll(this.line, this.arrowStart, this.arrowEnd, this.selectionHelper);
		getChildren().addAll(this.dashedLines);
	}

	public Arrow(PaneBox startBox, Point3D endPoint, RelationType type) {
		this(startBox.getCenterPoint(), endPoint, type);
		setPoints(startBox, endPoint);
		drawArrow();
	}
	
	public Arrow(PaneBox startBox, PaneBox endBox, RelationType type) {
		this(startBox.getCenterPoint(), endBox.getCenterPoint(), type);
		setPointsBasedOnBoxes(startBox, endBox);
		drawArrow();
	}
	
	public void setPoints(PaneBox startBox, Point3D endPoint) {
		setStartPoint(startBox.getCenterPoint());
		setEndPoint(endPoint);
		Point2D startIntersection = lineBoxIntersection(this.endPoint, startBox);
		if (startIntersection != null) {
			setStartPoint(new Point3D(startIntersection.getX(), this.startPoint.getY(), startIntersection.getY()));
		}
	}

	public void setPointsBasedOnBoxes(PaneBox startBox, PaneBox endBox) {
		setStartPoint(startBox.getCenterPoint());
		setEndPoint(endBox.getCenterPoint());
		Point2D startIntersection = lineBoxIntersection(this.endPoint, startBox);
		Point2D endIntersection = lineBoxIntersection(this.startPoint, endBox);
		if (startIntersection != null) {
			setStartPoint(new Point3D(startIntersection.getX(), this.startPoint.getY(), startIntersection.getY()));
		}
		if (endIntersection != null) {
			setEndPoint(new Point3D(endIntersection.getX(), endPoint.getY(), endIntersection.getY()));
		}
	}

	public void drawArrow() {
		getTransforms().clear();
		this.length = this.startPoint.distance(this.endPoint);

		boolean isDashedLine = LineType.DASHED_LINE.equals(this.type.getLineType());
		this.line.setVisible(!isDashedLine);
		for (Box dashedLine : this.dashedLines) {
			dashedLine.setVisible(isDashedLine);
		}

		setArrowLineEdge();

		double endGap = this.arrowEnd.getAdditionalGap();
		double startGap = this.arrowStart.getAdditionalGap();

		this.length -= (endGap + startGap) / 2;

		this.line.setDepth(this.length);
		this.line.setTranslateZ((-endGap + startGap) / 4);

		ArrayList<Point3D> dashedLineCoords = divideLine(new Point3D(0, 0, -this.length / 2), new Point3D(0, 0, this.length / 2), this.dashedLines.size());

		for (int i = 0; i < this.dashedLines.size(); i++) {
			Box dashedLine = this.dashedLines.get(i);
			dashedLine.setDepth(this.length / (2 * DASHED_ELEMENT_COUNT));
			Point3D dashedLineCoord = dashedLineCoords.get(i);
			dashedLine.setTranslateZ(dashedLineCoord.getZ() - dashedLine.getDepth() + (-endGap + startGap) / 4);
		}

		this.rotateZAngle = GeometryUtil.rotateZAngle(this.startPoint, this.endPoint);
		this.rotateXAngle = -GeometryUtil.rotateXAngle(this.startPoint, this.endPoint);

		Point3D midPoint = this.startPoint.midpoint(this.endPoint);
		setTranslateXYZ(midPoint);
		addRotateYAxis(this.rotateZAngle);
		addRotateXAxis(this.rotateXAngle);
		this.selection.setStartEndXYZ(this.startPoint, this.endPoint);
	}

	private Point2D lineBoxIntersection(Point3D externalPoint, PaneBox box) {
		Point3D boxCenter = box.getCenterPoint();
		double halfWidth = box.getWidth() / 2;
		double halfHeight = box.getHeight() / 2;

		Point2D lineStart = new Point2D(externalPoint.getX(), externalPoint.getZ());
		Point2D lineEnd = new Point2D(boxCenter.getX(), boxCenter.getZ());
		Point2D northEast = new Point2D(boxCenter.getX() - halfWidth, boxCenter.getZ() + halfHeight);
		Point2D southEast = new Point2D(boxCenter.getX() - halfWidth, boxCenter.getZ() - halfHeight);
		Point2D southWest = new Point2D(boxCenter.getX() + halfWidth, boxCenter.getZ() - halfHeight);
		Point2D northWest = new Point2D(boxCenter.getX() + halfWidth, boxCenter.getZ() + halfHeight);

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

	private Point3D divideLineFraction(Point3D start, Point3D end, double fraction) {
		double x = start.getX() + fraction * (end.getX() - start.getX());
		double y = start.getY() + fraction * (end.getY() - start.getY());
		double z = start.getZ() + fraction * (end.getZ() - start.getZ());
		return new Point3D(x, y, z);
	}

	private ArrayList<Point3D> divideLine(Point3D start, Point3D end, int count) {
		ArrayList<Point3D> pointList = new ArrayList<Point3D>();
		for (int i = 1; i <= count; i++) {
			pointList.add(divideLineFraction(start, end, ((double) i) / ((double) count)));
		}
		return pointList;
	}

	private void setArrowLineEdge() {
		this.arrowStart.setTranslateX(0);
		this.arrowStart.setTranslateY(0);
		this.arrowStart.setTranslateZ(0);
		this.arrowStart.getTransforms().clear();
		this.arrowStart.getTransforms().add(new Rotate(180, arrowStart.getTranslateX(), arrowStart.getTranslateY(), arrowStart.getTranslateZ(), Rotate.Y_AXIS));
		this.arrowStart.setTranslateZ(-this.length / 2 - EDGE_SPACING);
		this.arrowEnd.setTranslateZ(this.length / 2 + EDGE_SPACING);
	}

	public void setColor(Color color) {
		this.color = color;
		applyColor(this.line, this.color);
		for (Box dashedLine : this.dashedLines) {
			applyColor(dashedLine, this.color);
		}
		this.arrowStart.setColor(color);
		this.arrowEnd.setColor(color);
	}

	private void applyColor(Box box, Color color) {
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(color);
		material.setSpecularColor(color.brighter());
		box.setMaterial(material);
	}

	public Color getColor() {
		return this.color;
	}

	private void buildSelectionHelper() {
		this.selectionHelper = new Box(SELECTION_HELPER_WIDTH, SELECTION_HELPER_WIDTH, this.length);
		this.selectionHelper.depthProperty().bind(this.line.depthProperty());
		this.selectionHelper.translateXProperty().bind(this.line.translateXProperty());
		this.selectionHelper.translateYProperty().bind(this.line.translateYProperty());
		this.selectionHelper.translateZProperty().bind(this.line.translateZProperty());
		this.selectionHelper.rotateProperty().bind(this.line.rotateProperty());
		this.selectionHelper.setOpacity(0.0); // dont want to see it, but still receive mouse events
	}

	@Override
	public void setSelected(boolean selected) {
		this.selection.setVisible(selected);
		Color colorToApply = getColor();
		if (selected) {
			colorToApply = SELECTION_COLOR;
		}
		applyColor(this.line, colorToApply);
		for(Box dashedLine : this.dashedLines) {
			applyColor(dashedLine, colorToApply);
		}
		this.arrowStart.setColor(colorToApply);
		this.arrowEnd.setColor(colorToApply);
	}

	@Override
	public boolean isSelected() {
		return this.selection.isVisible();
	}

	@Override
	public ArrowSelection getSelection() {
		return this.selection;
	}

	public Box getSelectionHelper() {
		return selectionHelper;
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

	public double getLength() {
		return this.length;
	}

	public double getRotateZAngle() {
		return this.rotateZAngle;
	}

	public double getRotateXAngle() {
		return this.rotateXAngle;
	}

}
