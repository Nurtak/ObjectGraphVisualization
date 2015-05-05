package ch.hsr.ogv.view;

import javafx.scene.Group;

/**
 * 
 * @author Simon Gwerder
 *
 */
public interface Selectable {
	
	public void setSelected(boolean selected);
	public boolean isSelected();
	public Group getSelection();
	public void requestFocus();	
}
