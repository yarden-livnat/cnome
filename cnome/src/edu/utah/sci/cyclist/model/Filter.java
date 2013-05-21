package edu.utah.sci.cyclist.model;

import edu.utah.sci.cyclist.model.Field.Role;

public class Filter {

	private Field _field;
	private String _action = null;
	
	private String _name;
	
	public Filter(Field field) {
		_field = field.clone();
		if (_field.getRole() == Role.NUMERIC)
			setAction("SUM");
		else
			setAction(null);
	}
	
	public Field getField() {
		return _field;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setAction(String action) {
		_action = action;
		if (action == null) {
			_name = _field.getName();
		} else {
			_name = _action+"("+_field.getName()+")";
		}
	}
	
	public String getAction() {
		return _action;
	}
	
	public Field.Role getRole() {
		return _field.getRole();
	}
	
	public Field.Type getType() {
		return _field.getType();
	}
	
}
