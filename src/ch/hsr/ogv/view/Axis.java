package ch.hsr.ogv.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Axis {

	private static final int LENGTH = 200;
	private static final int THICKNESS = 2;
	
    private final Group axisGroup = new Group();
    
    private Color getBrighterColor(Color c) {
    	return c.brighter().brighter().brighter().brighter();
    }
    
	public Axis(Xform world) {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(getBrighterColor(Color.DARKRED));
        redMaterial.setSpecularColor(getBrighterColor(Color.RED));

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(getBrighterColor(Color.DARKGREEN));
        greenMaterial.setSpecularColor(getBrighterColor(Color.GREEN));

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(getBrighterColor(Color.DARKBLUE));
        blueMaterial.setSpecularColor(getBrighterColor(Color.BLUE));

        final Box xAxis = new Box(LENGTH, THICKNESS, THICKNESS);
        final Box yAxis = new Box(THICKNESS, LENGTH, THICKNESS);
        final Box zAxis = new Box(THICKNESS, THICKNESS, LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        
        world.getChildren().add(axisGroup);
    }
    
}
