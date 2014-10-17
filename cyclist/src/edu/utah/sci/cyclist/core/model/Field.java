/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved.
 *
 * License for the specific language governing rights and limitations under Permission
 * is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: The above copyright notice 
 * and this permission notice shall be included in all copies  or substantial portions of the Software. 
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR 
 *  A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import edu.utah.sci.cyclist.core.controller.IMemento;
import edu.utah.sci.cyclist.core.model.DataType.Role;
import edu.utah.sci.cyclist.core.util.SQL;

public class Field {
	
	private String _name;
	private String _semantic;
	private DataType _dataType;
	private BooleanProperty _selected;
	private ObjectProperty<Table> _tableProperty = new SimpleObjectProperty<>(); 
	private ListProperty<Object> _valuesProperty = new SimpleListProperty<>();
	private MapProperty<Object,Object> _rangeValuesProperty= new SimpleMapProperty<>();
	private Map<String, Object> _properties = new HashMap<>();
	private Boolean _isHidden = false;
	
	public Field(){
		this("");
	}

	public Field(Field other) {
		this(other._name, other._semantic);
		_dataType = new DataType(other._dataType);
		_selected = other._selected;
		setTable(other.getTable()); 
		for (Map.Entry<String, Object> entry : other._properties.entrySet()) {
			_properties.put(entry.getKey(), entry.getValue());
		}
	}
	
	public Field(String name) {
		this(name, name);
	}
	
	public Field(String name, String semantic) {
		this._name = name;
		this._semantic = semantic;
		_selected = new SimpleBooleanProperty();
		_selected.set(true);
	}

	public ListProperty<Object> valuesProperty() {
		return _valuesProperty;
	}
	
	public void setValues(ObservableList<Object> values) {
		valuesProperty().set(values);
	}
	
	public ObservableList<Object> getValues() {
		return valuesProperty().get();
	}
	
	public MapProperty<Object, Object> rangeValuesProperty() {
		return _rangeValuesProperty;
	}
	
	public void setRangeValues(ObservableMap<Object, Object> rangeValues) {
		rangeValuesProperty().set(rangeValues);
	}
	
	public ObservableMap<Object, Object> getRangeValues() {
		return rangeValuesProperty().get();
	}
	
	public String getName() {
		return _name;
	}

	public String getSemantic() {
		return _semantic;
	}
	
	public ObjectProperty<Table> tableProperty() {
		return _tableProperty;
	}
	
	public void setTable(Table table) {
		_tableProperty.set(table);
	}
	
	public Table getTable() {
		return _tableProperty.get();
	}
	
	public String getDataTypeName() {
		return (String) _properties.get(FieldProperties.DATA_TYPE_NAME);
	}

	public void set(String property, Object value) {
		_properties.put(property, value);
	}

	public Object get(String property) {
		return _properties.get(property);
	}
	
	public <T> T get(String key, Class<T> type) {
		Object value = _properties.get(key);
		return value == null ? null : type.cast(value);
	}
	
	public String getString(String property) {
		return (String) get(property);
	}
	
	public int getInt(String property) {
		return (int) get(property);
	}
	
	public DataType getDataType() {
		return _dataType;
	}
	
	public void setDataType(DataType dataType) {
		_dataType = dataType;
	}
	

	public DataType.Role getRole() {
		return _dataType.getRole();
	}
	
	public void setRole(DataType.Role role) {
		_dataType.setRole(role);
	}
	
	public DataType.Type getType() {
		return _dataType.getType();
	}
	
	public DataType.Classification getClassification() {
		return _dataType.getClassification();
	}
	
	public String toString() {
		return _name;
	}
	
	public boolean similar(Field field) {
		return getName().equals(field.getName()) && getType() == field.getType();
	}
	
	public boolean similarWithTable(Field field) {
		return getName().equals(field.getName()) && getType() == field.getType() && getTable() == field.getTable();
	}
	
	public Boolean isHidden(){
		return _isHidden;
	}
	
	
	// Save this field
	public void save(IMemento memento) {
	
		memento.putString("name", _name);
		
		IMemento dataMemento = memento.createChild("datatype");
		String role = getRole()!=null?getRole().toString():"";
		dataMemento.putString("role", role);
		dataMemento.putString("type", getType().toString());
		String interp =  _dataType.getInterpretation()!=null?_dataType.getInterpretation().toString():"";
		dataMemento.putString("interp", interp);		
		dataMemento.putString("classification", getClassification().toString());
		dataMemento.putString("semantic", _semantic);

		memento.putBoolean("selected", _selected.get());
		
		// Set things saved in the properties map
		Set<String> keys = _properties.keySet();
		for(String key: keys){
			
			// Get the value associated with this key
			Object value = _properties.get(key);

			// Create an entry memento
			IMemento entryMemento = memento.createChild("entry");
			entryMemento.putString("key", key);
			
			// If the value is null, record it
			if(value == null)
				entryMemento.putTextData("null");	
			else{

				// Put the type of the class
				entryMemento.putString("class", value.getClass().toString());

				// Save integers or strings as strings
				if(value.getClass().toString().equals(String.class.toString()) || 
						value.getClass().toString().equals(Integer.class.toString()))
					entryMemento.putTextData(value.toString());
				// TODO/FIXME: save some sort of Factory-ID
				else{

					System.out.println("Table:save() NEED TO CHECK FOR SAVE-ABLE OBJECTS!!");
					IMemento valueMemento = entryMemento.createChild("value");
					valueMemento.putString("value-ID", value.toString());		
				}
			}
		}
	}
	
	// Restore this field
	public void restore(IMemento memento) {
		try{
		// Get the name of the field
		_name = memento.getString("name");
		
		IMemento dataMemento = memento.getChild("datatype");
		DataType.Role role = dataMemento.getString("role")!= "" ? DataType.Role.valueOf(dataMemento.getString("role")):null;
		DataType.Type type = DataType.Type.valueOf(dataMemento.getString("type"));
		DataType.Interpretation interp = dataMemento.getString("interp")!= "" ? DataType.Interpretation.valueOf(dataMemento.getString("interp")):null;
		DataType.Classification classification = DataType.Classification.valueOf(dataMemento.getString("classification"));
		
		_dataType = new DataType(role, type, interp, classification);

		_semantic = memento.getString("semantic");
		_selected.set(memento.getBoolean("selected"));
				
		// Get the entries in the field
		IMemento[] entries = memento.getChildren("entry");
		for(IMemento entry:entries){
			
			// Get the key of the object
			String key = entry.getString("key");
						
			// Get the class of the object
			String classType = entry.getString("class");
							
			// If we have a string
			if(classType.equals(String.class.toString())){
				String value = entry.getTextData();
				set(key, value);
			}
			// If we have an Integer
			else if(classType.equals(Integer.class.toString())){
				Integer value = Integer.parseInt(entry.getTextData());
				set(key, value);
			}	
			else{
				System.out.println("Field:load() NEED TO IMPLEMENT OBJECT FACTORIES!!");
			}	
		}	
		} catch(NullPointerException e){
			
		}
	}
	
	/*
	 * Restores fields from the simulation configuration file.
	 * Restores only the information which is relevant to the simulation fields.
	 */
	public void restoreSimulated(IMemento memento) {
			_name = memento.getString("name");
			_semantic = memento.getString("semantic");
			if (_semantic == null) _semantic = _name;
			_isHidden = memento.getBoolean("isHidden");
			if(_isHidden==null) _isHidden=false;
			IMemento dataTypeMemento = memento.getChild("datatype");
			
			DataType.Type type = DataType.Type.valueOf(dataTypeMemento.getString("type"));
			DataType.Role role = dataTypeMemento.getString("role")!= "" ? DataType.Role.valueOf(dataTypeMemento.getString("role")):null;
			_dataType = new DataType(type, role);
			if (_dataType.getRole() == Role.MEASURE){
				set(FieldProperties.AGGREGATION_DEFAULT_FUNC, SQL.DEFAULT_FUNCTION);
			}
			
	}

	public BooleanProperty getSelectedProperty() {
		return _selected;
	}
	
	public void setSelectedProperty(boolean selected){
		_selected.set(selected);
	}
	
	public Field clone() {
		Field copy = new Field(getName());
		
		copy._dataType = new DataType(_dataType);
		copy._selected = _selected;
		copy.setTable(getTable()); 
		for (Map.Entry<String, Object> entry : _properties.entrySet()) {
			copy._properties.put(entry.getKey(), entry.getValue());
		}
		
		return copy;
	}
	
}