package ch.hsr.ogv.controller;

import java.io.IOException;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.util.FXMLResourceUtil;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class ContextMenuController extends Observable{
	
	private final static Logger logger = LoggerFactory.getLogger(ContextMenuController.class);
	
	private ContextMenu contextMenu;
	private ClassContextMenu classContextMenu;
	
	public ContextMenuController(){
		loadClassContextMenu();
	}
	
	private void loadClassContextMenu() {
		FXMLLoader loader = FXMLResourceUtil.prepareLoader(Resource.CLASSCONTEXTMENU_FXML); // load contextmenu preset from fxml file
		try {
			this.contextMenu = (ContextMenu) loader.load();
			this.classContextMenu = (ClassContextMenu) loader.getController();
		} catch (IOException | ClassCastException e) {
			logger.debug(e.getMessage());
		}
	}

	public void enableContextMenu(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		rightClickClassCM(paneBox, subSceneAdapter);
	}
	
	private void rightClickClassCM(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		paneBox.get().setOnMouseClicked((MouseEvent me) -> {
			if(paneBox.isSelected() && MouseButton.SECONDARY.equals(me.getButton())) {
				contextMenu.show(paneBox, me.getScreenX(), me.getScreenY());
			}
		});
	}


}
