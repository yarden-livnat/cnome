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
package edu.utah.sci.cyclist.event.ui;

import edu.utah.sci.cyclist.model.Table;
import javafx.event.Event;
import javafx.event.EventType;

public class CyclistDropEvent extends CyclistEvent {
	private static final long serialVersionUID = 1L;

	public static final EventType<CyclistDropEvent> DROP = new EventType<CyclistDropEvent>(CyclistEvent.ANY, "DROP");
	public static final EventType<CyclistDropEvent> DROP_DATASOURCE = new EventType<CyclistDropEvent>(CyclistEvent.ANY, "DROP_DATASOURCE");
	
	private Table _table;
	private double _x;
	private double _y;
	
	public CyclistDropEvent(EventType<? extends Event> eventType, Table table, double x, double y) {
		super(eventType);
		_table = table;
		_x = x;
		_y = y;
	}
	
	public Table getTable() {
		return _table;
		
	}
	public double getX() {
		return _x;
	}
	
	public double getY() {
		return _y;
	}
	
}
