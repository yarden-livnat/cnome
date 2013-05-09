/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.event.dnd;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.input.DataFormat;

public class DnD {
	
	public static final DataFormat DATA_SOURCE_FORMAT	= new DataFormat("cyclist.dnd.datasource");
	public static final DataFormat TOOL_FORMAT			= new DataFormat("cyclist.dnd.tool");
	public static final DataFormat FIELD_FORMAT 		= new DataFormat("cyclist.dnd.field");
	
	
	
	private static DnD _instance = new DnD();
	
	public static DnD getInstance() {
		return _instance;
	}
	
	
	private  LocalClipboard _clipboard;
	
	public LocalClipboard createLocalClipboard() {
		_clipboard = new LocalClipboard();
		return _clipboard;
	}
	
	public LocalClipboard getLocalClipboard() {
		return _clipboard;
	}
	
	public class LocalClipboard {

		private final Map<DataFormat, Entry<?>> _items = new HashMap<DataFormat, Entry<?>>();
		
		public LocalClipboard() {
			
		}
		
		public <T> void put(DataFormat key, Class<T> type, T value) {
			_items.put(key, new Entry<T>(type, value));
		}
		
		public <T> T get(DataFormat key, Class<T> type) {
			return type.cast(_items.get(key).value);
		}
		
		public boolean hasContent(DataFormat key) {
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
