package edu.utah.sci.cyclist.presenter;

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.view.View;

public abstract interface Presenter {
	public abstract void setView(final View view);
	public abstract void broadcast(CyclistNotification notification);
}
