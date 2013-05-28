package edu.utah.sci.cyclist.ui.components;

import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.FieldProperties;

public class FieldGlyph extends HBox {

	private Field _field;
	
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
			.children(
					LabelBuilder.create().styleClass("text").text(getTitle()).build()
				)
			.applyTo(this);
	}
}
