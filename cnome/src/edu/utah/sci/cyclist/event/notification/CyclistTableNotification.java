package edu.utah.sci.cyclist.event.notification;

import edu.utah.sci.cyclist.model.Table;

public class CyclistTableNotification extends CyclistNotification {
	private Table _table;
	
	public CyclistTableNotification(Table table) {
		super(CyclistNotifications.DATASOURCE_FOCUS);
		_table = table;
	}
	
	public Table getTable() {
		return _table;
	}
}
