package edu.utah.sci.cyclist.event.ui;


import edu.utah.sci.cyclist.model.Filter;

import javafx.event.Event;
import javafx.event.EventType;

public class FilterEvent extends CyclistEvent {
	private static final long serialVersionUID = -3420350628439528645L;

	public static final EventType<CyclistDropEvent> SHOW = new EventType<CyclistDropEvent>(CyclistEvent.ANY, "SHOW");
	
	private Filter _filter;
	
	public FilterEvent(EventType<? extends Event> eventType, Filter filter) {
		super(eventType);
		
		_filter = filter;
	}
	
	public Filter getFilter() {
		return _filter;
	}
}
