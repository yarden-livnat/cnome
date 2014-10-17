package edu.utah.sci.cyclist.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import edu.utah.sci.cyclist.neup.model.Nuclide;


public class DataFactory {
	
	static private Map<String, Function<String, Object>> _factories = new HashMap<>();
	
	
	public static Function<String, Object> findFactory(String className) {
		Function<String, Object> factory = _factories.get(className);
		if (factory == null) {
			try {
				factory = new GenericFactory(className);
			} catch (ClassNotFoundException e) {
				factory = s->{return null;};
			}
		}
		return factory;
	}
	
	static {
		_factories.put("edu.utah.sci.cyclist.neup.model.Nuclide", str->{return Nuclide.create(str); });
	}
	
}

class GenericFactory implements Function<String, Object> {
	private Class<?> _class;
	
	public GenericFactory(String className) throws ClassNotFoundException {
		_class = Class.forName(className);
	}
	
	public Object apply(String strValue) {
		try {
			return _class.getConstructor(String.class).newInstance(strValue);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
