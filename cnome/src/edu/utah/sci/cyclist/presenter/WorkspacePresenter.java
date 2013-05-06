package edu.utah.sci.cyclist.presenter;

import javafx.event.EventHandler;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.ui.CyclistDropEvent;
import edu.utah.sci.cyclist.model.Model;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.View;
import edu.utah.sci.cyclist.view.components.Workspace;

public class WorkspacePresenter extends PresenterBase {

	private Workspace _workspace;
	private Model _model;
	
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
					
				}
			});
		}
	}
	
	public void run() {
		// setup event listeners on the bus
	}

	
}
