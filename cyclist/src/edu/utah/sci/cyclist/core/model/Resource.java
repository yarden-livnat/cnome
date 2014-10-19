package edu.utah.sci.cyclist.core.model;

import edu.utah.sci.cyclist.core.controller.IMemento;

public interface Resource {
	String getUID();	
	void save(IMemento memento);
	void restore(IMemento memento, Context ctx);
	
}
