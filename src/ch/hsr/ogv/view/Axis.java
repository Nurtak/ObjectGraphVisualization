package ch.hsr.ogv.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import jfxtras.labs.util.Util;

/**
 * 
 * @author Simon Gwerder
 *
 */
public class Axis extends Group {

	private static final int LENGTH = 200;
	private static final int WIDTH = 2;
	
	private Color getBrighterColor(Color c) {
    	return Util.brighter(c, 0.9);
    }
    
	public Axis() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(getBrighterColor(Color.DARKRED));
        redMaterial.setSpecularColor(getBrighterColor(Color.RED));

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(getBrighterColor(Color.DARKGREEN));
        greenMaterial.setSpecularColor(getBrighterColor(Color.GREEN));

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(getBrighterColor(Color.DARKBLUE));
        blueMaterial.setSpecularColor(getBrighterColor(Color.BLUE));

        final Box xAxis = new Box(LENGTH, WIDTH, WIDTH);
        final Box yAxis = new Box(WIDTH, LENGTH, WIDTH);
        final Box zAxis = new Box(WIDTH, WIDTH, LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        getChildren().addAll(xAxis, yAxis, zAxis);
    }
    
}
