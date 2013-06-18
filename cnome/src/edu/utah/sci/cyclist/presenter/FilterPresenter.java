package edu.utah.sci.cyclist.presenter;

import edu.utah.sci.cyclist.event.notification.EventBus;

public class FilterPresenter extends PresenterBase {

	public FilterPresenter(EventBus bus) {
		super(bus);
		addNotificationListeners();
	}
	
	private void addNotificationListeners() {
		
	}
}
