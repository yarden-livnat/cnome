package edu.utah.sci.cyclist.ui.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import edu.utah.sci.cyclist.event.ui.FilterEvent;
import edu.utah.sci.cyclist.model.Filter;

public class FilterGlyph extends HBox {

	private Filter _filter;
	private StackPane _button;
	private ObjectProperty<EventHandler<FilterEvent>> _action = new SimpleObjectProperty<>();
	
	public FilterGlyph(Filter filter) {
		_filter = filter;
		build();
	}
	
	public Filter getFilter() {
		return _filter;
	}
	
	public ObjectProperty<EventHandler<FilterEvent>> onAction() {
		return _action;
	}
	
	public void setOnAction( EventHandler<FilterEvent> handler) {
		_action.set(handler);
	}
	
	public EventHandler<FilterEvent> getOnAction() {
		return _action.get();
	}
	
	private void build() {
		getStyleClass().add("filter-glyph");
		setSpacing(5);
		
		Label label = new Label(_filter.getName());
		label.getStyleClass().add("text");
	
		_button = new StackPane();
		_button.getStyleClass().add("arrow");
		_button.setMaxHeight(8);
		_button.setMaxWidth(6);
		
		StackPane sp = new StackPane();
		sp.getChildren().add(_button);
		sp.setAlignment(Pos.CENTER);
			
		getChildren().addAll(label, sp);
		
		createMenu();
	}
	
	private void createMenu() {
		final ContextMenu contextMenu = new ContextMenu();
		MenuItem item = new MenuItem("Show");
		item.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (getOnAction() != null) {
					getOnAction().handle(new FilterEvent(FilterEvent.SHOW, _filter));
				}
			}
		});
		
		contextMenu.getItems().add(item);
		
		_button.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				contextMenu.show(_button, Side.BOTTOM, 0, 0);	
			}
		});
	}
}
