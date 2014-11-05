package edu.utah.sci.cyclist.core.model;

import edu.utah.sci.cyclist.core.controller.IMemento;

public class Preferences {
	public static String DEFAULT_SERVER = "Default server";
	public static String CURRENT_SERVER = "Current server";
	public static String RUN_DEFAULT = "Run default";
	public static final String CLOUDIUS_URL = "http://cycrun.fuelcycle.org";
	
	private static Preferences _instance = new Preferences();
	private String _defaultServer = CLOUDIUS_URL;
	private String _currentServer = CLOUDIUS_URL;;
	private Boolean _runDefault = true;
	
	public static Preferences getInstance() {
		return _instance;
	}
	
	public String getDefaultServer(){
		return _defaultServer;
	}
	
	public String getCurrentServer(){
		return _currentServer;
	}
	
	public Boolean getRunDefault(){
		return _runDefault;
	}
	
	public void setDefaultServer(String defaultServer){
		_defaultServer = defaultServer;
	}
	
	public void setCurrentServer(String currentServer){
		_currentServer = currentServer;
	}
	
	public void setRunDefault(Boolean runDefault){
		_runDefault = runDefault;
	}
	
	public String getServerUrl(){
		return _runDefault?_defaultServer:_currentServer;
	}
	
	public void save(IMemento memento){
		IMemento defaultServer = memento.createChild("defaultServer");
		defaultServer.putString("value", _defaultServer);
		IMemento currServer = memento.createChild("currentServer");
		currServer.putString("value", _currentServer);
		IMemento runDefault = memento.createChild("runDefault");
		runDefault.putBoolean("value", _runDefault);
	}
	
	public void restore(IMemento memento){
		if (memento == null) return;
		_defaultServer = memento.getChild("defaultServer").getString("value");
		_currentServer = memento.getChild("currentServer").getString("value");
		_runDefault = memento.getChild("runDefault").getBoolean("value");
	}
}
