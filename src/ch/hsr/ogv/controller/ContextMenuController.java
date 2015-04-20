package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
	private Selectable selected;

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
	private MenuItem deleteRelation;

	// ModelBox
	private Menu createRelationM;
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
	private Menu attributeCM;
	private MenuItem renameAttribute;
	private MenuItem muteAttributeUp;
	private MenuItem muteAttributeDown;
	private MenuItem deleteAttribute;

	// Value (Attribute)
	private Menu valueCM;
	private MenuItem changeValue;

	public ContextMenuController() {

		// Subscene
		subSceneCM = new ContextMenu();
		createClass = getMenuItem("Create Class", Resource.CLASS_GIF, subSceneCM);

		// Class
		classCM = new ContextMenu();
		renameClass = getMenuItem("Rename Class", Resource.RENAME_GIF, classCM);
		createObject = getMenuItem("Create Object", Resource.OBJECT_GIF, classCM);
		addAttribute = getMenuItem("Add Attribute", Resource.ADD_GIF, classCM);
		deleteClass = getMenuItem("Delete Class", Resource.DELETE_PNG, classCM);
		relationMenu();

		// Object
		objectCM = new ContextMenu();
		renameObject = getMenuItem("Rename Object", Resource.RENAME_GIF, objectCM);
		deleteObject = getMenuItem("Delete Object", Resource.DELETE_PNG, objectCM);

		// Relation
		relationCM = new ContextMenu();
		changeDirection = getMenuItem("Change Direction", Resource.CHANGE_DIRECTION_GIF, relationCM);
		deleteRelation = getMenuItem("Delete Relation", Resource.DELETE_PNG, relationCM);

	}

	private void relationMenu() {
		// Class - Relation
		createRelationM = new Menu("Create Relation");
		createRelationM.setGraphic(getImageView(Resource.RELATION_GIF));

		createUndirectedAssociation = getMenuItem("Association", Resource.UNDIRECTED_ASSOCIATION_GIF, createRelationM);
		createDirectedAssociation = getMenuItem("Directed Association", Resource.DIRECTED_ASSOCIATION_GIF, createRelationM);
		createBidirectedAssociation = getMenuItem("Bidirected Association", Resource.BIDIRECTED_ASSOCIATION_GIF, createRelationM);
		createUndirectedAggregation = getMenuItem("Aggregation", Resource.UNDIRECTED_AGGREGATION_GIF, createRelationM);
		createDirectedAggregation = getMenuItem("Directed Aggregation", Resource.DIRECTED_AGGREGATION_GIF, createRelationM);
		createUndirectedComposition = getMenuItem("Composition", Resource.UNDIRECTED_COMPOSITION_GIF, createRelationM);
		createDirectedComposition = getMenuItem("Directed Composition", Resource.DIRECTED_COMPOSITION_GIF, createRelationM);
		createRelationM.getItems().add(new SeparatorMenuItem());
		createGeneralization = getMenuItem("Generalization", Resource.GENERALIZATION_GIF, createRelationM);
		createRelationM.getItems().add(new SeparatorMenuItem());
		createDependency = getMenuItem("Dependency", Resource.DEPENDENCY_GIF, createRelationM);

	}

	public void enableContextMenu(SubSceneAdapter subSceneAdapter) {
		subSceneAdapter.getSubScene().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				subSceneCM.hide();
				subSceneCM.show(subSceneAdapter.getSubScene(), me.getScreenX(), me.getScreenY());
			} else if (subSceneCM.isShowing()) {
				subSceneCM.hide();
			}
		});
	}

	public void enableContextMenu(ModelBox modelBox, PaneBox paneBox) {
		if (modelBox instanceof ModelClass) {
			paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (paneBox.isSelected() && me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
					classCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
					me.consume();
				}
			});
		} else if ((modelBox instanceof ModelObject)) {
			paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (paneBox.isSelected() && me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
					objectCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
					me.consume();
				}
			});
		}
	}

	public void enableContextMenu(Relation relation, Arrow arrow) {
		arrow.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (arrow.isSelected() && me.getButton() == MouseButton.SECONDARY && me.isStillSincePress()) {
				relationCM.show(arrow, me.getScreenX(), me.getScreenY());
				me.consume();
			}
		});
	}

	public void setMVConnector(ModelViewConnector mvConnector) {
		this.mvConnector = mvConnector;
		fillContextMenu();
	}

	public void fillContextMenu() {
		// Class
		createObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateNewObject(selected);
		});
		// renameClass.setOnAction((ActionEvent e) -> {
		// mvConnector.handleRename(selected);
		// });
		deleteClass.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});

		// Object
		// renameObject.setOnAction((ActionEvent e) -> {
		// mvConnector.handleRename(selected);
		// });
		deleteObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});

		// Relation
		deleteRelation.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
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

	private MenuItem getMenuItem(String text, Resource image, ContextMenu contextMenu) {
		MenuItem menuItem = new MenuItem(text);
		menuItem.setGraphic(getImageView(image));
		contextMenu.getItems().add(menuItem);
		return menuItem;
	}

	private MenuItem getMenuItem(String text, Resource image, Menu menu) {
		MenuItem menuItem = new MenuItem(text);
		menuItem.setGraphic(getImageView(image));
		menu.getItems().add(menuItem);
		return menuItem;
	}
}
