package ch.hsr.ogv.controller;

import java.util.Observable;
import java.util.Observer;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.view.PaneBox;

/**
 *
 * @author Adrian Rieser
 *
 */
public class ContextMenuController extends Observable implements Observer {

	private ModelViewConnector mvConnector;
	private PaneBox selected;

	private ContextMenu classCM;
	private MenuItem createObject;
	private MenuItem deleteClass;

	private ContextMenu objectCM;
	private MenuItem deleteObject;

	public ContextMenuController() {
		classCM = new ContextMenu();
		createObject = new MenuItem("Create Object");
		deleteClass = new MenuItem("Delete Class");
		classCM.getItems().add(createObject);
		classCM.getItems().add(deleteClass);

		objectCM = new ContextMenu();
		deleteObject = new MenuItem("Delete Object");
		objectCM.getItems().add(deleteObject);
	}

	public void enableContextMenu(ModelBox modelBox, PaneBox paneBox) {
		if (modelBox instanceof ModelClass) {
			paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (paneBox.isSelected() && me.getButton() == MouseButton.SECONDARY) {
					classCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
			});
		} else if ((modelBox instanceof ModelObject)) {
			paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (paneBox.isSelected() && me.getButton() == MouseButton.SECONDARY) {
					objectCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
			});
		}
	}

	public void setMVConnector(ModelViewConnector mvConnector) {
		this.mvConnector = mvConnector;
		fillContextMenu();
	}

	public void fillContextMenu() {
		createObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateNewObject(selected);
		});
		deleteClass.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof SelectionController && arg instanceof PaneBox) {
			SelectionController selectionController = (SelectionController) o;
			if (selectionController.hasCurrentSelection()) {
				this.selected = (PaneBox) arg;
			}
		}
	}
}
