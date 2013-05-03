package edu.utah.sci.cyclist.presenter;

import edu.utah.sci.cyclist.event.shared.EventBus;
import edu.utah.sci.cyclist.view.View;
import edu.utah.sci.cyclist.view.components.Workspace;

public class WorkspacePresenter implements Presenter {

	private Workspace _workspace;
	private EventBus _eventBus;
	
	public void setView(View workspace) {
		if (workspace instanceof Workspace)
		_workspace = (Workspace) workspace;
	}
	
	public void setEventBus(EventBus bus) {
		_eventBus = bus;
	}
	
	public void run() {
		// setup event listeners on the bus
	}

	
}
