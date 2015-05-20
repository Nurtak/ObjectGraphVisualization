package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import ch.hsr.ogv.controller.ModelViewConnector;
import ch.hsr.ogv.model.Attribute;
import ch.hsr.ogv.model.Endpoint;
import ch.hsr.ogv.model.ModelBox;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;

/**
 * 
 * @author Adrian Rieser, Simon Gwerder
 *
 */
public class Persistancy {


	private ModelManager modelManager;

	public Persistancy(ModelManager modelManager) {
		this.modelManager = modelManager;
	}

	public void saveOGVDataAsync(File file, PersistencyCallback callback) {
		OGVSerialization ogvSerialization = new OGVSerialization();
		saveDataAsync(ogvSerialization, file, callback);
	}

	private void saveDataAsync(SerializationStrategy serialStrategy, File file, PersistencyCallback callback) {
		serialStrategy.setClasses(new HashSet<ModelClass>(modelManager.getClasses()));
		serialStrategy.setRelations(new HashSet<Relation>(modelManager.getRelations()));
		Task<Void> loadTask = new Task<Void>() {
			@Override
			public Void call() {
				boolean saved = serialStrategy.serialize(file);
				Platform.runLater(() -> {
					callback.completed(saved);
				});
				return null;
			}
		};
		new Thread(loadTask).start();
	}
	
	public boolean saveOGVData(File file) {
		OGVSerialization ogvSerialization = new OGVSerialization();
		return saveData(ogvSerialization, file);
	}

	private boolean saveData(SerializationStrategy serialStrategy, File file) {
		serialStrategy.setClasses(new HashSet<ModelClass>(modelManager.getClasses()));
		serialStrategy.setRelations(new HashSet<Relation>(modelManager.getRelations()));
		return serialStrategy.serialize(file);
	}

	public void loadOGVDataAsync(File file, PersistencyCallback callback) {
		OGVSerialization ogvSerialization = new OGVSerialization();
		loadDataAsync(ogvSerialization, file, callback);
	}

	public void loadXMIDataAsync(File file, PersistencyCallback callback) {
		XMISerialization xmiSerialization = new XMISerialization();
		loadDataAsync(xmiSerialization, file, callback);
	}

	private void loadDataAsync(SerializationStrategy serialStrategy, File file, PersistencyCallback callback) {
		Task<Void> loadTask = new Task<Void>() {
			@Override
			public Void call() {
				boolean loaded = serialStrategy.parse(file);
				Platform.runLater(() -> {
					if (!loaded) {
						callback.completed(false);
					}
					else {
						loadedToModel(serialStrategy);
						callback.completed(true);
					}
				});
				return null;
			}
		};
		new Thread(loadTask).start();
	}

	public boolean loadOGVData(File file) {
		OGVSerialization ogvSerialization = new OGVSerialization();
		return loadData(ogvSerialization, file);
	}

	public boolean loadXMIData(File file) {
		XMISerialization xmiSerialization = new XMISerialization();
		return loadData(xmiSerialization, file);
	}
	
	private boolean loadData(SerializationStrategy serialStrategy, File file) {
		boolean loaded = serialStrategy.parse(file);
		if (!loaded) {
			return false;
		}
		loadedToModel(serialStrategy);
		return true;
	}

	private void loadedToModel(SerializationStrategy serialStrategy) {
		modelManager.clearClasses();
		modelManager.clearRelations();

		for (ModelClass loadedClass : serialStrategy.getClasses()) {
			loadedClassToModel(loadedClass);
		}

		for (Relation loadedRelation : serialStrategy.getRelations()) {
			loadedRelationToModel(loadedRelation);
		}
	}

	private void loadedClassToModel(ModelClass loadedClass) {
		ModelClass newClass = modelManager.createClass(new Point3D(loadedClass.getX(), ModelViewConnector.BASE_BOX_DEPTH, loadedClass.getZ()), loadedClass.getWidth(), loadedClass.getHeight(),
				loadedClass.getColor());
		if (newClass != null) {
			newClass.setName(loadedClass.getName());
			newClass.setEndpoints(loadedClass.getEndpoints());

			for (ModelObject loadedObject : loadedClass.getModelObjects()) {
				loadedObjectToModel(newClass, loadedObject);
			}

			for (Attribute loadedAttribute : loadedClass.getAttributes()) {
				loadedAttributeToModel(newClass, loadedClass, loadedAttribute);
			}

		}
	}

	private void loadedObjectToModel(ModelClass newClass, ModelObject loadedObject) {
		ModelObject newObject = modelManager.createObject(newClass);
		if (newObject != null) {
			newObject.setName(loadedObject.getName());
			newObject.setY(loadedObject.getY());
			newObject.setColor(loadedObject.getColor());
			newObject.setEndpoints(loadedObject.getEndpoints());
		}
	}

	private void loadedAttributeToModel(ModelClass newClass, ModelClass loadedClass, Attribute loadedAttribute) {
		Attribute newAttribute = newClass.createAttribute(loadedAttribute.getName());
		if (newAttribute != null) {
			loadedAttributeValueToModel(newClass, loadedClass, newAttribute, loadedAttribute);
		}
	}

	private void loadedAttributeValueToModel(ModelClass newClass, ModelClass loadedClass, Attribute newAttribute, Attribute loadedAttribute) {
		for (int i = 0; i < newClass.getModelObjects().size(); i++) {
			try {
				ModelObject loadedObject = loadedClass.getModelObjects().get(i);
				ModelObject newObject = newClass.getModelObjects().get(i);
				if (loadedObject != null && newObject != null) {
					String newAttributeValue = loadedObject.getAttributeValue(loadedAttribute.getName());
					if (newAttributeValue != null) {
						newObject.changeAttributeValue(newAttribute.getName(), newAttributeValue);
					}
				}
			}
			catch (IndexOutOfBoundsException ioobe) {
				continue;
			}
		}
	}

	private ModelBox handleBoxByEndpoint(Endpoint endpoint) {
		if (endpoint.getAppendant() != null) {
			return endpoint.getAppendant();
		}
		for (ModelClass modelClass : this.modelManager.getClasses()) {
			for (Endpoint classEndpoint : new ArrayList<Endpoint>(modelClass.getEndpoints())) {
				if (classEndpoint.getUniqueID().equals(endpoint.getUniqueID())) {
					modelClass.getEndpoints().remove(classEndpoint);
					return modelClass;
				}
			}
			for (ModelObject modelObject : modelClass.getModelObjects()) {
				for (Endpoint objectEndpoint : new ArrayList<Endpoint>(modelObject.getEndpoints())) {
					if (objectEndpoint.getUniqueID().equals(endpoint.getUniqueID())) {
						modelObject.getEndpoints().remove(objectEndpoint);
						return modelObject;
					}
				}
			}
		}
		return null;
	}

	private void loadedRelationToModel(Relation loadedRelation) {
		ModelBox loadedStartBox = handleBoxByEndpoint(loadedRelation.getStart());
		ModelBox loadedEndBox = handleBoxByEndpoint(loadedRelation.getEnd());
		if (loadedStartBox == null || loadedEndBox == null)
			return;
		ModelBox newStartBox = null;
		ModelBox newEndBox = null;
		if (loadedStartBox instanceof ModelClass && loadedEndBox instanceof ModelClass) {
			newStartBox = modelManager.getModelClass(loadedStartBox.getName());
			newEndBox = modelManager.getModelClass(loadedEndBox.getName());
		}
		else if (loadedStartBox instanceof ModelObject && loadedEndBox instanceof ModelObject) {
			for (ModelClass newModelClass : modelManager.getClasses()) {
				newStartBox = newModelClass.getModelObject(((ModelObject) loadedStartBox).getUniqueID());
				if (newStartBox != null) {
					break;
				}
			}
			for (ModelClass newModelClass : modelManager.getClasses()) {
				newEndBox = newModelClass.getModelObject(((ModelObject) loadedEndBox).getUniqueID());
				if (newEndBox != null) {
					break;
				}
			}
		}

		if (newStartBox == null || newEndBox == null)
			return;

		Relation newRelation = modelManager.createRelation(newStartBox, newEndBox, loadedRelation.getRelationType(), loadedRelation.getColor());
		if (newRelation != null) {
			loadedRoleMultiToModel(newRelation, loadedRelation);
		}
	}

	private void loadedRoleMultiToModel(Relation newRelation, Relation loadedRelation) {
		newRelation.setName(loadedRelation.getName());
		newRelation.setStartRoleName(loadedRelation.getStart().getRoleName());
		newRelation.setStartMultiplicity(loadedRelation.getStart().getMultiplicity());

		newRelation.setEndRoleName(loadedRelation.getEnd().getRoleName());
		newRelation.setEndMultiplicity(loadedRelation.getEnd().getMultiplicity());
	}

}
