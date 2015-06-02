package edu.utah.sci.cyclist.core.model;

import java.util.List;

import edu.utah.sci.cyclist.core.ui.views.Workspace;

public class Perspective {
	public String name;
	public List<String> tools;
	public Workspace workspace;
	public Boolean initialized = false;
	public int id;
	
	public Perspective(int id, String name, List<String> tools) {
		this.id = id;
		this.name = name;
		this.tools = tools;
	}
}
