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
package edu.utah.sci.cyclist.presenter;

import org.mo.closure.v1.Closure;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import edu.utah.sci.cyclist.event.notification.CyclistFilterNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.CyclistViewNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.notification.SimpleNotification;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.View;

public class ViewPresenter extends PresenterBase{

	private View _view;
	
	
	private SelectionModel _selectionModel = new SelectionModel();
	
	public ViewPresenter(EventBus bus) {
		super(bus);
		
		addListeners();
	}
	
	public void setView(View view) {
		_view = view;
		if (_view != null) {
			view.onCloseProperty().set(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					broadcast(new SimpleNotification(CyclistNotifications.REMOVE_VIEW, getId()));
				}
			});
			
			_view.setOnTableSelectedAction(new Closure.V2<Table, Boolean>() {
				
				@Override
				public void call(Table table, Boolean active) {
					getSelectionModel().tableSelected(table, active);
				}
			});
			
			_view.setOnSelectAction(new Closure.V0() {
				@Override
				public void call() {
					broadcast(new CyclistViewNotification(CyclistNotifications.VIEW_SELECTED, _view));
					
					Table table = getSelectionModel().getSelected();
					if (table != null)
						broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_FOCUS, table));
				}
			});
			
			_view.setOnShowFilter(new Closure.V1<Filter>() {
				@Override
				public void call(Filter filter) {
					broadcast(new CyclistFilterNotification(CyclistNotifications.SHOW_FILTER, filter));
				}
			});
		}
	}

	public View getView() {
		return _view;
	}

	public SelectionModel getSelectionModel() {
		return _selectionModel;
	}
	
	public void setSelectionModel(SelectionModel model) {
		_selectionModel = model;
		
	}


	public void setRemoteTables(List<SelectionModel.Entry> list) {
		for (SelectionModel.Entry record : list) {
			// infom the view but let the selection model determine if it should be active
			getView().addTable(record.table, true /*remote*/, false /* active */);
//			getSelectionModel().addTable(record.table, true, false, record.active);
		}
		getSelectionModel().setRemoteTables(list);
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
