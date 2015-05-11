package ch.hsr.ogv.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import ch.hsr.ogv.util.ResourceLocator.Resource;

public class FXMLResourceUtil {

	private final static Logger logger = LoggerFactory.getLogger(FXMLResourceUtil.class);

	public static Object loadPreset(Resource resource) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ResourceLocator.getResourcePath(resource));
		try {
			return loader.load();
		}
		catch (IOException e) {
			logger.debug(e.getMessage());
		}
		return null;
	}

	public static FXMLLoader prepareLoader(Resource resource) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ResourceLocator.getResourcePath(resource));
		return loader;
	}

}
