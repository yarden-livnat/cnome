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
