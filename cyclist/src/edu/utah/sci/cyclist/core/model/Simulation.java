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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

public class Simulation {

	public static final String DATA_SOURCE = "datasource";

	static Logger log = Logger.getLogger(Simulation.class);
	
	private Blob _simulationId;
	
	private CyclistDatasource _datasource;
	private int _startYear;
	private int _startMonth;
	private int _duration;
	private Map<String, Object> _properties = new HashMap<>();
	private  ObjectProperty<String> _aliasProperty = new SimpleObjectProperty<>();
	
	public Simulation() {
	}
	
	public Simulation(Blob simulationId) {
		_simulationId = simulationId;
		setAlias(_simulationId.toString());
	}
	
	public Simulation clone(){
		Simulation copy = new Simulation(_simulationId);
		copy.setDataSource(_datasource);
		copy.setAlias(getAlias());
		copy.setStartYear(_startYear);
		copy.setStartMonth(_startMonth);
		copy.setDuration(_duration);
		return copy;
	}
	
	public ObjectProperty<String> aliasProperty() {
		return _aliasProperty;
	}
	
	public void setAlias(String alias) {
		_aliasProperty.set(alias);
	}
	
	public String getAlias() {
		return _aliasProperty.get();
	}
	
	
	public void setStartYear(Integer year) {
		if (year == null) year = 0;
		_startYear = year;
	}
	
	public int getStartYear() {
		return _startYear;
	}
	
	public void setStartMonth(Integer month) {
		if (month == null) month = 0;
		_startMonth = month;
	}
	
	public int getStartMonth() {
		return _startMonth;
	}
	
	public void setDuration(Integer duration) {
		if (duration == null) duration = 1;
		_duration = duration;
	}
	
	public int getDuration() {
		return _duration;
	}
	
    /**Save the simulation alias and its date of deletion
     * 
     * @param memento
     */
	public void save(IMemento memento, String date) {
		memento.putString("simulation-id", getSimulationId().toString());
		memento.putString("alias", getAlias());
		memento.putString("date", date);
	}
	
	
	/** Save the simulation
	 * 
	 * @param memento
	 */
	public void save(IMemento memento) {
		memento.putString("simulation-id", getSimulationId().toString());
		memento.putString("datasource-uid", _datasource.getUID());
		memento.putString("alias", getAlias());
		memento.putInteger("startYear", _startYear);
		memento.putInteger("startMonth", _startMonth);
		memento.putInteger("duration", _duration);
	}
	
	/**
	 * Restores a simulation which contains only the simulation id and its alias 
	 * @param memento
	 */
	public void restoreAlias(IMemento memento){
		setSimulationId(memento.getString("simulation-id"));
		setAlias(memento.getString("alias"));
	}
	
	/**
	 *  Restore the simulation
	 * @param memento
	 * @param sources - the currently existing data sources in the model
	 */
	public void restore(IMemento memento, ObservableList<CyclistDatasource> sources){
		setSimulationId(memento.getString("simulation-id"));

		// Get the datasource
		String datasourceUID = memento.getString("datasource-uid");
		for(CyclistDatasource source: sources){
			if(source.getUID().equals(datasourceUID))
				setDataSource(source);
		}
		
		setAlias(memento.getString("alias"));
		setStartYear(memento.getInteger("startYear"));
		setStartMonth(memento.getInteger("startMonth"));
		setDuration(memento.getInteger("duration"));
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
}