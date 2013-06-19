package edu.utah.sci.cyclist.ui.components;

import edu.utah.sci.cyclist.ui.panels.Panel;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPaneBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PanelArea extends VBox {

	private SplitPane _sp;
	
	public PanelArea() {
		build();
	}
	
	public void add(Panel panel) {
		_sp.getItems().add(panel);
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
