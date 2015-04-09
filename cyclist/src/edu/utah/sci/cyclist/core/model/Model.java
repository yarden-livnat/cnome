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
 *******************************************************************************/
package edu.utah.sci.cyclist.core.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

	private ObservableList<Table> _tables = FXCollections.observableArrayList();
	private ObservableList<CyclistDatasource> _sources = FXCollections.observableArrayList();
	private CyclistDatasource _currentSource;
	private ObservableList<Simulation> _simulations = FXCollections.observableArrayList();
	private List<Table> _simulationTablesDef = new ArrayList<>();
	private Simulation _lastPanelSelectedSimulation = null;
	private Map<Simulation,String>_simulationsAliases = new HashMap<>();
	private List<String> _remoteServersList = new ArrayList<String>();
	/**
	 * getTables
	 * @return
	 */
	public ObservableList<Table> getTables() {
		return _tables;
	}
	
	public Table getTable(String name) {
		for (Table table : _tables) {
			if (table.getName().equals(name)) 
				return table;
		}
		return null;
	}
	
	public ObservableList<CyclistDatasource> getSources() {
		return _sources;
	}	
	
	public CyclistDatasource getSelectedDatasource(){
		return _currentSource;
	}
	
	public void setSelectedDatasource(CyclistDatasource source){
		_currentSource = source;
	}

	public ObservableList<Simulation> getSimulations() {
		return _simulations;
	}
	
	public Map<Simulation,String> getSimAliases(){
		return _simulationsAliases;
	}
	
	/**
	 * Checks if a given simulation already exists in the list.
	 * @param simId - the simulation id to check
	 * @return true - if simulation id already exists, false - if not.
	 */
	public Simulation simExists(Simulation simulation){
		Simulation response = null;
		for(Simulation sim: _simulations){
			if(sim.getSimulationId().toString().equals(simulation.getSimulationId().toString())){
				response = sim;
				break;
			}
		}
		return response;
	}
	
	
	/**
	 * Checks if a given data source already exists in the list.
	 * @param ds - the data source to check
	 * @return true - if data source id already exists, false - if not.
	 */
	public Boolean dataSourceExists(CyclistDatasource ds){
		Boolean response = false;
		for(CyclistDatasource dataSource : _sources){
			if(dataSource.getURL().equals(ds.getURL())){
				response = true;
				break;
			}
		}
		return response;
	}
	
	public List<Table> getSimulationsTablesDef(){
		return _simulationTablesDef;
	}
	
	public Simulation getLastSelectedSimulation(){
		return _lastPanelSelectedSimulation;
	}
	
	public void setLastSelectedSimulation(Simulation simulation){
		_lastPanelSelectedSimulation = simulation;
	}
	
	/**
	 * Adds a new simulation alias to the simulations aliases list.
	 * If the simulation already exists in the list - it will be updated with the specified date.
	 * If it's a new simulation - it will be added to the list with an empty date.
	 * @param Simulation sim - the simulation which its alias should be added/updated.
	 * @param date - the last date it was marked with.
	 */
	public void addNewSimALias(Simulation sim, String date){
		Simulation simulation = null;

		//First check if that simulation already exist 
		for (Map.Entry<Simulation, String> entry : _simulationsAliases.entrySet()){
			Simulation mapSim = entry.getKey();
			if(mapSim.getSimulationId().toString().equals(sim.getSimulationId().toString())){
				simulation = mapSim;
				break;
			}
		}
		if(simulation!= null){
			simulation.setAlias(sim.getAlias());
		}else{
			simulation = new Simulation();
		    simulation = sim.clone();
		}
		_simulationsAliases.put(simulation, date);
	}
	
	/**
	 * Mark an existing simulation alias as removed by entering the current date.
	 * @param Simulation sim - the simulation to update.
	 */
	public void markSimALiasAsRemoved(Simulation sim){
		Simulation simulation = null;
		for (Map.Entry<Simulation, String> entry : _simulationsAliases.entrySet()){
			Simulation mapSim = entry.getKey();
			if(mapSim.getSimulationId().toString().equals(sim.getSimulationId().toString())){
				simulation = mapSim;
				break;
			}
		}	
		if(simulation!= null){
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			_simulationsAliases.put(simulation, dateFormat.format(date));
		}
	}
	
	/*
	 * Adds a new remote server address to the list of remote server, if not exists yet.
	 * @param String remoteServer - the remote server to add
	 * @return false - if not added (because it already exists), true - if added.
	 */
	public Boolean addNewRemoteServer(String remoteServer){
		for(String remote:_remoteServersList){
			if(remoteServer.equals(remote)){
				return false;
			}
		}
		_remoteServersList.add(remoteServer);
		return true;
	}
	
	public List<String> getRemoteServersList(){
		return _remoteServersList;
	}
	
}
