package edu.utah.sci.cyclist.ui.components;

import edu.utah.sci.cyclist.ui.panels.TitledPanel;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class WorkspacePanelArea extends SplitPane {
	
	public WorkspacePanelArea(){
		configure();
	}
	
	public void add(TitledPanel panel) {
		getItems().add(panel);
		setVisible(true);
		setManaged(true);
	}
	
	public void remove(TitledPanel panel) {
		getItems().remove(panel);
		checkVisibility();
	}
	
	public void show(TitledPanel panel, boolean visible) {
		panel.setManaged(visible);
		panel.setVisible(visible);
		checkVisibility();
	}
	
	private void checkVisibility() {
		boolean visible = false;
		for (Node node : getItems()) {
			if (node.isVisible()) {
				visible = true;
				break;
			}
		}
		setVisible(visible);
		setManaged(visible);
	}
	
	private void configure() {
		setId("hiddenSplitter");
		setMinWidth(0);
		setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		setOrientation(Orientation.VERTICAL);
		setVisible(false);
		setManaged(false);
	}

}
