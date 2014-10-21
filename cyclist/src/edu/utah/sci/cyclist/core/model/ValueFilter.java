package edu.utah.sci.cyclist.core.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ValueFilter extends Filter {
	private String _name;
	
//	private ListProperty<Object> _values = FXCollections.observableArrayList();
	
	public ValueFilter(Field field, Object value) {
		super(field, false);
		
		_values.unbind();
		_values.set(FXCollections.observableArrayList());
		_values.get().add(value);
		selectValue(value, true);
		_name = getName()+"="+value.toString();
	}
	
	@Override
	public void setAllSelected(boolean value) {
		super.setAllSelected(false);
	}
	
	public String getLabel() {
		return _name;
	}
	
}
