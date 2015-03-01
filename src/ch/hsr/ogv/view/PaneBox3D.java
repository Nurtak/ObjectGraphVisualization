package ch.hsr.ogv.view;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import ch.hsr.ogv.MainApp;
import ch.hsr.ogv.util.ColorUtils;

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
		this.borderPane.setStyle(
			  "-fx-background-color: " + ColorUtils.toRGBCode(this.color) + ";\n"
			+ "-fx-border-color: black;\n"
			+ "-fx-border-width: 2;"
		);
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
	}
	
	private void initLayout() {
		FXMLLoader loader = new FXMLLoader(); // load classpreset from fxml file
        loader.setLocation(MainApp.class.getResource("view/PanePreset.fxml"));
        try {
			this.borderPane = (BorderPane) loader.load();
		} catch (IOException e) {
			logger.debug(e.getMessage());
            e.printStackTrace();
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
