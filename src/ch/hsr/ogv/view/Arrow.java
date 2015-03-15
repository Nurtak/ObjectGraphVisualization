package ch.hsr.ogv.view;

import ch.hsr.ogv.view.ArrowHead.ArrowHeadType;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class Arrow extends Group {
	
	private static final int INIT_WIDTH = 2;
	private static final int INIT_LENGTH = 200;
	private double length = INIT_LENGTH;
	private double width = INIT_WIDTH;
	
	private double gap = 3;
	
	private Box line;
	private ArrowHead head;
	private Color color = Color.BLACK;

	public Arrow() {
		this.line = new Box(this.width, this.width, this.length);
		setColor(this.color);
		setArrowHeadType(ArrowHeadType.BORDER);
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
		this.head = new ArrowHead(type, this.color);
		this.head.setTranslateZ(this.length / 2 + this.gap);
		getChildren().clear();
		getChildren().add(this.line);
		getChildren().add(this.head);
	}
	
	public double getWidth() {
		return width;
	}
	
	public double getLength() {
		return length;
	}

	
	
}
