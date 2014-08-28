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
package edu.utah.sci.cyclist.core.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.core.controller.IMemento;
import javafx.collections.ObservableList;

public class Simulation {

	public static final String DATA_SOURCE = "datasource";

	static Logger log = Logger.getLogger(Simulation.class);
	
	private Blob _simulationId;
	
	private CyclistDatasource _datasource;
	private String _alias;
	private Map<String, Object> _properties = new HashMap<>();
	
	public Simulation() {
//		this(null);
	}
	
	public Simulation(Blob simulationId) {
		_simulationId = simulationId;
		_alias = _simulationId.toString();
	}
	
	public Simulation clone(){
		Simulation copy = new Simulation(_simulationId);
		copy.setDataSource(_datasource);
		copy.setAlias(_alias);
		return copy;
	}
	
    // Save the simulation
	public void save(IMemento memento) {

		// Set the name
		memento.putString("simulation-id", getSimulationId().toString());
		
		// Save the uid of the data source
		memento.putString("datasource-uid", _datasource.getUID());
		
		//Save the alias
		memento.putString("alias", getAlias());
	}
	
	// Restore the simulation
	public void restore(IMemento memento, ObservableList<CyclistDatasource> sources){
	
		// Get the name
		setSimulationId(memento.getString("simulation-id"));

		// Get the datasource
		String datasourceUID = memento.getString("datasource-uid");
		for(CyclistDatasource source: sources){
			if(source.getUID().equals(datasourceUID))
				setDataSource(source);
		}
		
		//Get the alias
		setAlias(memento.getString("alias"));
	}
	
	public Blob getSimulationId(){
		return _simulationId;
	}
	
	public void setSimulationId(Blob simulationId){
		_simulationId = simulationId;
	}
	
	public void setSimulationId(String id) {
		setSimulationId(new Blob(id));
	}
	
	@Override
    public String toString() {
        return getSimulationId().toString();
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
	
	public String getAlias(){
		return _alias;
	}
	
	public void setAlias(String alias){
		_alias = alias;
	}
}