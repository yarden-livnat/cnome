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

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.controller.IMemento;
import edu.utah.sci.cyclist.controller.WorkDirectoryController;
import edu.utah.sci.cyclist.controller.XMLMemento;
import edu.utah.sci.cyclist.util.QueryBuilder;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;

public class Table {

	public static final String DATA_SOURCE = "datasource";
	public static final String REMOTE_TABLE_NAME = "remote-table-name";

	static Logger log = Logger.getLogger(Table.class);
	
	public enum SourceLocation {
		REMOTE,
		LOCAL_ALL,
		LOCAL_SUBSET
	}
	
	public enum NumericRangeValues {
		MIN,
		MAX,
		CHOSEN_MIN,
		CHOSEN_MAX
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
	private String _saveDir = "";
	
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
	
	public Table(Table tbl){
		_name = tbl.getName();
		_alias = tbl.getAlias();
		_sourceLocation = tbl.getSourceLocation();
		_datasource = tbl.getDataSource();
		_localDataFile = tbl.getLocalDatafile();
		_saveDir = tbl.getSaveDir();
        setProperty(REMOTE_TABLE_NAME, _name);	
        extractSchema();
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

//	private void printTypeInfo(Connection conn) {
//		DatabaseMetaData d;
//		try {
//			d = conn.getMetaData();
//			ResultSet rs = d.getTypeInfo();
//			ResultSetMetaData cmd = rs.getMetaData();
//			System.out.println("database types:");
//			while (rs.next()) {
//				for (int i=1; i<=cmd.getColumnCount(); i++) {
//					System.out.print(cmd.getColumnName(i)+": "+rs.getObject(i)+"  ");
//				}
//				System.out.println();
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
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

	public boolean hasField(Field field) {
		if (field.getTable() == this) return true;
		
		// for now check based on field name
		return _schema.contain(field);
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
	
	public Task<ObservableList<Object>> getFieldValues(final Field field) {
		final CyclistDatasource ds = getDataSource();
		
		Task<ObservableList<Object>> task = new Task<ObservableList<Object>>() {
			@Override
			protected ObservableList<Object> call() throws Exception {
				List<Object> values = new ArrayList<>();
				values = readFieldValuesFromFile(field.getName());
				if(values.size() == 0)
				{
					try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()){
//						updateMessage("connecting");
						updateMessage("querying");
						System.out.println("querying field values");
						long t1 = System.currentTimeMillis();
					
						// TODO: Fix this query building hack 
						String query = "select distinct "+field.getName()+" from "+getName()+" order by "+field.getName();
						log.debug("query: "+query);
						System.out.println(query);
						ResultSet rs = stmt.executeQuery(query);
						long t2 = System.currentTimeMillis();
						System.out.println("time: "+(t2-t1)/1000.0);
					
						while (rs.next()) {
							if (isCancelled()) {
								System.out.println("task canceled");
								stmt.cancel();
								updateMessage("Canceled");
								break;
							}
						
							values.add(rs.getObject(1));
						}
					
						long t3 = System.currentTimeMillis();
						System.out.println("gathering time: "+(t3-t2)/1000.0);
						writeFieldValuesToFile(field.getName(), values);
					} catch (SQLException e) {
						System.out.println("task sql exception: "+e.getLocalizedMessage());
						updateMessage(e.getLocalizedMessage());
						throw new Exception(e.getMessage(), e);
					} finally {
						ds.releaseConnection();
					}
				}
				return FXCollections.observableList(values);
			}
		};
		
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		
		return task;
	}
	
	public Task<ObservableList<Row>> getRows(final String query) {
		final CyclistDatasource ds = getDataSource();
		
		Task<ObservableList<Row>> task = new Task<ObservableList<Row>>() {

			@Override
			protected ObservableList<Row> call() throws Exception {
				List<Row> rows = new ArrayList<>();
				try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()) {
					updateMessage("querying");
					long t1 = System.currentTimeMillis();
					
					ResultSet rs = stmt.executeQuery(query);
					long t2 = System.currentTimeMillis();
					System.out.println("time: "+(t2-t1)/1000.0);
					ResultSetMetaData rmd = rs.getMetaData();
					
					int cols = rmd.getColumnCount();
					updateProgress(0, Long.MAX_VALUE);
					int n=0;
					while (rs.next()) {
						if (isCancelled()) {
							System.out.println("task canceled");
							stmt.cancel();
							updateMessage("Canceled");
							break;
						}
						Row row = new Row(cols);
						for (int i=0; i<cols; i++) {
							row.value[i] = rs.getObject(i+1);
						}
						// TODO: This is a hack. It seems that if the statement is '...where false' then a single row of nulls is return.
						if (row.value[0] == null) break;
						rows.add(row);
						n++;
						if (n % 1000 == 0) {
							updateMessage(n+" rows");
						}
					}
					long t3 = System.currentTimeMillis();
					System.out.println("gathering time: "+(t3-t2)/1000.0);
				} catch (SQLException e) {
					System.out.println("task sql exception: "+e.getLocalizedMessage());
					updateMessage(e.getLocalizedMessage());
					throw new Exception(e.getMessage(), e);
				} finally {
					ds.releaseConnection();
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
				try (Connection conn = ds.getConnection()){				
					StringBuilder builder = new StringBuilder("select ");
					for (int i=0; i<fields.size(); i++) {
						Field field = fields.get(i);
						
						builder.append(field.getName());
						if (i < fields.size()-1) builder.append(", ");
					}
					builder.append(" from ").append(getName()).append(" limit ").append(limit);
					System.out.println("query: ["+builder.toString()+"]");
					try (PreparedStatement stmt = conn.prepareStatement(builder.toString())) {
					
					ResultSet rs = stmt.executeQuery();
						int cols = fields.size();
						
						while (rs.next()) {
							Row row = new Row(cols);
							for (int i=0; i<cols; i++) {
								row.value[i] = rs.getObject(i+1);
							}
							rows.add(row);
						}
					} 
				}catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					ds.releaseConnection();
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
	public void setLocalDatafile(String workDir) {
		_saveDir = workDir;
		_localDataFile = workDir + "/" + getDataSource()+"/" + getName() + ".sqlite";
	}
	
	public String getSaveDir(){
		return _saveDir;
	}
	
	public class Row  {
		public Object[] value;

		public Row(int size) {
			value = new Object[size];
		}
	}
	
	/* Saves the values of a chosen filter into a file 
	 * Creates an xml file with the table name, and writes the filter's field name and its values
	 * If the field already exist in the file - do nothing. */
	private void writeFieldValuesToFile(String fieldName, List<Object> values){
		if(_saveDir == ""){
			_saveDir = WorkDirectoryController.DEFAULT_WORKSPACE;
		}
		
		// If the save directory does not exist, create it
		File saveDir = new File(_saveDir+ "/" + getDataSource()+"/");
		if (!saveDir.exists()){
			saveDir.mkdir();
		}
			
		// The save file
		File saveFile = new File(saveDir+"/"+ getName() + ".xml");
		
		XMLMemento root;
		IMemento fieldsNode = null;
		Boolean writeNewNode= true;
		
		 try {
			 //If file already exists - read the existing nodes, and add the new Field in its place.
			 if(saveFile.exists()){
				 Reader reader = new FileReader(saveFile);
				 
				 //Checks if the root node exists.
				 try{
					 root = XMLMemento.createReadRoot(reader);
					 fieldsNode = root.getChild("Fields");
				 }catch(Exception e){
					 root = XMLMemento.createWriteRoot("root");
				 }
				 
				 // Checks if the "Fields" node exists. 
				 // If yes - try to find the Field node with the given name. 
				 // If not - creates a new "Fields" node and mark that a new field node has to be written.
				 if(fieldsNode != null)
				 {
					 //Check if the field node already exists. If yes - no need to write it again to the table xml file.
					 if (getField(fieldsNode, fieldName) != null)
					 {
						 writeNewNode = false;
					 }
				 } else{
					 fieldsNode = root.createChild("Fields");
				 }
			 } else{
		
				 // If new file - Create the root memento
				 root = XMLMemento.createWriteRoot("root");
				// Create Fields node
				 fieldsNode = root.createChild("Fields");
			 }
	    
			 //If no such field node yet - write the field and its values into the file.
			 if(writeNewNode){
				 writeFieldNodeToFile(fieldName, fieldsNode, values);
			 }
			 root.save(new PrintWriter(saveFile));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/*Checks if a given field already exists in the table xml file.
	 * If field exists - return the field else- return null */
	private IMemento getField(IMemento fields, String fieldName){
		IMemento fieldResult = null;
		try
		{
			 IMemento[] fieldNodes = fields.getChildren("Field");
			 for (IMemento field:fieldNodes){
				 String name = field.getString("name");
				 if (name.equals(fieldName)){
					 fieldResult = field;
					 break;
				 }
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fieldResult;
	}
	
	/* Creates a new Field node in the table xml file*/
	private void writeFieldNodeToFile(String fieldName, IMemento fieldsNode, List<Object> values){
		
		//Create the Field node
		 IMemento FieldNode = fieldsNode.createChild("Field");
		
		 // Set the field name
		 FieldNode.putString("name", fieldName);
		 StringBuilder sb = new StringBuilder(); 
		 for(Object value:values){
			 if (value == null) {
				 System.out.println("*** Warning: field '"+fieldName+"' has a null value");
			 } else  {
				 sb.append(value.toString()+";");
			 }
		 }
		 FieldNode.putTextData(sb.toString());
	}
	
	/* Reads distinct values from a file 
	 * For a given field in a given table- 
	 * if the table xml file exists and it contains the field values - read the values from the file */
	private List<Object> readFieldValuesFromFile(String fieldName){
		
		List<Object> values = new ArrayList<>();
		if(_saveDir == ""){
			_saveDir = WorkDirectoryController.DEFAULT_WORKSPACE;
		}
		
		// If the save file does not exist - return an empty list.
		File saveFile = new File(_saveDir+ "/" + getDataSource() +"/"+ getName() + ".xml");
		if (!saveFile.exists()){
			return values;
		} else{
			try{
				 Reader reader = new FileReader(saveFile);
				 XMLMemento root = XMLMemento.createReadRoot(reader);
				 IMemento fieldsNode = root.getChild("Fields");
				 IMemento field = getField(fieldsNode, fieldName);
				 if(field != null){
					 String[] tmpValues = field.getTextData().split(";");
					 for(String value: tmpValues){
						 values.add(value);
					 }
				 }
				 return values;
			 }catch(Exception e){
				return values;
			 }
		}
	}
	
	public Task<ObservableMap<Object, Object>> getFieldRange(final Field field) {
		final CyclistDatasource ds = getDataSource();
		
		Task<ObservableMap<Object, Object>> task = new Task<ObservableMap<Object, Object>>() {
			
			@Override
			protected ObservableMap<Object, Object> call() throws Exception {
				Map<Object, Object> values = new HashMap<>();
				try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()){
					updateMessage("querying");
					System.out.println("querying field range");
					String query = "SELECT MIN("+field.getName()+") AS min, MAX(" + field.getName() + ") AS max FROM "+getName();
					log.debug("query: "+query);
					System.out.println(query);
					ResultSet rs = stmt.executeQuery(query);
					
					while (rs.next()) {
						if (isCancelled()) {
							System.out.println("task canceled");
							updateMessage("Canceled");
							break;
						}
					
						values.put(NumericRangeValues.MIN, rs.getDouble("min"));
						values.put(NumericRangeValues.MAX, rs.getDouble("max"));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				return FXCollections.observableMap(values);
			}
			
		};
		
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		
		return task;
		
	}
	
	

	
	private static final String GET_ROWS_QUERY = "select * from $table limit ?";
//	private static final String GET_NUM_ROWS_QUERY = "select count(*) from $table";
}