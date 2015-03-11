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
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Transform;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class SphereAdapter {
	
	private Sphere sphere;
	private Color color;
	
	public Color getColor() {
		return this.color;
	}

	public Sphere get() {
		return this.sphere;
	}
	
	public SphereAdapter() {
		this(5);
	}
	
	public SphereAdapter(double radius) {
		this(Color.WHITE, radius);
	}

	public SphereAdapter(Color color, double radius) {
		this.sphere = new Sphere(radius);
		this.sphere.setDepthTest(DepthTest.ENABLE);
		this.sphere.setCache(true);
		this.sphere.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		setColor(color);
        this.sphere.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				sphere.requestFocus();
			}
        });
	}
	
	public void setColor(Color color) {
		this.color = color;
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(this.color);
		material.setSpecularColor(this.color.brighter());
		sphere.setMaterial(material);
	}
	
	public DoubleProperty radiusProperty() {
		return this.sphere.radiusProperty();
	}
		
	public DoubleProperty translateXProperty() {
		return this.sphere.translateXProperty();
	}
	
	public DoubleProperty translateYProperty() {
		return this.sphere.translateYProperty();
	}
	
	public DoubleProperty translateZProperty() {
		return this.sphere.translateZProperty();
	}
	
	public ObservableList<Transform> getTransforms() {
		return this.sphere.getTransforms();
	}
	
	public ReadOnlyBooleanProperty focusedProperty() {
		return this.sphere.focusedProperty();
	}
	
	public void setVisible(boolean visible) {
		this.sphere.setVisible(visible);
	}
	
	public boolean isVisible() {
		return this.sphere.isVisible();
	}
	
	public void requestFocus() {
		this.sphere.requestFocus();
	}

}
