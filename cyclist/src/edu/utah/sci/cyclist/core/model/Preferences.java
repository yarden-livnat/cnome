package edu.utah.sci.cyclist.core.model;

import edu.utah.sci.cyclist.core.controller.IMemento;

public class Preferences {
	public static String DEFAULT_SERVER = "Default server";
	public static String CURRENT_SERVER = "Current server";
	public static String RUN_DEFAULT = "Run default";
	public static final String CLOUDIUS_URL = "http://cycrun.fuelcycle.org";
	
	private static Preferences _instance = new Preferences();
	private String _defaultServer = CLOUDIUS_URL;
	
	public static Preferences getInstance() {
		return _instance;
	}
	
	public String getDefaultServer(){
		return _defaultServer;
	}
	
	public void setDefaultServer(String defaultServer){
		_defaultServer = defaultServer;
	}
	
	public void save(IMemento memento){
		IMemento defaultServer = memento.createChild("defaultServer");
		defaultServer.putString("value", _defaultServer);
	}
	
	public void restore(IMemento memento){
		if (memento == null) return;
		_defaultServer = memento.getChild("defaultServer").getString("value");
	}
}
