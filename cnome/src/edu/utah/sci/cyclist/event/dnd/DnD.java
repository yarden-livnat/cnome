/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved.
 *
 * License for the specific language governing rights and limitations under Permission
 * is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: The above copyright notice 
 * and this permission notice shall be included in all copies  or substantial portions of the Software. 
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR 
 *  A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.event.dnd;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.input.DataFormat;

public class DnD {
	
	public static final DataFormat SOURCE_FORMAT		= new DataFormat("cyclist.dnd.source");
	public static final DataFormat VALUE_FORMAT			= new DataFormat("cyclist.dnd.value");
	public static final DataFormat TYPE_FORMAT			= new DataFormat("cyclist.dnd.type");
	public static final DataFormat TABLE_FORMAT			= new DataFormat("cyclist.dnd.datasource");
	public static final DataFormat TOOL_FORMAT			= new DataFormat("cyclist.dnd.tool");
	public static final DataFormat FIELD_FORMAT 		= new DataFormat("cyclist.dnd.field");
	public static final DataFormat DnD_SOURCE_FORMAT 	= new DataFormat("cyclist.dnd.dnd_source");
	public static final DataFormat FILTER_FORMAT 		= new DataFormat("cyclist.dnd.filter");
	public static final DataFormat INDICATOR_FORMAT		= new DataFormat("cyclist.dnd.indicator");
	
	private static DnD _instance = new DnD();
	
	public enum Status {MOVED, COPIED, ACCEPTED, IGNORED, NA};
	
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
		private Status _status = Status.NA;
		
		public LocalClipboard() {
			
		}
		public void setStatus(Status status) {
			_status = status;
		}
		
		public Status getStatus() {
			return _status;
		}
		public <T> void put(DataFormat key, Class<T> type, T value) {
			_items.put(key, new Entry<T>(type, value));
		}
		
		public <T> T get(DataFormat key, Class<T> type) {
			Entry<?> entry = _items.get(key);
			return entry == null ? null : type.cast(entry.value);
		}
		
		public Class<?> getType(DataFormat key) {
			Entry<?> entry = _items.get(key);
			return entry.value.getClass();
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
