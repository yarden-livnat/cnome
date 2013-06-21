package edu.utah.sci.cyclist.ui.components;

import edu.utah.sci.cyclist.ui.panels.TitledPanel;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;

public class PanelArea extends VBox {

	private SplitPane _sp;
	
	public PanelArea() {
		build();
	}
	
	public void add(TitledPanel panel) {
		_sp.getItems().add(panel);
	}
	
	public void remove(TitledPanel panel) {
		_sp.getItems().remove(panel);
	}
	
	public void show(TitledPanel panel, boolean visible) {
		panel.setManaged(visible);
		panel.setVisible(visible);
	}
	
	private void build() {
		setStyle("-fx-background-color: #ffbb00");
		setPrefHeight(USE_COMPUTED_SIZE);
		
		_sp = new SplitPane();
		_sp.setId("hiddenSplitter");
		_sp.setStyle("-fx-background-color: #00ffaa");
		_sp.setPrefHeight(USE_COMPUTED_SIZE);
		_sp.setOrientation(Orientation.VERTICAL);
	
		getChildren().addAll(_sp /*, new Spring()*/);

//		VBox.setVgrow(_sp, Priority.SOMETIMES);
	}
	
}
