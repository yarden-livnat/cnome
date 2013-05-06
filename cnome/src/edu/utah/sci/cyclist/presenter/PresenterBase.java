package edu.utah.sci.cyclist.presenter;

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.view.View;

public class PresenterBase implements Presenter {
	private View _view;
	private EventBus _eventBus;
	
	
	public PresenterBase(EventBus bus) {
		_eventBus = bus;
	}
	
	@Override
	public void setView(View view) {
		_view = view;
	}

	public View getView() {
		return _view;
	}

	public void addNotificationHandler(String type, CyclistNotificationHandler handler) {
		_eventBus.addHandler(type, handler);
	}
	
	@Override
	public void broadcast(CyclistNotification notification) {
		notification.setSource(this);
		_eventBus.notify(notification);
	}
}
