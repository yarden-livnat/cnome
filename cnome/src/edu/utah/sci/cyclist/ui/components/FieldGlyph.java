package edu.utah.sci.cyclist.ui.components;

import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import edu.utah.sci.cyclist.model.Field;

public class FieldGlyph extends HBox {

	private Field _field;
	
	public FieldGlyph(Field field) {
		_field = field;
		build();
	}
	
	private void build() {
		HBoxBuilder.create()
			.styleClass("field-glyph")
			.children(
					LabelBuilder.create().styleClass("text").text(_field.getName()).build()
				)
			.applyTo(this);
	}
}
