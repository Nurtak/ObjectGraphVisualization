package ch.hsr.ogv.view;

import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import jfxtras.labs.util.Util;

public class ArrowLabel extends Group {
	
	private GridPane container = new GridPane();
	private Text arrowText = new Text("");
	private TextField arrowTextField = new TextField("");
	private Point3D diffCoords = new Point3D(0, 0, 0);
	
	private Color color = Color.BLACK;
	
	private volatile boolean isLabelSelected = false;
	
	public GridPane getContainer() {
		return container;
	}
	
	public Point3D getDiffCoords() {
		return diffCoords;
	}

	public void setDiffCoords(Point3D diffCoords) {
		this.diffCoords = diffCoords;
		setTranslateDiff();
	}
	
	public ArrowLabel() {
		this.container.setStyle("-fx-border-color: transparent;\n" + "-fx-border-width: 1;");
		this.container.setMinWidth(10);
		this.container.setCache(true);
		this.container.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		this.container.setDepthTest(DepthTest.ENABLE);
		this.container.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		this.container.getTransforms().add(new Rotate(180, Rotate.Z_AXIS));
		this.container.setPadding(new Insets(1, 5, 1, 5));
		this.container.add(this.arrowText, 0, 0);
		setTranslateDiff();
		getChildren().add(this.container);
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		this.arrowText.setFill(color);
	}
	
	public boolean isLabelSelected() {
		return isLabelSelected;
	}

	public void setLabelSelected(boolean isLabelSelected) {
		this.isLabelSelected = isLabelSelected;
		if(isLabelSelected) {
			this.container.setStyle("-fx-border-color: " + Util.colorToCssColor(Arrow.SELECTION_COLOR) + ";\n" + "-fx-border-width: 1;");
			setColor(Arrow.SELECTION_COLOR);
		}
		else {
			this.container.setStyle("-fx-border-color: transparent;\n" + "-fx-border-width: 1;");
			setColor(Color.BLACK);
		}
	}
	
	public void allowTextInput(boolean value) {
		
	}
	
	public String getText() {
		return this.arrowText.getText();
	}
	
	public void setText(String text) {
		this.arrowText.setText(text);
		this.arrowTextField.setText(text);
	}
	
	public Font getFont() {
		return this.arrowText.getFont();
	}
	
	public void setFont(Font font) {
		this.arrowText.setFont(font);
		this.arrowTextField.setFont(font);
	}
	
	public void setDiffX(double x) {
		this.diffCoords = new Point3D(x, this.diffCoords.getY(), this.diffCoords.getZ());
		setTranslateX(this.diffCoords.getX());
	}
	
	public void setDiffY(double y) {
		this.diffCoords = new Point3D(this.diffCoords.getX(), y, this.diffCoords.getZ());
		setTranslateY(this.diffCoords.getY());
	}
	
	public void setDiffZ(double z) {
		this.diffCoords = new Point3D(this.diffCoords.getX(), this.diffCoords.getY(), z);
		setTranslateZ(this.diffCoords.getZ());
	}
	
	private void setTranslateDiff() {
		setTranslateX(this.diffCoords.getX());
		setTranslateY(this.diffCoords.getY());
		setTranslateZ(this.diffCoords.getZ());
	}
	
	public void addRotateYAxis(double degree) {
		getTransforms().add(new Rotate(degree, Rotate.Y_AXIS));
	}
	
}
