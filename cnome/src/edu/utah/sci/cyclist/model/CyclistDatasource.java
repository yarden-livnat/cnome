package edu.utah.sci.cyclist.model;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.utah.sci.cyclist.controller.XMLMemento;

public class CyclistDatasource implements DataSource {
	private Properties _properties = new Properties();
	private transient PrintWriter _logger;
	private String _url;
	private boolean _ready = false;
	private XMLMemento _memento;
	
	
	public CyclistDatasource() {
					 
		_memento = XMLMemento.createWriteRoot("CyclistDatasource");
		_memento.putString("class", "CyclistDatasource");
		_memento.putString("uid", UUID.randomUUID().toString());
	}		

	public CyclistDatasource(Properties prop) {
		setProperties(prop);
	}

	@Override
    public String toString() {
        return getName();
    }
	
	public String getName() {
		return _properties.getProperty("name");
	}

	public void setName(String name) {
		_properties.setProperty("name", name);
	}

	public StringProperty getNameProperty(){
		return new SimpleStringProperty(getName());
	}
	
	public void setProperties(Properties p) {
		_properties = p;
	}

	public Properties getProperties() {
		return _properties;
	}

	public XMLMemento getMemento(){
		Enumeration e = _properties.keys();
		while (e.hasMoreElements()){
			String key = (String) e.nextElement();
			String value = _properties.getProperty(key);
			_memento.putString(key, value);
		}
		return _memento;
	}
	
	public void setURL(String url) {
		if (_url != url) {
			_url = url;
			_ready = false;
		}
	}

	public String getURL() {
		return _url;
	}


	public boolean isReady() {
		return _ready;
	}

	public void setReady(boolean value) {
		_ready = value;
	}
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return _logger;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		_logger = out;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return (T) this;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface.isInstance(this);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(null, null);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		if (username != null)
            _properties.put("user", username);
        if (password != null)
            _properties.put("pass", password);
        return DriverManager.getConnection(_url, _properties);
	}

}