package ch.hsr.ogv.view;

import org.fxyz.shapes.primitives.CuboidMesh;

import javafx.beans.property.DoubleProperty;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * 
 * @author Simon Gwerder
 * @version OGV 3.1, May 2015
 *
 */
public class Cuboid extends Group {

	private CuboidMesh box = null;
	private Color color;

	public Cuboid() {
		this(10);
	}

	public Cuboid(double size) {
		this(Color.WHITE, size);
	}

	public Cuboid(Color color, double size) {
		this.box = new CuboidMesh(size, size, size);
		this.box.setDepthTest(DepthTest.ENABLE);
		this.box.setCache(true);
		this.box.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		setColor(color);
		getChildren().add(this.box);
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color color) {
		this.color = color;
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(this.color);
		material.setSpecularColor(this.color.brighter());
		this.box.setMaterial(material);
	}

	public DoubleProperty widthProperty() {
		return this.box.widthProperty();
	}

	public DoubleProperty heightProperty() {
		return this.box.heightProperty();
	}

	public DoubleProperty depthProperty() {
		return this.box.depthProperty();
	}

	public void setDrawTopFace(boolean drawTopFace) {
		this.box.setDrawTopFace(drawTopFace);
	}

	public void setDepth(double value) {
		this.box.setDepth(value);
	}

	public double getDepth() {
		return this.box.getDepth();
	}

}
