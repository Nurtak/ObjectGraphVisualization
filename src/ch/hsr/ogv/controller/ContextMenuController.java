package ch.hsr.ogv.controller;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.controller.contextmenu.ClassCMController;
import ch.hsr.ogv.controller.contextmenu.ObjectCMController;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
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

	public void enableClassCM(ModelClass modelClass, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		classCMController.show(modelClass, paneBox, subSceneAdapter);
	}

	public void enableObjectCM(ModelObject modelObject, PaneBox paneBox, SubSceneAdapter subSceneAdapter) {
		objectCMController.show(modelObject, paneBox, subSceneAdapter);
	}

}
