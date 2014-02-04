package edu.utah.sci.cyclist.presenter;

import java.util.List;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.event.notification.CyclistFilterNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.model.Simulation;
import edu.utah.sci.cyclist.ui.CyclistView;
import edu.utah.sci.cyclist.ui.View;

public class CyclistViewPresenter extends ViewPresenter {
	private SelectionModel<Table> _selectionModelTbl = new SelectionModel<Table>();
	
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
					getSelectionModel().itemSelected(table, active);
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
			getView().setOnSimulationDrop(new Closure.V1<Simulation>(){
				@Override
				public void call(Simulation simulation) {
//					getSelectionModel().addSimulation(simulation);
				}
			});
		}
	}
	
	public void onViewSelected(View view) {
		super.onViewSelected(view);
		
		Table table = getSelectionModel().getSelected();
		if (table != null)
			broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_FOCUS, table));
	}
	
	public void setRemoteTables(List<SelectionModel<Table>.Entry> list) {
		for (SelectionModel<Table>.Entry record : list) {
			// infom the view but let the selection model determine if it should be active
			getView().addTable((Table)record.item, true /*remote*/, false /* active */);
			//getSelectionModel().addTable(record.table, true, false, record.active);
		}
		getSelectionModel().setRemoteItems(list);
	}
	
	
	public void addRemoteFilters(List<Filter> filters) {
		getView().remoteFilters().addAll(filters);
	}
	
	public void addTable(Table table, boolean remote, boolean active, boolean remoteActive) {
		getView().addTable(table, remote, false);
		getSelectionModel().addItem(table, remote, active, remoteActive);
	}
	
	
	public void removeTable(Table table) {
		getSelectionModel().removeItem(table);
		getView().removeTable(table);
	}
	
	public void addSimulation(Simulation simulation, boolean remote) {
		getView().addSimulation(simulation, remote, false);
//		getSelectionModel().addTable(table, remote, active, remoteActive);
	}
	
	public List<SelectionModel<Table>.Entry> getTableRecords() {
		return _selectionModelTbl.getItemRecords();
	}
	
	public SelectionModel<Table> getSelectionModel() {
		return _selectionModelTbl;
	}
	
	public void setSelectionModel(SelectionModel<Table> model) {
		_selectionModelTbl = model;
		
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
	}
}
