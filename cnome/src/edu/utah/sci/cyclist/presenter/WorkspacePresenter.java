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

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.event.notification.CyclistFilterNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.CyclistViewNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.notification.SimpleNotification;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Model;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.View;
import edu.utah.sci.cyclist.ui.components.ViewBase;
import edu.utah.sci.cyclist.ui.tools.TableTool;
import edu.utah.sci.cyclist.ui.tools.Tool;
import edu.utah.sci.cyclist.ui.views.FilterPanel;
import edu.utah.sci.cyclist.ui.views.Workspace;

public class WorkspacePresenter extends PresenterBase {

	private List<Presenter> _presenters = new ArrayList<>();
	private List<FilterPresenter> _filterPresenters = new ArrayList<>();
	
	public WorkspacePresenter(EventBus bus, Model model) {
		super(bus);		
		addListeners();
	}
	
	public Workspace getWorkspace() {
		return (Workspace) getView();
	}
	
	public void setView(View view) {
		super.setView(view);
		
		if (view instanceof Workspace) {
			Workspace workspace = getWorkspace();
			workspace = (Workspace) workspace;
			
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
					broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_ADD, table));
					getSelectionModel().selectTable(table, true);
				}
				
			});
			
			workspace.setOnTableRemoved(new Closure.V1<Table>() {
				@Override
				public void call(Table table) {
					removeTable(table);
					broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_REMOVE, table));
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
		
		Presenter presenter = tool.getPresenter(getEventBus());
		if (presenter != null) {	
			_presenters.add(presenter);
			presenter.setView(view);	
			presenter.setRemoteTables(getTableRecords());
		}
		
		return presenter;
	}
	
	
	/*
	 * addListeners
	 */
	private void addListeners() {
		addNotificationHandler(CyclistNotifications.REMOVE_VIEW, new CyclistNotificationHandler() {	
			@Override
			public void handle(CyclistNotification event) {
				String id = ((SimpleNotification)event).getMsg();
				for (Presenter presenter : _presenters) {
					if (presenter.getId().equals(id)) {
						_presenters.remove(presenter);
						getWorkspace().removeView((ViewBase)presenter.getView());
						break;
					}
				}
				
			}
		});
		
		addNotificationHandler(CyclistNotifications.VIEW_SELECTED, new CyclistNotificationHandler() {
			@Override
			public void handle(CyclistNotification event) {
				View view = ((CyclistViewNotification)event).getView();
				getWorkspace().selectView(view);
			}
		});
		
		addNotificationHandler(CyclistNotifications.SHOW_FILTER, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				Filter filter = ((CyclistFilterNotification)event).getFilter();
				
				FilterPresenter presenter = getFilterPresenter(filter);
				if (presenter == null) {
					FilterPanel panel = new FilterPanel(filter);
					 presenter = new FilterPresenter(getEventBus());
					 presenter.setPanel(panel);
					 getWorkspace().addPanel(panel);
					 _filterPresenters.add(presenter);
				} else {
					getWorkspace().showPanel(presenter.getPanel(), true);
				}
			}
		});
		
		addNotificationHandler(CyclistNotifications.HIDE_FILTER, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				Filter filter = ((CyclistFilterNotification)event).getFilter();
				FilterPresenter presenter = getFilterPresenter(filter);
				if (presenter != null) {
					getWorkspace().showPanel(presenter.getPanel(), false);
				}
			}
		});
		
		addNotificationHandler(CyclistNotifications.REMOVE_FILTER, new CyclistNotificationHandler() {
			
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
				
		SelectionModel selectionModel = new SingleSelection();
		selectionModel.setOnSelectTableAction(new Closure.V2<Table, Boolean>() {

			@Override
			public void call(Table table, Boolean activate) {
				getView().selectTable(table, activate);	
				String msg = activate ? CyclistNotifications.DATASOURCE_SELECTED : CyclistNotifications.DATASOURCE_UNSELECTED;
				broadcast(new CyclistTableNotification(msg, table));
			}
		
		});
		
		setSelectionModel(selectionModel);
	}
	
	private FilterPresenter getFilterPresenter(Filter filter) {
		for (FilterPresenter p : _filterPresenters) {
			if (p.getFilter() == filter) 
				return p;
		}
		return null;
	}
}
