package edu.utah.sci.cyclist.core.model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.core.controller.IMemento;

public class Preferences {
	public static String DEFAULT_SERVER = "Default server";
	public static String CURRENT_SERVER = "Current server";
	public static String RUN_DEFAULT = "Run default";
	public static final String CLOUDIUS_URL = "http://cycrun.fuelcycle.org";
	
	private static Preferences _instance = new Preferences();
	
	private boolean _dirty = false;
	private String _defaultServer = CLOUDIUS_URL;
    private ObservableList<String> _servers = FXCollections.observableArrayList();
    private int _currentServer = 0;

    private Preferences() {
    	_servers.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				_dirty = true;
			}
		});
    	_servers.addAll("local", CLOUDIUS_URL);
    }
	public static Preferences getInstance() {
		return _instance;
	}
	
	public String getDefaultServer(){
		return _defaultServer;
	}
	
	public void setDefaultServer(String defaultServer){
		_defaultServer = defaultServer;
	}
	
	public ObservableList<String> servers() {
		return _servers;
	}
	
	public int getCurrentServerIndex() {
		return _currentServer;
	}
	
	public void setCurrentServerIndex(int value) {
		_currentServer = value;
		_dirty = true;
	}
	
	public boolean isDirty() {
		return _dirty;
	}
	
	public void save(IMemento memento){
		IMemento defaultServer = memento.createChild("defaultServer");
		defaultServer.putString("value", _defaultServer);
		
		IMemento m = memento.createChild("servers");
		m.putInteger("current", _currentServer);
		for (String server : _servers) {
			if (server != "")
				m.createChild("server").putTextData(server);
		}
		_dirty = false;
	}
	
	public void restore(IMemento memento){
		if (memento == null) return;
		
		boolean prev = _dirty;
		_defaultServer = memento.getChild("defaultServer").getString("value");
		
		_servers.clear();
		IMemento s = memento.getChild("servers");
		if (s == null) {
			_servers.addAll("local", CLOUDIUS_URL);
			_currentServer = 0;
		} else {
			_currentServer = s.getInteger("current");
			for (IMemento m : s.getChildren("server")) {
				_servers.add(m.getTextData());
			}
		}
		
		_dirty = prev;
	}
}
