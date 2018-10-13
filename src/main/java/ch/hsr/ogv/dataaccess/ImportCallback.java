package ch.hsr.ogv.dataaccess;

import ch.hsr.ogv.util.MessageBar;
import ch.hsr.ogv.util.MessageBar.MessageLevel;
import javafx.stage.Stage;

import java.io.File;

public class ImportCallback implements PersistencyCallback {

    private Stage primaryStage;
    private String appTitle;
    private File file;

    public ImportCallback(Stage primaryStage, String appTitle, File file) {
        this.primaryStage = primaryStage;
        this.appTitle = appTitle;
        this.file = file;
    }

    @Override
    public void completed(boolean success) {
        if (success) {
            this.primaryStage.setTitle(this.appTitle + " - " + file.getName()); // set new app title
            MessageBar.setText("Imported file: \"" + file.getPath() + "\".", MessageLevel.INFO);
        }
        else {
            MessageBar.setText("Could not import data from file: \"" + file.getPath() + "\".", MessageLevel.ALERT);
        }
    }
}
