package edu.utah.sci.cyclist.core.event.notification;

import edu.utah.sci.cyclist.core.ui.View;

public class CyclistViewNotification extends CyclistNotification {
	private View _view;
	
	public CyclistViewNotification(String type, View view) {
		super(type);
		_view = view;
	}
	
	public View getView() {
		return _view;
	}
}
