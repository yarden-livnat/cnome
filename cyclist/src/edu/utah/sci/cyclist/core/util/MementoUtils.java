package edu.utah.sci.cyclist.core.util;

import java.util.function.Function;

import edu.utah.sci.cyclist.core.controller.IMemento;

public class MementoUtils {
	static public void save(IMemento memento, Object obj) {
		memento.putString("class", obj.getClass().getCanonicalName());
		memento.putString("value", obj.toString());
	}
	
	static public Object restore(IMemento memento) {
		String cls = memento.getString("class");
		if (cls == null)
			return null;

		Function<String, Object> factory = DataFactory.findFactory(cls);
		return factory.apply(memento.getString("value"));
	}
}
