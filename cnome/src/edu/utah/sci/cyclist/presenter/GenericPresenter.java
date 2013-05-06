package edu.utah.sci.cyclist.presenter;

import javafx.event.EventHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.ui.CyclistDropEvent;
import edu.utah.sci.cyclist.view.View;

public class GenericPresenter extends PresenterBase {
	
	public GenericPresenter(EventBus bus) {
		super(bus);
		
		addNotificationHandlers();
	}

	public void setView(View view) {
		super.setView(view);
		
		view.onDatasourceActionProperty().set(new EventHandler<CyclistDropEvent>() {
				
			@Override
			public void handle(CyclistDropEvent event) {
//				Table table = _model.getTable(event.getName());
//				vb.addTable(table);
			}
		});
	}
	
	public void addNotificationHandlers() {
		addNotificationHandler(CyclistNotifications.DATASOURCE_ADD, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistTableNotification notification = (CyclistTableNotification) event;
				getView().addTable(notification.getTable());			
			}
		});
	}
}
