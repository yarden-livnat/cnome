package edu.utah.sci.cyclist.ui.components;

import edu.utah.sci.cyclist.ui.panels.Panel;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPaneBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;

public class PanelArea extends VBox {

	private SplitPane _sp;
	
	public PanelArea() {
		build();
	}
	
	public void add(Panel panel) {
		_sp.getItems().add(panel);
	}
	
	public void remove(Panel panel) {
		_sp.getItems().remove(panel);
	}
	
	public void show(Panel panel, boolean visible) {
		panel.setManaged(visible);
		panel.setVisible(visible);
	}
	
	private void build() {
		VBoxBuilder.create()
			.children(
				_sp = SplitPaneBuilder.create()
						.id("hiddenSplitter")
						.orientation(Orientation.VERTICAL)
						.build(),
				new Spring()
			)
			.applyTo(this);
		
		VBox.setVgrow(_sp, Priority.SOMETIMES);
	}
	
}
