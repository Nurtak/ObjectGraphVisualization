package ch.hsr.ogv.view;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import ch.hsr.ogv.ResourceLocator;
import ch.hsr.ogv.ResourceLocator.Resource;
import ch.hsr.ogv.util.ColorUtils;
import ch.hsr.ogv.util.TextUtils;

public class PaneBox3D {
	
	private final static Logger logger = LoggerFactory.getLogger(PaneBox3D.class);
	
	private final static int INIT_BOX_HEIGHT = 5;
	
	private Group group = new Group();
	private BorderPane borderPane = null;
	private Color color;
	private Cuboid3D box;
	
	public Group getNode() {
		return group;
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
        
        buildBox();
        
        group.getChildren().add(this.borderPane);
        group.getChildren().add(this.box.getNode());
        group.getTransforms().add(new Translate(0, INIT_BOX_HEIGHT, 0)); // position the group's center at the origin (0, 0, 0)
        
        setColor(color);
        
        getTop().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
            	adaptWidthByText(getTop().getFont(), newValue);
            }
        });
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
		this.group.setTranslateY(y);
	}
	
	public void setTranslateX(double x) {
		this.group.setTranslateX(x);
	}
	
	public void setTranslateZ(double z) {
		this.group.setTranslateZ(z);
	}
	
	public double getTranslateY() {
		return this.group.getTranslateY();
	}
	
	public double getTranslateX() {
		return this.group.getTranslateX();
	}
	
	public double getTranslateZ() {
		return this.group.getTranslateZ();
	}
	
	public void setVisible(boolean visible) {
		this.group.setVisible(visible);
	}
	
	public void setPaneVisible(boolean visible) {
		this.borderPane.setVisible(visible);
	}
	
	public void setBoxVisible(boolean visible) {
		this.box.setVisible(visible);
	}
	
}
