package edu.utah.sci.cyclist.presenter;

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.view.View;

public abstract interface Presenter {
	String getId();
	EventBus getEventBus();
	abstract void setView(final View view);
	abstract void broadcast(CyclistNotification notification);
}
