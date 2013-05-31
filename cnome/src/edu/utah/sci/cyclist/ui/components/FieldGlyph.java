package edu.utah.sci.cyclist.ui.components;

import utils.SQL;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import edu.utah.sci.cyclist.model.DataType.Classification;
import edu.utah.sci.cyclist.model.DataType.Interpretation;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.FieldProperties;

public class FieldGlyph extends HBox {

	private Field _field;
	private StackPane _button;
	
	
	public FieldGlyph(Field field) {
		_field = field;
		build();
	}
	
	private String getTitle() {
		String title = null;
		if (_field.getRole() == Role.DIMENSION) {
			title = _field.getName();
		} else {
			String func = _field.get(FieldProperties.AGGREGATION_FUNC, String.class);
			if (func == null)
				func = _field.get(FieldProperties.AGGREGATION_DEFAULT_FUNC, String.class);
			title = func+"("+_field.getName()+")";
		}
		
		return title;
	}
	
	private void build() {
		HBoxBuilder.create()
			.styleClass("field-glyph")
			.spacing(5)
			.children(
					LabelBuilder.create()
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

		for (final String op : SQL.OPERATIONS) {
			MenuItem item = new MenuItem(op);
			item.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					_field.set(FieldProperties.AGGREGATION_FUNC, op);
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
