package edu.utah.sci.cyclist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

	private String _name;
	private Map<String, Object> _properties = new HashMap<>();
	private List<Field> _fields;
	private List<TableRow> _rows = new ArrayList<>();

	public Table() {
		this("");
	}
	public Table(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;	
	}
	
	@Override
    public String toString() {
        return getName();
    }
		
	public void setDataSource(CyclistDatasource datasource){
		setProperty("DataSource", datasource);
	}
	
	public CyclistDatasource getDataSource(){
		return (CyclistDatasource) getProperty("DataSource");
	}
	
	public void setTableName(String tablename){
		setProperty("TableName", tablename);
	}
	
	public String getTableName(){
		return (String) getProperty("TableName");
	}
	
	public void setProperty(String property, Object value) {
		_properties.put(property, value);
	}

	public void removeProperty(String property) {
		_properties.remove(property);
	}

	public Object getProperty(String property) {
		return _properties.get(property);
	}

	public boolean hasProperty(String property) {
		return _properties.containsKey(property);
	}

	public String getStringProperty(String property) {
		Object value = _properties.get(property);
		if (value == null)
			return null;
		else if (value instanceof String)
			return (String)value;
		else
			return value.toString();
	}

	public void setFields(List<Field> fields) {
		_fields = fields;
	}

	public List<Field> getFields() {
		return _fields;
	}

	public Field getField(int index) {
		return _fields.get(index);
	}

	public int getNumColumns() {
		return _fields.size();
	}


	public void addRow(TableRow row) {
		_rows.add(row);
	}

	public List<TableRow> getRows() {
		return _rows;
	}

	public TableRow getRow(int index) {
		return _rows.get(index);
	}

	public class TableRow  {
		public Object[] value;

		public TableRow(int size) {
			value = new Object[size];
		}
	}

	
}