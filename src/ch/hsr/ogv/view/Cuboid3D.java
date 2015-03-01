package ch.hsr.ogv.view;

import org.fxyz.shapes.primitives.CuboidMesh;

import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

public class Cuboid3D {
	
	private CuboidMesh box = null;
	private Color color;
	
	public Color getColor() {
		return color;
	}

	public CuboidMesh getNode() {
		return box;
	}
	
	public Cuboid3D() {
		this(Color.WHITE);
	}

	public Cuboid3D(Color color) {
		this.box = new CuboidMesh();
		this.box.setDepthTest(DepthTest.ENABLE);
		this.box.setCache(true);
		this.box.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		setColor(color);
	}
	
	public void setColor(Color color) {
		this.color = color;
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(this.color);
		material.setSpecularColor(this.color.brighter());
		box.setMaterial(material);
	}
	
	public void setSize(double width, double depth, double height) {
		this.box.setWidth(width);
		this.box.setDepth(depth);
		this.box.setHeight(height);
	}
	
	public void setPosition(double x, double z, double y) {
		this.box.setTranslateX(x);
		this.box.setTranslateZ(z);
		this.box.setTranslateY(y);
	}

}
