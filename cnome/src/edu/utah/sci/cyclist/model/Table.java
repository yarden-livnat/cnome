package edu.utah.sci.cyclist.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.utah.sci.cyclist.controller.IMemento;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class Table {

	public static final String DATA_SOURCE = "datasource";
	public static final String REMOTE_TABLE_NAME = "remote-table-name";
	
	private String _name;
	private Schema _schema = new Schema();
	private CyclistDatasource _datasource;
	private Map<String, Object> _properties = new HashMap<>();

	private List<Row> _rows = new ArrayList<>();

	public Table() {
		this("");
	}
	public Table(String name) {
		_name = name;
		setProperty("uid", UUID.randomUUID().toString());
	}
	
    // Save the table
	public void save(IMemento memento) {

		// Create the child memento
		//XMLMemento memento = (XMLMemento) tablesListMemento.createChild("Table");

		// Set the name
		memento.putString("name", getName());
		
		// Save the schema
		_schema.save(memento.createChild("Schema"));
		
		// Save the uid of the data source
		memento.putString("datasource-uid", _datasource.getUID());
		
		// Save the map
		IMemento mapMemento = memento.createChild("property-map");

		// Set things saved in the properties map
		Set<String> keys = _properties.keySet();
		for(String key: keys){
			
			Object value = _properties.get(key);

			IMemento entryMemento = mapMemento.createChild("entry");
			entryMemento.putString("key", key);
			entryMemento.putString("class", value.getClass().toString());

			// Save integers or strings as strings
			if(value.getClass().toString().equals(String.class.toString()) || 
  			   value.getClass().toString().equals(Integer.class.toString()))
				entryMemento.putTextData((String)value);
			else{

				System.out.println("Table:save() NEED TO CHECK FOR SAVE-ABLE OBJECTS!!");
				IMemento valueMemento = entryMemento.createChild("value");
				valueMemento.putString("value-ID", value.toString());		
			}
		}
	}
	
	// Restore the table
	public void restore(IMemento memento){
		
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
		_datasource = datasource;
	}
	
	/**
	 * Convenient method
	 * @return
	 */
	public CyclistDatasource getDataSource(){
		return _datasource;
	}
	
	public void setSchema(Schema schema) {
		_schema = schema;
		clear();
	}
	
	public Schema getSchema() {
		return _schema;
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


	public void addRow(Row row) {
		_rows.add(row);
	}

	public List<Row> getRows() {
		return _rows;
	}

	public ReadOnlyObjectProperty<ObservableList<Row>> getRows(final int n) {
		final CyclistDatasource ds = getDataSource();
		
		Task<ObservableList<Row>> task = new Task<ObservableList<Row>>() {

			@Override
			protected ObservableList<Row> call() throws Exception {
				List<Row> rows = new ArrayList<>();
				try {
					Connection conn = ds.getConnection();
					String query = GET_ROWS_QUERY.replace("$table", getName());
					PreparedStatement stmt = conn.prepareStatement(query);
					stmt.setInt(1, n);
					
					ResultSet rs = stmt.executeQuery();
					ResultSetMetaData rmd = rs.getMetaData();
					
					int cols = rmd.getColumnCount();
					while (rs.next()) {
						Row row = new Row(cols);
						for (int i=0; i<cols; i++) {
							row.value[i] = rs.getObject(i+1);
						}
						rows.add(row);
					}
				}catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return FXCollections.observableList(rows);
			}
			
		};
		
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		
		return task.valueProperty();
	}
	
	public Row getRow(int index) {
		return _rows.get(index);
	}

	public void clear() {
		_rows.clear();
	}
	
	public class Row  {
		public Object[] value;

		public Row(int size) {
			value = new Object[size];
		}
	}

	private static final String GET_ROWS_QUERY = "select * from $table limit ?";
}