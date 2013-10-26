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
<<<<<<< HEAD
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import edu.utah.sci.cyclist.event.ui.FilterEvent;
=======
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.ui.FilterEvent;
import edu.utah.sci.cyclist.model.DataType.Interpretation;
>>>>>>> 000913116c1c6bc46009daf6cdb2b1d193d5d41c
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
			setStyle("-fx-background-color: #ffffc1");
			return true;
		}
		return false;
		
	}
	
	private void build() {
		getStyleClass().add("field-glyph");
		setSpacing(5);
		
		_label = new Label(getTitle());
		_label.getStyleClass().add("text");
			
		_button = new StackPane();
		_button.getStyleClass().add("arrow");
		_button.setMaxSize(6, 8);
		
		StackPane sp = new StackPane();
		sp.setAlignment(Pos.CENTER);
		sp.getChildren().add(_button);
		
		getChildren().addAll(_label, sp);

		createMenu();

		validProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				if (validProperty().get()) {
					setStyle("-fx-background-color: #ffffc1");
				} else {
					setStyle("-fx-background-color: #e4a1aa");
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
	
<<<<<<< HEAD
=======

>>>>>>> 000913116c1c6bc46009daf6cdb2b1d193d5d41c
	private void createMenu() {
		
		final ContextMenu contextMenu = new ContextMenu();

		final MenuItem categoryIitem = new MenuItem("Category", new ImageView(Resources.getIcon("ok")));
		final MenuItem numericIitem = new MenuItem("Measure", new ImageView(Resources.getIcon("ok")));
		
		if (_field.getRole() == Role.DIMENSION) {
			numericIitem.getGraphic().setVisible(false);
		} else {
			categoryIitem.getGraphic().setVisible(false);
		}
		
		
		contextMenu.getItems().add(categoryIitem);	
		contextMenu.getItems().add(numericIitem);
		
		categoryIitem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (_field.getRole() != Role.DIMENSION) {
					numericIitem.getGraphic().setVisible(false);
					categoryIitem.getGraphic().setVisible(true);
					_field.setRole(Role.DIMENSION);
					_label.setText(getTitle());
					fireActionEvent();
				}
			}
		});
		
		
		numericIitem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (_field.getRole() != Role.MEASURE) {
					numericIitem.getGraphic().setVisible(true);
					categoryIitem.getGraphic().setVisible(false);
					_field.setRole(Role.MEASURE);
					_label.setText(getTitle());
					fireActionEvent();
				}
			}
		});

		MenuItem item;
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
		
		//Add a filter which is connected directly to the field and its SQL functions.
		item = new MenuItem("Add Filter");
		item.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e){
				setStyle("-fx-background-color: -filter-bg");
				fireFilterActionEvent();
			}
		});
		contextMenu.getItems().add(item);
<<<<<<< HEAD
=======
		
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		//Change field type to discrete.
		final MenuItem discreteItem = new MenuItem("Discrete", new ImageView(Resources.getIcon("ok")));
		contextMenu.getItems().add(discreteItem);
		
		final MenuItem continuousIitem = new MenuItem("Continuous", new ImageView(Resources.getIcon("ok")));
		contextMenu.getItems().add(continuousIitem);
		
		if (_field.getDataType().getInterpretation() == Interpretation.DISCRETE) {
			continuousIitem.getGraphic().setVisible(false);
		}
		else {
			discreteItem.getGraphic().setVisible(false);	
		}
		
		discreteItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e){
//				if (_field.getDataType().getInterpretation() != Interpretation.DISCRETE) {
					discreteItem.getGraphic().setVisible(true);
					continuousIitem.getGraphic().setVisible(false);
					_field.getDataType().setInterpetation(Interpretation.DISCRETE);
					fireActionEvent();
//				}
			}
		});
		
		
		//Change field type to continuous.
		
		continuousIitem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e){
//				if (_field.getDataType().getInterpretation() != Interpretation.CONTINUOUS) {
					discreteItem.getGraphic().setVisible(false);
					continuousIitem.getGraphic().setVisible(true);
					_field.getDataType().setInterpetation(Interpretation.CONTINUOUS);
					fireActionEvent();
//				}
			}
		});
		
		
>>>>>>> 000913116c1c6bc46009daf6cdb2b1d193d5d41c

		_button.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				contextMenu.show(_button, Side.BOTTOM, 0, 0);	
			}
		});
	}
}
