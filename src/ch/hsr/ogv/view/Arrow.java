package ch.hsr.ogv.view;

import java.util.Observable;
import java.util.Observer;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import ch.hsr.ogv.controller.DragController;
import ch.hsr.ogv.util.GeometryUtil;
import ch.hsr.ogv.view.ArrowHead.ArrowHeadType;

/**
 * 
 * @author Simon Gwerder, Adrian Rieser
 *
 */
public class Arrow extends Group implements Observer {

	private static final int INIT_WIDTH = 2;
	private double width = INIT_WIDTH;
	private double length;
	private double angle;

	private double gap = 3;

	private PaneBox startBox;
	private PaneBox endBox;

	private Box line;
	private ArrowHead head;
	private Color color = Color.BLACK;

	public Arrow(PaneBox startBox, PaneBox endBox) {
		this.startBox = startBox;
		this.endBox = endBox;
		this.line = new Box(this.width, this.width, this.length);
		drawArrow();
	}

	private void drawArrow() {
		getTransforms().clear();
		Point3D startPoint = this.startBox.getCenterPoint();
		Point3D endPoint = this.endBox.getCenterPoint();

		Point2D arrowEndPoint2D = lineBoxIntersection(startPoint, this.endBox);
		Point2D arrowStartPoint2D = lineBoxIntersection(endPoint, this.startBox);
		Point3D arrowEndPoint3D = endPoint;
		Point3D arrowStartPoint3D = startPoint;
		if(arrowEndPoint2D != null) arrowEndPoint3D = new Point3D(arrowEndPoint2D.getX(), endPoint.getY(), arrowEndPoint2D.getY());
		if(arrowStartPoint2D != null) arrowStartPoint3D = new Point3D(arrowStartPoint2D.getX(), startPoint.getY(), arrowStartPoint2D.getY());
		
		this.length = arrowStartPoint3D.distance(arrowEndPoint3D);
		this.line.setDepth(this.length);
		
		setArrowHeadType(ArrowHeadType.OPEN);
		setColor(this.color);
		
		Point3D midPoint = arrowStartPoint3D.midpoint(arrowEndPoint3D);
		setTranslateXYZ(midPoint);
		
		double angle = GeometryUtil.getAngleBetweenXandZ(startPoint, endPoint);
		addRotate(angle);
	}

	private Point2D lineBoxIntersection(Point3D externalPoint, PaneBox box) {
		Point3D boxCenter = box.getCenterPoint();
		double halfWidth = box.getWidth() / 2;
		double halfHeight = box.getHeight() / 2;
		
		Point2D lineStart = new Point2D(externalPoint.getX(), externalPoint.getZ());
		Point2D lineEnd   = new Point2D(boxCenter.getX(), boxCenter.getZ());
		Point2D northEast  = new Point2D(boxCenter.getX() - halfWidth, boxCenter.getZ() + halfHeight);
		Point2D southEast  = new Point2D(boxCenter.getX() - halfWidth, boxCenter.getZ() - halfHeight);
		Point2D southWest  = new Point2D(boxCenter.getX() + halfWidth, boxCenter.getZ() - halfHeight);
		Point2D northWest  = new Point2D(boxCenter.getX() + halfWidth, boxCenter.getZ() + halfHeight);
		
		Point2D interEastHeight = GeometryUtil.lineIntersect(lineStart, lineEnd, northEast, southEast);
		Point2D interWestHeight = GeometryUtil.lineIntersect(lineStart, lineEnd, northWest, southWest);
		Point2D interNorthWidth = GeometryUtil.lineIntersect(lineStart, lineEnd, northEast, northWest);
		Point2D interSouthWidth = GeometryUtil.lineIntersect(lineStart, lineEnd, southEast, southWest);
		
		if(interEastHeight != null) return interEastHeight;
		if(interWestHeight != null) return interWestHeight;
		if(interNorthWidth != null) return interNorthWidth;
		if(interSouthWidth != null) return interSouthWidth;
		return null;
	}
	
	public void setColor(Color color) {
		this.color = color;
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(this.color);
		material.setSpecularColor(this.color.brighter());
		this.line.setMaterial(material);
	}

	public Color getColor() {
		return this.color;
	}

	public void setArrowHeadType(ArrowHeadType type) {
		getChildren().clear();
		getChildren().add(this.line);
		this.head = new ArrowHead(type, this.color);
		this.head.setTranslateZ(this.length / 2 + this.gap);
		getChildren().add(this.head);
	}

	public void setTranslateXYZ(Point3D point) {
		setTranslateXYZ(point.getX(), point.getY(), point.getZ());
	}

	public void setTranslateXYZ(double x, double y, double z) {
		getTransforms().add(new Translate(x, y, z));
	}

	public void addRotate(double degree) {
		getTransforms().add(new Rotate(degree, getTranslateX(), getTranslateY(), getTranslateZ(), Rotate.Y_AXIS));
	}

	public double getWidth() {
		return this.width;
	}

	public double getLength() {
		return this.length;
	}

	public double getAngle() {
		return this.angle;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof DragController) {
			drawArrow();
		}
		
	}

}
