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

public class ArrowEdge extends Group {
	
	private final static Logger logger = LoggerFactory.getLogger(ArrowEdge.class);
	
	private Color color = Color.BLACK;
	private List<MeshView> meshViews = new ArrayList<MeshView>();
	private double additionalGap;
	
	private EndpointType endpointType;
	
	public double getAdditionalGap() {
		return this.additionalGap;
	}

	public ArrowEdge(Color color) {
		this(EndpointType.NONE, color);
	}
	
	public ArrowEdge(EndpointType endpointType, Color color) {
		this.endpointType = endpointType;
		this.color = color;
		loadModel();
	}
	
	private void loadModel() {
		URL modelUrl = null;
		switch(this.endpointType) {
		case EMPTY_ARROW:
			modelUrl = ResourceLocator.getResourcePath(Resource.EMPTY_ARROW_OBJ);
			this.additionalGap = 30;
			break;
		case EMPTY_DIAMOND:
			modelUrl = ResourceLocator.getResourcePath(Resource.EMPTY_DIAMOND_OBJ);
			this.additionalGap = 50;
			break;
		case FILLED_ARROW:
			modelUrl = ResourceLocator.getResourcePath(Resource.FILLED_ARROW_OBJ);
			this.additionalGap = 0;
			break;
		case FILLED_DIAMOND:
			modelUrl = ResourceLocator.getResourcePath(Resource.FILLED_DIAMOND_OBJ);
			this.additionalGap = 0;
			break;
		case OPEN_ARROW:
			modelUrl = ResourceLocator.getResourcePath(Resource.OPEN_ARROW_OBJ);
			this.additionalGap = 0;
			break;
		case NONE:
			this.additionalGap = 0;
			break;
		default:
			this.additionalGap = 0;
			break;
		}
		loadModel(modelUrl);
	}
	
	private void loadModel(URL modelUrl) {
		ObjModelImporter tdsImporter = new ObjModelImporter();
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
		setColor(this.color);
		getChildren().clear();
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
	
	public EndpointType getEndpointType() {
		return endpointType;
	}

	public void setEndpointType(EndpointType endpointType) {
		this.endpointType = endpointType;
		loadModel();
	}
	
}
