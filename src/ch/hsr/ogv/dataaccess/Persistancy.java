package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.util.HashSet;

import javafx.geometry.Point3D;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.model.Relation;

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

		for (ModelClass modelClass : serialStrategy.getClasses()) {
			ModelClass newModelClass = modelManager.createClass(new Point3D(modelClass.getX(), modelClass.getY(), modelClass.getZ()), modelClass.getWidth(), modelClass.getHeight(), modelClass.getColor());
			if(newModelClass != null) {
				//TODO add attributes / objects
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
