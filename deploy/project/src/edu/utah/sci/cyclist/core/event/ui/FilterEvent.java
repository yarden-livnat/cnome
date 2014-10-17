package edu.utah.sci.cyclist.core.event.ui;


import edu.utah.sci.cyclist.core.model.Filter;
import javafx.event.Event;
import javafx.event.EventType;

public class FilterEvent extends CyclistEvent {
	private static final long serialVersionUID = -3420350628439528645L;

	public static final EventType<CyclistDropEvent> SHOW = new EventType<CyclistDropEvent>(CyclistEvent.ANY, "SHOW");
	public static final EventType<CyclistDropEvent> DELETE = new EventType<CyclistDropEvent>(CyclistEvent.ANY, "DELETE");
	public static final EventType<CyclistDropEvent> REMOVE_FILTER_FIELD = new EventType<CyclistDropEvent>(CyclistEvent.ANY, "REMOVE_FILTER_FIELD");
	
	private Filter _filter;
	
	public FilterEvent(EventType<? extends Event> eventType, Filter filter) {
		super(eventType);
		
		_filter = filter;
	}
	
	public Filter getFilter() {
		return _filter;
	}
}
