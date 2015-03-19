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
		drawArrow();
	}

	private void drawArrow() {
		getTransforms().clear();
		Point3D startPoint = this.startBox.getCenterPoint();
		Point3D endPoint = this.endBox.getCenterPoint();

		Point2D interEndPoint2D = intersectionEndpoint();
		Point3D interEndPoint3D = endPoint;
		if(interEndPoint2D != null) {
			interEndPoint3D = new Point3D(interEndPoint2D.getX(), endPoint.getY(), interEndPoint2D.getY());
		}

		this.length = startPoint.distance(interEndPoint3D);
		if(this.line == null) {
			this.line = new Box(this.width, this.width, this.length);
		}
		this.line.setDepth(this.length);
		setTranslateXYZ(startPoint.midpoint(interEndPoint3D));
		double angle = getAngleBetweenXandZ(startPoint, endPoint);
		addRotate(angle);

		setArrowHeadType(ArrowHeadType.OPEN);
		setColor(this.color);
	}

	private double getAngleBetweenXandZ(Point3D p1, Point3D p2) {
		double xDiff = p2.getX() - p1.getX();
		double zDiff = p2.getZ() - p1.getZ();
		return Math.toDegrees(Math.atan2(xDiff, zDiff));
	}
	
	private Point2D intersectionEndpoint() {
		Point3D startPoint = this.startBox.getCenterPoint();
		Point3D endPoint   = this.endBox.getCenterPoint();
		
		double halfWidth = endBox.getWidth() / 2;
		double halfHeight = endBox.getHeight() / 2;
		
		Point2D arrowStart = new Point2D(startPoint.getX(), startPoint.getZ());
		Point2D arrowEnd   = new Point2D(endPoint.getX(), endPoint.getZ());
		Point2D northEast  = new Point2D(endPoint.getX() - halfWidth, endPoint.getZ() + halfHeight);
		Point2D southEast  = new Point2D(endPoint.getX() - halfWidth, endPoint.getZ() - halfHeight);
		Point2D southWest  = new Point2D(endPoint.getX() + halfWidth, endPoint.getZ() - halfHeight);
		Point2D northWest  = new Point2D(endPoint.getX() + halfWidth, endPoint.getZ() + halfHeight);
		
		Point2D interEastHeight = lineIntersect(arrowStart, arrowEnd, northEast, southEast);
		Point2D interWestHeight = lineIntersect(arrowStart, arrowEnd, northWest, southWest);
		Point2D interNorthWidth = lineIntersect(arrowStart, arrowEnd, northEast, northWest);
		Point2D interSouthWidth = lineIntersect(arrowStart, arrowEnd, southEast, southWest);
		
		if(interEastHeight != null) {
			return interEastHeight;
		}
		
		if(interWestHeight != null) {
			return interWestHeight;
		}
		
		if(interNorthWidth != null) {
			return interNorthWidth;
		}
		
		if(interSouthWidth != null) {
			return interSouthWidth;
		}
		
		return null;
	}

    private Point2D lineIntersect(Point2D firstLineStart, Point2D firstLineEnd, Point2D secondLineStart, Point2D secondLineEnd) {
    	double x1 = firstLineStart.getX();
    	double y1 = firstLineEnd.getY();
    	double x2 = firstLineEnd.getX();
    	double y2 = firstLineEnd.getY();
    	double x3 = secondLineStart.getX();
    	double y3 = secondLineStart.getY();
    	double x4 = secondLineEnd.getX();
    	double y4 = secondLineEnd.getY();
    	double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		if (denom == 0.0) { // Lines are parallel.
		   return null;
		}
		double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
		double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;
		if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
		    return new Point2D((x1 + ua*(x2 - x1)), (y1 + ua*(y2 - y1))); // Get the intersection point.
		}
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
