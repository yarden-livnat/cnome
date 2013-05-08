package edu.utah.sci.cyclist.event.ui;

import edu.utah.sci.cyclist.model.Table;
import javafx.event.Event;
import javafx.event.EventType;

public class CyclistDropEvent extends CyclistEvent {
	private static final long serialVersionUID = 1L;

	public static final EventType<CyclistDropEvent> DROP = new EventType<CyclistDropEvent>(CyclistEvent.ANY, "DROP");
	public static final EventType<CyclistDropEvent> DROP_DATASOURCE = new EventType<CyclistDropEvent>(CyclistEvent.ANY, "DROP_DATASOURCE");
	
	private Table _table;
	private double _x;
	private double _y;
	
	public CyclistDropEvent(EventType<? extends Event> eventType, Table table, double x, double y) {
		super(eventType);
		_table = table;
		_x = x;
		_y = y;
	}
	
	public Table getTable() {
		return _table;
		
	}
	public double getX() {
		return _x;
	}
	
	public double getY() {
		return _y;
	}
	
}
