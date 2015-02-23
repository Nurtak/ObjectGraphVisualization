package ch.hsr.ogv;
	
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	public static final int x = 3;
	
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(Main.class);
	    logger.info("Starting OGV");
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Object Graph Visualization");
        this.primaryStage.setMinWidth(800);
        this.primaryStage.setMinHeight(600);

        // Set the application icon.
        this.primaryStage.getIcons().add(new Image("file:resources/images/dummy_icon.png"));
        
        initRootLayout();
        
        Scene scene = new Scene(this.rootLayout);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
	}
	
	/**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(); // load rootlayout from fxml file
            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            this.rootLayout = (BorderPane) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
