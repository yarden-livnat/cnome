/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.presenter;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.notification.SimpleNotification;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.View;

public class PresenterBase implements Presenter {
	private static int _idCounter = 0;
	private String _id;
	
	private View _view;
	private EventBus _eventBus;
	
	public PresenterBase(EventBus bus) {
		_id = "presenter"+_idCounter++;
		_eventBus = bus;
	}
	
	@Override
	public String getId() {
		return _id;
	}
	
	@Override
	public EventBus getEventBus() {
		return _eventBus;
	}
	
	@Override
	public void setView(View view) {
		_view = view;
		if (_view != null) {
			view.onCloseProperty().set(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					broadcast(new SimpleNotification(CyclistNotifications.REMOVE_VIEW, getId()));
				}
			});
		}
	}

	public View getView() {
		return _view;
	}

	public void addNotificationHandler(String type, CyclistNotificationHandler handler) {
		_eventBus.addHandler(type, _id, handler);
	}
	
	@Override
	public void broadcast(CyclistNotification notification) {
		notification.setSource(this);
		_eventBus.notify(notification);
	}

	@Override
	public void setTables(List<Table> list, Table current) {
		getView().setTables(list, current);
	}
}
