package edu.utah.sci.cyclist.core.presenter;

import java.util.List;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.core.event.notification.CyclistFilterNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.core.event.notification.CyclistSimulationNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.model.Filter;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.ui.CyclistView;
import edu.utah.sci.cyclist.core.ui.View;

public class CyclistViewPresenter extends ViewPresenter {
	private SelectionModel<Table> _selectionModelTbl = new SelectionModel<Table>();
	private SelectionModel<Simulation> _selectionModelSim = new SelectionModel<Simulation>();
	
	public CyclistViewPresenter(EventBus bus) {
		super(bus);
		
		addListeners();
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
				}
			});
			
			getView().setOnSimulationSelectedAction(new Closure.V2<Simulation, Boolean>() {
				
				@Override
				public void call(Simulation simulation, Boolean active) {
					getSelectionModelSim().itemSelected(simulation, active);
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
					broadcast(new CyclistFilterNotification(CyclistNotifications.SHOW_FILTER, filter));
				}
			});
			
			getView().setOnRemoveFilter(new Closure.V1<Filter>() {
				@Override
				public void call(Filter filter) {
					broadcast(new CyclistFilterNotification(CyclistNotifications.REMOVE_FILTER, filter));
				}
			});
			
			//If none of the subclasses has set its specific action for simulation dropping - set a general action.
			if(getView().getOnSimulationDrop() == null){
				getView().setOnSimulationDrop(new Closure.V1<Simulation>(){
					@Override
					public void call(Simulation simulation) {
						addLocalSimulation(simulation);
					}
				});
			}
			//If none of the subclasses has set its specific action for simulation removal - set a general action.
			if(getView().getOnSimulationRemoved() == null){
				getView().setOnSimulationRemoved(new Closure.V1<Simulation>(){
					@Override
					public void call(Simulation simulation) {
						getSelectionModelSim().removeItem(simulation);
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
	}
	
	
	public void removeTable(Table table) {
		getSelectionModelTbl().removeItem(table);
		getView().removeTable(table);
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
				}
			
			});
		}
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
		getSelectionModelSim().addItem(simulation, false /*remote*/, true /*active*/, false /*remoteActive*/);
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
}
