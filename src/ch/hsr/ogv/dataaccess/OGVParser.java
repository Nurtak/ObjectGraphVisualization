package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.view.MessageBar;
import ch.hsr.ogv.view.MessageBar.MessageLevel;

public class OGVParser implements FileStrategy {

	private final static Logger logger = LoggerFactory.getLogger(OGVParser.class);
	
	private ModelManager modelManager = new ModelManager();
	
	public ModelManager getModelManager() {
		return modelManager;
	}

	@Override
	public Set<ModelClass> getClasses() {
		return this.modelManager.getClasses();
	}

	@Override
	public Set<Relation> getRelations() {
		return this.modelManager.getRelations();
	}
	
	@Override
	public boolean setClasses(Set<ModelClass> modelClasses) {
		this.modelManager.setClasses(modelClasses);
		return true;
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
			MessageBar.setText("Could not load data from file: \"" + file.getPath() + "\".", MessageLevel.ERROR);
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
