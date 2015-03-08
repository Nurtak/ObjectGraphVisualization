package ch.hsr.ogv.view;

import org.fxyz.shapes.primitives.CuboidMesh;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Transform;

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
		this(10);
	}
	
	public Cuboid3D(double size) {
		this(Color.WHITE, size);
	}

	public Cuboid3D(Color color, double size) {
		this.box = new CuboidMesh(size, size , size);
		this.box.setDepthTest(DepthTest.ENABLE);
		this.box.setCache(true);
		this.box.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		setColor(color);
        this.box.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				box.requestFocus();
			}

        });
	}
	
	public void setColor(Color color) {
		this.color = color;
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(this.color);
		material.setSpecularColor(this.color.brighter());
		box.setMaterial(material);
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
	
	public DoubleProperty translateXProperty() {
		return this.box.translateXProperty();
	}
	
	public DoubleProperty translateYProperty() {
		return this.box.translateYProperty();
	}
	
	public DoubleProperty translateZProperty() {
		return this.box.translateZProperty();
	}
	
	public ObservableList<Transform> getTransforms() {
		return this.box.getTransforms();
	}
	
	public ReadOnlyBooleanProperty focusedProperty() {
		return this.box.focusedProperty();
	}
	
	public void requestFocus() {
		this.box.requestFocus();
	}
		
	public void setDrawTopFace(boolean drawTopFace) {
		this.box.setDrawTopFace(drawTopFace);
	}
	
	public void setVisible(boolean visible) {
		this.box.setVisible(visible);
	}
	
	public boolean isVisible() {
		return this.box.isVisible();
	}

}
