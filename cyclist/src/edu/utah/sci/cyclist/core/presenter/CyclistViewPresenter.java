	package edu.utah.sci.cyclist.core.presenter;

import java.util.ArrayList;
import java.util.List;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.core.controller.IMemento;
import edu.utah.sci.cyclist.core.event.notification.CyclistFilterNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.core.event.notification.CyclistSimulationNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.model.Context;
import edu.utah.sci.cyclist.core.model.Filter;
import edu.utah.sci.cyclist.core.model.Resource;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.ui.CyclistView;
import edu.utah.sci.cyclist.core.ui.View;

public class CyclistViewPresenter extends ViewPresenter implements Resource  {
	private SelectionModel<Table> _selectionModelTbl = new SelectionModel<Table>();
	private SelectionModel<Simulation> _selectionModelSim = new SelectionModel<Simulation>();
	private Boolean _dirtyFlag = false;
	
	public CyclistViewPresenter(EventBus bus) {
		super(bus);
		
		addListeners();
	}

	@Override
    public String getUID() {
		// not used
	    return null;
    }
	
	public CyclistView getView() {
		return (CyclistView) super.getView();
	}
	
	public void setView(View view) {
		super.setView(view);
		
		if (view != null && view instanceof CyclistView) {
			getView().setOnTableSelectedAction(new Closure.V2<Table, Boolean>() {
				
				@Override
				public void call(Table table, Boolean active) {
					getSelectionModelTbl().itemSelected(table, active);
					_dirtyFlag = true;
				}
			});
			
			getView().setOnSimulationSelectedAction(new Closure.V2<Simulation, Boolean>() {
				
				@Override
				public void call(Simulation simulation, Boolean active) {
					getSelectionModelSim().itemSelected(simulation, active);
					_dirtyFlag = true;
				}
			});
			
			getView().setOnSelectAction(new Closure.V0() {
				@Override
				public void call() {
					onViewSelected(getView());				}
			});
			
			getView().setOnShowFilter(new Closure.V1<Filter>() {
				@Override
				public void call(Filter filter) {
					_dirtyFlag = true;
					broadcast(new CyclistFilterNotification(CyclistNotifications.SHOW_FILTER, filter));
				}
			});
			
			getView().setOnRemoveFilter(new Closure.V1<Filter>() {
				@Override
				public void call(Filter filter) {
					_dirtyFlag = true;
					broadcast(new CyclistFilterNotification(CyclistNotifications.REMOVE_FILTER, filter));
				}
			});
			
			//If none of the subclasses has set its specific action for simulation dropping - set a general action.
			if(getView().getOnSimulationDrop() == null){
				getView().setOnSimulationDrop(new Closure.V1<Simulation>(){
					@Override
					public void call(Simulation simulation) {
						addLocalSimulation(simulation);
						_dirtyFlag = true;
					}
				});
			}
			//If none of the subclasses has set its specific action for simulation removal - set a general action.
			if(getView().getOnSimulationRemoved() == null){
				getView().setOnSimulationRemoved(new Closure.V1<Simulation>(){
					@Override
					public void call(Simulation simulation) {
						getSelectionModelSim().removeItem(simulation);
						_dirtyFlag = true;
					}
				});
			}
		}
	}
	
	public void onViewSelected(View view) {
		super.onViewSelected(view);
		
		Table table = getSelectionModelTbl().getSelected();
		if (table != null)
			broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_FOCUS, table));
	}
	
	public void setRemoteTables(List<SelectionModel<Table>.Entry> list) {
		for (SelectionModel<Table>.Entry record : list) {
			// infom the view but let the selection model determine if it should be active
			getView().addTable(record.item, true /*remote*/, false /* active */);
			//getSelectionModel().addTable(record.table, true, false, record.active);
		}
		getSelectionModelTbl().setRemoteItems(list);
	}
	
	public void addRemoteFilters(List<Filter> filters) {
		getView().remoteFilters().addAll(filters);
	}
	
	public void addTable(Table table, boolean remote, boolean active, boolean remoteActive) {
		getView().addTable(table, remote, false);
		getSelectionModelTbl().addItem(table, remote, active, remoteActive);
		_dirtyFlag = true;
	}
	
	
	public void removeTable(Table table) {
		getSelectionModelTbl().removeItem(table);
		getView().removeTable(table);
		_dirtyFlag = true;
	}
	
	public List<SelectionModel<Table>.Entry> getTableRecords() {
		return _selectionModelTbl.getItemRecords();
	}
	
	public SelectionModel<Table> getSelectionModelTbl() {
		return _selectionModelTbl;
	}
	
	public void setSelectionModelTbl(SelectionModel<Table> model) {
		_selectionModelTbl = model;
		
	}
	
	public SelectionModel<Simulation> getSelectionModelSim() {
		return _selectionModelSim;
	}
	
	public List<SelectionModel<Simulation>.Entry> getSimulationRecords() {
		return _selectionModelSim.getItemRecords();
	}
	
	public void setSelectionModelSim(SelectionModel<Simulation> model) {
		_selectionModelSim = model;
		
		//If none of the subclasses set its specific action - set a general action.
		if(_selectionModelSim.getOnSelectItemAction() == null){
			_selectionModelSim.setOnSelectItemAction(new Closure.V2<Simulation, Boolean>() {
				@Override
				public void call(Simulation simulation, Boolean value) {
					getView().selectSimulation(simulation, value);
					//If simulation becomes active
					if(value){
						updateSimulationData();
					}else{
						removeSimulationData();
					}
				}
			});
		}
	}
	
	/**
	 * Saves the data specific to cyclic view presenter 
	 * (E.g tables and simulations which have been dropped to the view header)
	 * And the last selected table and simulation.
	 */
	@Override
	public void save(IMemento memento) {
		super.save(memento);
		// Tables
		IMemento group = memento.createChild("tables");
		for (SelectionModel<Table>.Entry entry : getTableRecords()){
			// save only the local (non-remote) tables
			if(!entry.remote){
				Table table = entry.item;
				group.createChild("local-table").putString("ref-uid", table.getUID());
			}
		}
		if (_selectionModelTbl.getSelected() != null) {
			memento.createChild("selected-table").putString("ref-uid", _selectionModelTbl.getSelected().getUID() );
		}

		//Simulations
		group = memento.createChild("simulations");
		for (SelectionModel<Simulation>.Entry entry : getSimulationRecords()){
			//Saves only the local (non-remote) tables
			if(!entry.remote){
				Simulation simulation = entry.item;
				group.createChild("simulation").putString("ref-uid", simulation.getUID());
			}
		}
		if(_selectionModelSim.getSelected() != null) {
			memento.createChild("selected-simulation").putString("ref-uid", _selectionModelSim.getSelected().getUID());
		}
		
		//Filters
		group = memento.createChild("filters");
		for(Filter filter :getView().filters() ){
			filter.save(group.createChild("filter"));
		}
		
		getView().save(memento.createChild("view"));
		//All the changes are saved - dirty flag should be reset.
		_dirtyFlag = false;
	}
	
	/**
	 * Restores the data specific to cyclic view presenter 
	 * (E.g tables and simulations which have been dropped to the view header)
	 * And the last selected table and simulation.
	 */
	@Override
	public void restore(IMemento memento, Context ctx) {	
		super.restore(memento, ctx);
		
		String ref;
		IMemento node;
		
		// clear the old data 
		//(If loading data after changing workspace there might be some old data from the previous ws)
		clearData();
		
		// restore local tables
		IMemento group = memento.getChild("tables");
		for(IMemento child :  group.getChildren("local-table")) {
			System.out.println("lookup table: "+child.getString("ref-uid"));
			Table table = ctx.get(child.getString("ref-uid"), Table.class);
			addTable(table, false, false, false);	
		}
		
		// restore selected table.
		node = memento.getChild("selected-table");
		if (node != null ) {
			Table table = ctx.get(node.getString("ref-uid"), Table.class);
			getSelectionModelTbl().itemSelected(table, true);
		}
		
		// restore the non-remote simulations
		group = memento.getChild("simulations");
		for (IMemento child : group.getChildren("simulation")) {
			Simulation sim = ctx.get(child.getString("ref-uid"), Simulation.class);
			if(sim != null) {
				getView().addSimulation(sim, false /*remote*/, false /* active */);
				getSelectionModelSim().addItem(sim, false /*remote*/, false /*active*/, false /*remoteActive*/);
			}
		}
		
		// restore the selected simulation.
		node = memento.getChild("selected-simulation");
		if (node != null) {
			ref = node.getString("ref-uid");
			Simulation sim = ctx.get(ref, Simulation.class);
			getSelectionModelSim().itemSelected(sim, true);
		}
		
		// restore local filters
		group = memento.getChild("filters");
		for(IMemento child :  group.getChildren("filter")) {
			Filter filter = new Filter();
			filter.restore(child, ctx);
			getView().addFilter(filter);
		}
		
		getView().restore(memento.getChild("view"), ctx);
		//If the dirty flag was set during restore - reset it back to false.
		_dirtyFlag = false;
	}
	
	/*
	 * Returns the dirty flag - which signals whether or not there were changes in the view.
	 * @return Boolean - the flag value.
	 */
	@Override
	public Boolean getDirtyFlag(){
		return _dirtyFlag;
	}
	
	/*
	 * Sets the dirty flag - which signals whether or not there were changes in the view.
	 * @param Boolean - the flag value.
	 */
	public void setDirtyFlag(Boolean flag){
		_dirtyFlag = flag;
	}
	
	
	/*
	 * For each view under the workspace - add the remote simulations inherited from the workspace.
	 * Then call the selectionModel to decide whether to select the simulation button or not.
	 * 
	 * @param - List<SelectionModel<Simulation>.Entry> list , list of all the simulations entries in the 
	 *          workspace.
	 */
	protected void setRemoteSimulations(List<SelectionModel<Simulation>.Entry> list) {
		for (SelectionModel<Simulation>.Entry record : list) {
			// infom the view but let the selection model determine if it should be active
			getView().addSimulation(record.item, true /*remote*/, false /* active */);
		}
		getSelectionModelSim().setRemoteItems(list);
	}
	
	/*
	 * Add a local simulation to the view. 
	 * (i.e. a simulation which was dropped directly to the current view). 
	 * Add it as the active simulation.
	 * 
	 * @param - Simulation: The simulation to add locally.
	 */
	protected void addLocalSimulation(Simulation simulation){
		getView().addSimulation(simulation, false /*remote*/, true /* active */);
		getSelectionModelSim().addItem(simulation, false /*remote*/, true /*active*/, false /*remoteActive*/);
	}
	
	/*
	 * Update the view data after a new simulation has been selected.
	 * Mainly for the different views to implement, depending on the data the specific view consumes.
	 */
	protected void updateSimulationData(){	
	}
	
	/*
	 * Update the view data after a simulation has been removed.
	 * Mainly for the different views to implement, depending on the data the specific view consumes.
	 */
	protected void removeSimulationData(){	
	}
	
	/*
	 * Add a remote simulation to the view.
	 * (i.e. a simulation which was inherited from the workspace.). 
	 * Add it as remote and non-active to the selection model. 
	 * The selection model decides if to select it or keep it non active, regarding to the other simulations in the view.
	 * 
	 * @param - Simulation: The simulation to add as a remote.
	 */
	private void addRemoteSimulation(Simulation simulation) {
		getView().addSimulation(simulation, true, /*remote*/ false /*active*/);
		getSelectionModelSim().addItem(simulation, true, /*remote*/ true, /*active*/ false /*remote active*/);
	}
	
	public void removeSimulation(Simulation simulation) {
		getSelectionModelSim().removeItem(simulation);
		getView().removeSimulation(simulation);
	}

	private void addListeners() {
		addNotificationHandler(CyclistNotifications.ADD_REMOTE_FILTER, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistFilterNotification notification = (CyclistFilterNotification) event;
				getView().remoteFilters().add(notification.getFilter());
			}
		});
		
		addNotificationHandler(CyclistNotifications.REMOVE_REMOTE_FILTER, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistFilterNotification notification = (CyclistFilterNotification) event;
				getView().remoteFilters().remove(notification.getFilter());
			}
		});
		
		addNotificationHandler(CyclistNotifications.SIMULATION_ADD, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistSimulationNotification notification = (CyclistSimulationNotification) event;
				addRemoteSimulation(notification.getSimulation());
			}
		});
		
		addNotificationHandler(CyclistNotifications.SIMULATION_REMOVE, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistSimulationNotification notification = (CyclistSimulationNotification) event;
				removeSimulation(notification.getSimulation());			
			}
		});
		
		
		addNotificationHandler(CyclistNotifications.SIMULATION_SELECTED, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistSimulationNotification notification = (CyclistSimulationNotification) event;
				getSelectionModelSim().selectItem(notification.getSimulation(), true);
			}
		});
		
		addNotificationHandler(CyclistNotifications.SIMULATION_UNSELECTED, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistSimulationNotification notification = (CyclistSimulationNotification) event;
				getSelectionModelSim().selectItem(notification.getSimulation(), false);
			}
		});
	}
	
	/*
	 * Finds a table in a given tables list list which matches the specified name and data source.
	 * @param: String tableName
	 * @param: String dataSource
	 * @param: List<Table> tablesList - the tables list to look for the specified table.
	 */
	private Table findTable(String tableName, String dataSource, List<Table> tablesList){
		if(tableName != null)
		{
			for(Table tbl:tablesList){
				if(tbl.getName().equals(tableName)){
					if(dataSource!=null && !dataSource.isEmpty()){
						if(tbl.getDataSource() != null && tbl.getDataSource().getUID().equals(dataSource)){
							return tbl;
						}
					//Find a table without a data source.
					} else if((dataSource == null || dataSource.isEmpty()) && tbl.getDataSource() == null){
						return tbl;
					}
				}
			}
		}
		return null;
	}
	
	/*
	 * Finds a simulation in a given simulations list, which matches the specified simulation id.
	 * @param : simulationAlias
	 * @return: Simulation. The simulation instance if found, null otherwise.
	 */
	private Simulation findSimulation(String simulationId, List<Simulation> simulationsList){
		if(simulationId != null && !simulationId.isEmpty())
		{
			for(Simulation sim: simulationsList){
				if(sim.getSimulationId().toString().equals(simulationId)){
						return sim;
				}
			}
		}
		return null;
	}
	
	/*
	 * Clears the old data from the tool bar.
	 * (E.g data that was inserted by another workspace etc.)
	 */
	private void clearData(){
		//Clear old simulations
		List<SelectionModel<Simulation>.Entry> simEntries = new ArrayList<SelectionModel<Simulation>.Entry>(getSelectionModelSim().getEntries());
		for(SelectionModel<Simulation>.Entry entry : simEntries){
			if(!entry.remote){
				removeSimulation(entry.item);
			}
		}
		
		//Clear old tables
		List<SelectionModel<Table>.Entry> tblEntries = new ArrayList<SelectionModel<Table>.Entry>(getSelectionModelTbl().getEntries());
		for(SelectionModel<Table>.Entry entry : tblEntries){
			if(!entry.remote){
				removeTable(entry.item);
			}
		}
		
	}
}
