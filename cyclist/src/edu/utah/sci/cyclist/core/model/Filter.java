package edu.utah.sci.cyclist.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import edu.utah.sci.cyclist.core.model.DataType.Classification;
import edu.utah.sci.cyclist.core.model.DataType.FilterType;
import edu.utah.sci.cyclist.core.model.DataType.Role;
import edu.utah.sci.cyclist.core.model.DataType.Type;
import edu.utah.sci.cyclist.core.model.Table.NumericRangeValues;

public class Filter implements Observable {
	
	private boolean _valid = true;
	private String _value = "1=1";  //Sqlite doesn't accept "where true" but accepts "where 1=1".
	private CyclistDatasource _ds = null;
	private Field _field;
	private DataType _dataType;
	private ObservableSet<Object> _selectedItems = FXCollections.observableSet();
	private ListProperty<Object> _values = new SimpleListProperty<>();
	private List<InvalidationListener> _listeners = new ArrayList<>();
	private MapProperty<Object, Object> _rangeValues = new SimpleMapProperty<>();
	private Map<Object,Object> _selectedRangeValues = new HashMap<>();
	private  Consumer<Void> _onDSUpdated = null;
	private Boolean _dsChanged = true;
	
	public Filter(Field field) {
		this(field, true);
	}
	
	public Filter(Field field, boolean auto){
		_field = field;
		_dataType = new DataType(field.getDataType());
		
		if (auto) {
			/*if(_dataType.getRole() != Role.DIMENSION){
				_field.set(FieldProperties.AGGREGATION_FUNC, field.getString(FieldProperties.AGGREGATION_DEFAULT_FUNC));
			}*/
			
			if (getValues() != null) {
				_selectedItems.addAll(getValues());
				_values.set(getValues());
			}
			
			if(_field.getRangeValues() != null){
				_rangeValues.set(_field.getRangeValues());
				_selectedRangeValues.put(NumericRangeValues.MIN, _field.getRangeValues().get(NumericRangeValues.MIN));
				_selectedRangeValues.put(NumericRangeValues.MAX, _field.getRangeValues().get(NumericRangeValues.MAX));
			}
			
			_field.valuesProperty().addListener((Observable o)-> {
				_selectedItems.clear();  //TBD - should clear the previous values???
				
				if (getValues() != null){
					_selectedItems.addAll(getValues());
				}
				else{
					resetFilterValues();
				}
				_values.set(getValues());
			});
			
			_field.rangeValuesProperty().addListener((Observable o)-> {
				_selectedRangeValues.clear();
				if (_field.getRangeValues() != null){	
					_selectedRangeValues.put(NumericRangeValues.MIN, _field.getRangeValues().get(NumericRangeValues.MIN));
					_selectedRangeValues.put(NumericRangeValues.MAX, _field.getRangeValues().get(NumericRangeValues.MAX));
				}else{
					resetFilterValues();
				}
				
				_rangeValues.set(_field.getRangeValues());
			});

		}	
	}
	
	public void setOnDSUpdated(Consumer<Void> action){
		_onDSUpdated = action;
	}
	
	public void setDatasource(CyclistDatasource ds) {
		if (_ds == ds) return;
		
		_ds = ds;
		_dsChanged = true;
		if(_onDSUpdated != null){
			_onDSUpdated.accept(null);
		}
	}
	
	public CyclistDatasource getDatasource(){
		return _ds;
	}
	
	public Field getField() {
		return _field;
	}
	
	public String getName() {
		return _field.getName();
	}
	
	public String getLabel() {
		return getName();
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
	
	public FilterType getFilterType(){
		return _dataType.getFilterType();
	}
	
	public Type getType() {
		return _dataType.getType();
	}
	
	public boolean isValid() {
		Boolean reply = getValues() != null && !_dsChanged;
		_dsChanged = false;
		return reply;
	}
	
	public boolean isRangeValid() {
		Boolean reply =  _field.getRangeValues() != null && !_dsChanged;
		_dsChanged = false;
		return reply;
	}
	
	public ObservableList<Object> getValues() {
		return _field.getValues();
	}
	
	public ListProperty<Object> valuesProperty() {
		return _values;
	}
	
	public MapProperty<Object,Object> rangeValuesProperty() {
		return _rangeValues;
	}
	
	public ObservableSet<Object> getSelectedValues() {
		return _selectedItems;
	}
	
	public boolean isSelected(Object value) {
		return _selectedItems.contains(value);
	}
	
	public void selectValue(Object value, boolean select) {
		if (select) {
			_selectedItems.add(value);
		} else {
			_selectedItems.remove(value);
		}
		invalidate();
	}

	public void selectAll(boolean value) {
		if (value) {
			if (_selectedItems.size() == getValues().size()) return;
			_selectedItems.addAll(_values);
		} else {
			if (_selectedItems.size() == 0) return;
			_selectedItems.clear();
		}
		invalidate();
	}
	
	public void selectMinMaxValues(Object minValue, Object maxValue){
		_selectedRangeValues.put(NumericRangeValues.MIN, minValue);
		_selectedRangeValues.put(NumericRangeValues.MAX, maxValue);
		invalidate();
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
	
	private void invalidate() {
		if (_valid) {
			_valid = false;
			for (InvalidationListener listener : _listeners) {
				listener.invalidated(this);
			}
		}
	}
	
	/*
	 * Reset the filter to include all the values.
	 * (As when it is first created)
	 * Is used when a new simulation brings new set of values to the filter, so the old selected values have to be removed.
	 * ===  TBD - Might change depending on the request of what to do if the data source of the filter has been changed. ===  
	 */
	private void resetFilterValues(){
		if (_valid) {
			_value = "1=1";
		}
	}
	
	private Boolean isBlob(String item){
		return (item.indexOf("X'") == 0 && item.lastIndexOf("'")== item.length()-1);
	}
	
	public boolean isActive() {
		boolean active = true;
		if (getValues() == null || _selectedItems.size() == getValues().size()) {
			if(_selectedRangeValues.size() == 0) {
				active = false;
			}
		} 
		
		return active;
	}
	
	public boolean allValuesSelected() {
		return _selectedItems.size() == getValues().size();
	}
	
	public String toString() {
		if (!_valid) {
			if (getValues() == null || allValuesSelected()) {
				if(_selectedRangeValues.size() > 0){
					String function = _field.getString(FieldProperties.AGGREGATION_FUNC);
					String name = function != null? (function.indexOf(")") >-1 ? function.substring(0, function.indexOf(")"))+ " " +getName() +")" : function+"("+getName()+")") : getName();
					String str = name+" >=" + _selectedRangeValues.get(NumericRangeValues.MIN) + " AND " + name +" <=" + _selectedRangeValues.get(NumericRangeValues.MAX);
					_value = str;
					
				}else{
					_value = "1=1";
				}
			} else {
				StringBuilder builder = new StringBuilder();
				if (_selectedItems.size() > 0) {
					builder.append(getName()).append(" in (");
					boolean first = true;
					for (Object item : _selectedItems) {
						if (first) first = false;
						else builder.append(", ");
			
						if (item instanceof String && !isBlob(item.toString())) {
							builder.append("'").append(item.toString()).append("'");
						} else
							builder.append(item.toString());
					}
					builder.append(")");
					
				} else {
					builder.append("1=0");
				}
			
				_value = builder.toString();
			}
			_valid = true;
		}
		
		return _value;
	}
	
	public void setValid(Boolean isValid) {
		_valid = isValid;
	}
}
