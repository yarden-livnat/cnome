package edu.utah.sci.cyclist.event.notification;

import edu.utah.sci.cyclist.model.Filter;

public class CyclistFilterNotification extends CyclistNotification {
	private Filter _filter;
	
	public CyclistFilterNotification(String type, Filter filter) {
		super(type);
		_filter = filter;
	}

	public Filter getFilter() {
		return _filter;
	}
}
