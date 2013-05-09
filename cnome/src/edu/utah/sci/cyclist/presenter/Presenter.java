package edu.utah.sci.cyclist.presenter;

import java.util.List;

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.view.View;
import edu.utah.sci.cyclist.model.Table;

public abstract interface Presenter {
	String getId();
	EventBus getEventBus();
	
	void setView(final View view);
	View getView();
	
	void setTables(List<Table> list, Table current);
	
	void broadcast(CyclistNotification notification);
}
