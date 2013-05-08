package edu.utah.sci.cyclist.model;

import java.util.HashMap;
import java.util.Map;

public class Field {

	private String _name;
	private Map<String, Object> _properties = new HashMap<>();


	public Field(String name) {
		this._name = name;
	}

	public String getName() {
		return _name;
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
	
	public String getString(String property) {
		return (String) get(property);
	}
	
	public int getInt(String property) {
		return (int) get(property);
	}
	
	public String toString() {
		return _name;
	}
}