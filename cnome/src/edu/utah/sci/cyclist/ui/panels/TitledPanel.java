package edu.utah.sci.cyclist.ui.panels;

import javafx.scene.Node;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;

public class TitledPanel extends VBox {

	private HBox _header;
	
	private ScrollPane _pane;
	
	public TitledPanel(String title) {
		build(title);
	}
	
	
	
	private void build(String title) {
		VBox vbox;
		
		VBoxBuilder.create()
			.styleClass("cnome-panel")
			.children(
					 _header = HBoxBuilder.create()
						.styleClass("header")
						.children(
								LabelBuilder.create()
									.styleClass("header")
									.text(title)
									.build()
								)
						.build(),
					_pane = ScrollPaneBuilder.create()
								.styleClass("pane")
								.prefHeight(USE_COMPUTED_SIZE)
								.content(
									vbox = VBoxBuilder.create()
										.styleClass("panel-vbox")
										.prefHeight(USE_COMPUTED_SIZE)
										.children() // empty 
										.build()
									)
								.build()
				)
			.applyTo(this);
		
//		VBox.setVgrow(vbox, Priority.ALWAYS);
//		VBox.setVgrow(this, Priority.ALWAYS);
//		VBox.setVgrow(_pane, Priority.ALWAYS);
			
	}
	
	public void setContent(Node node) {
		_pane.setContent(node);
	}
	
	public VBox getContent() {
		return (VBox)_pane.getContent();
	}
	
	public HBox getHeader() {
		return _header;
	}
	
	public Node getPane() {
		return _pane;
	}
	
	
}
