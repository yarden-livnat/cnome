package edu.utah.sci.cyclist.view.components;

public class MySQLPage extends DatasourcePage {

	public MySQLPage(String ds) {
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
