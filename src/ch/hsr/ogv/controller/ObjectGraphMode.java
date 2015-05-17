package ch.hsr.ogv.controller;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.view.Arrow;
import ch.hsr.ogv.view.PaneBox;
import ch.hsr.ogv.view.SubSceneAdapter;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class ObjectGraphMode {
	
	private List<PaneBox> arrayObjects = new ArrayList<PaneBox>();
	private List<Arrow> arrayRelations = new ArrayList<Arrow>();
	
	private ModelViewConnector mvConnector;
	private SubSceneAdapter subSceneAdapter;
	
	public ObjectGraphMode(ModelViewConnector mvConnector, SubSceneAdapter subSceneAdapter) {
		this.mvConnector = mvConnector;
		this.subSceneAdapter = subSceneAdapter;
		setupObjectGraphMode();
	}
	
	private void addGraphObject(PaneBox paneBox) {
		this.subSceneAdapter.add(paneBox.get());
		paneBox.get().applyCss();
	}
	
	private void removeGraphObject(PaneBox paneBox) {
		this.subSceneAdapter.remove(paneBox.get());
	}
	
	private void setupObjectGraphMode() {
		for(ModelBox modelBox : this.mvConnector.getBoxes().keySet()) {
			if(modelBox instanceof ModelClass) {
				ModelClass modelClass = (ModelClass) modelBox;
				for(ModelObject modelObject : modelClass.getModelObjects()) {
					buildGraphObject(modelObject);
				}
			}
		}
	}
	
	private void buildGraphObject(ModelObject modelObject) {
		PaneBox paneBox = new PaneBox();
		paneBox.setTopText(modelObject.getName() + " : " + modelObject.getModelClass().getName());
		paneBox.setTopUnderline(true);
		paneBox.setColor(modelObject.getColor());
		paneBox.setTranslateXYZ(modelObject.getCoordinates());
		
		addGraphObject(paneBox);
	}
	
	public void buildGraphRelation(Relation relation) {
		
	}

}
