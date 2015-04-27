package ch.hsr.ogv.util;

import java.io.File;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ch.hsr.ogv.model.ModelManager;

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
	        ModelManager savedModelManager = (ModelManager) um.unmarshal(file);

	        modelManager.clearClasses();
	        modelManager.setClasses(savedModelManager.getClasses());

	    } catch (Exception e) { // catches ANY exception
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not load data");
	        alert.setContentText("Could not load data from file:\n" + file.getPath());

	        alert.showAndWait();
	    }
	}

	public void savePersonData(File file) {
	    try {
	        JAXBContext context = JAXBContext.newInstance(ModelManager.class);
	        Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

//	        // Wrapping our person data.
//	        ModelManager modelManager = new ModelManager();
//	        modelManager.setPersons(personData);

	        // Marshalling and saving XML to the file.
	        m.marshal(modelManager, file);

	    } catch (Exception e) { // catches ANY exception
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not save data");
	        alert.setContentText("Could not save data to file:\n" + file.getPath());

	        alert.showAndWait();
	    }
	}
}
