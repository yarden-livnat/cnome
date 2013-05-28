package edu.utah.sci.cyclist.model;

import edu.utah.sci.cyclist.model.DataType.Classification;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.model.DataType.Type;

public class Filter {
	private Field _field;
	private DataType _dataType;
	
	public Filter(Field field){
		_field = field;
		_dataType = new DataType(field.getDataType());
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
}
