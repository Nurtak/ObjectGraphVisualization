package ch.hsr.ogv.view;

import javafx.scene.Group;

public interface Selectable {

    public void setSelected(boolean selected);

    public boolean isSelected();

    public Group getSelection();

    public void requestFocus();
}
