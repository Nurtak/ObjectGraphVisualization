package ch.hsr.ogv.view;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Object3D {
	
	private Box box = null;
	
	public Box getBox() {
		return box;
	}

	public Object3D() {
		this.box = new Box(10, 10, 10);
	}
	
	public void setColor(Color color) {
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(color);
		material.setSpecularColor(color.brighter());
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
