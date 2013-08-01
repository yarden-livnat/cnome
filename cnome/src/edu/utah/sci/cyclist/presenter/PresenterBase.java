package edu.utah.sci.cyclist.presenter;

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.EventBus;

public class PresenterBase implements Presenter {
	private static int _idCounter = 0;
	
	private String _id;
	private EventBus _eventBus;
	
	public PresenterBase(EventBus bus) {
		_id = "presenter"+_idCounter++;
		_eventBus = bus;
	}
	
	@Override
	public String getId() {
		return _id;
	}
	
	@Override
	public EventBus getEventBus() {
		return _eventBus;
	}
	
	public void addNotificationHandler(String type, CyclistNotificationHandler handler) {
		_eventBus.addHandler(type, _id, handler);
	}
	
	
	@Override
	public void broadcast(CyclistNotification notification) {
		notification.setSource(this);
		_eventBus.notify(notification);
	}
	
	public void broadcast(EventBus bus, CyclistNotification notification) {
		notification.setSource(this);
		bus.notify(notification);
	}

}
