package ch.hsr.ogv.controller;

import java.util.Observable;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.util.ModelUtil;
import ch.hsr.ogv.view.PaneBox;

/**
 *
 * @author Adrian Rieser
 *
 */
public class ContextMenuController extends Observable {

	private ContextMenu classCM;
	private MenuItem cInstantiateObject;
	private MenuItem cRename;

	private ContextMenu objectCM;
	private MenuItem oRename;

	public ContextMenuController() {
		classCM = new ContextMenu();
		cInstantiateObject = new MenuItem("Instantiate Object");
		cRename = new MenuItem("Rename Class");
		classCM.getItems().add(cInstantiateObject);
		classCM.getItems().add(cRename);

		objectCM = new ContextMenu();
		oRename = new MenuItem("Rename Object");
		objectCM.getItems().add(oRename);
	}

	public void enableContextMenu(ModelBox modelBox, PaneBox paneBox) {
		if (ModelUtil.isClass(modelBox)) {
			paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (paneBox.isSelected() && me.getButton() == MouseButton.SECONDARY) {
					System.out.println("Open contextmenu on class " + modelBox.getName());
					classCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
			});
		} else if (ModelUtil.isObject(modelBox)) {
			paneBox.get().addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
				if (paneBox.isSelected() && me.getButton() == MouseButton.SECONDARY) {
					System.out.println("Open contextmenu on object " + modelBox.getName());
					objectCM.show(paneBox.get(), me.getScreenX(), me.getScreenY());
				}
			});
		}
	}

}
