package ch.hsr.ogv.dataaccess;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.view.MessageBar;
import ch.hsr.ogv.view.MessageBar.MessageLevel;

public class Persistancy {

	private ModelManager modelManager;

	public void setModelManager(ModelManager modelManager) {
		this.modelManager = modelManager;
	}

	public void loadPersonData(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(ModelManager.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			ModelManager loadedModelManager = (ModelManager) um.unmarshal(file);

			modelManager.clearClasses();
			modelManager.clearRelations();

			modelManager.setClasses(loadedModelManager.getClasses());
			modelManager.setRelations(loadedModelManager.getRelations());

		} catch (Exception e) {
			MessageBar.setText("Could not load data from file:\n" + file.getPath(), MessageLevel.ERROR);
			System.out.println(e);
		}
	}

	public void savePersonData(File file) {
		System.out.println(file);
		try {
			JAXBContext context = JAXBContext.newInstance(ModelManager.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			ModelManager wrapper = new ModelManager();
			wrapper.setClasses(modelManager.getClasses());
			wrapper.setRelations(modelManager.getRelations());

			// Marshalling and saving XML to the file.
			m.marshal(modelManager, file);

		} catch (Exception e) {
			MessageBar.setText("Could not save data to file:\n" + file.getPath(), MessageLevel.ERROR);
			System.out.println(e.getMessage());
		}
	}
}
