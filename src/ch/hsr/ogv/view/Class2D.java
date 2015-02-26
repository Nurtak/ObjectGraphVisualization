package ch.hsr.ogv.view;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Rotate;
import ch.hsr.ogv.MainApp;

public class Class2D {
	
	private final static Logger logger = LoggerFactory.getLogger(Class2D.class);
	
	private BorderPane class2D = null;
	
	public BorderPane getBorderPane() {
		return class2D;
	}

	public Class2D() {
		FXMLLoader loader = new FXMLLoader(); // load classpreset from fxml file
        loader.setLocation(MainApp.class.getResource("view/ClassPreset.fxml"));
        try {
			this.class2D = (BorderPane) loader.load();
		} catch (IOException e) {
			logger.debug(e.getMessage());
            e.printStackTrace();
		}
        class2D.getTransforms().add(new Rotate(180, 0, 0, 0));
        class2D.getTransforms().add(new Rotate(90, new Point3D(0,0,0)));
        
        //TODO: Use following lines in SubScene3D:
        //Class2D class2D = new Class2D();
        //world.getChildren().add(class2D.getBorderPane());
	}

}
