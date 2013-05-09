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
package edu.utah.sci.cyclist.view;

import java.util.List;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.model.Table;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface View {
	ObjectProperty<EventHandler<ActionEvent>> onMinmaxProperty();
	ObjectProperty<EventHandler<ActionEvent>> onCloseProperty();
	
	void setOnTableDrop(Closure.V1<Table> action);
	void setOnTableSelected(Closure.V1<Table> action);
	
	void setTables(List<Table> list, Table table);
	void addTable(Table table, boolean local);
	void addTable(Table table, boolean local, boolean activate);
	void tableSelected(Table table);
}
