package edu.utah.sci.cyclist.event.shared;

public abstract class EventBus {
	
	public abstract void addHandler(String type, CyclistEventHandler handler); 
	public abstract void removeHandler(String type, CyclistEventHandler handler);
	
	public abstract void fireEvent(CyclistEvent event);
	
	
	
}
