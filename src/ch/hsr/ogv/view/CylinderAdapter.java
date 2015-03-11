package ch.hsr.ogv.view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Transform;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class CylinderAdapter {
	
	private Cylinder cylinder;
	private Color color;
	
	public Color getColor() {
		return color;
	}

	public Cylinder get() {
		return this.cylinder;
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
		this.cylinder.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				cylinder.requestFocus();
			}
        });
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
		
	public DoubleProperty translateXProperty() {
		return this.cylinder.translateXProperty();
	}
	
	public DoubleProperty translateYProperty() {
		return this.cylinder.translateYProperty();
	}
	
	public DoubleProperty translateZProperty() {
		return this.cylinder.translateZProperty();
	}
	
	public ObservableList<Transform> getTransforms() {
		return this.cylinder.getTransforms();
	}
	
	public ReadOnlyBooleanProperty focusedProperty() {
		return this.cylinder.focusedProperty();
	}
	
	public void setVisible(boolean visible) {
		this.cylinder.setVisible(visible);
	}
	
	public boolean isVisible() {
		return this.cylinder.isVisible();
	}
	
	public void requestFocus() {
		this.cylinder.requestFocus();
	}
	
}
