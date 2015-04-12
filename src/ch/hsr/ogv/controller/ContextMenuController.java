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
	private MenuItem cInstantiateObject;
	private MenuItem cDeleteClass;

	private ContextMenu objectCM;

	public ContextMenuController() {
		classCM = new ContextMenu();
		cInstantiateObject = new MenuItem("Instantiate Object");
		cDeleteClass = new MenuItem("Delete Class");
		classCM.getItems().add(cInstantiateObject);
		classCM.getItems().add(cDeleteClass);

		objectCM = new ContextMenu();
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
		cInstantiateObject.setOnAction((ActionEvent e) -> {
			mvConnector.handleCreateNewObject(selected);
		});
		cDeleteClass.setOnAction((ActionEvent e) -> {
			mvConnector.handleDelete(selected);
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof SelectionController && arg instanceof PaneBox) {
			SelectionController selectionController = (SelectionController) o;
			if (selectionController.hasSelection()) {
				this.selected = (PaneBox) arg;
			}
		}
	}
}
