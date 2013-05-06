package edu.utah.sci.cyclist.presenter;



import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.ui.CyclistDropEvent;
import edu.utah.sci.cyclist.model.Model;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.View;
import edu.utah.sci.cyclist.view.components.ViewBase;
import edu.utah.sci.cyclist.view.components.Workspace;
import edu.utah.sci.cyclist.view.tool.Tool;
import edu.utah.sci.cyclist.view.tool.Tools;

public class WorkspacePresenter extends PresenterBase {

	private Workspace _workspace;
	private Model _model;
	private List<Presenter> _presenters = new ArrayList<>();
	
	public WorkspacePresenter(EventBus bus, Model model) {
		super(bus);
		_model = model;
	}
	
	public void setView(View workspace) {
		if (workspace instanceof Workspace) {
			_workspace = (Workspace) workspace;
			
			_workspace.setOnDatasourceAction(new EventHandler<CyclistDropEvent>() {
				
				@Override
				public void handle(CyclistDropEvent event) {
					Table table = _model.getTable(event.getName());
					_workspace.addTable(table);
					broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_ADD, table));
				}
			});
			
			_workspace.setOnToolDrop(new EventHandler<CyclistDropEvent>() {
				
				@Override
				public void handle(CyclistDropEvent event) {
					try {
						String name = event.getName();
						Tool tool = Tools.getTool(name);
						ViewBase view = (ViewBase) tool.getView();
						view.setTranslateX(event.getX());
						view.setTranslateY(event.getY());
						_workspace.addView(view);
						
						Presenter presenter = tool.getPresenter(getEventBus());
						if (presenter != null) {	
							_presenters.add(presenter);
							presenter.setView(view);
						 
//							mediator.handleNotification(new Notification(ApplicationConstants.MEDIATOR_INIT));
						}
					} catch (Exception e) {
//						log.error("Error while creating Tool and Mediator", e);
					}
				}
			});
		}
	}
	
	public void run() {
		// setup event listeners on the bus
	}

	
}
