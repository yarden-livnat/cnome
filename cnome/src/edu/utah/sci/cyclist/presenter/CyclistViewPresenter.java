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
import edu.utah.sci.cyclist.ui.CyclistView;
import edu.utah.sci.cyclist.ui.View;

public class CyclistViewPresenter extends ViewPresenter {
	private SelectionModel _selectionModel = new SelectionModel();
	
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
					getSelectionModel().tableSelected(table, active);
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
		}
	}
	
	public void onViewSelected(View view) {
		super.onViewSelected(view);
		
		Table table = getSelectionModel().getSelected();
		if (table != null)
			broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_FOCUS, table));
	}
	
	public void setRemoteTables(List<SelectionModel.Entry> list) {
		for (SelectionModel.Entry record : list) {
			// infom the view but let the selection model determine if it should be active
			getView().addTable(record.table, true /*remote*/, false /* active */);
			//getSelectionModel().addTable(record.table, true, false, record.active);
		}
		getSelectionModel().setRemoteTables(list);
	}
	
	
	public void addRemoteFilters(List<Filter> filters) {
		getView().remoteFilters().addAll(filters);
	}
	
	public void addTable(Table table, boolean remote, boolean active, boolean remoteActive) {
		getView().addTable(table, remote, false);
		getSelectionModel().addTable(table, remote, active, remoteActive);
	}
	
	
	public void removeTable(Table table) {
		getSelectionModel().removeTable(table);
		getView().removeTable(table);
	}
	
	public List<SelectionModel.Entry> getTableRecords() {
		return _selectionModel.getTableRecords();
	}
	
	public SelectionModel getSelectionModel() {
		return _selectionModel;
	}
	
	public void setSelectionModel(SelectionModel model) {
		_selectionModel = model;
		
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
