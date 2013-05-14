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

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.notification.SimpleNotification;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.View;

public class PresenterBase implements Presenter {
	private static int _idCounter = 0;
	private String _id;
	
	private View _view;
	private EventBus _eventBus;
	
	private SelectionModel _selectionModel = new SelectionModel();
	
	public PresenterBase(EventBus bus) {
		_id = "presenter"+_idCounter++;
		_eventBus = bus;
	}
	
	@Override
	public String getId() {
		return _id;
	}
	
	@Override
	public EventBus getEventBus() {
		return _eventBus;
	}
	
	@Override
	public void setView(View view) {
		_view = view;
		if (_view != null) {
			view.onCloseProperty().set(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					broadcast(new SimpleNotification(CyclistNotifications.REMOVE_VIEW, getId()));
				}
			});
			
			_view.setOnTableSelected(new Closure.V2<Table, Boolean>() {
				
				@Override
				public void call(Table table, Boolean active) {
					getSelectionModel().tableSelected(table, active);
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
	public void addNotificationHandler(String type, CyclistNotificationHandler handler) {
		_eventBus.addHandler(type, _id, handler);
	}
	
	@Override
	public void broadcast(CyclistNotification notification) {
		notification.setSource(this);
		_eventBus.notify(notification);
	}

	@Override
	public void setRemoteTables(List<TableRecord> list) {
		for (TableRecord record : list) {
			// infom the view but let the selection model determine if it should be active
			getView().addTable(record.table, true /*remote*/, false /* active */);
			_selectionModel.addTable(record.table, true, false, record.active);
		}
	}
	
	@Override
	public void addTable(Table table, boolean remote, boolean active, boolean remoteActive) {
		getView().addTable(table, remote, false);
		getSelectionModel().addTable(table, remote, active, remoteActive);
	}
	
	
	@Override
	public void removeTable(Table table) {
		getView().removeTable(table);
		getSelectionModel().removeTable(table);
	}
	
	@Override
	public List<TableRecord> getTableRecords() {
		return _selectionModel.getTableRecords();
	}
	
	public class TableRecord {
		Table table;
		boolean active;
		
		public TableRecord(Table table, boolean active) {
			this.table = table;
			this.active = active;
		}
	}
	
	public class SelectionModel {
		private List<Entry> _list = new ArrayList<Entry>();
		private int _remotes = 0;
		
//		public int getRemotes() {
//			return _remotes;
//		}
		
		public void addTable(Table table, boolean remote, boolean active, boolean remoteActive) {
			_list.add(new Entry(table, remote, active, remoteActive));
			if (remote) _remotes++;
		}
		
		public void removeTable(Table table) {
			Entry entry = getEntry(table);
			if (entry != null) {
				_list.remove(entry);
			}
		}
		
		public Entry getEntry(Table table) {
			for (Entry entry : _list)
				if (entry.table == table)
					return entry;
			return null;
		}
		
		public List<TableRecord> getTableRecords() {
			List<TableRecord> records = new ArrayList<>();
			for (Entry entry : _list) {
				records.add(new TableRecord(entry.table, entry.active));
			}
			return records;
		}
		
		public List<Entry> getRemotes() {
			List<Entry> records = new ArrayList<>();
			for (Entry entry : _list)
				if (entry.remote)
					records.add(entry);
			return records;
		}
		
		public void selectTable(Table table, boolean active) {
			// ignore here.
		}
		
		public void tableSelected(Table table, boolean active) {
			// ignore here
		}
		
		public class Entry {
			Table table;
			boolean remote;
			boolean active;
			boolean remoteActive;
			
			public Entry(Table table, boolean remote, boolean active, boolean remoteActive) {
				this.table = table;
				this.remote = remote;
				this.active = active;
				this.remoteActive = remoteActive;
			}
		}
	}


	
}
