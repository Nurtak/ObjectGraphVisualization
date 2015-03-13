package ch.hsr.ogv.controller;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ch.hsr.ogv.StageManager;
import ch.hsr.ogv.ThemeChooser.Style;
import ch.hsr.ogv.util.UserPreferences;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneCamera;

/**
 * The controller for the root layout. The root layout provides the basic
 * application layout containing a menu bar and space where other JavaFX
 * elements can be placed.
 * 
 * @author Simon Gwerder
 */
public class RootLayoutController implements Observer {

	
	private StageManager stageManager; // reference back to the stage manager
	private ThemeMenuController themeMenuController = new ThemeMenuController();
	private CameraController cameraController = new CameraController();
	private SubSceneController subSceneController = new SubSceneController();
	
	private SelectionController selectionController = new SelectionController();
	private TextInputController textInputController = new TextInputController();
	private DragMoveController dragMoveController = new DragMoveController();
	private DragResizeController dragResizeController = new DragResizeController();

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param stageManager
	 */
	private void initController(StageManager stageManager) {
		this.stageManager = stageManager;
		initSubSceneController();
		initCameraController();
		initPaneBoxController();
	}
	
	private void initCameraController() {
        this.cameraController.handleMouse(this.stageManager.getSubSceneAdpater());
        this.cameraController.handleKeyboard(this.stageManager.getSubSceneAdpater());
	}
	
	private void initSubSceneController() {
		this.subSceneController.handleMouse(this.stageManager.getSubSceneAdpater());
	}
	
	private void initPaneBoxController() {
		this.dragMoveController.addObserver(this.cameraController);
		this.dragResizeController.addObserver(this.cameraController);
		this.selectionController.addObserver(this.dragMoveController);
	}
	
	private void addPaneBoxControls(PaneBox paneBox) {
		this.selectionController.enableSelection(paneBox);
		this.textInputController.enableTextInput(paneBox);
		this.dragMoveController.enableDrag(paneBox);
		this.dragResizeController.enableDrag(paneBox);
	}

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
		if(previousFile != null && previousFile.getParentFile() != null && previousFile.getParentFile().isDirectory()) {
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
		if(previousFile != null && previousFile.getParentFile() != null && previousFile.getParentFile().isDirectory()) {
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
		SubSceneCamera ssCamera = stageManager.getSubSceneAdpater().getSubSceneCamera();
		this.cameraController.handle2DClassView(ssCamera);
	}
	
	@FXML
	private CheckMenuItem modena;
	
	@FXML
	private CheckMenuItem caspian;
	
	@FXML
	private CheckMenuItem aqua;
		
	@FXML
	private void handleSetModena() {
		this.themeMenuController.handleSetTheme(this.stageManager, this.modena, Style.MODENA);
	}
	
	@FXML
	private void handleSetCaspian() {
		this.themeMenuController.handleSetTheme(this.stageManager, this.caspian, Style.CASPIANDARK);
	}
	
	@FXML
	private void handleSetAqua() {
		this.themeMenuController.handleSetTheme(this.stageManager, this.aqua, Style.AQUA);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof StageManager && arg instanceof StageManager) {
			StageManager stageManager = (StageManager) arg;
			initController(stageManager);
		}
		
		else if(o instanceof StageManager && arg instanceof PaneBox) {
			PaneBox paneBox = (PaneBox) arg;
			addPaneBoxControls(paneBox);
		}
	}
		
}