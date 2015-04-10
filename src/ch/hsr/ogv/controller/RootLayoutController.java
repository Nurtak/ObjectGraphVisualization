package ch.hsr.ogv.controller;

import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ch.hsr.ogv.controller.ThemeMenuController.Style;
import ch.hsr.ogv.dataaccess.UserPreferences;
import ch.hsr.ogv.view.Floor;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;
import ch.hsr.ogv.view.TSplitMenuButton;

/**
 * The controller for the root layout. The root layout provides the basic application layout containing a menu bar and space where other JavaFX elements can be placed.
 *
 * @author Simon Gwerder
 */
public class RootLayoutController implements Observer, Initializable {

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
			// TODO
		}
	}

	/**
	 * Saves the file to the ogv file that is currently open. If there is no open file, the "save as" dialog is shown.
	 */
	@FXML
	private void handleSave() {
		File file = UserPreferences.getSavedFile();
		if (file != null) {
			// TODO
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
			// TODO
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
		stage.getIcons().add(new Image("file:resources/images/application_icon.gif")); // add a custom icon
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
	MenuItem centerView;

	@FXML
	CheckMenuItem lockedTopView;

	@FXML
	CheckMenuItem showObjects;

	@FXML
	CheckMenuItem showModelAxis;

	@FXML
	private void handleCenterView() {
		this.stageManager.handleCenterView();
	}

	@FXML
	private void handleLockedTopView() {
		this.stageManager.handleLockedTopView(this.lockedTopView.isSelected());
	}

	@FXML
	private void handleShowObjects() {
		if (this.showObjects.isSelected()) {
			this.createObject.setDisable(false);
		} else {
			this.createObject.setSelected(false);
			this.createObject.setDisable(true);
		}
		this.stageManager.handleShowObjects(this.showObjects.isSelected());
	}

	@FXML
	private void handleShowModelAxis() {
		this.stageManager.handleShowModelAxis(this.showModelAxis.isSelected());
	}

	@FXML
	private CheckMenuItem modena;

	@FXML
	private CheckMenuItem caspian;

	@FXML
	private CheckMenuItem aqua;

	private void setMenuSelection(CheckMenuItem choosenMenu) {
		Menu theme = choosenMenu.getParentMenu();
		for (MenuItem menuItem : theme.getItems()) {
			if (menuItem instanceof CheckMenuItem) {
				CheckMenuItem cMenuItem = (CheckMenuItem) menuItem;
				cMenuItem.setSelected(false);
			}
		}
		choosenMenu.setSelected(true);
	}

	@FXML
	private void handleSetModena() {
		setMenuSelection(this.modena);
		this.stageManager.handleSetTheme(Style.MODENA);
	}

	@FXML
	private void handleSetCaspian() {
		setMenuSelection(this.caspian);
		this.stageManager.handleSetTheme(Style.CASPIANDARK);
	}

	@FXML
	private void handleSetAqua() {
		setMenuSelection(this.aqua);
		this.stageManager.handleSetTheme(Style.AQUA);
	}

	@FXML
	private ToggleGroup createToolbar;

	@FXML
	private ToggleButton createClass;

	@FXML
	private ToggleButton createObject;

	@FXML
	private SplitMenuButton createAssociation;
	private TSplitMenuButton tSplitMenuButton;

	@FXML
	private MenuItem createUndirectedAssociation;

	@FXML
	private MenuItem createDirectedAssociation;

	@FXML
	private MenuItem createBidirectedAssociation;

	@FXML
	private MenuItem createUndirectedAggregation;

	@FXML
	private MenuItem createDirectedAggregation;

	@FXML
	private MenuItem createUndirectedComposition;

	@FXML
	private MenuItem createDirectedComposition;

	@FXML
	private ToggleButton createGeneralization;

	@FXML
	private ToggleButton createDependency;

	@FXML
	private void handleCreateClass() {
		this.stageManager.onlyFloorMouseEvent(this.createClass.isSelected());
	}

	@FXML
	private void handleCreateObject() {
		// Nothing
	}

	private void splitMenuButtonSelect(MenuItem choosenItem) {
		this.tSplitMenuButton.setChoice(choosenItem);
		this.createToolbar.selectToggle(this.tSplitMenuButton);
	}

	@FXML
	private void handleCreateAssociation() {
		if (tSplitMenuButton.isSelected()) {
			this.createToolbar.selectToggle(null);
		} else {
			this.createToolbar.selectToggle(this.tSplitMenuButton);
		}
	}

	@FXML
	private void handleCreateUndirectedAssociation() {
		splitMenuButtonSelect(this.createUndirectedAssociation);
	}

	@FXML
	private void handleCreateDirectedAssociation() {
		splitMenuButtonSelect(this.createDirectedAssociation);
	}

	@FXML
	private void handleCreateBidirectedAssociation() {
		splitMenuButtonSelect(this.createBidirectedAssociation);
	}

	@FXML
	private void handleCreateUndirectedAggregation() {
		splitMenuButtonSelect(this.createUndirectedAggregation);
	}

	@FXML
	private void handleCreateDirectedAggregation() {
		splitMenuButtonSelect(this.createDirectedAggregation);
	}

	@FXML
	private void handleCreateUndirectedComposition() {
		splitMenuButtonSelect(this.createUndirectedComposition);
	}

	@FXML
	private void handleCreateDirectedComposition() {
		splitMenuButtonSelect(this.createDirectedComposition);
	}

	@FXML
	private void handleCreateGeneralization() {
		// TODO
	}

	@FXML
	private void handleCreateDependency() {
		// TODO
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof StageManager && arg instanceof StageManager) { // give a reference back to the StageManager.
			StageManager stageManager = (StageManager) arg;
			this.stageManager = stageManager;
			this.stageManager.getSelectionController().addObserver(this);
		} else if (o instanceof SelectionController && arg instanceof Floor) {
			SelectionController selectionController = (SelectionController) o;
			if (createClass != null && createClass.isSelected()) {
				this.stageManager.handleCreateNewClass(selectionController.getSelectionCoordinates());
				this.createClass.setSelected(false);
			}
		} else if (o instanceof SelectionController && arg instanceof PaneBox) {
			SelectionController selectionController = (SelectionController) o;
			Selectable selected = selectionController.getSelected();
			if (selectionController.hasSelection() && selected instanceof PaneBox && createObject != null && createObject.isSelected()) {
				this.stageManager.handleCreateNewObject((PaneBox) selected);
				this.createObject.setSelected(false);
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) { // called once FXML is loaded and all fields injected
		this.tSplitMenuButton = new TSplitMenuButton(this.createAssociation, this.createUndirectedAssociation, this.createToolbar);
	}
}
