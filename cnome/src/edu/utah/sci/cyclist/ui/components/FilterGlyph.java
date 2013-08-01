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
	private boolean _remote;
	
	private ObjectProperty<EventHandler<FilterEvent>> _action = new SimpleObjectProperty<>();
	
	public FilterGlyph(Filter filter) {
		this(filter, false);
	}
	
	public FilterGlyph(Filter filter, boolean remote) {
		_filter = filter;
		_remote = remote;
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
		
		if (_remote) {
			setStyle("-fx-background-color: #d0ced1");
		}
		
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
		MenuItem show = new MenuItem("Show");
		show.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (getOnAction() != null) {
					getOnAction().handle(new FilterEvent(FilterEvent.SHOW, _filter));
				}
			}
		});
		
		contextMenu.getItems().add(show);
		
		if (!_remote) {
			MenuItem delete = new MenuItem("Delete");
			delete.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					if (getOnAction() != null) {
						getOnAction().handle(new FilterEvent(FilterEvent.DELETE, _filter));
					}
				}
			});
			contextMenu.getItems().add(delete);
		}
		
		_button.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				contextMenu.show(_button, Side.BOTTOM, 0, 0);	
			}
		});
	}
}
