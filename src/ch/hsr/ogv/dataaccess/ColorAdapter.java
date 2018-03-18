package ch.hsr.ogv.dataaccess;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import ch.hsr.ogv.util.ColorUtil;
import javafx.scene.paint.Color;

/**
 * 
 * @author Adrian Rieser
 * @version OGV 3.1, May 2015
 * 
 */
public class ColorAdapter extends XmlAdapter<String, Color> {

	/*
	 * Java => XML
	 */
	@Override
	public String marshal(Color val) throws Exception {
		return ColorUtil.colorToWebColor(val);
	}

	/*
	 * XML => Java
	 */
	@Override
	public Color unmarshal(String val) throws Exception {
		return ColorUtil.webColorToColor(val);
	}
}
