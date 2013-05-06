package edu.utah.sci.cyclist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

	public static final String DATA_SOURCE = "datasource";
	public static final String REMOTE_TABLE_NAME = "remote-table-name";
	
	private String _name;
	private Schema _schema = new Schema();
	private Map<String, Object> _properties = new HashMap<>();

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
	
	/**
	 * Convenient method
	 * @param datasource
	 */
	public void setDataSource(CyclistDatasource datasource){
		setProperty(DATA_SOURCE, datasource);
	}
	
	/**
	 * Convenient method
	 * @return
	 */
	public CyclistDatasource getDataSource(){
		return (CyclistDatasource) getProperty(DATA_SOURCE);
	}
	
	public void setSchema(Schema schema) {
		_schema = schema;
		clear();
	}
	
	public List<Field> getFields() {
		List<Field> list = new ArrayList<Field>();
		int n = _schema.size();
		for (int i=0; i<n; i++) {
			list.add(_schema.getField(i));
		}
		return list;
	}

	public Field getField(int index) {
		return _schema.getField(index);
	}

	public int getNumColumns() {
		return _schema.size();
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

	public void clear() {
		_rows.clear();
	}
	
	public class TableRow  {
		public Object[] value;

		public TableRow(int size) {
			value = new Object[size];
		}
	}

	
}