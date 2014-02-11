package edu.utah.sci.cyclist.core.ui.components;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import edu.utah.sci.cyclist.core.event.ui.FilterEvent;
import edu.utah.sci.cyclist.core.model.FieldProperties;
import edu.utah.sci.cyclist.core.model.Filter;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

public class FilterGlyph extends HBox {

	private Filter _filter;
	private Label _button;
	private boolean _remote;
	private boolean _valid;
	
	private ObjectProperty<EventHandler<FilterEvent>> _action = new SimpleObjectProperty<>();
	
	private BooleanProperty _validProperty = new SimpleBooleanProperty();
	
	public FilterGlyph(Filter filter) {
		this(filter, false, true);
	}
	
	public FilterGlyph(Filter filter, boolean remote, boolean valid) {
		_filter = filter;
		_remote = remote;
		_valid = valid;
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
	
	public BooleanProperty validProperty() {
		return _validProperty;
	}
	
	public boolean isValid() {
		return _validProperty.get();
	}
	
	
	private void build() {
		this.getStyleClass().add("filter-glyph");

		if (_remote) this.getStyleClass().add("remote");
		if (!_valid) this.getStyleClass().add("invalid");
		
		Label label = new Label(_filter.getLabel());
		label.getStyleClass().add("text");
	
		_button = GlyphRegistry.get(AwesomeIcon.CARET_DOWN);
		_button.getStyleClass().add("arrow1");
		
		this.getChildren().addAll(label, _button); 
		createMenu();
		validProperty().set(_valid);
		
		validProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				
				if (validProperty().get()) {
					getStyleClass().remove("invalid");
				} else {
					getStyleClass().add("invalid");
				}

				if (validProperty().get()) {
					_valid=true;
				} else {
					_valid=false;
				}
				
			}
		});
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
						//This message is for removing the filter completely also from the field glyph.
						if(_filter.getField().getString(FieldProperties.AGGREGATION_FUNC) != null)
							{
								getOnAction().handle(new FilterEvent(FilterEvent.REMOVE_FILTER_FIELD, _filter));
							}
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
