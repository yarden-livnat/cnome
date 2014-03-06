package edu.utah.sci.cyclist.core.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ValueFilter extends Filter {
	private String _name;
	
	private ObservableList<Object> _values = FXCollections.observableArrayList();
	
	public ValueFilter(Field field, Object value) {
		super(field, false);
		
		_values.add(value);
		selectValue(value, true);
		_name = getName()+"="+value.toString();
	}
	
	public String getLabel() {
		return _name;
	}
	public ObservableList<Object> getValues() {
		return _values;
	}
	
	public boolean allValuesSelected() {
		return false;
	}
	
}
