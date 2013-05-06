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

	public void update() {
		for (Field field : _fields) {
			updateField(field);
		}
	}
	
	public void updateField(Field field) {
		// set field type
		int remote_type = (int) field.get(FieldProperties.REMOTE_DATA_TYPE);
		
		// for now assume local type is the same as remote type
		field.set(FieldProperties.DATA_TYPE, remote_type);
		field.set(FieldProperties.DATA_TYPE_NAME, field.getString(FieldProperties.REMOTE_DATA_TYPE_NAME));
				
		switch (remote_type) {
		case java.sql.Types.INTEGER:
		case java.sql.Types.BIGINT:
		case java.sql.Types.SMALLINT:
		case java.sql.Types.TINYINT:
		case java.sql.Types.DECIMAL:
		case java.sql.Types.FLOAT:
		case java.sql.Types.DOUBLE:
		case java.sql.Types.REAL:
			field.set(FieldProperties.ROLE, FieldProperties.VALUE_MEASURE);
			break;
		case java.sql.Types.BOOLEAN:
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGNVARCHAR:
		case java.sql.Types.LONGVARCHAR:
		case java.sql.Types.NCHAR:
		case java.sql.Types.BIT:
		case java.sql.Types.DATE:
		case java.sql.Types.TIME:
		case java.sql.Types.TIMESTAMP:
		case java.sql.Types.ROWID:
			field.set(FieldProperties.ROLE, FieldProperties.VALUE_DIMENSION);
			break;
		case java.sql.Types.ARRAY:
		case java.sql.Types.BINARY:
		case java.sql.Types.BLOB:
		case java.sql.Types.CLOB:
		case java.sql.Types.DISTINCT:
		case java.sql.Types.DATALINK:
		case java.sql.Types.JAVA_OBJECT:
		case java.sql.Types.LONGVARBINARY:
		case java.sql.Types.NCLOB:
		case java.sql.Types.NULL:
		case java.sql.Types.OTHER:
		case java.sql.Types.REF:
		case java.sql.Types.SQLXML:
		case java.sql.Types.STRUCT:
		case java.sql.Types.VARBINARY:
			field.set(FieldProperties.ROLE, FieldProperties.VALUE_UNKNOWN);
		}
	}
}
