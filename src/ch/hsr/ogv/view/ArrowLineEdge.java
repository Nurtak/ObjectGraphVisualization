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
import ch.hsr.ogv.model.EndpointType;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

public class ArrowLineEdge extends Group {
	
	private final static Logger logger = LoggerFactory.getLogger(ArrowLineEdge.class);
	
	private Color color = Color.BLACK;
	private List<MeshView> meshViews = new ArrayList<MeshView>();
	
	public ArrowLineEdge(Color color) {
		this(EndpointType.NONE, color);
	}
	
	public ArrowLineEdge(EndpointType endpointType, Color color) {
		ObjModelImporter tdsImporter = new ObjModelImporter();
		
		URL modelUrl = null;
		switch(endpointType) {
		case EMPTY_ARROW:
			modelUrl = ResourceLocator.getResourcePath(Resource.EMPTY_ARROW_OBJ);
			break;
		case EMPTY_DIAMOND:
			modelUrl = ResourceLocator.getResourcePath(Resource.EMPTY_DIAMOND_OBJ);
			break;
		case FILLED_ARROW:
			modelUrl = ResourceLocator.getResourcePath(Resource.FILLED_ARROW_OBJ);
			break;
		case FILLED_DIAMOND:
			modelUrl = ResourceLocator.getResourcePath(Resource.FILLED_DIAMOND_OBJ);
			break;
		case OPEN_ARROW:
			modelUrl = ResourceLocator.getResourcePath(Resource.OPEN_ARROW_OBJ);
			break;
		case NONE:
			break;
		default:
			break;
		}
		Node[] rootNodes = {};
		if(modelUrl != null) {
			try {
			    tdsImporter.read(modelUrl);            
			}
			catch (ImportException e) {
			    e.printStackTrace();
			    logger.debug(e.getMessage());
			}
			rootNodes = tdsImporter.getImport();
			
			for(Node n : rootNodes) {
				MeshView  mv = (MeshView) n;
				this.meshViews.add(mv);
			}
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
	
}
