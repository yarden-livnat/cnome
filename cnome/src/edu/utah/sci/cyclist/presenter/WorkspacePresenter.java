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

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.notification.SimpleNotification;
import edu.utah.sci.cyclist.model.Model;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.View;
import edu.utah.sci.cyclist.view.components.ViewBase;
import edu.utah.sci.cyclist.view.components.Workspace;
import edu.utah.sci.cyclist.view.tool.GenericTool;
import edu.utah.sci.cyclist.view.tool.Tool;

public class WorkspacePresenter extends PresenterBase {

	private Workspace _workspace;
	private List<Presenter> _presenters = new ArrayList<>();
	
	public WorkspacePresenter(EventBus bus, Model model) {
		super(bus);		
		addListeners();
	}
	
	public void setView(View workspace) {
		if (workspace instanceof Workspace) {
			_workspace = (Workspace) workspace;
			
			_workspace.setOnToolDrop(new Closure.V3<Tool, Double, Double>() {

				@Override
				public void call(Tool tool, Double x, Double y) {
					addTool(tool, x, y);
				}
			});
			
			_workspace.setOnTableDrop(new Closure.V1<Table>() {

				@Override
				public void call(Table table) {
					_workspace.addTable(table, true, false);
					broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_ADD, table));
					_workspace.selectTable(table, true);
				}
				
			});
			
			_workspace.setOnTableSelected(new Closure.V1<Table>() {

				@Override
				public void call(Table table) {
					broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_SELECTED, table));				
				}
				
			});
			
			_workspace.setOnShowTable(new Closure.V3<Table, Double, Double>() {

				@Override
				public void call(Table table, Double x, Double y) {
					TablePresenter presenter = (TablePresenter) addTool(new GenericTool(), x, y);
					presenter.addTable(table, true);
				}
			});
		}
	}

	private Presenter addTool(Tool tool, double x, double y) {
		ViewBase view = (ViewBase) tool.getView();
		view.setTranslateX(x);
		view.setTranslateY(y);
		_workspace.addView(view);
		
		Presenter presenter = tool.getPresenter(getEventBus());
		if (presenter != null) {	
			_presenters.add(presenter);
			presenter.setView(view);	
			presenter.setTables(_workspace.getLocalTables(), _workspace.getSelectedTable());
		}
		
		return presenter;
	}
	
	private void addListeners() {
		addNotificationHandler(CyclistNotifications.REMOVE_VIEW, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				String id = ((SimpleNotification)event).getMsg();
				for (Presenter presenter : _presenters) {
					if (presenter.getId().equals(id)) {
						_presenters.remove(presenter);
						_workspace.removeView((ViewBase)presenter.getView());
						break;
					}
				}
				
			}
		});
	}
	
}
