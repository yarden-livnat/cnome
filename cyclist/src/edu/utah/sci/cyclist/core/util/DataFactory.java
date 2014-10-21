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
	
	public static boolean register(@SuppressWarnings("rawtypes") Class cls, Function<String, Object> func) {
		String key = cls.getCanonicalName();
		if (_factories.containsKey(key)) {
			return false;
		}
		_factories.put(key, func);
		return true;
	}
	
	static {
		_factories.put("edu.utah.sci.cyclist.neup.model.Nuclide", str->{return Nuclide.create(str); });
		_factories.put(String.class.toString(), str->{return str;});
		_factories.put(Integer.class.toString(), str->{return Integer.valueOf(str); });
		_factories.put(Float.class.toString(), str->{return Float.valueOf(str); });
		_factories.put(Double.class.toString(), str->{return Double.valueOf(str); });
	}
	
}

class GenericFactory implements Function<String, Object> {
	private Class<?> _class;
	
	public GenericFactory(String className) throws ClassNotFoundException {
		_class = Class.forName(className);
	}
	
	// TODO: Maybe check if there is a fromString method?
	public Object apply(String strValue) {
		try {
			return _class.getConstructor(String.class).newInstance(strValue);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
