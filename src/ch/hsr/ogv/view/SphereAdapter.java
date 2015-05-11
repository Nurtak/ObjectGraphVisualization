package ch.hsr.ogv.view;

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class SphereAdapter extends Group {

	private Sphere sphere;
	private Color color;

	public Color getColor() {
		return this.color;
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
		getChildren().add(this.sphere);
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

	public void requestFocus() {
		this.sphere.requestFocus();
	}

}
