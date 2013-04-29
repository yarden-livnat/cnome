package edu.utah.sci.cyclist.event.shared;

import javafx.event.EventHandler;
import javafx.event.EventType;

public abstract class EventBus {
	
	public abstract <T extends CyclistEvent>  void addHandler(EventType<T> type, EventHandler<T> handler); 
	public abstract void fireEvent(CyclistEvent event);
	
	// TODO: removeHandler
	
}
