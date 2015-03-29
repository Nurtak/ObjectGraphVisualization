package ch.hsr.ogv.controller;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ch.hsr.ogv.controller.ThemeMenuController.Style;
import ch.hsr.ogv.dataaccess.UserPreferences;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 * 
 * @author Simon Gwerder
 */
public class RootLayoutController implements Observer {
	
	private StageManager stageManager; // reference back to the stage manager
	
	/**
	 * Creates an empty view.
	 */
	@FXML
	private void handleNew() {
		this.stageManager.setAppTitle(this.stageManager.getAppTitle()); // set new app title TODO
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
		if (previousFile != null && previousFile.getParentFile() != null && previousFile.getParentFile().isDirectory()) {
			fileChooser.setInitialDirectory(previousFile.getParentFile());
		}
		// Show open file dialog
		File file = fileChooser.showOpenDialog(stageManager.getPrimaryStage());

		if (file != null) {
			this.stageManager.setAppTitle(this.stageManager.getAppTitle() + " - " + file.getName()); // set new app title
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
		if (previousFile != null && previousFile.getParentFile() != null && previousFile.getParentFile().isDirectory()) {
			fileChooser.setInitialDirectory(previousFile.getParentFile());
		}
		// Show save file dialog
		File file = fileChooser.showSaveDialog(this.stageManager.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".ogv")) {
				file = new File(file.getPath() + ".ogv");
			}
			UserPreferences.setSavedFilePath(file);
			this.stageManager.setAppTitle(this.stageManager.getAppTitle() + " - " + file.getName()); // set new app title
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
		alert.initOwner(this.stageManager.getPrimaryStage());
		alert.showAndWait();
	}

	/**
	 * Closes the application.
	 */
	@FXML
	private void handleExit() {
		Platform.exit();
	}
	
	@FXML
	private void handle2DClassView() {
		this.stageManager.handle2DClassView();
	}
	
	@FXML
	private CheckMenuItem modena;
	
	@FXML
	private CheckMenuItem caspian;
	
	@FXML
	private CheckMenuItem aqua;
		
	@FXML
	private void handleSetModena() {
		this.stageManager.handleSetTheme(this.modena, Style.MODENA);
	}
	
	@FXML
	private void handleSetCaspian() {
		this.stageManager.handleSetTheme(this.caspian, Style.CASPIANDARK);
	}
	
	@FXML
	private void handleSetAqua() {
		this.stageManager.handleSetTheme(this.aqua, Style.AQUA);
	}

	@FXML
	private ToggleButton createClass;
	
	@FXML
	private ToggleButton createInstance;
	
	@FXML
	private ToggleButton createGeneralization;
	
	@FXML
	private ToggleButton createDependency;

	@FXML
	private void handleCreateClass() {	
		this.stageManager.onlyFloorMouseEvent(this.createClass.isSelected());
	}
	
	@FXML
	private void handleCreateInstance() {	
		//TODO
		}
	
	@FXML
	private void handleCreateGeneralization() {	
		//TODO
	}
	
	@FXML
	private void handleCreateDependency() {	
		//TODO
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof StageManager && arg instanceof StageManager) { // give a reference back to the StageManager.
			StageManager stageManager = (StageManager) arg;
			this.stageManager = stageManager;
			this.stageManager.getSubSceneController().addObserver(this);
		}
		
		if (o instanceof SubSceneController && arg instanceof Point3D) {
			Point3D mouseCoords = (Point3D) arg;
			if(createClass != null && createClass.isSelected()) {
				this.stageManager.handleCreateNewClass(mouseCoords);
				this.createClass.setSelected(false);
			}
		}
		
	}
}
