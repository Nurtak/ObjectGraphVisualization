package ch.hsr.ogv.view;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import ch.hsr.ogv.util.ColorUtils;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.TextUtils;
import ch.hsr.ogv.util.ResourceLocator.Resource;

public class PaneBox3D {
	
	private final static Logger logger = LoggerFactory.getLogger(PaneBox3D.class);
	
	private final static int INIT_BOX_HEIGHT = 5;
	
	private Group paneBoxSelection = new Group();
	private Selection3D selection3D = null;
	private BorderPane borderPane = null;
	private Group paneBox = new Group();
	private Color color;
	private Cuboid3D box;
	
	public Group getNode() {
		return paneBoxSelection;
	}
	
	public Group getPaneBox() {
		return this.paneBox;
	}
	
	public Selection3D getSelection3D() {
		return selection3D;
	}
	
	public Cuboid3D getBox() {
		return this.box;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		this.borderPane.setStyle(getPaneStyle());
		this.box.setColor(color);
	}
	
	public PaneBox3D() {
		this(Color.WHITE);
	}
	
	public PaneBox3D(Color color) {
		initLayout();
        this.borderPane.setCache(true);
        this.borderPane.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        
        // rotate the pane correctly
        this.borderPane.getTransforms().add(new Rotate(180, Rotate.Y_AXIS));
        this.borderPane.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        
        // position the pane so, that the center is at the scene's origin (0, 0, 0)
        this.borderPane.translateXProperty().bind(this.borderPane.widthProperty().divide(2));
        this.borderPane.translateZProperty().bind(this.borderPane.heightProperty().divide(2));
        
        // build the box that stays beneath the borderPane
        buildBox();
        
        // create the selection objects that stays with this box
        this.selection3D = new Selection3D(this);
        
        this.paneBoxSelection.getChildren().addAll(this.borderPane, this.box.getNode(), this.selection3D.getNode());
        this.selection3D.getNode().setVisible(false);
        paneBoxSelection.getTransforms().add(new Translate(0, INIT_BOX_HEIGHT, 0)); // position the group's center at the origin (0, 0, 0)
        
        setColor(color);
	}
	
	private String getPaneStyle() {
		return "-fx-background-color: " + ColorUtils.toRGBCode(this.color) + ";\n"
		+ "-fx-border-color: black;\n"
		+ "-fx-border-width: 2;";
	}
	
	private void initLayout() {
		FXMLLoader loader = new FXMLLoader(); // load classpreset from fxml file
        loader.setLocation(ResourceLocator.getResourcePath(Resource.PANEPRESET_FXML));
        try {
			this.borderPane = (BorderPane) loader.load();
		} catch (IOException e) {
			logger.debug(e.getMessage());
            e.printStackTrace();
		}
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
		double newWidth = TextUtils.computeTextWidth(font, text, 0.0D) + 50;
		this.borderPane.setPrefWidth(newWidth);
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
		}
	}
	
	public void setSelected(boolean value) {
		this.selection3D.getNode().setVisible(value);
		getTop().setEditable(value);
		getTop().setDisable(!value);
	}
	
	private void buildBox() {
		this.box = new Cuboid3D(INIT_BOX_HEIGHT * 2);
		this.box.setDrawTopFace(false);
		this.box.widthProperty().bind(this.borderPane.widthProperty());
		this.box.heightProperty().bind(this.borderPane.heightProperty());
		this.box.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		this.box.translateXProperty().bind(this.borderPane.translateXProperty().subtract(this.borderPane.widthProperty().divide(2)));
		this.box.translateZProperty().bind(this.borderPane.translateZProperty().subtract(this.borderPane.heightProperty().divide(2)));
		this.box.translateYProperty().bind(this.borderPane.translateYProperty().subtract(INIT_BOX_HEIGHT));
	}
	
	public void setBoxHeightScale(double scale) {
		this.box.getTransforms().add(new Scale(1, 1, scale));
		this.borderPane.getTransforms().add(new Translate(0, 0, (scale * -INIT_BOX_HEIGHT) + INIT_BOX_HEIGHT));
	}
	
	public void setTranslateY(double y) {
		this.paneBoxSelection.setTranslateY(y);
	}
	
	public void setTranslateX(double x) {
		this.paneBoxSelection.setTranslateX(x);
	}
	
	public void setTranslateZ(double z) {
		this.paneBoxSelection.setTranslateZ(z);
	}
	
	public double getTranslateY() {
		return this.paneBoxSelection.getTranslateY();
	}
	
	public double getTranslateX() {
		return this.paneBoxSelection.getTranslateX();
	}
	
	public double getTranslateZ() {
		return this.paneBoxSelection.getTranslateZ();
	}
	
	public void setVisible(boolean visible) {
		this.paneBoxSelection.setVisible(visible);
	}
	
	public void setPaneVisible(boolean visible) {
		this.borderPane.setVisible(visible);
	}
	
	public void setBoxVisible(boolean visible) {
		this.box.setVisible(visible);
	}
	
}
