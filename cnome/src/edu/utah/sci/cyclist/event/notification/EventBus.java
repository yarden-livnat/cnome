package edu.utah.sci.cyclist.event.notification;

public abstract class EventBus {
	
	public abstract void addHandler(String type, CyclistNotificationHandler handler); 
	public abstract void removeHandler(String type, CyclistNotificationHandler handler);
	
	public abstract void notify(CyclistNotification event);
	
	
	
}
