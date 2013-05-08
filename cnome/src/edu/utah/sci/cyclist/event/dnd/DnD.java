package edu.utah.sci.cyclist.event.dnd;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.input.DataFormat;

public class DnD {
	
	public static final DataFormat DATA_SOURCE_FORMAT	= new DataFormat("cyclist.dnd.datasource");
	public static final DataFormat TOOL_FORMAT			= new DataFormat("cyclist.dnd.tool");
	public static final DataFormat FIELD_FORMAT 		= new DataFormat("cyclist.dnd.field");
	
	public static class LocalDragboard {
		private final Map<Class<?>, Object> contents ;
		
		private final static LocalDragboard instance = new LocalDragboard();
		
		private LocalDragboard() {
			this.contents = new HashMap<Class<?>, Object>();
		}
		
		public static LocalDragboard getInstance() {
			return instance ;
		}
		
		public <T> void putValue(Class<T> type, T value) {
			contents.put(type, type.cast(value));
		}
		
		public <T> T getValue(Class<T> type) {
			return type.cast(contents.get(type));
		}
		
		public boolean hasType(Class<?> type) {
			return contents.keySet().contains(type);
		}
		
		public void clear(Class<?> type) {
			contents.remove(type);
		}
		
		public void clearAll() {
			contents.clear();
		}
	}
}
