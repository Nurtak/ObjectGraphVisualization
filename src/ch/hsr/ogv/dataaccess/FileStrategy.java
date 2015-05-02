package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.util.Set;

import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.Relation;

public interface FileStrategy {
	
	public boolean parse(File file);
	public boolean serialize(File file);
	public Set<ModelClass> getClasses();
	public Set<Relation> getRelations();
	public boolean setClasses(Set<ModelClass> modelClasses);
	public boolean setRelations(Set<Relation> relations);
	
}
