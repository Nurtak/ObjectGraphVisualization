package ch.hsr.ogv;
	
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;

import javafx.application.Application;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Starts the application.
 * @author Simon Gwerder
 *
 */
public class MainApp extends Application {
	
	private final static Logger logger = LoggerFactory.getLogger(MainApp.class);
	
	public static void main(String[] args) {
		//System.setProperty("prism.lcdtext", "false");
		//System.setProperty("prism.text", "t2k");
		Locale.setDefault(new Locale("en", "EN")); // set to English
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() { 
			 public void uncaughtException(Thread thread, final Throwable throwable) {
				 logger.debug("Error in thread " + thread + ": " + throwable.getMessage());
				 throwable.printStackTrace();
			 }
		 });
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		new StageManager(primaryStage);
	}
	
}
