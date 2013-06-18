package edu.utah.sci.cyclist.ui.panels;

import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPaneBuilder;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;

public class Panel extends VBox {

	private ProgressIndicator _indicator;
	private Task<?> _task;
	
	private ScrollPane _pane;
	
	public Panel(String header) {
		build2(header);
	}
	
	
	
	
	public void setTask(Task<?> task) {
		if (_task != null && _task.isRunning()) {
			_task.cancel();
		}
		
		_task = task;
		
		if (_task == null) {
			_indicator.visibleProperty().unbind();
			_indicator.setVisible(false);
		} else {
			_indicator.visibleProperty().bind(_task.runningProperty());	
			_indicator.setOnMouseClicked(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					System.out.println("Canceling task: "+_task.cancel());				
				}
			});
		}
	}
	
	private void build2(String header) {
		VBox vbox;
		
		VBoxBuilder.create()
			.styleClass("cnome-panel")
			.children(
					HBoxBuilder.create()
						.styleClass("header")
						.children(
								LabelBuilder.create()
									.styleClass("header")
									.text(header)
									.build(),
								_indicator = ProgressIndicatorBuilder.create()
									.progress(-1)
									.maxWidth(20)
									.maxHeight(20)
									.visible(false)
									.build()
								)
						.build(),
					_pane = ScrollPaneBuilder.create()
								.styleClass("pane")
								.content(
									vbox = VBoxBuilder.create()
										.styleClass("panel-vbox")
										.children() // empty 
										.build()
									)
								.build()
				)
			.applyTo(this);
		
		VBox.setVgrow(vbox, Priority.ALWAYS);
		VBox.setVgrow(this, Priority.ALWAYS);
		VBox.setVgrow(_pane, Priority.ALWAYS);
			
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
								)
							.build();
		
		vbox.getStyleClass().add("panel-vbox");
		_pane.setContent(vbox);
		
		VBox.setVgrow(vbox, Priority.ALWAYS);
		VBox.setVgrow(this, Priority.ALWAYS);
		VBox.setVgrow(_pane, Priority.ALWAYS);

		getChildren().addAll(title, _pane);
	}
	
	public void setContent(Node node) {
		_pane.setContent(node);
	}
	
	public Node getContent() {
		return _pane.getContent();
	}
	
	public Node getPane() {
		return _pane;
	}
	
	
}
