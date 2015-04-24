package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import javafx.event.ActionEvent;
import javafx.geometry.Point3D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.Selectable;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 *
 * @author Adrian Rieser
 *
 */
public class ContextMenuController extends Observable implements Observer {

	private ModelViewConnector mvConnector;
	private volatile Selectable selected;
	private volatile Point3D position;

	// Subscene
	private ContextMenu subSceneCM;
	private MenuItem createClass;

	// Class
	private ContextMenu classCM;
	private MenuItem createObject;
	private MenuItem renameClass;
	private MenuItem addAttribute;
	private MenuItem deleteClass;

	// Object
	private ContextMenu objectCM;
	private MenuItem renameObject;
	private MenuItem deleteObject;

	// Relation
	private ContextMenu relationCM;
	private MenuItem changeDirection;
	private MenuItem associationClass;
	private MenuItem addMultiplicity;
	private MenuItem addRole;
	private MenuItem deleteMultiplicity;
	private MenuItem deleteRole;
	private MenuItem deleteRelation;
	private MenuItem createUndirectedAssociation;
	private MenuItem createDirectedAssociation;
	private MenuItem createBidirectedAssociation;
	private MenuItem createUndirectedAggregation;
	private MenuItem createDirectedAggregation;
	private MenuItem createUndirectedComposition;
	private MenuItem createDirectedComposition;
	private MenuItem createGeneralization;
	private MenuItem createDependency;

	// Attribute
	private MenuItem renameAttribute;
	private MenuItem moveAttributeUp;
	private MenuItem moveAttributeDown;
	private MenuItem deleteAttribute;

	// Value (Attribute)
	private MenuItem changeValue;
	private MenuItem deleteValue;

	public ContextMenuController() {

		// Subscene
		subSceneCM = new ContextMenu();
		createClass = getMenuItem("Create Class", Resource.CLASS_GIF, subSceneCM);

		// Class
		classCM = new ContextMenu();
		renameClass = getMenuItem("Rename Class", Resource.RENAME_GIF, classCM);
		createObject = getMenuItem("Create Object", Resource.OBJECT_GIF, classCM);
		getClassRelationMenu("Create Relation", Resource.RELATION_GIF, classCM);
		addAttribute = getMenuItem("Add Attribute", Resource.ADD_ATTR_GIF, classCM);
		classCM.getItems().add(new SeparatorMenuItem());
		renameAttribute = getMenuItem("Rename Attribute", Resource.RENAME_ATTR_GIF, classCM);
		moveAttributeUp = getMenuItem("Move Up", Resource.MOVE_UP_PNG, classCM);
		moveAttributeDown = getMenuItem("Move Down", Resource.MOVE_DOWN_PNG, classCM);
		deleteAttribute = getMenuItem("Delete Attribute", Resource.DELETE_PNG, classCM);
		classCM.getItems().add(new SeparatorMenuItem());
		deleteClass = getMenuItem("Delete Class", Resource.DELETE_PNG, classCM);

		// Object
		objectCM = new ContextMenu();
		renameObject = getMenuItem("Rename Object", Resource.RENAME_GIF, objectCM);
		objectCM.getItems().add(new SeparatorMenuItem());
		changeValue = getMenuItem("Change Value", Resource.RENAME_ATTR_GIF, objectCM);
		deleteValue = getMenuItem("Delete Value", Resource.DELETE_PNG, objectCM);
		objectCM.getItems().add(new SeparatorMenuItem());
		deleteObject = getMenuItem("Delete Object", Resource.DELETE_PNG, objectCM);

		// Relation
		relationCM = new ContextMenu();
		changeDirection = getMenuItem("Change Direction", Resource.CHANGE_DIRECTION_GIF, relationCM);
		associationClass = getMenuItem("To Association Class", Resource.ASSOCIATION_CLASS_GIF, relationCM);
		relationCM.getItems().add(new SeparatorMenuItem());
		addMultiplicity = getMenuItem("Add Multiplicity", Resource.ADD_MULTIPLICITY_GIF, relationCM);
		deleteMultiplicity = getMenuItem("Delete Multiplicity", Resource.DELETE_PNG, relationCM);
		relationCM.getItems().add(new SeparatorMenuItem());
		addRole = getMenuItem("Add Role", Resource.ADD_ROLE_GIF, relationCM);
		deleteRole = getMenuItem("Delete Role", Resource.DELETE_PNG, relationCM);
		relationCM.getItems().add(new SeparatorMenuItem());
		deleteRelation = getMenuItem("Delete Relation", Resource.DELETE_PNG, relationCM);
	}

	private Menu getClassRelationMenu(String title, Resource image, ContextMenu parent) {
		Menu relationMenu = new Menu(title);
		relationMenu.setGraphic(getImageView(image));

		createUndirectedAssociation = getMenuItem("Association", Resource.UNDIRECTED_ASSOCIATION_GIF, relationMenu);
		createDirectedAssociation = getMenuItem("Directed Association", Resource.DIRECTED_ASSOCIATION_GIF, relationMenu);
		createBidirectedAssociation = getMenuItem("Bidirected Association", Resource.BIDIRECTED_ASSOCIATION_GIF, relationMenu);
		createUndirectedAggregation = getMenuItem("Aggregation", Resource.UNDIRECTED_AGGREGATION_GIF, relationMenu);
		createDirectedAggregation = getMenuItem("Directed Aggregation", Resource.DIRECTED_AGGREGATION_GIF, relationMenu);
		createUndirectedComposition = getMenuItem("Composition", Resource.UNDIRECTED_COMPOSITION_GIF, relationMenu);
		createDirectedComposition = getMenuItem("Directed Composition", Resource.DIRECTED_COMPOSITION_GIF, relationMenu);
		relationMenu.getItems().add(new SeparatorMenuItem());
		createGeneralization = getMenuItem("Generalization", Resource.GENERALIZATION_GIF, relationMenu);
		relationMenu.getItems().add(new SeparatorMenuItem());
		createDependency = getMenuItem("Dependency", Resource.DEPENDENCY_GIF, relationMenu);
		parent.getItems().add(relationMenu);
		return relationMenu;

	}

	public void enableContextMenu(SubSceneAdapter subSceneAdapter) {
		subSceneAdapter.getFloor().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (subSceneAdapter.getFloor().isSelected() && me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				PickResult pick = me.getPickResult();
				if (pick != null && pick.getIntersectedNode() != null && subSceneAdapter.getFloor().hasTile(pick.getIntersectedNode())) {
					this.position = pick.getIntersectedNode().localToParent(pick.getIntersectedPoint());
				}
				subSceneCM.hide();
				subSceneCM.show(subSceneAdapter.getFloor(), me.getScreenX(), me.getScreenY());
			} else if (subSceneCM.isShowing()) {
				subSceneCM.hide();
			}
		});
	}

	public void enableContextMenu(ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				hideAllContextMenus();
				if (modelBox instanceof ModelClass) {
					// Class
					enableAttributeSelected(false);
					addAttribute.setDisable(paneBox.numberCenterLabelShowing() >= PaneBox.MAX_CENTER_LABELS);
					classCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				} else if ((modelBox instanceof ModelObject)) {
					// Object
					hideAllContextMenus();
					changeValue.setDisable(true);
					objectCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
			}
			me.consume();
		});

		for (Label centerLabel : paneBox.getCenterLabels()) {
			enableContextMenu(centerLabel, modelBox, paneBox, subSceneAdapter);
		}
	}

	private void enableContextMenu(Label label, ModelBox modelBox, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		label.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				hideAllContextMenus();
				if (modelBox instanceof ModelClass) {
					// Label on Class
					enableAttributeSelected(true);
					addAttribute.setDisable(paneBox.numberCenterLabelShowing() >= PaneBox.MAX_CENTER_LABELS);
					int rowIndex = paneBox.getCenterLabels().indexOf(paneBox.getSelectedLabel());
					moveAttributeUp.setDisable(rowIndex <= 0 || rowIndex > paneBox.numberCenterLabelShowing() - 1);
					moveAttributeDown.setDisable(rowIndex < 0 || rowIndex >= paneBox.numberCenterLabelShowing() - 1);
					classCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				} else if (modelBox instanceof ModelObject) {
					// Label on Object
					changeValue.setDisable(false);
					objectCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
				me.consume();
			}
		});

	}

	public void enableContextMenu(Relation relation, Arrow arrow) {
		arrow.getLineSelectionHelper().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				hideAllContextMenus();
				addMultiplicity.setDisable(true);
				addRole.setDisable(true);
				deleteMultiplicity.setDisable(true);
				deleteRole.setDisable(true);
				relationCM.show(arrow, me.getScreenX(), me.getScreenY());
				me.consume();
			}
		});

		arrow.getStartSelectionHelper().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				hideAllContextMenus();
				addMultiplicity.setDisable(false);
				addRole.setDisable(false);
				deleteMultiplicity.setDisable(false);
				deleteRole.setDisable(false);
				relationCM.show(arrow, me.getScreenX(), me.getScreenY());
				me.consume();
			}
		});

		arrow.getEndSelectionHelper().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				hideAllContextMenus();
				addMultiplicity.setDisable(false);
				addRole.setDisable(false);
				deleteMultiplicity.setDisable(false);
				deleteRole.setDisable(false);
				relationCM.show(arrow, me.getScreenX(), me.getScreenY());
				me.consume();
			}
		});
	}

	private void hideAllContextMenus() {
		subSceneCM.hide();
		classCM.hide();
		objectCM.hide();
		relationCM.hide();
	}

	private void enableAttributeSelected(boolean isAttributeActive) {
		renameAttribute.setDisable(!isAttributeActive);
		moveAttributeUp.setDisable(!isAttributeActive);
		moveAttributeDown.setDisable(!isAttributeActive);
		deleteAttribute.setDisable(!isAttributeActive);
	}

	public void setMVConnector(ModelViewConnector mvConnector) {
		this.mvConnector = mvConnector;
		fillContextMenu();
	}

	public void fillContextMenu() {

		// SubScene
		createClass.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateNewClass(position);
		});

		// Class
		createObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateNewObject(selected);
		});
		renameClass.setOnAction((ActionEvent e) -> {
			mvConnector.handleRename(selected);
		});
		addAttribute.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateNewAttribute(selected);

		});
		deleteClass.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});
		createUndirectedAssociation.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateUndirectedAssociation(selected);
		});
		createDirectedAssociation.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateDirectedAssociation(selected);
		});
		createBidirectedAssociation.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateBidirectedAssociation(selected);
		});
		createUndirectedAggregation.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateUndirectedAggregation(selected);
		});
		createDirectedAggregation.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateDirectedAggregation(selected);
		});
		createUndirectedComposition.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateUndirectedComposition(selected);
		});
		createDirectedComposition.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateDirectedComposition(selected);
		});
		createGeneralization.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateGeneralization(selected);
		});
		createDependency.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateDependency(selected);
		});

		// Object
		renameObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleRename(selected);
		});
		deleteObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});

		// Relation
		changeDirection.setOnAction((ActionEvent e) -> {
			mvConnector.handleChangeDirection(selected);
		});
		deleteRelation.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});

		// Attribute
		renameAttribute.setOnAction((ActionEvent e) -> {
			mvConnector.handleRename(selected);
		});
		moveAttributeUp.setOnAction((ActionEvent e) -> {
			mvConnector.handleMoveAttributeUp(selected);
		});
		moveAttributeDown.setOnAction((ActionEvent e) -> {
			mvConnector.handleMoveAttributeDown(selected);
		});
		deleteAttribute.setOnAction((ActionEvent e) -> {
			mvConnector.handleDeleteAttribute(selected);
		});

		// Value (Attribute)
		changeValue.setOnAction((ActionEvent e) -> {
			mvConnector.handleRename(selected);
		});
		
		deleteValue.setOnAction((ActionEvent e) -> {
			mvConnector.handleDeleteAttributeValue(selected);
		});

	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof SelectionController && (arg instanceof PaneBox || arg instanceof Arrow)) {
			SelectionController selectionController = (SelectionController) o;
			if (selectionController.hasCurrentSelection()) {
				this.selected = (Selectable) arg;
			}
		}
	}

	private ImageView getImageView(Resource image) {
		return new ImageView(ResourceLocator.getResourcePath(image).toExternalForm());
	}

	private MenuItem getMenuItem(String title, Resource image, ContextMenu parent) {
		MenuItem menuItem = new MenuItem(title);
		menuItem.setGraphic(getImageView(image));
		parent.getItems().add(menuItem);
		return menuItem;
	}

	private MenuItem getMenuItem(String title, Resource image, Menu parent) {
		MenuItem menuItem = new MenuItem(title);
		menuItem.setGraphic(getImageView(image));
		parent.getItems().add(menuItem);
		return menuItem;
	}
}
