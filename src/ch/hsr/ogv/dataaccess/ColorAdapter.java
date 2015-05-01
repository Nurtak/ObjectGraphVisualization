package ch.hsr.ogv.dataaccess;

import javafx.scene.paint.Color;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import jfxtras.labs.util.Util;

public class ColorAdapter extends XmlAdapter<String, Color> {

	/*
	 * Java => XML
	 */
	@Override
	public String marshal(Color val) throws Exception {
		return Util.colorToWebColor(val);
	}

	/*
	 * XML => Java
	 */
	@Override
	public Color unmarshal(String val) throws Exception {
		return Util.webColorToColor(val);
	}
}
