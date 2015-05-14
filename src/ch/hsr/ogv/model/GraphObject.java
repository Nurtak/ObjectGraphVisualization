package ch.hsr.ogv.model;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

public class GraphObject extends ModelBox {
	
	private String allocate = "";
	private List<Attribute> attributes = new ArrayList<Attribute>();
	
	public GraphObject(String name, Point3D coordinates, double width, double height, Color color, String allocate) {
		super(name, coordinates, width, height, color);
		this.allocate = allocate;
	}
	
}
