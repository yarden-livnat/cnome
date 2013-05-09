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
package edu.utah.sci.cyclist.event.notification;

import edu.utah.sci.cyclist.model.Table;

public class CyclistTableNotification extends CyclistNotification {
	private Table _table;
	
	public CyclistTableNotification(String type, Table table) {
		super(type);
		_table = table;
	}
	
	public Table getTable() {
		return _table;
	}
}
