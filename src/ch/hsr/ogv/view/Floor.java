package ch.hsr.ogv.view;

import javafx.geometry.Point3D;
import javafx.scene.DepthTest;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class Floor {
	
	private final double SIZE = 10000;
	private Color color = Color.WHITESMOKE;
	
	private Rectangle floor;
	
	public Rectangle get() {
		return this.floor;
	}
	
	public Floor() {
		this.floor = new Rectangle(SIZE, SIZE, color);
		setMouseTransparent(true);
		this.floor.setDepthTest(DepthTest.ENABLE);
		this.floor.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		this.floor.setTranslateX(- SIZE / 2);
		this.floor.setTranslateZ(- SIZE / 2);
		this.floor.setOpacity(0.3);
	}
	
	public void setSeeable(boolean value) {
		if(value) {
			this.floor.setFill(getColor());
		}
		else {
			this.floor.setFill(Color.TRANSPARENT);
		}
	}
	
	public void setColor(Color color) {
		if(this.floor != null) this.floor.setFill(color);
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}
		
	public void setMouseTransparent(boolean value) {
		this.floor.setMouseTransparent(value);
	}
	
	public Point3D localToParent(double x, double y, double z) {
		return localToParent(new Point3D(x,y,z));
	}
	
	public Point3D localToParent(Point3D coords) {
		return this.floor.localToParent(coords);
	}

}
