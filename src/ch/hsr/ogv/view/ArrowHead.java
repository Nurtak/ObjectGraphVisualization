package ch.hsr.ogv.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

public class ArrowHead extends Group {
	
	private final static Logger logger = LoggerFactory.getLogger(ArrowHead.class);
	
	private Color color = Color.BLACK;
	private List<MeshView> meshViews = new ArrayList<MeshView>();
	
	public ArrowHead(Color color) {
		this(ArrowHeadType.OPEN, color);
	}
	
	public ArrowHead(ArrowHeadType arrowHeadType, Color color) {
		ObjModelImporter tdsImporter = new ObjModelImporter();
		
		URL modelUrl;
		switch(arrowHeadType) {
		case BORDER:
			modelUrl = ResourceLocator.getResourcePath(Resource.BORDER_OBJ);
			break;
		case FILLED:
			modelUrl = ResourceLocator.getResourcePath(Resource.FILLED_OBJ);
			break;
		case OPEN:
			modelUrl = ResourceLocator.getResourcePath(Resource.OPEN_OBJ);
			break;
		default:
			modelUrl = ResourceLocator.getResourcePath(Resource.OPEN_OBJ);
			break;
		}
		
		try {
		    tdsImporter.read(modelUrl);            
		}
		catch (ImportException e) {
		    e.printStackTrace();
		    logger.debug(e.getMessage());
		}
		Node[] rootNodes = tdsImporter.getImport();
		
		for(Node n : rootNodes) {
			MeshView  mv = (MeshView) n;
			this.meshViews.add(mv);
		}
		setColor(color);
		getChildren().addAll(Arrays.asList(rootNodes));
	}
	
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseColor(this.color);
		material.setSpecularColor(this.color.brighter());
		for(MeshView mv : this.meshViews) {
			mv.setMaterial(material);
		}
	}
		
	
	
	public enum ArrowHeadType {
		OPEN, FILLED, BORDER
	}

}
