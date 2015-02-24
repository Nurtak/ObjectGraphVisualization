package ch.hsr.ogv.controller;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ch.hsr.ogv.MainApp;
import ch.hsr.ogv.util.UserPreferences;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 * 
 * @author Simon Gwerder
 */
public class RootLayoutController {

	// Reference to the main application
	private MainApp mainApp;

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Creates an empty view.
	 */
	@FXML
	private void handleNew() {
		this.mainApp.setAppTitle(this.mainApp.getAppTitle()); // set new app title TODO
	}

	/**
	 * Opens a FileChooser to let the user select an address book to load.
	 */
	@FXML
	private void handleOpen() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OGV (*.ogv)", "*.ogv");
		fileChooser.getExtensionFilters().add(extFilter);
		File previousFile = UserPreferences.getSavedFile();
		if(previousFile != null && previousFile.getParentFile() != null && previousFile.getParentFile().isDirectory()) {
			fileChooser.setInitialDirectory(previousFile.getParentFile());
		}
		// Show open file dialog
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

		if (file != null) {
			this.mainApp.setAppTitle(this.mainApp.getAppTitle() + " - " + file.getName()); // set new app title
			//TODO
		}
	}

	/**
	 * Saves the file to the ogv file that is currently open. If there is no
	 * open file, the "save as" dialog is shown.
	 */
	@FXML
	private void handleSave() {
		File file = UserPreferences.getSavedFile();
		if (file != null) {
			//TODO
		} else {
			handleSaveAs();
		}
	}

	/**
	 * Opens a FileChooser to let the user select a file to save to.
	 */
	@FXML
	private void handleSaveAs() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OGV (*.ogv)", "*.ogv");
		fileChooser.getExtensionFilters().add(extFilter);
		File previousFile = UserPreferences.getSavedFile();
		if(previousFile != null && previousFile.getParentFile() != null && previousFile.getParentFile().isDirectory()) {
			fileChooser.setInitialDirectory(previousFile.getParentFile());
		}
		// Show save file dialog
		File file = fileChooser.showSaveDialog(this.mainApp.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".ogv")) {
				file = new File(file.getPath() + ".ogv");
			}
			UserPreferences.setSavedFilePath(file);
			this.mainApp.setAppTitle(this.mainApp.getAppTitle() + " - " + file.getName()); // set new app title
			//TODO
		}
	}

	/**
	 * Opens an about dialog.
	 */
	@FXML
	private void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("About");
		alert.setContentText("Authors: Simon Gwerder, Adrian Rieser");
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("file:resources/images/dummy_icon.png")); // add a custom icon
		alert.initOwner(this.mainApp.getPrimaryStage());
		alert.showAndWait();
	}

	/**
	 * Closes the application.
	 */
	@FXML
	private void handleExit() {
		System.exit(0);
	}
}