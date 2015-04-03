package ch.hsr.ogv.view;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import jfxtras.labs.util.Util;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class PaneBox implements Selectable {
	
	private final static Logger logger = LoggerFactory.getLogger(PaneBox.class);
	
	public final static int CLASSBOX_DEPTH = 10;
	public final static int OBJECTBOX_DEPTH = 20;
	public final static int INIT_DEPTH = 10;

	public final static Color DEFAULT_COLOR = Color.CORNSILK;
	
	public final static int MIN_WIDTH = 100;
	public final static int MIN_HEIGHT = 100;
	
	public final static int MAX_WIDTH = 500;
	public final static int MAX_HEIGHT = 500;
	
	private Group paneBox = new Group();
	private BoxSelection selection = null;
	private BorderPane borderPane = null;

	private Label topLabel = null;
	private TextField topTextField = null;
	private Color color;
	private Cuboid box;
	
	public Group get() {
		return this.paneBox;
	}
	
	public Cuboid getBox() {
		return this.box;
	}

	public BoxSelection getSelection() {
		return this.selection;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		this.borderPane.setStyle(getPaneStyle());
		this.box.setColor(color);
	}
	
	public PaneBox() {
		this(DEFAULT_COLOR);
	}
	
	public PaneBox(Color color) {
		initLayout();
        this.borderPane.setCache(true);
        this.borderPane.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        this.borderPane.setDepthTest(DepthTest.ENABLE);
        
        // rotate the pane correctly
        this.borderPane.getTransforms().add(new Rotate(180, Rotate.Y_AXIS));
        this.borderPane.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        
        // position the pane so, that the center is at the scene's origin (0, 0, 0)
        this.borderPane.translateXProperty().bind(this.borderPane.widthProperty().divide(2));
        this.borderPane.translateZProperty().bind(this.borderPane.heightProperty().divide(2));
        
        // build the box that stays beneath the borderPane
        buildBox();
        
        // create the selection objects that stays with this box
        this.selection = new BoxSelection(getBox());
        this.selection.setVisible(false);
        
        //this.paneBoxSelection.getChildren().addAll(this.borderPane, this.box.getNode(), this.selection.getNode());
        this.paneBox.getChildren().addAll(this.borderPane, this.box);
        
        // position the whole group so, that the center is at scene's origin (0, 0, 0)
        setTranslateY(INIT_DEPTH / 2);
        setColor(color);
	}
	
	private String getPaneStyle() {
		return "-fx-background-color: " + Util.colorToCssColor(getColor()) + ";\n"
		+ "-fx-border-color: black;\n"
		+ "-fx-border-width: 2;";
	}
	
	private void initLayout() {
		FXMLLoader loader = new FXMLLoader(); // load pane preset from fxml file
        loader.setLocation(ResourceLocator.getResourcePath(Resource.PANEPRESET_FXML));
        try {
			this.borderPane = (BorderPane) loader.load();
		} catch (IOException e) {
			logger.debug(e.getMessage());
            e.printStackTrace();
		}
        
		loader = new FXMLLoader(); // load textfield preset from fxml file
        loader.setLocation(ResourceLocator.getResourcePath(Resource.TEXTFIELDPRESET_FXML));
		try {
			this.topTextField = (TextField) loader.load();
		} catch (IOException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}

		Node topNode = this.borderPane.getTop();
		if ((topNode instanceof HBox)) {
			HBox topHBox = (HBox) topNode;
			if (!topHBox.getChildren().isEmpty() && topHBox.getChildren().get(0) instanceof Label) {
				this.topLabel = (Label) topHBox.getChildren().get(0);
			}
			HBox.setMargin(this.topTextField, new Insets(-1, -1, 0, -1));
			HBox.setHgrow(this.topTextField, Priority.ALWAYS);
			HBox.setMargin(this.topLabel, new Insets(-1, -1, 0, -1));
			HBox.setHgrow(this.topLabel, Priority.ALWAYS);
		}
	}
	
	private void buildBox() {
		this.box = new Cuboid(INIT_DEPTH);
		this.box.setDrawTopFace(false);
		this.box.widthProperty().bind(this.borderPane.widthProperty());
		this.box.heightProperty().bind(this.borderPane.heightProperty());
		this.box.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		this.box.translateXProperty().bind(this.borderPane.translateXProperty().subtract(this.borderPane.widthProperty().divide(2)));
		this.box.translateZProperty().bind(this.borderPane.translateZProperty().subtract(this.borderPane.heightProperty().divide(2)));
		this.box.translateYProperty().bind(this.borderPane.translateYProperty().subtract(INIT_DEPTH / 2));
	}
	
	public TextField getTopTextField() {
		return this.topTextField;
	}
	
	public Label getTopLabel() {
		return this.topLabel;
	}
	
	private void swapTop(Node labelOrField) {
		Node topNode = this.borderPane.getTop();
		if ((topNode instanceof HBox)) {
			HBox topHBox = (HBox) topNode;
			topHBox.getChildren().clear();
			topHBox.getChildren().add(labelOrField);
		}
	}
	
	public void setTopText(String text) {
		if (this.topTextField == null || this.topLabel == null) return;
		this.topTextField.setText(text);
		this.topLabel.setText(text);
	}

	public void setTopFont(Font font) {
		if (this.topTextField == null || this.topLabel == null) return;
		this.topTextField.setFont(font);
		this.topLabel.setFont(font);
	}
	
	public Font getTopFont() {
		return this.topLabel.getFont(); // doesnt matter if Font is taken from topLabel or topTextField
	}

	public void setTopUnderline(boolean underline) {
		this.topLabel.setUnderline(underline);
	}

	public void allowTopTextInput(boolean value) {
		if (value) {
			swapTop(this.topTextField);
			Platform.runLater(() -> {
				this.topTextField.requestFocus();
				this.topTextField.selectAll();
				this.topTextField.applyCss();
			});
		} else {
			swapTop(this.topLabel);
		}
		this.topTextField.setEditable(value);
		this.topTextField.setDisable(!value);
	}

	public GridPane getCenter() {
		Node centerNode = this.borderPane.getCenter();
		if (centerNode instanceof GridPane) {
			return (GridPane) centerNode;
		}
		return null;
	}
	
	public void setCenterText(int rowIndex, String text) {
		Node node = null;
		try {
			node = getCenter().getChildren().get(rowIndex);
		}
		catch(IndexOutOfBoundsException iobe) {
		}
		if(node != null && node instanceof TextField) {
			TextField centerTextField = (TextField) node;
			centerTextField.setText(text);
		}
	}
	
	public void setSelected(boolean selected) {
		this.selection.setVisible(selected);
		if (!selected) {
			allowTopTextInput(false);
		}
	}
	
	public boolean isSelected() {
		return this.selection.isVisible();
	}
	
	private double restrictedWidth(double width) {
		double retWidth = width;
		if (width < MIN_WIDTH) {
			retWidth = MIN_WIDTH;
		} else if (width > MAX_WIDTH) {
			retWidth = MAX_WIDTH;
		}
		return retWidth;
	}

	private double restrictedHeight(double height) {
		double retHeight = height;
		if (height < MIN_HEIGHT) {
			retHeight = MIN_HEIGHT;
		} else if (height > MAX_HEIGHT) {
			retHeight = MAX_HEIGHT;
		}
		return retHeight;
	}

	public void setWidth(double witdh) {
		this.borderPane.setPrefWidth(witdh);
	}

	public double getWidth() {
		return this.borderPane.getPrefWidth();
	}

	/**
	 * Sets the minimum width of this box. Note that it can not be set blow
	 * {@link PaneBox#MIN_WIDTH}.
	 * 
	 * @param width
	 */
	public void setMinWidth(double width) {
		this.borderPane.setMinWidth(restrictedWidth(width));
	}

	public double getMinWidth() {
		return this.borderPane.getMinWidth();
	}

	public void setHeight(double height) {
		this.borderPane.setPrefHeight(height);
	}

	public double getHeight() {
		return this.borderPane.getPrefHeight();
	}

	/**
	 * Sets the maximum width of this box. Note that it can not be set blow
	 * {@link PaneBox#MAX_WIDTH}.
	 * 
	 * @param width
	 */
	public void setMaxWidth(double width) {
		this.borderPane.setMaxWidth(restrictedWidth(width));
	}

	public double getMaxWidth() {
		return this.borderPane.getMaxWidth();
	}

	/**
	 * Sets the minimum height of this box. Note that it can not be set above
	 * {@link PaneBox#MIN_HEIGHT}.
	 * 
	 * @param height
	 */
	public void setMinHeight(double height) {
		this.borderPane.setMinHeight(restrictedHeight(height));
	}

	public double getMinHeight() {
		return this.borderPane.getMinHeight();
	}

	/**
	 * Sets the maximum height of this box. Note that it can not be set above
	 * {@link PaneBox#MAX_HEIGHT}.
	 * 
	 * @param width
	 */
	public void setMaxHeight(double height) {
		this.borderPane.setMaxHeight(restrictedHeight(height));
	}

	public double getMaxHeight() {
		return this.borderPane.getMaxHeight();
	}

	public void setDepth(double depth) {
		this.box.setDepth(depth);
		this.box.translateYProperty().bind(this.borderPane.translateYProperty().subtract(depth / 2));
		setTranslateY((depth / 2) - (getTranslateY() / 2));
	}

	public double getDepth() {
		return this.box.getDepth();
	}

	public void setTranslateY(double y) {
		this.paneBox.setTranslateY(y);
		this.selection.setTranslateY(y);
	}

	public void setTranslateXYZ(Point3D point) {
		setTranslateXYZ(point.getX(), point.getY(), point.getZ());
	}

	public void setTranslateXYZ(double x, double y, double z) {
		setTranslateX(x);
		setTranslateY(y);
		setTranslateZ(z);
	}

	public void setTranslateX(double x) {
		this.paneBox.setTranslateX(x);
		this.selection.setTranslateX(x);
	}

	public void setTranslateZ(double z) {
		this.paneBox.setTranslateZ(z);
		this.selection.setTranslateZ(z);
	}

	public double getTranslateY() {
		return this.paneBox.getTranslateY();
	}

	public double getTranslateX() {
		return this.paneBox.getTranslateX();
	}

	public double getTranslateZ() {
		return this.paneBox.getTranslateZ();
	}

	public void setVisible(boolean visible) {
		this.paneBox.setVisible(visible);
	}

	public void setPaneVisible(boolean visible) {
		this.borderPane.setVisible(visible);
	}

	public void setBoxVisible(boolean visible) {
		this.box.setVisible(visible);
	}

	public Point3D getCenterPoint() {
		double y = this.paneBox.getTranslateY() - (this.box.getDepth() / 2);
		return new Point3D(this.paneBox.getTranslateX(), y, this.paneBox.getTranslateZ());
	}

}
