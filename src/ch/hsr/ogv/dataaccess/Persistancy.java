package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.util.HashSet;

import javafx.geometry.Point3D;
import ch.hsr.ogv.controller.ModelViewConnector;
import ch.hsr.ogv.model.Attribute;
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
	
	public boolean saveOGVData(File file) {
		OGVSerialization ogvSerialization = new OGVSerialization();
		return saveData(ogvSerialization, file);
	}
	
	public boolean saveData(SerializationStrategy serialStrategy, File file) {
		serialStrategy.setClasses(new HashSet<ModelClass>(modelManager.getClasses()));
		serialStrategy.setRelations(new HashSet<Relation>(modelManager.getRelations()));
		return serialStrategy.serialize(file);
	}

	public boolean loadOGVData(File file) {
		OGVSerialization ogvSerialization = new OGVSerialization();
		return loadData(ogvSerialization, file);
	}
	
	public boolean loadXMIData(File file) {
		XMISerialization xmiSerialization = new XMISerialization();
		return loadData(xmiSerialization, file);
	}
	
	public boolean loadData(SerializationStrategy serialStrategy, File file) {
		boolean loaded = serialStrategy.parse(file);
		if(!loaded) {
			return false;
		}
		
		modelManager.clearClasses();
		modelManager.clearRelations();
		
		System.out.println("getRelations.size: " + serialStrategy.getRelations().size());

		for (ModelClass loadedClass : serialStrategy.getClasses()) {
			loadedClassToModel(loadedClass);
		}
		
		for (Relation loadedRelation : serialStrategy.getRelations()) {
			loadedRelationToModel(loadedRelation);
		}
		
		return true;
	}
	
	private void loadedClassToModel(ModelClass loadedClass) {
		ModelClass newClass = modelManager.createClass(new Point3D(loadedClass.getX(), ModelViewConnector.BASE_BOX_DEPTH, loadedClass.getZ()), loadedClass.getWidth(), loadedClass.getHeight(), loadedClass.getColor());
		if(newClass != null) {
			newClass.setName(loadedClass.getName());
			
			for(ModelObject loadedObject : loadedClass.getModelObjects()) {
				loadedObjectToModel(newClass, loadedObject);
			}
			
			for(Attribute loadedAttribute : loadedClass.getAttributes()) {
				loadedAttributeToModel(newClass, loadedClass, loadedAttribute);
			}
			
		}
	}
	
	private void loadedObjectToModel(ModelClass newClass, ModelObject loadedObject) {
		ModelObject newObject = modelManager.createObject(newClass);
		if(newObject != null) {
			newObject.setName(loadedObject.getName());
			newObject.setY(loadedObject.getY());
			newObject.setColor(loadedObject.getColor());
		}
	}
	
	private void loadedAttributeToModel(ModelClass newClass, ModelClass loadedClass, Attribute loadedAttribute) {
		Attribute newAttribute = newClass.createAttribute(loadedAttribute.getName());
		if(newAttribute != null) {
			loadedAttributeValueToModel(newClass, loadedClass, newAttribute, loadedAttribute);
		}
	}
	
	private void loadedAttributeValueToModel(ModelClass newClass, ModelClass loadedClass, Attribute newAttribute, Attribute loadedAttribute) {
		for(int i = 0; i < newClass.getModelObjects().size(); i++) {
			try {
				ModelObject loadedObject = loadedClass.getModelObjects().get(i);
				ModelObject newObject = newClass.getModelObjects().get(i);
				if(loadedObject != null && newObject != null) {
					String newAttributeValue = loadedObject.getAttributeValue(loadedAttribute.getName());
					if(newAttributeValue != null) {
						newObject.changeAttributeValue(newAttribute.getName(), newAttributeValue);
					}
				}
			}
			catch(IndexOutOfBoundsException ioobe) {
				continue;
			}
		}
	}
	
	private void loadedRelationToModel(Relation loadedRelation) {
		ModelBox loadedStartBox = loadedRelation.getStart().getAppendant();
		ModelBox loadedEndBox = loadedRelation.getEnd().getAppendant();
		if(loadedStartBox == null || loadedEndBox == null) return;
		ModelBox newStartBox = null;
		ModelBox newEndBox = null;
		if(loadedStartBox instanceof ModelClass && loadedEndBox instanceof ModelClass) {
			newStartBox = modelManager.getModelClass(loadedStartBox.getName());
			newEndBox = modelManager.getModelClass(loadedEndBox.getName());
		}
		else if(loadedStartBox instanceof ModelObject && loadedEndBox instanceof ModelObject) {
			for(ModelClass newModelClass : modelManager.getClasses()) {
				newStartBox = newModelClass.getModelObject(loadedStartBox.getName());
				newEndBox = newModelClass.getModelObject(loadedEndBox.getName());
			}
		}
		
		if(newStartBox == null || newEndBox == null) return;
		
		Relation newRelation = modelManager.createRelation(newStartBox, newEndBox, loadedRelation.getType(), loadedRelation.getColor());
		if(newRelation != null) {
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
