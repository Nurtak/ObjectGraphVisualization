package ch.hsr.ogv.dataaccess;

import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.Relation;

import java.io.File;
import java.util.Set;

public interface SerializationStrategy {

    public boolean parse(File file);

    public boolean serialize(File file);

    public Set<ModelClass> getClasses();

    public Set<Relation> getRelations();

    public void setClasses(Set<ModelClass> modelClasses);

    public void setRelations(Set<Relation> relations);

}
