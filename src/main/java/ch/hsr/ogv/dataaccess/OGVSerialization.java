package ch.hsr.ogv.dataaccess;

import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.Relation;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

@XmlRootElement(name = "model")
@XmlType(propOrder = {"classes", "relations"})
public class OGVSerialization implements SerializationStrategy {

    private final static Logger logger = LoggerFactory.getLogger(OGVSerialization.class);

    private Set<ModelClass> classes = new LinkedHashSet<ModelClass>();
    private Set<Relation> relations = new LinkedHashSet<Relation>();

    @XmlElementWrapper(name = "classes")
    @XmlElement(name = "class")
    @Override
    public Set<ModelClass> getClasses() {
        return this.classes;
    }

    @Override
    public void setClasses(Set<ModelClass> classes) {
        this.classes = classes;
    }

    @XmlElementWrapper(name = "relations")
    @XmlElement(name = "relation")
    @Override
    public Set<Relation> getRelations() {
        return this.relations;
    }

    @Override
    public void setRelations(Set<Relation> relations) {
        this.relations = relations;
    }

    @Override
    public boolean parse(File file) {
        JAXBContext context = null;
        Unmarshaller um;
        try {
            context = JAXBContext.newInstance(OGVSerialization.class);
            um = context.createUnmarshaller();
            OGVSerialization ogvUnmarshalled = (OGVSerialization) um.unmarshal(file); // Reading XML from the file and unmarshalling.
            setClasses(ogvUnmarshalled.getClasses());
            setRelations(ogvUnmarshalled.getRelations());
            return true;
        }
        catch (JAXBException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean serialize(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(OGVSerialization.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(this, file);
            return true;
        }
        catch (JAXBException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        }
        return false;
    }

}
