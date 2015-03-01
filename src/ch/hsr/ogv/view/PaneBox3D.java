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
import javafx.scene.transform.Translate;
import ch.hsr.ogv.MainApp;
import ch.hsr.ogv.util.ColorUtils;

public class PaneBox3D {
	
	private final static Logger logger = LoggerFactory.getLogger(PaneBox3D.class);
	
	private Group class2D = new Group();
	private BorderPane borderPane = null;
	private Color color;
	private Cuboid3D carryBox;
	
	public Group getNode() {
		return class2D;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		this.borderPane.setStyle("-fx-background-color: " + ColorUtils.toRGBCode(this.color) + ";\n"
				+ "-fx-border-color: black;\n"
				+ "-fx-border-width: 2;");
		this.carryBox.setColor(color);
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
        
        buildCarryBox();
        
        class2D.getChildren().add(this.borderPane);
        class2D.getChildren().add(this.carryBox.getNode());
        class2D.getTransforms().add(new Translate(0,5,0)); // position the group's center at the origin (0, 0, 0)
        
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
	
	private void buildCarryBox() {
		this.carryBox = new Cuboid3D();
		this.carryBox.getNode().setDrawTopFace(false);
		this.carryBox.getNode().widthProperty().bind(this.borderPane.widthProperty());
		this.carryBox.getNode().heightProperty().bind(this.borderPane.heightProperty());
		this.carryBox.getNode().getTransforms().add(new Rotate(90, Rotate.X_AXIS));
		this.carryBox.getNode().translateXProperty().bind(this.borderPane.translateXProperty().subtract(this.borderPane.widthProperty().divide(2)));
		this.carryBox.getNode().translateZProperty().bind(this.borderPane.translateZProperty().subtract(this.borderPane.heightProperty().divide(2)));
		this.carryBox.getNode().translateYProperty().bind(this.borderPane.translateYProperty().subtract(5));
	}
	
	public void setCarryVisible(boolean visible) {
		this.carryBox.getNode().setVisible(visible);
	}
	
}
