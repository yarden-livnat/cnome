package edu.utah.sci.cyclist.event.notification;

import edu.utah.sci.cyclist.model.Table;

public class CyclistTableNotification extends CyclistNotification {
	private Table _table;
	
	public CyclistTableNotification(String type, Table table) {
		super(type);
		_table = table;
	}
	
	public Table getTable() {
		return _table;
	}
}
