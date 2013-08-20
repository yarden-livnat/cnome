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

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ListChangeListener;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.event.notification.CyclistFilterNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.CyclistViewNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.notification.SimpleEventBus;
import edu.utah.sci.cyclist.event.notification.SimpleNotification;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.View;
import edu.utah.sci.cyclist.ui.components.ViewBase;
import edu.utah.sci.cyclist.ui.tools.TableTool;
import edu.utah.sci.cyclist.ui.tools.Tool;
import edu.utah.sci.cyclist.ui.views.FilterPanel;
import edu.utah.sci.cyclist.ui.views.Workspace;

public class WorkspacePresenter extends ViewPresenter {

	private List<ViewPresenter> _presenters = new ArrayList<>();
	private List<FilterPresenter> _filterPresenters = new ArrayList<>();
	private EventBus _localBus;
	
	public WorkspacePresenter(EventBus bus/*, Model model*/) {
		super(bus);
		build();
		_localBus = /*getWorkspace().isToplevel() ? getEventBus() : */ new SimpleEventBus();
		addListeners();
	}
	
	public Workspace getWorkspace() {
		return (Workspace) getView();
	}
	
	public void setView(View view) {
		super.setView(view);
		
		if (view instanceof Workspace) {
			Workspace workspace = getWorkspace();
			

			if (getWorkspace().isToplevel())
				addToplevelListeners();

			workspace.setOnToolDrop(new Closure.V3<Tool, Double, Double>() {

				@Override
				public void call(Tool tool, Double x, Double y) {
					addTool(tool, x, y);
				}
			});
			
			workspace.setOnTableDrop(new Closure.V1<Table>() {

				@Override
				public void call(Table table) {
					addTable(table, false /*remote*/, false /* active */, false /* remoteActive */);
					broadcast(getLocalEventBus(), new CyclistTableNotification(CyclistNotifications.DATASOURCE_ADD, table));
					getSelectionModel().selectTable(table, true);
				}
				
			});
			
			workspace.setOnTableRemoved(new Closure.V1<Table>() {
				@Override
				public void call(Table table) {
					removeTable(table);
					broadcast(getLocalEventBus(), new CyclistTableNotification(CyclistNotifications.DATASOURCE_REMOVE, table));
					getSelectionModel().removeTable(table);
				}
			});
			
			workspace.setOnShowTable(new Closure.V3<Table, Double, Double>() {

				@Override
				public void call(Table table, Double x, Double y) {
					TablePresenter presenter = (TablePresenter) addTool(new TableTool(), x, y);
					presenter.addTable(table, false /* remote */, true /* active */, false /* remoteActive */);
				}
			});
			
			workspace.filters().addListener(new ListChangeListener<Filter>() {

				@Override
				public void onChanged(ListChangeListener.Change<? extends Filter> change) {
					while (change.next()) {
						for (Filter filter : change.getRemoved()) {
							broadcast(getLocalEventBus(), new CyclistFilterNotification(CyclistNotifications.REMOVE_REMOTE_FILTER, filter));
						}
						for (Filter filter : change.getAddedSubList()) {
							broadcast(getLocalEventBus(), new CyclistFilterNotification(CyclistNotifications.ADD_REMOTE_FILTER, filter));
						}
					}
				}				
			});
			
			workspace.remoteFilters().addListener(new ListChangeListener<Filter>() {

				@Override
				public void onChanged(ListChangeListener.Change<? extends Filter> change) {
					while (change.next()) {
						for (Filter filter : change.getRemoved()) {
							broadcast(getLocalEventBus(), new CyclistFilterNotification(CyclistNotifications.REMOVE_REMOTE_FILTER, filter));
						}
						for (Filter filter : change.getAddedSubList()) {
							broadcast(getLocalEventBus(), new CyclistFilterNotification(CyclistNotifications.ADD_REMOTE_FILTER, filter));
						}
					}
				}				
			});
			
			workspace.setOnShowFilter(new Closure.V1<Filter>() {
				@Override
				public void call(Filter filter) {
					broadcast(getLocalEventBus(), new CyclistFilterNotification(CyclistNotifications.SHOW_FILTER, filter));
				}
			});
		}
	}

	/*
	 * addTool
	 */
	private Presenter addTool(Tool tool, double x, double y) {
		ViewBase view = (ViewBase) tool.getView();
		view.setTranslateX(x);
		view.setTranslateY(y);
		getWorkspace().addView(view);
		
		ViewPresenter presenter = tool.getPresenter(getLocalEventBus());
		if (presenter != null) {	
			_presenters.add(presenter);
			presenter.setView(view);	
			presenter.setRemoteTables(getTableRecords());
			presenter.addRemoteFilters(getWorkspace().filters());
			presenter.addRemoteFilters(getWorkspace().remoteFilters());
		}
		
		return presenter;
	}
	
	private void build() {
		
		SelectionModel selectionModel = new SingleSelection();
		selectionModel.setOnSelectTableAction(new Closure.V2<Table, Boolean>() {

			@Override
			public void call(Table table, Boolean activate) {
				getView().selectTable(table, activate);	
				String msg = activate ? CyclistNotifications.DATASOURCE_SELECTED : CyclistNotifications.DATASOURCE_UNSELECTED;
				broadcast(getLocalEventBus(), new CyclistTableNotification(msg, table));
			}
		
		});
		
		setSelectionModel(selectionModel);
	}
	
	
	
	/*
	 * addListeners
	 */
	private void addListeners() {
		
		// local notifications
		addLocalNotificationHandler(CyclistNotifications.REMOVE_VIEW, new CyclistNotificationHandler() {	
			@Override
			public void handle(CyclistNotification event) {
				String id = ((SimpleNotification)event).getMsg();
				for (ViewPresenter presenter : _presenters) {
					if (presenter.getId().equals(id)) {
						_presenters.remove(presenter);
						getWorkspace().removeView((ViewBase)presenter.getView());
						break;
					}
				}
				
			}
		});
		
		addLocalNotificationHandler(CyclistNotifications.VIEW_SELECTED, new CyclistNotificationHandler() {
			@Override
			public void handle(CyclistNotification event) {
				View view = ((CyclistViewNotification)event).getView();
				getWorkspace().selectView(view);
				if (view instanceof ViewBase) {
					ViewBase base = (ViewBase) view;
					List<Filter> f1 = base.filters();
					List<Filter> f2 = base.remoteFilters();
					for (FilterPresenter p : _filterPresenters) {
						Filter f = p.getFilter();
						p.highlight(f1.contains(f) || f2.contains(f));
					}
				}
			}
		});
		
		addLocalNotificationHandler(CyclistNotifications.SHOW_FILTER, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				Filter filter = ((CyclistFilterNotification)event).getFilter();
				
				FilterPresenter presenter = getFilterPresenter(filter);
				if (presenter == null) {
					FilterPanel panel = new FilterPanel(filter);
					 presenter = new FilterPresenter(getLocalEventBus());
					 presenter.setPanel(panel);
					 getWorkspace().addPanel(panel);
					 _filterPresenters.add(presenter);
				} else {
					getWorkspace().showPanel(presenter.getPanel(), true);
				}
			}
		});
		
		addLocalNotificationHandler(CyclistNotifications.HIDE_FILTER, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				Filter filter = ((CyclistFilterNotification)event).getFilter();
				FilterPresenter presenter = getFilterPresenter(filter);
				if (presenter != null) {
					getWorkspace().showPanel(presenter.getPanel(), false);
				}
			}
		});
		
		addLocalNotificationHandler(CyclistNotifications.REMOVE_FILTER, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				Filter filter = ((CyclistFilterNotification)event).getFilter();
				FilterPresenter presenter = getFilterPresenter(filter);
				if (presenter != null) {
					_filterPresenters.remove(presenter);
					getWorkspace().removePanel(presenter.getPanel());
				}
			}
		});
				
		
		// parent notifications
		addNotificationHandler(CyclistNotifications.REMOVE_REMOTE_FILTER, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				broadcast(getLocalEventBus(), event);
			}
		});
		
		addNotificationHandler(CyclistNotifications.ADD_REMOTE_FILTER, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				broadcast(getLocalEventBus(), event);
			}
		});
		
	}
	
	private void addToplevelListeners() {
		addLocalNotificationHandler(CyclistNotifications.DATASOURCE_FOCUS, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				broadcast(event);
			}
		});
	}
	
	@Override
	public void onViewSelected(View view) {
		super.onViewSelected(view);
		if (getWorkspace().isToplevel())
			broadcast(getLocalEventBus(), (new CyclistViewNotification(CyclistNotifications.VIEW_SELECTED, view)));
	}
	
	private FilterPresenter getFilterPresenter(Filter filter) {
		for (FilterPresenter p : _filterPresenters) {
			if (p.getFilter() == filter) 
				return p;
		}
		return null;
	}
	
	private EventBus getLocalEventBus() {
		return _localBus;
	}
	
	public void addLocalNotificationHandler(String type, CyclistNotificationHandler handler) {
		_localBus.addHandler(type, getId(), handler);
	}
}
