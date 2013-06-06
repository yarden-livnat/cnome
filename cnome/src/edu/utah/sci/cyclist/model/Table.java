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
 *     Kristi Potter
 *******************************************************************************/
package edu.utah.sci.cyclist.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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

import utils.QueryBuilder;

import edu.utah.sci.cyclist.controller.IMemento;
import edu.utah.sci.cyclist.model.DataType.Type;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class Table {

	public static final String DATA_SOURCE = "datasource";
	public static final String REMOTE_TABLE_NAME = "remote-table-name";
	private static final String SAVE_DIR = System.getProperty("user.dir") + "/.cnome/";
	
	public enum SourceLocation {
		REMOTE,
		LOCAL_ALL,
		LOCAL_SUBSET
	}
	
	
	private String _alias;
	private String _name;
	private Schema _schema = new Schema(this);
	private CyclistDatasource _datasource;
	private Map<String, Object> _properties = new HashMap<>();

	private List<Row> _rows = new ArrayList<>();
	private String _localDataFile;

	private SourceLocation _sourceLocation;
	private int _dataSubset;
	
	public Table() {
		this("");
	}
	public Table(String name) {
		_name = name;
		_alias = name;
		_sourceLocation = SourceLocation.REMOTE;
		_dataSubset = 0;
		setProperty("uid", UUID.randomUUID().toString());
	}
	
    // Save the table
	public void save(IMemento memento) {

		// Set the name
		memento.putString("name", getName());
		
		// Set the alias
		memento.putString("alias", getAlias());
			
		// Save the schema
		_schema.save(memento.createChild("Schema"));
		
		// Save the uid of the data source
		memento.putString("datasource-uid", _datasource.getUID());
		
		// Save the location of the data source
		memento.putString("source-location", _sourceLocation.toString());
		
		// Save the subset
		memento.putInteger("subset", _dataSubset);
		
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
	public void restore(IMemento memento, ObservableList<CyclistDatasource> sources){
	
		// Get the name
		setName(memento.getString("name"));
		
		 // Get the alias
		setAlias(memento.getString("alias"));
		
		// Get the number of rows
//		_numRows = memento.getInteger("NumRows");

		// Get the datasource
		String datasourceUID = memento.getString("datasource-uid");
		for(CyclistDatasource source: sources){
			if(source.getUID().equals(datasourceUID))
				setDataSource(source);
		}
		
		// Get the location of the data source
		setSourceLocation(memento.getString("source-location"));
		
		// Get the data subset
		setDataSubset(memento.getInteger("subset"));
		
		// Save the subset
		memento.putInteger("subset", _dataSubset);
		
		// Get values in the property map	
		IMemento mapMemento = memento.getChild("property-map");
		IMemento[] entries =  mapMemento.getChildren("entry");
		for(IMemento entry: entries){

			// Get the key of the object
			String key = entry.getString("key");
			
			// Get the class of the object
			String classType = entry.getString("class");
				
			// If we have a string
			if(classType.equals(String.class.toString())){
				 String value = entry.getTextData();
				 setProperty(key, value);
			}
			// If we have an Integer
			else if(classType.equals(Integer.class.toString())){
				 Integer value = Integer.parseInt(entry.getTextData());
				 setProperty(key, value);
			}
			else{
				System.out.println("Table:load() NEED TO IMPLEMENT OBJECT FACTORIES!!");
			}	
		}
		
		// Restore the schema
		Schema schema = new Schema(this);
		schema.restore(memento.getChild("Schema"));
		setSchema(schema);
//		extractSchema();
		
	}

	// Extract the table schema from the database
	public void extractSchema(){
		
		try (Connection conn = _datasource.getConnection()) {
			//printTypeInfo(conn);
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getColumns(null, null, getName(), null);
			while (rs.next()) {				
				String colName = rs.getString("COLUMN_NAME");
			
				Field field = new Field(colName);
				field.setTable(this);
				field.set(FieldProperties.REMOTE_NAME, colName);
				field.set(FieldProperties.REMOTE_DATA_TYPE, rs.getInt("DATA_TYPE"));
				field.set(FieldProperties.REMOTE_DATA_TYPE_NAME, rs.getString("TYPE_NAME"));
				
				_schema.addField(field);
			}
		} catch (Exception e) {
			System.out.println("Error while parsing schema: "+e.getMessage());
		}
		_schema.update();
	}

	private void printTypeInfo(Connection conn) {
		DatabaseMetaData d;
		try {
			d = conn.getMetaData();
			ResultSet rs = d.getTypeInfo();
			ResultSetMetaData cmd = rs.getMetaData();
			System.out.println("database types:");
			while (rs.next()) {
				for (int i=1; i<=cmd.getColumnCount(); i++) {
					System.out.print(cmd.getColumnName(i)+": "+rs.getObject(i)+"  ");
				}
				System.out.println();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;	
	}
	
	public String getAlias(){
		if(_alias.equals(""))
			return getName();
		else
			return _alias;
	}
	
	public void setAlias(String alias){
		_alias = alias;
	}
	
	public void setSourceLocation(SourceLocation source){
		_sourceLocation = source;
	}
	
	public void setSourceLocation(String source){
		if(source.equals("REMOTE"))
			_sourceLocation = SourceLocation.REMOTE;
		else if(source.equals("LOCAL_ALL"))
			_sourceLocation = SourceLocation.LOCAL_ALL;
		else if(source.equals("LOCAL_SUBSET"))
			_sourceLocation = SourceLocation.LOCAL_SUBSET;
	}
	
	public SourceLocation getSourceLocation(){
		return _sourceLocation;
	}
	
	public void setDataSubset(int subset){
		_dataSubset = subset;
	}
	
	public int getDataSubset(){
		return _dataSubset;
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

	public void setFieldSelected(int index, boolean selected){
		_schema.getField(index).setSelectedProperty(selected);
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

	
	public QueryBuilder queryBuilder() {
		return new QueryBuilder(this);
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
	
	public Task<ObservableList<Row>> getRows(final String query) {
		final CyclistDatasource ds = getDataSource();
		
		Task<ObservableList<Row>> task = new Task<ObservableList<Row>>() {

			@Override
			protected ObservableList<Row> call() throws Exception {
				List<Row> rows = new ArrayList<>();
				try {
					Connection conn = ds.getConnection();
					PreparedStatement stmt = conn.prepareStatement(query);
					
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
		
		return task;
	}
	public ReadOnlyObjectProperty<ObservableList<Row>> getRows(final List<Field> fields, final int limit) {
		final CyclistDatasource ds = getDataSource();
		
		Task<ObservableList<Row>> task = new Task<ObservableList<Row>>() {

			@Override
			protected ObservableList<Row> call() throws Exception {
				List<Row> rows = new ArrayList<>();
				try {
					Connection conn = ds.getConnection();
					StringBuilder builder = new StringBuilder("select ");
					for (int i=0; i<fields.size(); i++) {
						Field field = fields.get(i);
						
						builder.append(field.getName());
						if (i < fields.size()-1) builder.append(", ");
					}
					builder.append(" from ").append(getName()).append(" limit ").append(limit);
					System.out.println("query: ["+builder.toString()+"]");
					PreparedStatement stmt = conn.prepareStatement(builder.toString());
					
					ResultSet rs = stmt.executeQuery();
					int cols = fields.size();
					
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
	
	public String getLocalDatafile() {
		return _localDataFile;
	}
	public void setLocalDatafile() {
		_localDataFile = SAVE_DIR + getDataSource() + getName() + ".sqlite";
	}
	
	public class Row  {
		public Object[] value;

		public Row(int size) {
			value = new Object[size];
		}
	}

	
	private static final String GET_ROWS_QUERY = "select * from $table limit ?";
	private static final String GET_NUM_ROWS_QUERY = "select count(*) from $table";
}