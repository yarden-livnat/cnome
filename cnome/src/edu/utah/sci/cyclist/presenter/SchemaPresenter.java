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

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.panels.SchemaPanel;


public class SchemaPresenter  extends PresenterBase {
	private SchemaPanel _panel;
	
	public SchemaPresenter(EventBus bus) {
		super(bus);
		addNotificationListeners();
		
	}
	
	public void setPanel(SchemaPanel panel) {
		_panel = panel;
		
	}
	
	private void addNotificationListeners() {
		addNotificationHandler(CyclistNotifications.DATASOURCE_FOCUS, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification notification) {
				CyclistTableNotification tableNotification = (CyclistTableNotification) notification;
				Table table = tableNotification.getTable();
				_panel.setSchema(table.getSchema());
			}
		});
	}
}
