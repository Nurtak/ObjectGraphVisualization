package ch.hsr.ogv.view;

import ch.hsr.ogv.util.ColorUtil;
import ch.hsr.ogv.util.FXMLResourceUtil;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.util.TextUtil;
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

/**
 * 
 * @author Simon Gwerder
 * @version OGV 3.1, May 2015
 *
 */
public class ArrowLabel extends Group {
	
	private static final double MIN_WIDTH = 20;
	private static final Color SELECTION_COLOR = Color.DODGERBLUE;
	
	private HBox container = new HBox();
	private Text arrowText;
	private TextField arrowTextField;
	private Point3D coords = new Point3D(0, 0, 0);
	
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
		return coords;
	}

	public void setDiffCoords(Point3D diffCoords) {
		this.coords = diffCoords;
		setTranslate();
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
		setTranslate();
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
			this.container.setStyle("-fx-border-color: " + ColorUtil.colorToCssColor(SELECTION_COLOR) + ";\n" + "-fx-border-width: 1;");
			setColor(SELECTION_COLOR);
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
	
	public String getTextFieldText() {
		return this.arrowTextField.getText();
	}
	
	public String getLabelText() {
		return this.arrowText.getText();
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
	
	public void setCoordsX(double x) {
		this.coords = new Point3D(x, this.coords.getY(), this.coords.getZ());
		setTranslateX(this.coords.getX());
	}
	
	public void setCoordsY(double y) {
		this.coords = new Point3D(this.coords.getX(), y, this.coords.getZ());
		setTranslateY(this.coords.getY());
	}
	
	public void setCoordsZ(double z) {
		this.coords = new Point3D(this.coords.getX(), this.coords.getY(), z);
		setTranslateZ(this.coords.getZ());
	}
	
	public void setTranslateXYZ(double x, double y, double z) {
		setCoordsX(x);
		setCoordsY(y);
		setCoordsZ(z);
	}
	
	private void setTranslate() {
		setTranslateX(this.coords.getX());
		setTranslateY(this.coords.getY());
		setTranslateZ(this.coords.getZ());
	}
	
	public void setRotateYAxis(double degree) {
		setRotationAxis(Rotate.Y_AXIS);
		setRotate(degree);
	}
	
	public double calcMinWidth() {
		return TextUtil.computeTextWidth(getFont(), getTextFieldText(), 0.0D) + 15;
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
