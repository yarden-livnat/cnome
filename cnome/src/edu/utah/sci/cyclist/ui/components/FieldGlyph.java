package edu.utah.sci.cyclist.ui.components;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import edu.utah.sci.cyclist.event.ui.FilterEvent;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.FieldProperties;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.util.SQL;

public class FieldGlyph extends HBox {

	private Field _field;
	private StackPane _button;
	private Label _label;
	private BooleanProperty _validProperty = new SimpleBooleanProperty();
	private Filter _currFilter = null;
	
	private ObjectProperty<EventHandler<ActionEvent>> _action = new SimpleObjectProperty<>();
	private ObjectProperty<EventHandler<FilterEvent>> _filterAction = new SimpleObjectProperty<>();
	
	public FieldGlyph(Field field) {
		_field = field;
		build();
	}
	
	public Field getField() {
		return _field;
	}
	
	public ObjectProperty<EventHandler<ActionEvent>> onAction() {
		return _action;
	}
	
	public void setOnAction( EventHandler<ActionEvent> handler) {
		_action.set(handler);
	}
	
	public EventHandler<ActionEvent> getOnAction() {
		return _action.get();
	}
	
	public ObjectProperty<EventHandler<FilterEvent>> onFilterAction() {
		return _filterAction;
	}
	
	public void setOnFilterAction( EventHandler<FilterEvent> handler) {
		_filterAction.set(handler);
	}
	
	public EventHandler<FilterEvent> getOnFilterAction() {
		return _filterAction.get();
	}
	
	public BooleanProperty validProperty() {
		return _validProperty;
	}
	
	public boolean isValid() {
		return _validProperty.get();
	}
	
	public String getTitle() {
		String title = null;
		if (_field.getRole() == Role.DIMENSION) {
			title = _field.getName();
		} else {
			String funcName = _field.get(FieldProperties.AGGREGATION_FUNC, String.class);
			if (funcName == null)
				funcName = _field.get(FieldProperties.AGGREGATION_DEFAULT_FUNC, String.class);
			SQL.Function func = SQL.getFunction(funcName);
			title = func.getLabel(_field.getName());
		}
		
		return title;
	}
	
	public boolean removeFieldFilter(Object filter){
		if(_field == ((Filter)filter).getField()){
			_currFilter = null;
			setStyle("-fx-background-color: -active-bg");
			return true;
		}
		return false;
		
	}
	
	private void build() {
		this.getStyleClass().add("field-glyph");
		this.setSpacing(5);
		
		_label = new Label(getTitle());
		_label.getStyleClass().add("text");
		
		StackPane sp = new StackPane();
		sp.setAlignment(Pos.CENTER);
		
		_button = new StackPane();
		_button.getStyleClass().add("arrow");
		_button.setMaxHeight(8);
		_button.setMaxWidth(6);
		
		sp.getChildren().add(_button);
		this.getChildren().addAll(_label,sp);
		
		
		
		/*HBoxBuilder.create()
			.styleClass("field-glyph")
			.spacing(5)
			.children(
					_label = LabelBuilder.create()
						.styleClass("text")
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

		validProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				if (validProperty().get()) {
					setStyle("-fx-background-color: -active-bg");
				} else {
					setStyle("-fx-background-color: -inactive-bg");
				}
				
			}
		});
	}
	
	private void fireActionEvent() {
		if (getOnAction() != null) {
			getOnAction().handle(new ActionEvent(this, null));
		}
	}
	
	private void fireFilterActionEvent() {
		if (getOnFilterAction() != null) {
			if(_currFilter != null){
				getOnFilterAction().handle(new FilterEvent(FilterEvent.DELETE, _currFilter));
				_field.rangeValuesProperty().unbind();
				_field.setRangeValues(null);
			}
			_currFilter = new Filter(_field);
			getOnFilterAction().handle(new FilterEvent(FilterEvent.SHOW, _currFilter));
		}
	}
	
	private void createMenu() {
		
		final ContextMenu contextMenu = new ContextMenu();

		MenuItem item = new MenuItem("Dimension");
		item.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				_field.setRole(Role.DIMENSION);
				_label.setText(getTitle());
				fireActionEvent();
			}
		});
		contextMenu.getItems().add(item);
		
		for (final SQL.Function func : SQL.FUNCTIONS) {
			item = new MenuItem(func.getName());
			item.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					_field.setRole(Role.MEASURE);
					_field.set(FieldProperties.AGGREGATION_FUNC, func.getName());
					_label.setText(getTitle());
					fireActionEvent();
					if(_currFilter != null){
						fireFilterActionEvent();
					}
				}
			});
			contextMenu.getItems().add(item);
		}
		
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		item = new MenuItem("Add Filter");
		item.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e){
				setStyle("-fx-background-color: -filter-bg");
				fireFilterActionEvent();
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
