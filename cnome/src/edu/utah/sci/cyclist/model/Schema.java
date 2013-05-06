package edu.utah.sci.cyclist.model;

import java.util.List;
import java.util.Vector;



public class Schema {

	private Vector<Field> _fields = new Vector<Field>();
	
	public Schema() {
		
	}
	
	public int size() {
		return _fields.size();
	}
	
	public Field getField(int index) {
		return _fields.get(index);
	}
	
	public Field getField(String name) {
		for (Field field : _fields)
			if (field.getName().equals(name)) 
				return field;
		return null;
	}
	
	public void addField(Field field) {
		_fields.add(field);
	}
	
	public void set(List<Field> list) {
		_fields.clear();
		_fields.addAll(list);
	}
	
}
