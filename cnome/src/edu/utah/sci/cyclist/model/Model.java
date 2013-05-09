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
package edu.utah.sci.cyclist.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

	private ObservableList<Table> _tables = FXCollections.observableArrayList();
	private ObservableList<CyclistDatasource> _sources = FXCollections.observableArrayList();
	
	
	/**
	 * getTables
	 * @return
	 */
	public ObservableList<Table> getTables() {
		return _tables;
	}
	
	public Table getTable(String name) {
		for (Table table : _tables) {
			if (table.getName().equals(name)) 
				return table;
		}
		return null;
	}
	
	public ObservableList<CyclistDatasource> getSources() {
		return _sources;
	}	
}
