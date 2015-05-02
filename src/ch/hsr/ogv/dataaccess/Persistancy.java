package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.util.HashSet;

import javafx.geometry.Point3D;
import ch.hsr.ogv.controller.ModelViewConnector;
import ch.hsr.ogv.model.Attribute;
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
		
		System.out.println(serialStrategy.getClasses());
		System.out.println("getClasses.size: " + serialStrategy.getClasses().size());
		System.out.println("getRelations.size: " + serialStrategy.getRelations().size());

		for (ModelClass loadedClass : serialStrategy.getClasses()) {
			ModelClass newClass = modelManager.createClass(new Point3D(loadedClass.getX(), ModelViewConnector.BASE_BOX_DEPTH, loadedClass.getZ()), loadedClass.getWidth(), loadedClass.getHeight(), loadedClass.getColor());
			if(newClass != null) {
				newClass.setName(loadedClass.getName());
				
				for(ModelObject loadedObject : loadedClass.getModelObjects()) {
					ModelObject newObject = modelManager.createObject(newClass);
					if(newObject != null) {
						newObject.setName(loadedObject.getName());
						newObject.setY(loadedObject.getY());
						newObject.setColor(loadedObject.getColor());
					}
				}
				
				for(Attribute loadedAttribute : loadedClass.getAttributes()) {
					Attribute newAttribute = newClass.createAttribute(loadedAttribute.getName());
					if(newAttribute != null) {
						for(int i = 0; i < newClass.getModelObjects().size(); i++) {
							try {
								ModelObject loadedObject = loadedClass.getModelObjects().get(i);
								ModelObject newObject = newClass.getModelObjects().get(i);
								if(loadedObject != null && newObject != null) {
									String newAttributeValue = loadedObject.getAttributeValues().get(loadedAttribute);
									if(newAttributeValue != null) {
										newObject.changeAttributeValue(newAttribute, newAttributeValue);
									}
								}
							}
							catch(IndexOutOfBoundsException ioobe) {
								continue;
							}
						}
					}
				}
				
			}
		}
		
		for (Relation relation : serialStrategy.getRelations()) {
			Relation newRelation = modelManager.createRelation(relation.getStart().getAppendant(), relation.getEnd().getAppendant(), relation.getType(), relation.getColor());
			if(newRelation != null) {
				//TODO roles / multiplicity
			}
		}
		
		return true;
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
	
}
