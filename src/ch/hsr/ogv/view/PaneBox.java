package ch.hsr.ogv.view;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import ch.hsr.ogv.util.ColorUtil;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.TextUtil;
import ch.hsr.ogv.util.ResourceLocator.Resource;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class PaneBox {
	
	private final static Logger logger = LoggerFactory.getLogger(PaneBox.class);
	
	public final static int INIT_DEPTH = 10;
	
	public final static int MIN_WIDTH = 100;
	public final static int MIN_HEIGHT = 100;
	
	public final static int MAX_WIDTH = 500;
	public final static int MAX_HEIGHT = 500;
	
	public final static Color DEFAULT_COLOR = Color.CORNSILK;
	
	private Group paneBox = new Group();
	private Selection selection = null;
	private BorderPane borderPane = null;
	private Color color;
	private Cuboid box;
	
	public Group get() {
		return this.paneBox;
	}
	
	public Cuboid getBox() {
		return this.box;
	}

	public Selection getSelection() {
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
        this.selection = new Selection(getBox());
        
        //this.paneBoxSelection.getChildren().addAll(this.borderPane, this.box.getNode(), this.selection.getNode());
        this.paneBox.getChildren().addAll(this.borderPane, this.box);
        this.selection.setVisible(false);
        
        // position the whole group so, that the center is at scene's origin (0, 0, 0)
        setTranslateY(INIT_DEPTH / 2);
        setColor(color);
	}
	
	private String getPaneStyle() {
		return "-fx-background-color: " + ColorUtil.toRGBCode(this.color) + ";\n"
		+ "-fx-border-color: black;\n"
		+ "-fx-border-width: 2;";
	}
	
	private void initLayout() {
		FXMLLoader loader = new FXMLLoader(); // load class preset from fxml file
        loader.setLocation(ResourceLocator.getResourcePath(Resource.PANEPRESET_FXML));
        try {
			this.borderPane = (BorderPane) loader.load();
		} catch (IOException e) {
			logger.debug(e.getMessage());
            e.printStackTrace();
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
	
	public TextField getTop() {
		Node topNode = this.borderPane.getTop();
		if((topNode instanceof VBox)) {
			VBox topVBox = (VBox) topNode;
			if(!topVBox.getChildren().isEmpty() && topVBox.getChildren().get(0) instanceof TextField) {
				return (TextField) topVBox.getChildren().get(0);
			}
		}
		return null;
	}
	
	public void adaptWidthByText(Font font, String text) {
		// + 50px for some additional space to compensate insets, borders etc.
		double newWidth = TextUtil.computeTextWidth(font, text, 0.0D) + 50;
		//double origWidth = getWidth();
		setMinWidth(newWidth);
		//if(newWidth < origWidth) {
		//	setWidth(origWidth);
		//}
	}
	
	public void setTopText(String text) {
		TextField topTextField = getTop();
		if(topTextField == null) return;
		adaptWidthByText(topTextField.getFont(), text);
		topTextField.setText(text);
	}
	
	public void setTopFont(Font font) {
		TextField topTextField = getTop();
		if(topTextField != null) {
			topTextField.setFont(font);
			adaptWidthByText(topTextField.getFont(), topTextField.getText());
		}
	}
	
	public void allowTextInput(boolean value) {
		getTop().setEditable(value);
		getTop().setDisable(!value);
	}
	
	public void setSelected(boolean value) {
		this.selection.setVisible(value);
		allowTextInput(value);
	}
	
	private double restrictedWidth(double width) {
		double retWidth = width;
		if(width < MIN_WIDTH) {
			retWidth = MIN_WIDTH;
		}
		else if(width > MAX_WIDTH) {
			retWidth = MAX_WIDTH;
		}
		return retWidth;
	}
	
	private double restrictedHeight(double height) {
		double retHeight = height;
		if(height < MIN_HEIGHT) {
			retHeight = MIN_HEIGHT;
		}
		else if(height > MAX_HEIGHT) {
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
	 * Sets the minimum width of this box. Note that it can not be set blow {@link PaneBox#MIN_WIDTH}.
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
	 * Sets the maximum width of this box. Note that it can not be set blow {@link PaneBox#MAX_WIDTH}.
	 * @param width
	 */
	public void setMaxWidth(double width) {
		this.borderPane.setMaxWidth(restrictedWidth(width));
	}
	
	public double getMaxWidth() {
		return this.borderPane.getMaxWidth();
	}
	
	/**
	 * Sets the minimum height of this box. Note that it can not be set above {@link PaneBox#MIN_HEIGHT}.
	 * @param height
	 */
	public void setMinHeight(double height) {
		this.borderPane.setMinHeight(restrictedHeight(height));
	}

	public double getMinHeight() {
		return this.borderPane.getMinHeight();
	}
	
	/**
	 * Sets the maximum height of this box. Note that it can not be set above {@link PaneBox#MAX_HEIGHT}.
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
		setTranslateY( (depth / 2) - (getTranslateY() / 2) );
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
		double y = (this.paneBox.getTranslateY() + this.box.getTranslateY()) / 2;
		return new Point3D(this.paneBox.getTranslateX(), y, this.paneBox.getTranslateZ());
	}
	
}
