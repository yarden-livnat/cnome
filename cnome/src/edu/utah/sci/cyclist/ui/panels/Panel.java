package edu.utah.sci.cyclist.ui.panels;

import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.ui.components.Spring;
import javafx.beans.property.ObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPaneBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;

public class Panel extends VBox {

	private ProgressIndicator _indicator;
	private Button _closeButton;
	private Task<?> _task;
	
	private ScrollPane _pane;
	
	public Panel(String header) {
		build(header);
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
	
	/*
	 * Close 
	 */
	public ObjectProperty<EventHandler<ActionEvent>> onCloseProperty() {
		return _closeButton.onActionProperty();
	}
	
	public EventHandler<ActionEvent> getOnClose() {
		return _closeButton.getOnAction();
	}
	
	public void setOnClose(EventHandler<ActionEvent> handler) {
		_closeButton.setOnAction(handler);
	}
	
	private void build(String header) {
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
									.build(),
								new Spring(),
								_closeButton = ButtonBuilder.create()
									.styleClass("flat-button")
									.graphic(new ImageView(Resources.getIcon("close_view")))
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
