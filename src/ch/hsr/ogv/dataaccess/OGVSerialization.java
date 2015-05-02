package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.model.Relation;

@XmlRootElement(name = "model")
@XmlType(propOrder = { "classes", "relations"})
public class OGVSerialization implements SerializationStrategy {

	private final static Logger logger = LoggerFactory.getLogger(OGVSerialization.class);
	
	private ModelManager modelManager = new ModelManager();
	
	public ModelManager getModelManager() {
		return modelManager;
	}

	@XmlElementWrapper (name = "classes")
	@XmlElement (name = "class")
	@Override
	public Set<ModelClass> getClasses() {
		return this.modelManager.getClasses();
	}

	@Override
	public boolean setClasses(Set<ModelClass> modelClasses) {
		this.modelManager.setClasses(modelClasses);
		return true;
	}
	
	@XmlElementWrapper (name = "relations")
	@XmlElement (name = "relation")
	@Override
	public Set<Relation> getRelations() {
		return this.modelManager.getRelations();
	}
	
	@Override
	public boolean setRelations(Set<Relation> relations) {
		this.modelManager.setRelations(relations);
		return true;
	}

	@Override
	public boolean parse(File file) {
		JAXBContext context = null;
		Unmarshaller um;
		try {
			context = JAXBContext.newInstance(ModelManager.class);
			um = context.createUnmarshaller();
			this.modelManager = (ModelManager) um.unmarshal(file); // Reading XML from the file and unmarshalling.
			return true;
		} catch (JAXBException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean serialize(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(ModelManager.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(modelManager, file);
			return true;
		} catch (JAXBException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		return false;
	}

}
