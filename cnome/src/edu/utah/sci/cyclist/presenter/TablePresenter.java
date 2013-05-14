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

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.View;

public class TablePresenter extends PresenterBase {
	
	public TablePresenter(EventBus bus) {
		super(bus);
		
		setSelectionModel(new SingleSelection());
		addNotificationHandlers();
	}

	public void setView(View view) {
		super.setView(view);
		
		getView().setOnTableDrop(new Closure.V1<Table>() {
			
			@Override
			public void call(Table table) {
				addTable(table, false /* remote */, true /* active */, false /* remoteActive */);
			}
		});
	}
	
	public void addNotificationHandlers() {
		addNotificationHandler(CyclistNotifications.DATASOURCE_ADD, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistTableNotification notification = (CyclistTableNotification) event;
				
				addTable(notification.getTable(), true /*remote*/, false /* active */, false /* remoteActive */);			
			}
		});
		
		addNotificationHandler(CyclistNotifications.DATASOURCE_REMOVE, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistTableNotification notification = (CyclistTableNotification) event;
				removeTable(notification.getTable());			
			}
		});
		
		addNotificationHandler(CyclistNotifications.DATASOURCE_SELECTED, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistTableNotification notification = (CyclistTableNotification) event;
				getSelectionModel().selectTable(notification.getTable(), true);
			}
		});
		
		addNotificationHandler(CyclistNotifications.DATASOURCE_UNSELECTED, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistTableNotification notification = (CyclistTableNotification) event;
				getSelectionModel().selectTable(notification.getTable(), false);			
			}
		});
	}
	
	
	public class SingleSelection extends SelectionModel {
		private Entry _current = null;
		
		@Override
		public void addTable(Table table, boolean remote, boolean active, boolean remoteActive) {
			super.addTable(table, remote, false, remoteActive);
			
			if (active) {
				if (_current == null) {
					selectTable(table, true);
				} else if (!remote) {
					selectTable(table, true);
				} else {
					// select a remote only if no local table is active
				}
			}
		}
		
		public void selectTable(Table table, boolean active) {
			System.out.println("selectTable: "+table.getName()+"  active:"+active);
			Entry entry = getEntry(table);
			if (entry.active == active) {
				// ignore
			} else if (active) {
				if (_current != null) {
					if (!_current.remote && entry.remote) {
						// ignore. 
						// switch from a local to a remote on on user explicit request (tableSelected) 
						entry.remoteActive = true;
						return;
					} else {
						_current.active = false;
						getView().selectTable(_current.table, false);
					}
				}
				_current = entry;
				entry.active = true;
				getView().selectTable(table, true);
			} else /* unselect */ {
				entry.active = false;
				if (entry == _current) {
					_current = null;
				}
				getView().selectTable(table, false);
				
				// TODO: select a default one from the remote?
			}
		}
		
		@Override
		public void tableSelected(Table table, boolean active) {
			System.out.println("tableSelected: "+table.getName()+"  active:"+active);
			Entry entry = getEntry(table);
			if (entry.active == active) {
				// ignore
			} else if (active) {
				if (_current != entry && _current != null) {
					_current.active = false;
					getView().selectTable(_current.table, false);
				}
				_current = entry;
				_current.active = true;
			} else /* not active */ {
				_current.active = false;
				_current = null;
				
				// check if there is a remoteActive
				for (Entry remoteEntry : getRemotes()) {
					if (remoteEntry.remoteActive) {
						selectTable(remoteEntry.table, true);
						break;
					}
				}
			}
		}
	}
}
