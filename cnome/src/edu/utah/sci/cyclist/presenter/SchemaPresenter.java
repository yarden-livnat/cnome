package edu.utah.sci.cyclist.presenter;

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.Schema;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.View;
import edu.utah.sci.cyclist.view.components.SchemaPanel;


public class SchemaPresenter  extends PresenterBase {
	private Schema _schema = null;
	
	public SchemaPresenter(EventBus bus) {
		super(bus);
		addNotificationListeners();
		
	}
	
	@Override
	public void setView(View view) {
		if (view instanceof SchemaPanel) {
			super.setView(view);
			if (_schema != null) 
				getView().setSchema(_schema);
		}
	}
	
	public SchemaPanel getView() {
		return (SchemaPanel) super.getView();
	}
	
	private void addNotificationListeners() {
		addNotificationHandler(CyclistNotifications.DATASOURCE_FOCUS, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification notification) {
				CyclistTableNotification tableNotification = (CyclistTableNotification) notification;
				Table table = tableNotification.getTable();
				getView().setSchema(table.getSchema());
			}
		});
	}
}
