package ch.hsr.ogv.dataaccess;

import java.io.File;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelManager;
import ch.hsr.ogv.view.MessageBar;
import ch.hsr.ogv.view.MessageBar.MessageLevel;

public class Persistancy {

	private ModelManager modelManager;

	public Persistancy(ModelManager modelManager) {
		this.modelManager = modelManager;
	}

	public void loadData(File file) {
		try {
			OGVParser ogvParser = new OGVParser();
			ogvParser.parse(file);
			
			modelManager.clearClasses();
			modelManager.clearRelations();

			System.out.println(ogvParser.getModelManager());
			System.out.println(ogvParser.getModelManager().getClass("A"));
			System.out.println(ogvParser.getClasses());
			System.out.println("getClasses.size: " + ogvParser.getClasses().size());
			System.out.println("getRelations.size: " + ogvParser.getRelations().size());

			for (ModelClass modelClass : ogvParser.getClasses()) {
				modelManager.createClass(new Point3D(modelClass.getX(), modelClass.getY(), modelClass.getZ()), modelClass.getWidth(), modelClass.getHeight(), new Color(0,0,0,0));
			}
			MessageBar.setText("Loaded file:\"" + file.getPath() + "\".", MessageLevel.INFO);
		} catch (Exception e) {
			
		}
	}

	public void saveData(File file) {
		MessageBar.setText("Saved file: \"" + file.getPath() + "\".", MessageLevel.INFO);
		MessageBar.setText("Could not save data to file: \"" + file.getPath() + "\".", MessageLevel.ERROR);
	}
}
