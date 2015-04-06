package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Class that handles user preferences
 * @author Simon Gwerder
 *
 */
public class UserPreferences {
	
	private static final String SAVE_FILE_PREFKEY = "savedFilePath";
	
	  /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     * 
     * @return the last used file or null.
     */
    public static File getSavedFile() {
        Preferences prefs = Preferences.userNodeForPackage(UserPreferences.class);
        String filePath = prefs.get(SAVE_FILE_PREFKEY, null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     * 
     * @param file the file or null to remove the path
     */
    public static void setSavedFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(UserPreferences.class);
        if (file != null) {
            prefs.put(SAVE_FILE_PREFKEY, file.getPath());
        } else {
            prefs.remove(SAVE_FILE_PREFKEY);
        }
    }
}
