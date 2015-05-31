package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Class that handles user preferences
 * 
 * @author Simon Gwerder
 * @version OGV 3.1, May 2015
 *
 */
public class UserPreferences {

	private static final String OGV_FILE_PREFKEY = "ogvFilePath";
	private static final String XMI_FILE_PREFKEY = "xmiFilePath";

	private static void setPrefFilePath(String prefKey, File file) {
		Preferences prefs = Preferences.userNodeForPackage(UserPreferences.class);
		if (file != null) {
			prefs.put(prefKey, file.getPath());
		}
		else {
			prefs.remove(prefKey);
		}
	}

	private static File getPrefFilePath(String prefKey) {
		Preferences prefs = Preferences.userNodeForPackage(UserPreferences.class);
		String filePath = prefs.get(prefKey, null);
		if (filePath != null) {
			return new File(filePath);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns the ogv file preference, i.e. the file that was last opened. The preference is read from the OS specific registry. If no such preference can be found, null is returned.
	 * 
	 * @return the last used file or null.
	 */
	public static File getOGVFilePath() {
		return getPrefFilePath(OGV_FILE_PREFKEY);
	}

	/**
	 * Returns the xmi file preference, i.e. the file that was last opened. The preference is read from the OS specific registry. If no such preference can be found, null is returned.
	 * 
	 * @return the last used file or null.
	 */
	public static File getXMIFilePath() {
		return getPrefFilePath(XMI_FILE_PREFKEY);
	}

	/**
	 * Sets the ogv file path of the currently loaded file. The path is persisted in the OS specific registry.
	 * 
	 * @param file
	 *            the file or null to remove the path
	 */
	public static void setOGVFilePath(File file) {
		setPrefFilePath(OGV_FILE_PREFKEY, file);
	}

	/**
	 * Sets the ogv file path of the currently loaded file. The path is persisted in the OS specific registry.
	 * 
	 * @param file
	 *            the file or null to remove the path
	 */
	public static void setXMIFilePath(File file) {
		setPrefFilePath(XMI_FILE_PREFKEY, file);
	}

}
