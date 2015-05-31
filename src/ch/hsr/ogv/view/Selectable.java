package ch.hsr.ogv.view;

import javafx.scene.Group;

/**
 * 
 * @author Simon Gwerder
 * @version OGV 3.1, May 2015
 *
 */
public interface Selectable {

	public void setSelected(boolean selected);

	public boolean isSelected();

	public Group getSelection();

	public void requestFocus();
}
