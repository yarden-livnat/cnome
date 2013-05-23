package edu.utah.sci.cyclist.ui.components;

import edu.utah.sci.cyclist.model.Filter;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;

public class FilterGlyph extends HBox {

	private Filter _filter;
	
	public FilterGlyph(Filter filter) {
		_filter = filter;
		build();
	}
	
	public Filter getFilter() {
		return _filter;
	}
	
	private void build() {
		HBoxBuilder.create()
			.styleClass("filter-glyph")
			.children(
					LabelBuilder.create().styleClass("text").text(_filter.getName()).build()
				)
			.applyTo(this);
	}
}
