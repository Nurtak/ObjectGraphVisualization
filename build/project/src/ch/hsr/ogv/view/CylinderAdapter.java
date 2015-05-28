package ch.hsr.ogv.view;

import javafx.beans.property.DoubleProperty;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class CylinderAdapter extends Group {

	private Cylinder cylinder;
	private Color color;

	public Color getColor() {
		return color;
	}

	public CylinderAdapter() {
		this(5);
	}

	public CylinderAdapter(double radius) {
		this(Color.WHITE, radius, 10);
	}

	public CylinderAdapter(Color color, double radius, double height) {
		this.cylinder = new Cylinder(radius, height);
		this.cylinder.setDepthTest(DepthTest.ENABLE);
		this.cylinder.setCache(true);
		this.cylinder.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		setColor(color);
		getChildren().add(this.cylinder);
	}

	public void setColor(Color color) {
		this.color = color;
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(this.color);
		material.setSpecularColor(this.color.brighter());
		this.cylinder.setMaterial(material);
	}

	public DoubleProperty radiusProperty() {
		return this.cylinder.radiusProperty();
	}

	public DoubleProperty heightProperty() {
		return this.cylinder.heightProperty();
	}

}
