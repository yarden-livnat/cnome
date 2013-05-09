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
package edu.utah.sci.cyclist.view.components;

import edu.utah.sci.cyclist.model.CyclistDatasource;

public class MySQLPage extends DatasourcePage {

	public MySQLPage(CyclistDatasource ds) {
		super(ds);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void init() {
		super.init();
		_driver = "mysql";
		_type = "MySQL";
		if (_port.getText() == null || _port.getText().equals(""))
			_port.setText("3306");
	}
}
