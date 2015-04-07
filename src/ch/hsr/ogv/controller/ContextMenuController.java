package ch.hsr.ogv.controller;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 * 
 * @author Adrian Rieser
 *
 */
public class ContextMenuController extends Observable {

	private final static Logger logger = LoggerFactory.getLogger(ContextMenuController.class);

	private ClassCMController classCMController;
	private ObjectCMController objectCMController;

	public ContextMenuController() {
		classCMController = new ClassCMController();
		objectCMController = new ObjectCMController();
	}

	public void enableContextMenu(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		
		rightClickClassCM(paneBox, subSceneAdapter);
	}

	private void rightClickClassCM(PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		classCMController.show(paneBox, subSceneAdapter);
	}

}
