package edu.utah.sci.cyclist.ui.components;

import edu.utah.sci.cyclist.event.ui.FilterEvent;
import edu.utah.sci.cyclist.model.FieldProperties;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.util.SQL;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;

public class FilterGlyph extends HBox {

	private Filter _filter;
	private StackPane _button;
	private ObjectProperty<EventHandler<FilterEvent>> _action = new SimpleObjectProperty<>();
	private Label _label;
	
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
		this.getStyleClass().add("filter-glyph");
		this.setSpacing(5);
		
		 _label = new Label(_filter.getName());
		_label.getStyleClass().add("text");
		
		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER);
		
		_button = new StackPane();
		_button.getStyleClass().add("arrow");
		_button.setMaxHeight(8);
		_button.setMaxWidth(6);
		
		stackPane.getChildren().add(_button);
		
		this.getChildren().addAll(_label,stackPane);
		
		/*HBoxBuilder.create()
			.styleClass("filter-glyph")
			.spacing(5)
			.children(
					LabelBuilder.create()
						.styleClass("text")
						//.text(_filter.getName())
						.text(getTitle())
						.build(),
					StackPaneBuilder.create()
						.children(
							_button = StackPaneBuilder.create()
								.styleClass("arrow")
								.maxHeight(8)
								.maxWidth(6)
								.build()
							)
						.alignment(Pos.CENTER)
					.build()
				)
			.applyTo(this);*/
		
		createMenu();
	}
	
	private void createMenu() {
		final ContextMenu contextMenu = new ContextMenu();
		MenuItem item = new MenuItem("Show");
		item.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				/*if (getOnAction() != null) {
					getOnAction().handle(new FilterEvent(FilterEvent.SHOW, _filter));
				}*/
				fireActionEvent();
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
	
	private void fireActionEvent() {
		if (getOnAction() != null) {
			getOnAction().handle(new FilterEvent(FilterEvent.SHOW, _filter));
		}
	}
}
