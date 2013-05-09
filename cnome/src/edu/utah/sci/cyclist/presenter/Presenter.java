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

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.view.View;
import edu.utah.sci.cyclist.model.Table;

public abstract interface Presenter {
	String getId();
	EventBus getEventBus();
	
	void setView(final View view);
	View getView();
	
	void setTables(List<Table> list, Table current);
	
	void broadcast(CyclistNotification notification);
}
