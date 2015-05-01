package ch.hsr.ogv.dataaccess;

import java.io.File;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.view.MessageBar;
import ch.hsr.ogv.view.MessageBar.MessageLevel;

public class Persistancy {

	private final static Logger logger = LoggerFactory.getLogger(Persistancy.class);
	
	private ModelManager modelManager;

	public void setModelManager(ModelManager modelManager) {
		this.modelManager = modelManager;
	}

	public void loadData(File file) {
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
			ModelClass.modelClassCounter.set(0);
			ModelObject.modelObjectCounter.set(0);
			MessageBar.setText("Loaded file:\"" + file.getPath() + "\".", MessageLevel.INFO);
		} catch (Exception e) {
			MessageBar.setText("Could not load data from file: \"" + file.getPath() + "\".", MessageLevel.ERROR);
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
	}

	public void saveData(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(ModelManager.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(modelManager, file);
			ModelClass.modelClassCounter.set(0);
			ModelObject.modelObjectCounter.set(0);
			MessageBar.setText("Saved file: \"" + file.getPath() + "\".", MessageLevel.INFO);
		} catch (Exception e) {
			MessageBar.setText("Could not save data to file: \"" + file.getPath() + "\".", MessageLevel.ERROR);
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
	}
}
