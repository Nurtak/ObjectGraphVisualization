package ch.hsr.ogv.dataaccess;

import java.io.File;

import javafx.stage.Stage;
import ch.hsr.ogv.view.MessageBar;
import ch.hsr.ogv.view.MessageBar.MessageLevel;

public class LoadCallback implements PersistencyCallback {
	
	private Stage primaryStage;
	private String appTitle;
	private File file;
	
	public LoadCallback(Stage primaryStage, String appTitle, File file) {
		this.primaryStage = primaryStage;
		this.appTitle = appTitle;
		this.file = file;
	}

	@Override
	public void completed(boolean success) {
		if (success) {
			this.primaryStage.setTitle(this.appTitle + " - " + file.getName()); // set new app title
			MessageBar.setText("Loaded file: \"" + file.getPath() + "\".", MessageLevel.INFO);
		} else {
			MessageBar.setText("Could not load data from file: \"" + file.getPath() + "\".", MessageLevel.ALERT);
		}
	}
}
