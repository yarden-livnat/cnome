package edu.utah.sci.cyclist.presenter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import edu.utah.sci.cyclist.event.notification.CyclistFilterNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.ui.views.FilterPanel;

public class FilterPresenter extends PresenterBase {

	private FilterPanel _panel;
	
	public FilterPresenter(EventBus bus) {
		super(bus);
		addNotificationListeners();
	}
	
	public void setPanel(FilterPanel panel) {
		_panel = panel;
		
		_panel.setOnClose(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				broadcast(new CyclistFilterNotification(CyclistNotifications.REMOVE_FILTER, getFilter())); 
			}
		});
	}
	
	public Filter getFilter() {
		return _panel.getFilter();
	}
	
	public FilterPanel getPanel() {
		return _panel;
	}
	
	private void addNotificationListeners() {
//		addNotificationHandler(CyclistNotifications.SHOW_FILTER, new CyclistNotificationHandler() {
//			
//			@Override
//			public void handle(CyclistNotification event) {
//				CyclistFilterNotification notification = (CyclistFilterNotification) event;
//				if (notification.getFilter() == _panel.getFilter()) {
//					
//				}
//					
//			}
//		});
	}
}
