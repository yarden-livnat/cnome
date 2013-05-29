package edu.utah.sci.cyclist.model;

public class Filter {
	private Field _field;
	
	public Filter(Field field){
		_field = field;
	}
	
	public Field getField() {
		return _field;
	}
	
	public String getName() {
		return _field.getName();
	}
}
