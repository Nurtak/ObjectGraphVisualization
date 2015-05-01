package ch.hsr.ogv.dataaccess;

import java.io.File;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ch.hsr.ogv.model.ModelClass;
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

			System.out.println(loadedModelManager);
			System.out.println(loadedModelManager.getClass("A"));
			System.out.println(loadedModelManager.getClasses());
			System.out.println("getClasses.size: " + loadedModelManager.getClasses().size());
			System.out.println("getRelations.size: " + loadedModelManager.getRelations().size());

			for (ModelClass modelClass : loadedModelManager.getClasses()) {
				modelManager.createClass(new Point3D(modelClass.getX(), modelClass.getY(), modelClass.getZ()), modelClass.getWidth(), modelClass.getHeight(), new Color(0,0,0,0));

			}

		} catch (Exception e) {
			MessageBar.setText("Could not load data from file:\n" + file.getPath(), MessageLevel.ERROR);
			e.printStackTrace();
		}
	}

	public void savePersonData(File file) {
		System.out.println(file);
		try {
			JAXBContext context = JAXBContext.newInstance(ModelManager.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(modelManager, file);

		} catch (Exception e) {
			MessageBar.setText("Could not save data to file:\n" + file.getPath(), MessageLevel.ERROR);
			System.out.println(e.getMessage());
		}
	}
}
