package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.view.MessageBar;
import ch.hsr.ogv.view.MessageBar.MessageLevel;

/**
 * This class represents a XMI Parser. It reads the Version of the XMI File and use an according handler. If no handler for the version exists or the 
 * version is not supported an Error Message is created.
 * 
 * @author Dario Vonaesch, Simon Gwerder
 * @version 3DCOV 3.0, May 2007 / OGV 3.0, May 2015
 */
public class XMIParser {

	private static final String[] SUPPORTED_VERSIONS = { "1.1" };
	
	private SAXParser mParser;
	private VersionHandler mVersionHandler;
	private XMIHandler xmiHandler;

	/**
	 * Constructor - initialises variables.
	 * 
	 */
	public XMIParser() {
		xmiHandler = new XMIHandler();
	}

	/**
	 * @return the read class associations
	 */
	public ArrayList<Relation> getClassAssociations() {
		return xmiHandler.getRelations();
	}

	/**
	 * @return the read classes
	 */
	public ArrayList<ModelClass> getClasses() {
		return xmiHandler.getClasses();
	}

	public void parse(File xmiFile) throws SAXException, IOException, ParserConfigurationException {
		mParser = SAXParserFactory.newInstance().newSAXParser();
		mVersionHandler = new VersionHandler();
		try {
			mParser.parse(xmiFile, mVersionHandler);
		} catch (org.xml.sax.SAXParseException e) {

		}
		String version = mVersionHandler.getVersion();
		ArrayList<String> supportedVersions = new ArrayList<String>(Arrays.asList(SUPPORTED_VERSIONS));
		// version not recognised
		if (version == null) {
			MessageBar.setText("Unable to read XMI file: \"" + xmiFile.getPath() + "\".", MessageLevel.ERROR);
		}
		else if (supportedVersions.contains(version)) { // parsing file
			xmiHandler = new XMI_V1_1();
			try {
				mParser.parse(xmiFile, xmiHandler);
			} catch (org.xml.sax.SAXParseException e) {
				MessageBar.setText("Unable to read XMI file: \"" + xmiFile.getPath() + "\".", MessageLevel.ERROR);
			}
		}
		// version not supported
		else {
			xmiHandler = new XMIHandler();
			MessageBar.setText("The XMI version " + version + " is not supported. Supported XMI versions: " + String.join(",", SUPPORTED_VERSIONS), MessageLevel.ERROR);
		}

	}

}