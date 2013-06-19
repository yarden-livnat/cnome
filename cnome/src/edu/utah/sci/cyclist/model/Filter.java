package edu.utah.sci.cyclist.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import edu.utah.sci.cyclist.model.DataType.Classification;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.model.DataType.Type;

public class Filter implements Observable {
	private Field _field;
	private DataType _dataType;
	private ObservableSet<Object> _selectedItems = FXCollections.observableSet();
	private ListProperty<Object> _values = new SimpleListProperty<>();
	private List<InvalidationListener> _listeners = new ArrayList<>();
	
	public Filter(Field field){
		_field = field;
		_dataType = new DataType(field.getDataType());
		if (_field.getValues() != null) {
			_selectedItems.addAll(_field.getValues());
		}
		
		_field.valuesProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable arg0) {
				if (_field.getValues() != null)
					_selectedItems.addAll(_field.getValues());
				else
					_selectedItems.clear();
				_values.set(_field.getValues());
			}
		});
	}
	
	public Field getField() {
		return _field;
	}
	
	public String getName() {
		return _field.getName();
	}
	
	public DataType getDataType() {
		return _dataType;
	}
	
	public Role getRole() {
		return _dataType.getRole();
	}
	
	public Classification getClassification() {
		return _dataType.getClassification();
	}
	
	public Type getType() {
		return _dataType.getType();
	}
	
	public boolean isValid() {
		return _field.getValues() != null;
	}
	
	public ObservableList<Object> getValues() {
		return _field.getValues();
	}
	
	public ListProperty<Object> valuesProperty() {
		return _values;
	}
	
	public ObservableSet<Object> getSelectedValues() {
		return _selectedItems;
	}
	
	public boolean isSelected(Object value) {
		return _selectedItems.contains(value);
	}
	
	public void selectValue(Object value, boolean select) {
		boolean changed = false;
		if (select) {
			if (!_selectedItems.contains(value)) {
				_selectedItems.add(value);
				changed = true;
			}
		} else {
			changed = _selectedItems.remove(value);
		}
		
		if (changed)
			fireInvalidationEvent();
	}

	public void selectAll(boolean value) {
		if (value) {
			_selectedItems.addAll(_values);
		}
	}
	
	@Override
	public void addListener(InvalidationListener listener) {
		if (!_listeners.contains(listener))
			_listeners.add(listener);
		
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		_listeners.remove(listener);
	}
	
	private void fireInvalidationEvent() {
		for (InvalidationListener listener : _listeners) {
			listener.invalidated(Filter.this);
		}
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (_selectedItems.size() > 0) {
			builder.append(getName()).append(" in (");
			boolean first = true;
			for (Object item : _selectedItems) {
				if (first) first = false;
				else builder.append(", ");
	
				if (item instanceof String) {
					builder.append("'").append(item.toString()).append("'");
				} else
					builder.append(item.toString());
			}
			builder.append(")");
		} else {
			builder.append("false");
		}
		
		return builder.toString();
	}
}
