package edu.utah.sci.cyclist.event.dnd;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.input.DataFormat;

public class DnD {
	
	public static final DataFormat DATA_SOURCE_FORMAT	= new DataFormat("cyclist.dnd.datasource");
	public static final DataFormat TOOL_FORMAT			= new DataFormat("cyclist.dnd.tool");
	public static final DataFormat FIELD_FORMAT 		= new DataFormat("cyclist.dnd.field");
	
	public static class LocalDragboard {
		private final static LocalDragboard _instance = new LocalDragboard();
		
		private final Map<DataFormat, Entry<?>> _items = new HashMap<DataFormat, Entry<?>>();
		
		private LocalDragboard() {
		}
		
		public static LocalDragboard getInstance() {
			return _instance ;
		}
		
		public <T> void putValue(DataFormat key, Class<T> type, T value) {
			_items.put(key, new Entry<T>(type, value));
		}
		
		public <T> T getValue(DataFormat key, Class<T> type) {
			return type.cast(_items.get(type).value);
		}
		
		public boolean hasKey(DataFormat key) {
			return _items.containsKey(key);
		}
		
		public void remove(DataFormat key) {
			_items.remove(key);
		}
		
		public void clearAll() {
			_items.clear();
		}
		
		class Entry<T> {
			public Class<T> type;
			public T value;
			
			public Entry(Class<T> type, T value) {
				this.type = type;
				this.value = value;
			}
		}
	}
	

}
