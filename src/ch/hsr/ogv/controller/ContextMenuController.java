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

	private ContextMenu subSceneCM;
	private MenuItem createClass;

	private ContextMenu classCM;
	private MenuItem createObject;
	private MenuItem renameClass;
	private MenuItem deleteClass;

	private ContextMenu objectCM;
	private MenuItem renameObject;
	private MenuItem deleteObject;

	private ContextMenu relationCM;
	private MenuItem changeDirection;
	private MenuItem deleteRelation;

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

	public ContextMenuController() {
		// Subscene
		subSceneCM = new ContextMenu();
		createClass = new MenuItem("Create Class");
		ImageView createClassIV = new ImageView(ResourceLocator.getResourcePath(Resource.CLASS_GIF).toExternalForm());
		createClass.setGraphic(createClassIV);

		subSceneCM.getItems().add(createClass);

		// Class
		classCM = new ContextMenu();
		createObject = new MenuItem("Create Object");
		ImageView createObjectIV = new ImageView(ResourceLocator.getResourcePath(Resource.OBJECT_GIF).toExternalForm());
		createObject.setGraphic(createObjectIV);

		renameClass = new MenuItem("Rename Class");
		ImageView renameClassIV = new ImageView(ResourceLocator.getResourcePath(Resource.RENAME_GIF).toExternalForm());
		renameClass.setGraphic(renameClassIV);

		deleteClass = new MenuItem("Delete Class");
		ImageView deleteIV = new ImageView(ResourceLocator.getResourcePath(Resource.DELETE_PNG).toExternalForm());
		deleteClass.setGraphic(deleteIV);

		classCM.getItems().add(createObject);
		classCM.getItems().add(renameClass);
		classCM.getItems().add(deleteClass);

		// Class - Relation
		createRelationM = new Menu("Create Relation");
		ImageView relationIV = new ImageView(ResourceLocator.getResourcePath(Resource.RELATION_GIF).toExternalForm());
		createRelationM.setGraphic(relationIV);

		createUndirectedAssociation = new MenuItem("Association");
		ImageView undirectedAssociationIV = new ImageView(ResourceLocator.getResourcePath(Resource.UNDIRECTED_ASSOCIATION_GIF).toExternalForm());
		createUndirectedAssociation.setGraphic(undirectedAssociationIV);

		createDirectedAssociation = new MenuItem("Directed Association");
		ImageView directedAssociationIV = new ImageView(ResourceLocator.getResourcePath(Resource.DIRECTED_ASSOCIATION_GIF).toExternalForm());
		createDirectedAssociation.setGraphic(directedAssociationIV);

		createBidirectedAssociation = new MenuItem("Bidirected Association");
		ImageView bidirectedAssociationIV = new ImageView(ResourceLocator.getResourcePath(Resource.BIDIRECTED_ASSOCIATION_GIF).toExternalForm());
		createBidirectedAssociation.setGraphic(bidirectedAssociationIV);

		createUndirectedAggregation = new MenuItem("Aggregation");
		ImageView undirectedAggregationIV = new ImageView(ResourceLocator.getResourcePath(Resource.UNDIRECTED_AGGREGATION_GIF).toExternalForm());
		createUndirectedAggregation.setGraphic(undirectedAggregationIV);

		createDirectedAggregation = new MenuItem("Directed Aggregation");
		ImageView directedAggregationIV = new ImageView(ResourceLocator.getResourcePath(Resource.DIRECTED_AGGREGATION_GIF).toExternalForm());
		createDirectedAggregation.setGraphic(directedAggregationIV);

		createUndirectedComposition = new MenuItem("Composition");
		ImageView undirectedCompositionnIV = new ImageView(ResourceLocator.getResourcePath(Resource.UNDIRECTED_COMPOSITION_GIF).toExternalForm());
		createUndirectedComposition.setGraphic(undirectedCompositionnIV);

		createDirectedComposition = new MenuItem("Directed Composition");
		ImageView directedCompositionIV = new ImageView(ResourceLocator.getResourcePath(Resource.DIRECTED_COMPOSITION_GIF).toExternalForm());
		createDirectedComposition.setGraphic(directedCompositionIV);

		createGeneralization = new MenuItem("Generalization");
		ImageView generalizationIV = new ImageView(ResourceLocator.getResourcePath(Resource.GENERALIZATION_GIF).toExternalForm());
		createGeneralization.setGraphic(generalizationIV);

		createDependency = new MenuItem("Dependency");
		ImageView dependencyIV = new ImageView(ResourceLocator.getResourcePath(Resource.DEPENDENCY_GIF).toExternalForm());
		createDependency.setGraphic(dependencyIV);

		createRelationM.getItems().add(createUndirectedAssociation);
		createRelationM.getItems().add(createDirectedAssociation);
		createRelationM.getItems().add(createBidirectedAssociation);
		createRelationM.getItems().add(createUndirectedAggregation);
		createRelationM.getItems().add(createDirectedAggregation);
		createRelationM.getItems().add(createUndirectedComposition);
		createRelationM.getItems().add(createDirectedComposition);
		createRelationM.getItems().add(new SeparatorMenuItem());
		createRelationM.getItems().add(createGeneralization);
		createRelationM.getItems().add(new SeparatorMenuItem());
		createRelationM.getItems().add(createDependency);
		classCM.getItems().add(createRelationM);

		// Object
		objectCM = new ContextMenu();
		renameObject = new MenuItem("Rename Object");
		ImageView renameObjectIV = new ImageView(ResourceLocator.getResourcePath(Resource.RENAME_GIF).toExternalForm());
		renameObject.setGraphic(renameObjectIV);

		deleteObject = new MenuItem("Delete Object");
		ImageView deleteObjectIV = new ImageView(ResourceLocator.getResourcePath(Resource.DELETE_PNG).toExternalForm());
		deleteObject.setGraphic(deleteObjectIV);

		objectCM.getItems().add(renameObject);
		objectCM.getItems().add(deleteObject);

		// Relation
		relationCM = new ContextMenu();
		changeDirection = new MenuItem("Change Direction");
		ImageView changeDirectionIV = new ImageView(ResourceLocator.getResourcePath(Resource.CHANGE_DIRECTION_GIF).toExternalForm());
		changeDirection.setGraphic(changeDirectionIV);

		deleteRelation = new MenuItem("Delete Relation");
		ImageView deleteRelationIV = new ImageView(ResourceLocator.getResourcePath(Resource.DELETE_PNG).toExternalForm());
		deleteRelation.setGraphic(deleteRelationIV);

		relationCM.getItems().add(changeDirection);
		relationCM.getItems().add(deleteRelation);

	}

	public void enableContextMenu(SubSceneAdapter subSceneAdapter) {
		subSceneAdapter.getSubScene().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (me.getButton() == MouseButton.SECONDARY) {
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
				if (paneBox.isSelected() && me.getButton() == MouseButton.SECONDARY) {
					classCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
					me.consume();
				}
			});
		} else if ((modelBox instanceof ModelObject)) {
			paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (paneBox.isSelected() && me.getButton() == MouseButton.SECONDARY) {
					objectCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
					me.consume();
				}
			});
		}
	}

	public void enableContextMenu(Relation relation, Arrow arrow) {
		arrow.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
			if (arrow.isSelected() && me.getButton() == MouseButton.SECONDARY) {
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
}
