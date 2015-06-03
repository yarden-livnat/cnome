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
package edu.utah.sci.cyclist.core.presenter;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.ToolsLibrary;
import edu.utah.sci.cyclist.core.controller.IMemento;
import edu.utah.sci.cyclist.core.event.notification.CyclistFilterNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.core.event.notification.CyclistSimulationNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistViewNotification;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.event.notification.SimpleEventBus;
import edu.utah.sci.cyclist.core.event.notification.SimpleNotification;
import edu.utah.sci.cyclist.core.model.Context;
import edu.utah.sci.cyclist.core.model.Filter;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.tools.Tool;
import edu.utah.sci.cyclist.core.ui.View;
import edu.utah.sci.cyclist.core.ui.components.CyclistViewBase;
import edu.utah.sci.cyclist.core.ui.components.ViewBase;
import edu.utah.sci.cyclist.core.ui.views.BaseWorkspace;
import edu.utah.sci.cyclist.core.ui.views.FilterPanel;
import edu.utah.sci.cyclist.core.ui.views.VisWorkspace;

public class BaseWorkspacePresenter extends ViewPresenter implements WorkspacePresenter {

	private List<ViewPresenter> _presenters = new ArrayList<>();
	ObservableList<Tool> _tools = FXCollections.observableArrayList();
	private EventBus _localBus;
	private boolean _dirtyFlag = false;

	public BaseWorkspacePresenter(EventBus bus/*, Model model*/) {
		super(bus);
		build();
		_localBus = /*getWorkspace().isToplevel() ? getEventBus() : */ new SimpleEventBus();
		addListeners();
	}

	public BaseWorkspace getWorkspace() {
		return (BaseWorkspace) getView();
	}

	public void setView(View view) {
		super.setView(view);

		if (view instanceof VisWorkspace) {
			
			_tools.addListener(new ListChangeListener<Tool>(){
				@Override
				public void onChanged(ListChangeListener.Change<? extends Tool> newList) {
					setDirtyFlag(true);
				}
			});
			
			
			BaseWorkspace workspace = getWorkspace();


//			if (getWorkspace().isToplevel())
//				addToplevelListeners();

			workspace.setOnToolDrop(new Closure.V3<Tool, Double, Double>() {

				@Override
				public void call(Tool tool, Double x, Double y) {
					addTool(tool, x, y);
				}
			});
		}
	}

	public void setDirtyFlag(Boolean flag){
		_dirtyFlag = flag;
	}
	
	/*
	 * addTool
	 */
	public void addTool(Tool tool) {
		addTool(tool, 100, 100);
	}
	
	@Override
	public void save(IMemento memento) {
		super.save(memento);
		for (Tool tool : _tools){
			IMemento toolMemento = memento.createChild("Tool");
			toolMemento.putString("name", tool.getName());
			toolMemento.putString("id", tool.getId());
			toolMemento.putString("x", Double.toString(((Node)tool.getView()).getLayoutX()));
			toolMemento.putString("y", Double.toString(((Node)tool.getView()).getLayoutY()));
			toolMemento.putString("width", Double.toString(((Region)tool.getView()).getPrefWidth()));
			toolMemento.putString("height", Double.toString(((Region)tool.getView()).getPrefHeight()));
			tool.getPresenter(_localBus).save(toolMemento);
			//Reset the dirty flag after save.
			setDirtyFlag(false);
		}
	}
	
	@Override
	public void restore(IMemento memento, Context ctx) {	
		super.restore(memento, ctx);
		if(memento != null){
			IMemento[] tools = memento.getChildren("Tool");
			for(IMemento toolMemento : tools)
			{
				//Get the location
				Double x = Double.parseDouble(toolMemento.getString("x"));
				Double y = Double.parseDouble(toolMemento.getString("y"));
				
				double width = Double.parseDouble(toolMemento.getString("width"));
				double height = Double.parseDouble(toolMemento.getString("height"));
				
				String toolName = toolMemento.getString("name");
				
				try {
					Tool tool = ToolsLibrary.createTool(toolName);
					addTool(tool,x,y);
					((Region)tool.getView()).setPrefSize(width, height);
					tool.getPresenter(_localBus).restore(toolMemento, ctx);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		//Reset the dirty flag after restore.
		setDirtyFlag(false);
	}
	
	/*
	 * Clears all the existing views and filters of the workspace.
	 * (For example when changing between workspaces).
	 */
	public void clearWorkspace(){
		removeOldViews();
	}
	
	private Presenter addTool(Tool tool, double x, double y) {
		ViewBase view = (ViewBase) tool.getView();
		ViewPresenter presenter = tool.getPresenter(getLocalEventBus());
		
		_tools.add(tool);
		
		return addTool(view, presenter, x, y);
	}
	
	private Presenter addTool(ViewBase view, ViewPresenter presenter, double x, double y) {
		view.setLayoutX(x);
		view.setLayoutY(y);
		getWorkspace().addView(view);

		if (presenter != null) {        
			_presenters.add(presenter);
			presenter.setView(view);   
		}
	
		return presenter;
	}

	private void build() {
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
				removeView(id);
			}
		});

		addLocalNotificationHandler(CyclistNotifications.VIEW_SELECTED, new CyclistNotificationHandler() {
			@Override
			public void handle(CyclistNotification event) {
				View view = ((CyclistViewNotification)event).getView();
				getWorkspace().selectView(view);
			}
		});

		addLocalNotificationHandler(CyclistNotifications.DUPLICATE_VIEW, new CyclistNotificationHandler() {
			@Override
			public void handle(CyclistNotification event) {
				Presenter presenter = (Presenter) event.getSource();
				if (presenter instanceof ChartPresenter) {
					duplicateView((ChartPresenter) presenter);
				}
			}

		});
	 }

	 private void duplicateView(ViewPresenter presenter) {
		 ViewBase view = (ViewBase) presenter.getView();

		 ViewBase newView = view.clone();
		 newView.setLayoutX( view.getLayoutX()+10);
		 newView.setLayoutY( view.getLayoutY()+10);
		 getWorkspace().addView(newView);

		 ViewPresenter newPresenter = presenter.clone(newView);
		 _presenters.add(newPresenter);
	 }


	 @Override
	 public void onViewSelected(View view) {
		 super.onViewSelected(view);
		 if (getWorkspace().isToplevel())
			 broadcast(getLocalEventBus(), (new CyclistViewNotification(CyclistNotifications.VIEW_SELECTED, view)));
	 }

	 private EventBus getLocalEventBus() {
		 return _localBus;
	 }
	 
	 /*
	  * Removes a tool from the tools list according to its presenter id.
	  * @parameter: String presenterId. 
	  */
	 private void removeTool(String presenterId){
		 for(Tool tool : _tools){
			 if(tool.getPresenter(_localBus).getId().equals(presenterId)){
				 _tools.remove(tool);
				 break;
				 
			 }
		 }
	 }
	
	 
	 /*
	  * Removes a view from the workspace.
	  * @param: String id - the id of the view to be removed.
	  */
	 private void removeView(String id){
		 for (ViewPresenter presenter : _presenters) {
				if (presenter.getId().equals(id)) {
					_presenters.remove(presenter);
					ViewBase view = (ViewBase)presenter.getView();
					getWorkspace().removeView(view);
					removePresenterIdFromEventBus(id);
					break;
				}
		}
		removeTool(id);
	 }
	 
	 /*
	  * Clears old views before restoring the new ones.
	  * (For example when changing the work directory - 
	  * have to clear the old work directory tools, before loading the tools of the current one.
	  */
	 private void removeOldViews(){
		 List<ViewPresenter> presenters = new ArrayList<>(_presenters);
		 for (ViewPresenter presenter : presenters) {
			 removeView(presenter.getId());
			 //If the presenter itself is a workspace - clear it's contents as well.
			 if(presenter instanceof BaseWorkspacePresenter){
				 ((BaseWorkspacePresenter) presenter).clearWorkspace();
			 }
		 }
	 }
	

	 /* 
	  * Calls the local event bus to remove all the handlers of the specified target.
	  * @param String target - the target id to be removed.
	  */
	  private void removePresenterIdFromEventBus(String target){
		 _localBus.removeAllTargetHandlers(target);
	 }

	 public void addLocalNotificationHandler(String type, CyclistNotificationHandler handler) {
		 _localBus.addHandler(type, getId(), handler);
	 }
	 
	 /*
	 * Returns the dirty flag - which signals whether or not there were changes in the view.
	 * @return Boolean - the flag value.
	 */
	 @Override
	 public Boolean getDirtyFlag(){
		if(super.getDirtyFlag()){
			return true;
		}
		for(Tool tool: _tools){
			if(tool.getPresenter(_localBus).getDirtyFlag()){
				return true;
			}
		}
		return false;
	}

}