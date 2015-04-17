package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import jfxtras.labs.util.Util;

/**
 *
 * @author Adrian Rieser
 *
 */
public class ModelClass extends ModelBox {

	private final static double OBJECT_LEVEL_DIFF = 100;

	private List<Attribute> attributes = new ArrayList<Attribute>();
	private List<ModelObject> modelObjects = new ArrayList<ModelObject>();

	public static volatile AtomicInteger modelClassCounter = new AtomicInteger(0);

	public ModelClass(Point3D coordinates, double width, double heigth, Color color) {
		super("Class" + modelClassCounter.addAndGet(1), coordinates, width, heigth, color);
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public void changeAttributeName(int rowIndex, String name) throws IndexOutOfBoundsException {
		Attribute attribute = this.attributes.get(rowIndex);
		attribute.setName(name);
		setChanged();
		notifyObservers(attribute);
		for (ModelObject modelObject : this.modelObjects) {
			modelObject.changeAttributeName(attribute, name);
		}
	}

	public List<ModelObject> getModelObjects() {
		return modelObjects;
	}

	public void setModelObjects(List<ModelObject> modelObjects) {
		this.modelObjects = modelObjects;
	}

	private boolean addModelObject(ModelObject modelObject) {
		if (!modelObjects.contains(modelObject)) {
			return modelObjects.add(modelObject);
		}
		return false;
	}

	private boolean addAttribute(Attribute attribute) {
		if (!attributes.contains(attribute) && attributes.add(attribute)) {
			for (ModelObject modelObject : getModelObjects()) {
				modelObject.addAttributeValue(attribute, "");
			}
			return true;
		}
		return false;
	}

	public ModelObject createModelObject() {
		double levelPlus = getTopLevel() + OBJECT_LEVEL_DIFF;
		Point3D modelObjectCoordinates = new Point3D(this.getX(), levelPlus, this.getZ());
		ModelObject modelObject = new ModelObject(this, modelObjectCoordinates, this.getWidth(), this.getHeight(), Util.brighter(this.getColor(), 0.1));
		for (Attribute attribute : getAttributes()) {
			modelObject.addAttributeValue(attribute, "");
		}
		boolean added = addModelObject(modelObject);
		if (added) {
			return modelObject;
		}
		return null;
	}
	
	public void resetObjectLevel() {
		for(int i = 0; i < this.modelObjects.size(); i++) {
			ModelObject modelObject = this.modelObjects.get(i);
			double level = (i + 1.0) * OBJECT_LEVEL_DIFF;
			Point3D modelObjectCoordinates = new Point3D(modelObject.getX(), level, this.getZ());
			modelObject.setCoordinates(modelObjectCoordinates);
		}
	}
	
	private double getTopLevel() {
		double y = 0.0;
		for(ModelObject modelObject : this.modelObjects) {
			if(modelObject.getY() > y) {
				y = modelObject.getY();
			}
		}
		return y;
	}

	public Attribute createAttribute() {
		Attribute attribute = new Attribute("field" + (this.attributes.size() + 1));
		boolean added = addAttribute(attribute);
		if (added) {
			setChanged();
			notifyObservers(attribute);
			return attribute;
		}
		return null;
	}

	public void deleteModelObjects() {
		// ModelObject.modelObjectCounter.decrementAndGet();
		modelObjects.clear();
	}

	public boolean deleteModelObject(ModelObject modelObject) {
		// ModelObject.modelObjectCounter.decrementAndGet();
		boolean removed =  modelObjects.remove(modelObject);
		if(removed) {
			double level = getTopLevel() + OBJECT_LEVEL_DIFF;
			Point3D modelObjectCoordinates = new Point3D(modelObject.getX(), level, this.getZ());
			modelObject.setCoordinates(modelObjectCoordinates);
		}
		return removed;
	}

	public boolean deleteAttribute(int index) {
		if (getAttributes().isEmpty() || index < 0 || index >= getAttributes().size()) {
			return false;
		}
		Attribute attribute = getAttributes().get(index);
		boolean deletedObject = deleteAttribute(attribute);

		return deletedObject;
	}

	public boolean deleteAttribute(Attribute attribute) {
		for (ModelObject modelObject : getModelObjects()) {
			modelObject.deleteAttributeValue(attribute);
		}
		boolean deleted = attributes.remove(attribute);
		if (deleted) {
			setChanged();
			notifyObservers(attribute);
		}
		return deleted;
	}

	public boolean hasSuperClass() {
		for (Endpoint endpoint : this.getEndpoints()) {
			if (endpoint.getFriend().getType() == EndpointType.EMPTY_ARROW) {
				return true;
			}
		}
		return false;
	}

	public ModelClass getSuperClass() {
		for (Endpoint endpoint : this.getEndpoints()) {
			if (endpoint.getFriend().getType() == EndpointType.EMPTY_ARROW) {
				return (ModelClass) endpoint.getAppendant();
			}
		}
		return null;
	}

}
