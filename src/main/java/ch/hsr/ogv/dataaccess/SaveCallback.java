package ch.hsr.ogv.dataaccess;

import ch.hsr.ogv.util.MessageBar;
import ch.hsr.ogv.util.MessageBar.MessageLevel;
import javafx.stage.Stage;

import java.io.File;

public class SaveCallback implements PersistenceCallback {

    private Stage primaryStage;
    private String appTitle;
    private File file;

    public SaveCallback(Stage primaryStage, String appTitle, File file) {
        this.primaryStage = primaryStage;
        this.appTitle = appTitle;
        this.file = file;
    }

    @Override
    public void completed(boolean success) {
        if (success) {
            this.primaryStage.setTitle(this.appTitle + " - " + file.getName()); // set new app title
            MessageBar.setText("Saved file: \"" + file.getPath() + "\".", MessageLevel.INFO);
        }
        else {
            MessageBar.setText("Could not save data to file: \"" + file.getPath() + "\".", MessageLevel.ALERT);
        }
    }
}
