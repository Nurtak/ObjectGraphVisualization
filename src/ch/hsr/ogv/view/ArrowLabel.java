package ch.hsr.ogv.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import jfxtras.labs.util.Util;
import ch.hsr.ogv.util.FXMLResourceUtil;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.util.TextUtil;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class ArrowLabel extends Group {
	
	private static final double MIN_WIDTH = 20;
	
	private HBox container = new HBox();
	private Text arrowText;
	private TextField arrowTextField;
	private Point3D diffCoords = new Point3D(0, 0, 0);
	
	private Color color = Color.BLACK;
	
	private volatile boolean isLabelSelected = false;
	
	public HBox getContainer() {
		return container;
	}
	
	public Text getArrowText() {
		return arrowText;
	}

	public TextField getArrowTextField() {
		return arrowTextField;
	}
	
	public Point3D getDiffCoords() {
		return diffCoords;
	}

	public void setDiffCoords(Point3D diffCoords) {
		this.diffCoords = diffCoords;
		setTranslateDiff();
	}
	
	public ArrowLabel() {
		this.arrowText = new Text("");
		this.arrowTextField = loadTextField();
		this.container.setStyle("-fx-border-color: transparent;\n" + "-fx-border-width: 1;");
		this.container.setCacheHint(CacheHint.SCALE_AND_ROTATE);
		this.container.setDepthTest(DepthTest.ENABLE);
		this.container.setAlignment(Pos.CENTER);
		this.container.getChildren().add(this.arrowText);
		this.container.setMaxWidth(Double.MAX_VALUE);
		this.container.setPadding(new Insets(2, 0, 2, 0));
		HBox.setHgrow(this.arrowTextField, Priority.ALWAYS);
		setWidth(calcMinWidth());
		setTranslateDiff();
		this.container.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		this.container.getTransforms().add(new Rotate(180, Rotate.Z_AXIS));
		getChildren().add(this.container);
		showLabel(false);
	}
	
	private TextField loadTextField() {
		Object loadedPreset = FXMLResourceUtil.loadPreset(Resource.ARROWTEXTFIELD_FXML); // load center textfield preset from fxml file
		if (loadedPreset != null && loadedPreset instanceof TextField) {
			return (TextField) loadedPreset;
		}
		return new TextField();
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
		this.container.getChildren().clear();
		if (value) {
			this.container.getChildren().add(this.arrowTextField);
			this.container.setPadding(new Insets(0, 0, 0, 0));
			Platform.runLater(() -> {
				this.arrowTextField.requestFocus();
				this.arrowTextField.selectAll();
				this.arrowTextField.applyCss();
			});
		} else {
			this.container.getChildren().add(this.arrowText);
			this.container.setPadding(new Insets(2, 0, 2, 0));
			if (isLabelSelected()) {
				setLabelSelected(true);
			}
		}
		this.arrowTextField.setEditable(value);
		this.arrowTextField.setDisable(!value);
	}
	
	public String getText() {
		return this.arrowTextField.getText();
	}
	
	public void setText(String text) {
		showLabel(text != null && !text.isEmpty());
		this.arrowText.setText(text);
		this.arrowTextField.setText(text);
	}
	
	public Font getFont() {
		return this.arrowTextField.getFont();
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
	
	public double calcMinWidth() {
		return TextUtil.computeTextWidth(getFont(), getText(), 0.0D) + 15;
	}
	
	public void setWidth(double width) {
		if(width < MIN_WIDTH) {
			width = MIN_WIDTH;
		}
		this.container.setMinWidth(width);
		this.container.prefWidth(width);
		this.arrowText.prefWidth(width);
		this.arrowTextField.setPrefWidth(width);
		this.arrowTextField.selectAll();
		this.arrowTextField.deselect();
	}
	
	public void showLabel(boolean show) {
		if(!show) {
			setLabelSelected(false);
			allowTextInput(false);
			this.arrowText.setText("");
			this.arrowTextField.setText("");
		}
		this.arrowText.setVisible(show);
		this.arrowText.setDisable(!show);
		this.arrowTextField.setVisible(show);
		this.arrowTextField.setEditable(show);
		this.arrowTextField.setDisable(!show);
		setMouseTransparent(!show);
	}
	
}
