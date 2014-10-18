package edu.utah.sci.cyclist.neup.ui.views.flow;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class ToggleIcon extends Pane {
	private boolean _selected = false;
	
	public ToggleIcon(Node first, Node second) {
		super();
		
		getChildren().addAll(first, second);
		select(false);
	}
	
	public void select(boolean value) {
		_selected = value;
		getChildren().get(0).setVisible(!value);
		getChildren().get(1).setVisible(value);
	}
	
	public boolean getSelected() {
		return _selected;
	}
}
