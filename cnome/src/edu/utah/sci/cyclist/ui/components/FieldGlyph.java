package edu.utah.sci.cyclist.ui.components;

import utils.SQL;
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
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import edu.utah.sci.cyclist.model.DataType.Interpretation;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.FieldProperties;

public class FieldGlyph extends HBox {

	private Field _field;
	private StackPane _button;
	private Label _label;
	private BooleanProperty _validProperty = new SimpleBooleanProperty();
	
	private ObjectProperty<EventHandler<ActionEvent>> _action = new SimpleObjectProperty<>();
	
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
	
	public BooleanProperty validProperty() {
		return _validProperty;
	}
	
	public boolean isValid() {
		return _validProperty.get();
	}
	
	private String getTitle() {
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
	
	private void build() {
		HBoxBuilder.create()
			.styleClass("field-glyph")
			.spacing(5)
			.children(
					_label = LabelBuilder.create()
						.styleClass("text")
						.text(getTitle())
						.build()
				)
			.applyTo(this);
		
		if (_field.getDataType().getInterpretation() == Interpretation.CONTINUOUS) {
			getChildren().add(
				StackPaneBuilder.create()
				.children(
						_button = StackPaneBuilder.create()
							.styleClass("arrow")
							.maxHeight(8)
							.maxWidth(6)
							.build()
					)
				.alignment(Pos.CENTER)
				.build());
			createMenu();
		}

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
	
	private void createMenu() {
		
		final ContextMenu contextMenu = new ContextMenu();
		
//		contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
//		    public void handle(WindowEvent e) {
//		        System.out.println("showing");
//		    }
//		});
//		contextMenu.setOnShown(new EventHandler<WindowEvent>() {
//		    public void handle(WindowEvent e) {
//		        System.out.println("shown");
//		    }
//		});

		for (final SQL.Function func : SQL.FUNCTIONS) {
			MenuItem item = new MenuItem(func.getName());
			item.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					_field.set(FieldProperties.AGGREGATION_FUNC, func.getName());
					_label.setText(getTitle());
					fireActionEvent();
				}
			});
			contextMenu.getItems().add(item);
		}
				
		_button.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				contextMenu.show(_button, Side.BOTTOM, 0, 0);	
			}
		});
	}
}
