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

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

	private ObservableList<Table> _tables = FXCollections.observableArrayList();
	private ObservableList<CyclistDatasource> _sources = FXCollections.observableArrayList();
	private CyclistDatasource _currentSource;
	private ObservableList<Simulation> _simulations = FXCollections.observableArrayList();
	private List<Table> _simulationTablesDef = new ArrayList<>();
	private Simulation _lastPanelSelectedSimulation = null;
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
	
	/**
	 * Checkes if a given simulation already exists in the list.
	 * @param simId - the simulation id to check
	 * @return true - if simulation id already exists, false - if not.
	 */
	public Boolean simExists(Simulation simulation){
		Boolean response = false;
		for(Simulation sim: _simulations){
			if(sim.getSimulationId().equals(simulation.getSimulationId())){
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
	
}
