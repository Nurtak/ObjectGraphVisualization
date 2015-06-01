package ch.hsr.ogv.controller;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jfxtras.labs.util.Util;
import ch.hsr.ogv.dataaccess.ImportCallback;
import ch.hsr.ogv.dataaccess.LoadCallback;
import ch.hsr.ogv.dataaccess.Persistancy;
import ch.hsr.ogv.dataaccess.SaveCallback;
import ch.hsr.ogv.dataaccess.UserPreferences;
import ch.hsr.ogv.model.Endpoint;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.RelationType;
import ch.hsr.ogv.util.MessageBar;
import ch.hsr.ogv.util.MessageBar.MessageLevel;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.Floor;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;
import ch.hsr.ogv.view.SubSceneAdapter;
import ch.hsr.ogv.view.SubSceneCamera;
import ch.hsr.ogv.view.TSplitMenuButton;

/**
 * 
 * @author Simon Gwerder
 * @version OGV 3.1, May 2015
 *
 */
public class ViewController implements Observer, Initializable {

	private Stage primaryStage;
	private String appTitle;

	private SubSceneAdapter subSceneAdapter;
	private ModelViewConnector mvConnector;
	private ObjectGraph objectGraph;
	private Persistancy persistancy;
	private SelectionController selectionController;
	private CameraController cameraController;
	private RelationCreationController relationCreationController;
	

	private HashMap<Object, RelationType> toggleRelationMap = new HashMap<Object, RelationType>();

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.appTitle = primaryStage.getTitle();
	}
	
	public void setSubSceneAdapter(SubSceneAdapter subSceneAdapter) {
		this.subSceneAdapter = subSceneAdapter;
	}

	public void setMVConnector(ModelViewConnector mvConnector) {
		this.mvConnector = mvConnector;
	}
	
	public void setObjectGraph(ObjectGraph objectGraph) {
		this.objectGraph = objectGraph;
	}
	
	public void setPersistancy(Persistancy persistancy) {
		this.persistancy = persistancy;
	}

	public void setSelectionController(SelectionController selectionController) {
		this.selectionController = selectionController;
	}

	public void setCameraController(CameraController cameraController) {
		this.cameraController = cameraController;
	}

	public void setMessageBar() {
		this.messageBarContainer.getChildren().add(MessageBar.getTextField());
	}

	public void setRelationCreationController(RelationCreationController relationCreationController) {
		this.relationCreationController = relationCreationController;

	}

	/**
	 * Creates an empty view.
	 */
	@FXML
	private void handleNew() {
		this.primaryStage.setTitle(this.appTitle);
		UserPreferences.setOGVFilePath(null);
		this.mvConnector.handleClearAll();
		this.selectionController.setSelected(this.subSceneAdapter, true, this.subSceneAdapter);
		exitObjectGraphMode();
		toggleToolbar(null);
		this.subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
		handleCenterView();
		MessageBar.setText("Initialized new workspace.", MessageLevel.INFO);
	}

	/**
	 * Opens a FileChooser to let the user select a ogv file to load.
	 */
	@FXML
	private void handleOpen() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("OGV (*.ogv)", "*.ogv");
		fileChooser.getExtensionFilters().add(extFilter);
		File previousFile = UserPreferences.getOGVFilePath();
		if (previousFile != null && previousFile.getParentFile() != null && previousFile.getParentFile().isDirectory()) {
			fileChooser.setInitialDirectory(previousFile.getParentFile());
		}
		// Show open file dialog
		File file = fileChooser.showOpenDialog(this.primaryStage);
		if (file != null) {
			UserPreferences.setOGVFilePath(file);
			MessageBar.setText("Loading file: \"" + file.getPath() + "\"...", MessageLevel.WARN);
			persistancy.loadOGVDataAsync(file, new LoadCallback(this.primaryStage, this.appTitle, file));
			exitObjectGraphMode();
		}
	}

	/**
	 * Opens a FileChooser to let the user select a xmi file to import.
	 */
	@FXML
	private void handleImport() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XMI 1.1 (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File previousFile = UserPreferences.getXMIFilePath();
		if (previousFile != null && previousFile.getParentFile() != null && previousFile.getParentFile().isDirectory()) {
			fileChooser.setInitialDirectory(previousFile.getParentFile());
		}
		// Show open file dialog
		File file = fileChooser.showOpenDialog(this.primaryStage);
		if (file != null) {
			UserPreferences.setXMIFilePath(file);
			UserPreferences.setOGVFilePath(null);
			MessageBar.setText("Importing file: \"" + file.getPath() + "\"...", MessageLevel.WARN);
			persistancy.loadXMIDataAsync(file, new ImportCallback(this.primaryStage, this.appTitle, file));
			exitObjectGraphMode();
		}
	}

	/**
	 * Saves the file to the ogv file that is currently open. If there is no open file, the "save as" dialog is shown.
	 */
	@FXML
	private void handleSave() {
		File file = UserPreferences.getOGVFilePath();
		if (file != null) {
			MessageBar.setText("Saving file: \"" + file.getPath() + "\"...", MessageLevel.WARN);
			persistancy.saveOGVDataAsync(file, new SaveCallback(this.primaryStage, this.appTitle, file));
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
		File previousFile = UserPreferences.getOGVFilePath();
		if (previousFile != null && previousFile.getParentFile() != null && previousFile.getParentFile().isDirectory()) {
			fileChooser.setInitialDirectory(previousFile.getParentFile());
		}
		// Show save file dialog
		File file = fileChooser.showSaveDialog(this.primaryStage);

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".ogv")) {
				file = new File(file.getPath() + ".ogv");
			}
			UserPreferences.setOGVFilePath(file);
			MessageBar.setText("Saving file: \"" + file.getPath() + "\"...", MessageLevel.WARN);
			persistancy.saveOGVDataAsync(file, new SaveCallback(this.primaryStage, this.appTitle, file));
		} else {
			MessageBar.setText("Could not save data. No save file specified.", MessageLevel.ALERT);
		}
	}

	/**
	 * Opens an about dialog.
	 */
	@FXML
	private void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("Object Graph Visualizer");
		alert.setContentText("Version:\t3.1"
				+ "\nAuthors:\tSimon Gwerder, Adrian Rieser"
				+ "\nRelease:\t12.06.2015\n"
				+ "\nBachelor Thesis"
				+ "\nHSR University of Applied Sciences Rapperswil");
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("file:resources/images/OGV.gif")); // add a custom icon
		alert.initOwner(this.primaryStage);
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
		SubSceneCamera ssCamera = this.subSceneAdapter.getSubSceneCamera();
		this.cameraController.handleCenterView(ssCamera);
	}

	@FXML
	private void handleLockedTopView() {
		SubSceneCamera ssCamera = this.subSceneAdapter.getSubSceneCamera();
		this.cameraController.handleLockedTopView(ssCamera, this.lockedTopView.isSelected());
	}
	
	@FXML
	private void handleShowObjects() {
		if (this.showObjects.isSelected()) {
			this.createObject.setDisable(!isModelClassSelected());
			this.createObjectRelation.setDisable(false);
			this.subSceneAdapter.setYSpaceVisible(true);
			this.objectGraphMode.setDisable(false);
		} else {
			this.createObject.setDisable(true);
			this.createObjectRelation.setDisable(true);
			this.subSceneAdapter.getVerticalHelper().setVisible(false);
			this.subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
			this.subSceneAdapter.setYSpaceVisible(false);
			this.objectGraphMode.setDisable(true);
		}
		showModelObjects(this.showObjects.isSelected());
	}
	
	private boolean isModelClassSelected() {
		Selectable selected = this.selectionController.getCurrentSelected();
		if(selected != null && selected instanceof PaneBox) {
			ModelBox modelBox = this.mvConnector.getModelBox((PaneBox) selected);
			return modelBox instanceof ModelClass;
		}
		return false;
	}

	private void showModelObjects(boolean show) {
		for (ModelBox modelBox : this.mvConnector.getBoxes().keySet()) {
			if (modelBox instanceof ModelObject) {
				PaneBox paneBox = this.mvConnector.getPaneBox(modelBox);
				paneBox.setVisible(show);

				if (!show && paneBox.isSelected() && this.selectionController != null) {
					paneBox.getSelection().setVisible(false);
				}

				for (Endpoint endpoint : modelBox.getEndpoints()) {
					Arrow arrow = this.mvConnector.getArrow(endpoint.getRelation());
					arrow.setVisible(show);
					if (!show && arrow.isSelected() && this.selectionController != null) {
						arrow.getSelection().setVisible(false);
					}
				}
			}
		}
	}
	
	private void showGraphObjects(boolean show) {
		for (PaneBox paneBox : this.objectGraph.getBoxes()) {
			paneBox.setVisible(show);
		}
		for (Arrow arrow : this.objectGraph.getArrows()) {
			arrow.setVisible(show);
		}
	}
	
	@FXML
	private void handleShowModelAxis() {
		Group axis = this.subSceneAdapter.getAxis();
		if (this.showModelAxis.isSelected()) {
			axis.setVisible(true);
		} else {
			axis.setVisible(false);
		}
	}

	@FXML
	private ToggleGroup toolbarGroup;

	@FXML
	private ToggleButton createClass;

	@FXML
	private Button createObject;

	@FXML
	private Label classRelationLabel;
	
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
	private MenuItem createGeneralization;

	@FXML
	private MenuItem createDependency;

	@FXML
	private ToggleButton createObjectRelation;

	@FXML
	private Button deleteSelected;

	@FXML
	private Label pickColorLabel;
	
	@FXML
	private ColorPicker pickColor;

	@FXML
	private ToggleButton objectGraphMode;
	
	@FXML
	private HBox messageBarContainer;
	
	@FXML
	private void handleCreateClass() {
		if (this.createClass.isSelected()) {
			toggleToolbar(this.createClass);
			this.subSceneAdapter.worldRestrictMouseEvents();
			this.subSceneAdapter.receiveMouseEvents(this.subSceneAdapter.getFloor());
			this.subSceneAdapter.getSubScene().setCursor(Cursor.CROSSHAIR);
			this.selectionController.setSelected(this.subSceneAdapter, true, this.subSceneAdapter);
			this.pickColorLabel.setDisable(true);
			this.pickColor.setDisable(true);
		} else {
			toggleToolbar(null);
			this.subSceneAdapter.worldReceiveMouseEvents();
			this.subSceneAdapter.restrictMouseEvents(this.subSceneAdapter.getVerticalHelper());
			this.subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
			this.selectionController.setSelected(this.subSceneAdapter.getFloor(), true, this.subSceneAdapter);
		}
	}

	@FXML
	private void handleCreateObject() {
		toggleToolbar(null);
		this.subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
		Selectable selected = this.selectionController.getCurrentSelected();
		if (this.selectionController.hasCurrentSelection() && selected instanceof PaneBox && mvConnector.getModelBox((PaneBox) selected) instanceof ModelClass) {
			PaneBox newPaneBox = this.mvConnector.handleCreateNewObject(selected);
			if (newPaneBox != null) {
				new QuickCreationController(newPaneBox, this.mvConnector);
				this.selectionController.setSelected(newPaneBox, true, this.subSceneAdapter);
			}
		}
	}

	private void splitMenuButtonSelect(MenuItem choosenItem) {
		this.tSplitMenuButton.setChoice(choosenItem);
		// toggleToolbar(null);
		handleCreateAssociation();
	}
	
	@FXML
	private void handleCreateAssociation() {
		if (this.tSplitMenuButton.isSelected()) {
			toggleToolbar(null);
			this.relationCreationController.endChoosingStartBox();
			this.subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
			this.selectionController.setSelected(this.subSceneAdapter.getFloor(), true, this.subSceneAdapter);
		} else {
			toggleToolbar(this.tSplitMenuButton);
			this.relationCreationController.startChoosingStartBox(getToggledRelationType());
			this.subSceneAdapter.getSubScene().setCursor(Cursor.CROSSHAIR);
			this.selectionController.setSelected(this.subSceneAdapter, true, this.subSceneAdapter);
			this.pickColorLabel.setDisable(true);
			this.pickColor.setDisable(true);
		}
	}

	@FXML
	private void handleCreateObjectRelation() {
		if(this.createObjectRelation.isSelected()) {
			toggleToolbar(this.createObjectRelation);
			this.relationCreationController.startChoosingStartBox(RelationType.OBJDIAGRAM);
			this.subSceneAdapter.getSubScene().setCursor(Cursor.CROSSHAIR);
			this.selectionController.setSelected(this.subSceneAdapter, true, this.subSceneAdapter);
			this.pickColorLabel.setDisable(true);
			this.pickColor.setDisable(true);
		}
		else {
			this.relationCreationController.endChoosingStartBox();
			toggleToolbar(null);
			this.subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
			this.selectionController.setSelected(this.subSceneAdapter.getFloor(), true, this.subSceneAdapter);
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
		splitMenuButtonSelect(this.createGeneralization);
	}

	@FXML
	private void handleCreateDependency() {
		splitMenuButtonSelect(this.createDependency);
	}

	@FXML
	private void handleDeleteSelected() {
		toggleToolbar(null);
		Selectable selected = this.selectionController.getCurrentSelected();
		if (this.selectionController.hasCurrentSelection()) {
			this.mvConnector.handleDelete(selected);
			if (!this.mvConnector.containsSelectable(selected)) {
				this.selectionController.setSelected(this.subSceneAdapter, true, this.subSceneAdapter);
			}
		}
	}

	@FXML
	private void handlePickColor() {
		toggleToolbar(null);
		Selectable selected = this.selectionController.getCurrentSelected();
		if (this.selectionController.hasCurrentSelection()) {
			this.mvConnector.handleColorPick(selected, this.pickColor.getValue());
		}
	}
	
	private void exitObjectGraphMode() {
		if (this.objectGraphMode.isSelected()) {
			this.objectGraphMode.setSelected(false);
		}
		handleObjectGraphMode();
	}
	
	
	@FXML
	private void handleObjectGraphMode() {
		if(this.objectGraphMode.isSelected()) {
			toggleToolbar(this.objectGraphMode);
			this.selectionController.setSelected(this.subSceneAdapter.getFloor(), true, this.subSceneAdapter);
			this.subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
			this.subSceneAdapter.worldRestrictMouseEvents();
			disableAll(true);
			this.objectGraphMode.setDisable(false);
			
			this.showModelObjects(false);
			this.objectGraph.setup();
			this.showGraphObjects(true);
		}
		else {
			toggleToolbar(null);
			this.subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
			this.subSceneAdapter.worldReceiveMouseEvents();
			this.subSceneAdapter.restrictMouseEvents(this.subSceneAdapter.getVerticalHelper());
			this.selectionController.setSelected(this.subSceneAdapter.getFloor(), true, this.subSceneAdapter);
			
			this.showGraphObjects(false);
			this.objectGraph.tearDown();
			this.showModelObjects(this.showObjects.isSelected());
		}
	}

	private void addButtonAccelerators() {
		if (this.createClass != null && this.createObject != null && this.deleteSelected != null) {
			Platform.runLater(() -> {
				this.primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C), () -> {
					if (!this.createClass.isDisable()) {
						this.createClass.requestFocus();
						this.createClass.fire();
					}
				});

				this.primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.O), () -> {
					if (!this.createObject.isDisable()) {
						this.createObject.fire();
					}
				});

				this.primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DELETE), () -> {
					if (!this.deleteSelected.isDisable()) {
						this.deleteSelected.fire();
					}
				});
				
				this.primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> {
					if (this.relationCreationController.isInProcess()) {
						this.relationCreationController.abortProcess();
						toggleToolbar(null);
						this.subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
						this.selectionController.setSelected(this.subSceneAdapter.getFloor(), true, this.subSceneAdapter);
					}
					else {
						exitObjectGraphMode();
					}
				});
			});
			
		}
	}
	
	private RelationType getToggledRelationType() {
		Toggle toggle = this.toolbarGroup.getSelectedToggle();
		if (toggle != null && toggle.equals(this.tSplitMenuButton)) {
			MenuItem selectedChoice = this.tSplitMenuButton.selectedChoice();
			if (selectedChoice != null && this.toggleRelationMap.containsKey(selectedChoice)) {
				return this.toggleRelationMap.get(selectedChoice);
			}
		}
		return null;
	}

	private void startRelationCreation(PaneBox selectedPaneBox) {
		subSceneAdapter.getSubScene().setCursor(Cursor.CROSSHAIR);
		this.relationCreationController.startProcess(selectedPaneBox);
	}

	private void endRelationCreation(PaneBox selectedPaneBox) {
		this.subSceneAdapter.getSubScene().setCursor(Cursor.DEFAULT);
		this.relationCreationController.endProcess(selectedPaneBox);
		toggleToolbar(null);
		this.tSplitMenuButton.setSelected(false);
	}

	private void toggleToolbar(Toggle value) {
		this.toolbarGroup.selectToggle(value);
		if(value != null && value.equals(this.tSplitMenuButton)) {
			this.tSplitMenuButton.setSelected(true);
		}
		else {
			this.relationCreationController.abortProcess();
			this.tSplitMenuButton.setSelected(false);
		}
	}

	private void disableAll(boolean value) {
		this.showObjects.setDisable(value);
		this.createClass.setDisable(value);
		this.classRelationLabel.setDisable(value);
		this.createAssociation.setDisable(value);
		this.deleteSelected.setDisable(value);
		this.pickColorLabel.setDisable(value);
		this.pickColor.setDisable(value);
		this.objectGraphMode.setDisable(value);
		boolean disableObjectButtons = value;
		if(!value && !this.showObjects.isSelected()) {
			disableObjectButtons = true;
		}
		this.createObject.setDisable(disableObjectButtons);
		this.createObjectRelation.setDisable(disableObjectButtons);
	}

	private void initToggleRelationMap() {
		this.toggleRelationMap.put(this.createUndirectedAssociation, RelationType.UNDIRECTED_ASSOCIATION);
		this.toggleRelationMap.put(this.createDirectedAssociation, RelationType.DIRECTED_ASSOCIATION);
		this.toggleRelationMap.put(this.createBidirectedAssociation, RelationType.BIDIRECTED_ASSOCIATION);
		this.toggleRelationMap.put(this.createUndirectedAggregation, RelationType.UNDIRECTED_AGGREGATION);
		this.toggleRelationMap.put(this.createDirectedAggregation, RelationType.DIRECTED_AGGREGATION);
		this.toggleRelationMap.put(this.createUndirectedComposition, RelationType.UNDIRECTED_COMPOSITION);
		this.toggleRelationMap.put(this.createDirectedComposition, RelationType.DIRECTED_COMPOSITION);
		this.toggleRelationMap.put(this.createGeneralization, RelationType.GENERALIZATION);
		this.toggleRelationMap.put(this.createDependency, RelationType.DEPENDENCY);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) { // called once FXML is loaded and all fields injected
		setMessageBar(); // sets the message bar into the footer
		addButtonAccelerators();
		this.tSplitMenuButton = new TSplitMenuButton(this.createAssociation, this.createUndirectedAssociation, this.toolbarGroup);
		this.pickColor.getCustomColors().add(SubSceneAdapter.DEFAULT_COLOR);
		this.pickColor.getCustomColors().add(Floor.DEFAULT_COLOR);
		this.pickColor.getCustomColors().add(PaneBox.DEFAULT_COLOR);
		this.pickColor.getCustomColors().add(Util.brighter(PaneBox.DEFAULT_COLOR, 0.1));
		initToggleRelationMap();
	}
	
	// Refactor!!
	@Override
	public void update(Observable o, Object arg) {
		if (selectionController == null) {
			return;
		}
		else if (o instanceof SelectionController && arg instanceof Floor && selectionController.hasCurrentSelection() && createClass.isSelected() && !relationCreationController.isInProcess()) { // creating class
			PaneBox newPaneBox = mvConnector.handleCreateNewClass(selectionController.getCurrentSelectionCoord());
			if (newPaneBox != null) {
				new QuickCreationController(newPaneBox, mvConnector);
				selectionController.setSelected(newPaneBox, true, subSceneAdapter);
				createClass.setSelected(false);
			}
			subSceneAdapter.worldReceiveMouseEvents();
			subSceneAdapter.restrictMouseEvents(subSceneAdapter.getVerticalHelper());
		}
		else if (o instanceof SelectionController && arg instanceof Floor && selectionController.hasCurrentSelection() && relationCreationController.isInProcess()) {
			selectionController.setSelected(relationCreationController.getViewArrow(), true, subSceneAdapter);
		}
		else if (o instanceof SelectionController && (arg instanceof PaneBox && (relationCreationController.isChoosingStartBox() || relationCreationController.isInProcess()) || arg instanceof Arrow)) { // PaneBox, Arrow selected
			Selectable selectable = selectionController.getCurrentSelected();
			// creating relations
			if ((selectionController.isCurrentSelected(selectable) && selectable instanceof PaneBox) || arg instanceof Floor) {
				PaneBox selectedPaneBox = (PaneBox) selectable;
				if (!relationCreationController.isInProcess()) { // first selection
					startRelationCreation(selectedPaneBox);
				} else { // second selection
					endRelationCreation(selectedPaneBox);
					handleShowObjects();
				}
			}

			if (relationCreationController.isInProcess()) {
				disableAll(true);
			}
		}
		// button enabling / disabling
		if (o instanceof SelectionController && selectionController.hasCurrentSelection() && !relationCreationController.isInProcess() && !this.objectGraphMode.isSelected()) {
			disableAll(false);
			createObject.setDisable(true);
			Selectable selectable = selectionController.getCurrentSelected();
			if (selectable instanceof PaneBox && selectionController.isCurrentSelected(selectable)) {
				PaneBox selectedPaneBox = (PaneBox) selectable;
				pickColor.setValue(selectedPaneBox.getColor());
				ModelBox modelBox = mvConnector.getModelBox(selectedPaneBox);
				if (modelBox instanceof ModelClass) {
					createObject.setDisable(!this.showObjects.isSelected());
				}
				else if (modelBox instanceof ModelObject) {
					ModelObject modelObject = (ModelObject) modelBox;
					if(modelObject.isSuperObject() && (selectedPaneBox.getSelectedLabel() == null || selectedPaneBox.getSelectedLabel().equals(selectedPaneBox.getTopLabel()))) {
						deleteSelected.setDisable(true);
					}
				}
			} else if (selectable instanceof Arrow && selectionController.isCurrentSelected(selectable)) {
				Arrow selectedArrow = (Arrow) selectable;
				pickColor.setValue(selectedArrow.getColor());
			}
		}
		if (selectionController.hasCurrentSelection()
				&& !this.objectGraphMode.isSelected()
				&& (selectionController.getCurrentSelected().equals(subSceneAdapter)
				|| selectionController.getCurrentSelected().equals(subSceneAdapter.getFloor()))) { // SubSceneAdapter selected
			createObject.setDisable(true);
			deleteSelected.setDisable(true);
			pickColorLabel.setDisable(this.relationCreationController.isChoosingStartBox());
			pickColor.setDisable(this.relationCreationController.isChoosingStartBox());
			pickColor.setValue(subSceneAdapter.getFloor().getColor());
		}
	}

}
