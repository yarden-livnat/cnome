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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.controller.IMemento;
import edu.utah.sci.cyclist.controller.WorkDirectoryController;
import edu.utah.sci.cyclist.controller.XMLMemento;
import javafx.collections.ObservableList;

public class Simulation {

	public static final String DATA_SOURCE = "datasource";

	static Logger log = Logger.getLogger(Simulation.class);
	
	private String _simulationId;
	
	private CyclistDatasource _datasource;
	private Map<String, Object> _properties = new HashMap<>();

	private String _saveDir = "";
	
	public Simulation() {
		this("");
	}
	public Simulation(String simulationId) {
		setProperty("uid", UUID.randomUUID().toString());
		_simulationId = simulationId;
	}
	
	public Simulation(Simulation sim){
		_datasource = sim.getDataSource();
		_saveDir = sim.getSaveDir();
		_simulationId = sim.getSimulationId();
	}
	
    // Save the simulation
	public void save(IMemento memento) {

		// Set the name
		memento.putString("simulation-id", getSimulationId());
		
		// Save the uid of the data source
		memento.putString("datasource-uid", _datasource.getUID());
	}
	
	// Restore the table
	public void restore(IMemento memento, ObservableList<CyclistDatasource> sources){
	
		// Get the name
		setSimulationId(memento.getString("simulation-id"));

		// Get the datasource
		String datasourceUID = memento.getString("datasource-uid");
		for(CyclistDatasource source: sources){
			if(source.getUID().equals(datasourceUID))
				setDataSource(source);
		}
	}
	
	public String getSimulationId(){
		return _simulationId;
	}
	
	public void setSimulationId(String simulationId){
		_simulationId = simulationId;
	}
	
	@Override
    public String toString() {
        return getSimulationId();
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
	
	public String getSaveDir(){
		return _saveDir;
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
		File saveFile = new File(saveDir+"/"+ getSimulationId() + ".xml");
		
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
		File saveFile = new File(_saveDir+ "/" + getDataSource() +"/"+ getSimulationId() + ".xml");
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
	
}