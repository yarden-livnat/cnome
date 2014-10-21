package edu.utah.sci.cyclist.core.model;

import java.util.HashMap;

public class Context extends HashMap<String, Object> {
	
    private static final long serialVersionUID = 1L;

	public <T> T get(String key, Class<T> type)  {
		Object obj = get(key);
//		if (obj == null) throw new RuntimeException("Context: can not find item: "+key);
		return obj == null ? null : type.cast(obj);
	}
}
