package ch.hsr.ogv.controller;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import ch.hsr.ogv.StageManager;
import ch.hsr.ogv.ThemeChooser;
import ch.hsr.ogv.ThemeChooser.Style;

public class ThemeMenuController {

	private ThemeChooser themeChooser;
	
	public ThemeMenuController(StageManager stageManager) {
		this.themeChooser = new ThemeChooser(stageManager);
	}
	
	public void handleSetTheme(CheckMenuItem choosenMenu, Style style) {
		if(choosenMenu == null || style == null) return;
		this.themeChooser.setStyle(style);
		Menu theme = choosenMenu.getParentMenu();
		for(MenuItem menuItem : theme.getItems()) {
			if(menuItem instanceof CheckMenuItem) {
				CheckMenuItem cMenuItem = (CheckMenuItem) menuItem;
				cMenuItem.setSelected(false);
			}
		}
		choosenMenu.setSelected(true);
	}
	
}
