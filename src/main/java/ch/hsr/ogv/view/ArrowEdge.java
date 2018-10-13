package ch.hsr.ogv.view;

import ch.hsr.ogv.model.EndpointType;
import ch.hsr.ogv.util.ObjModelLoader;
import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrowEdge extends Group {

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
        switch (this.endpointType) {
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
            case ARC:
                modelUrl = ResourceLocator.getResourcePath(Resource.ARC_OBJ);
                this.additionalGap = 50;
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
        Node[] rootNodes = ObjModelLoader.load(modelUrl);
        for (Node n : rootNodes) {
            MeshView mv = (MeshView) n;
            this.meshViews.add(mv);
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
        for (MeshView mv : this.meshViews) {
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

    public void setRotateYAxis(double degree) {
        setRotationAxis(Rotate.Y_AXIS);
        setRotate(degree);
    }

    public void setRotateXAxis(double degree) {
        setRotationAxis(Rotate.X_AXIS);
        setRotate(degree);
    }

}
