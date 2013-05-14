package edu.utah.sci.cyclist.view.panels;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPaneBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;

public class Panel extends VBox {

	ScrollPane _pane;
	
	public Panel(String header) {
		build(header);
	}
	
	private void build(String header) {
		getStyleClass().add("cnome-panel");
		Label title = LabelBuilder.create()
					.text(header)
					.build();
		
		title.getStyleClass().add("header");
		title.prefWidthProperty().bind(widthProperty());
		
		_pane = ScrollPaneBuilder.create()
							.build();
		_pane.getStyleClass().add("pane");
		
		VBox vbox =  VBoxBuilder.create()
							.children(
//								LabelBuilder.create().text("item 1").build(),
//								LabelBuilder.create().text("item 2").build(),
//								LabelBuilder.create().text("item 3").build()
								)
							.build();
		
		vbox.getStyleClass().add("panel-vbox");
		_pane.setContent(vbox);
		
//		title.relocate(0, 0);
//		
//		pane.relocate(0, 30);
//		pane.layoutYProperty().bind(title.heightProperty());
//		pane.heightProperty()
		getChildren().addAll(title, _pane);
	}
	
	public void setContent(Node node) {
		_pane.setContent(node);
	}
	
	public Node getContent() {
		return _pane.getContent();
	}
	
}
