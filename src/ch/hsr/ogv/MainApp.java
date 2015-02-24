package ch.hsr.ogv;
	
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.controller.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

/**
 * Starting point of the application.
 * @author Simon Gwerder
 *
 */
public class MainApp extends Application {
	
	private final Logger logger = LoggerFactory.getLogger(MainApp.class);
	
	private String appTitle = "Object Graph Visualization";
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	private static final int MIN_WIDTH = 800;
	private static final int MIN_HEIGHT = 600;
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public String getAppTitle() {
		return appTitle;
	}

	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
		this.getPrimaryStage().setTitle(this.appTitle);
	}
		
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.setAppTitle(this.appTitle);
        this.primaryStage.setMinWidth(MIN_WIDTH);
        this.primaryStage.setMinHeight(MIN_HEIGHT);

        this.primaryStage.getIcons().add(new Image("file:resources/images/dummy_icon.png")); // set the application icon
        
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
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            this.rootLayout = (BorderPane) loader.load();
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
        	logger.debug(e.getMessage());
            e.printStackTrace();
        }
    }
    
}
