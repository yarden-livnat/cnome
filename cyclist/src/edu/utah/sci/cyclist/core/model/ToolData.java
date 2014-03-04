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
 *     Kristi Potter
 *******************************************************************************/
package edu.utah.sci.cyclist.core.model;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.core.controller.IMemento;
import edu.utah.sci.cyclist.core.tools.TableTool;
import edu.utah.sci.cyclist.core.tools.Tool;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public class ToolData {

	static Logger log = Logger.getLogger(ToolData.class);
	
	private Map<String, Object> _properties = new HashMap<>();
	private Point2D _point;
	private Tool _tool;
	private String _tableName;
	private String _dataSourceUid;
	private double _width;
	private double _height;

	
	public ToolData() {
		setProperty("uid", UUID.randomUUID().toString());
	}
	public ToolData(Tool tool, double x, double y, double width, double height ) {
		setProperty("uid", UUID.randomUUID().toString());
		_point = new Point2D(x, y);
		_tool = tool;
		_width = width;
		_height = height;
	}
	
	public ToolData(Tool tool, double x, double y, double width, double height, Table table ) {
		setProperty("uid", UUID.randomUUID().toString());
		_point = new Point2D(x, y);
		_tool = tool;
		_tableName = table.getName();
		_dataSourceUid = table.getDataSource()!=null?table.getDataSource().getUID():"";
		_width = width;
		_height = height;
	}
	
	public void setProperty(String property, Object value) {
		_properties.put(property, value);
	}

	public void removeProperty(String property) {
		_properties.remove(property);
	}

	public Object getProperty(String property) {
		return _properties.get(property);
	}

	public boolean hasProperty(String property) {
		return _properties.containsKey(property);
	}
	
	
    // Save the workspace data.
	public void save(IMemento memento) {
		
		// Save the uid
		memento.putString("uid", getProperty("uid").toString());
		memento.putString("class", _tool.getClass().getName());
		//Use the view to get the location, because the workspace doesn't detect "mouse dragged" event for a view.
		memento.putString("x", Double.toString(((Node)_tool.getView()).getTranslateX()));
		memento.putString("y", Double.toString(((Node)_tool.getView()).getTranslateY()));
		memento.putString("width", Double.toString(((Region)_tool.getView()).getPrefWidth()));
		memento.putString("height", Double.toString(((Region)_tool.getView()).getPrefHeight()));
        
		if(_tool.getClass().equals(TableTool.class)){
			IMemento tableMemento = memento.createChild("toolTable");
			tableMemento.putString("name", _tableName);
			tableMemento.putString("dataSource", _dataSourceUid);
		}
	}

	
	// Restore the workspace data
	public void restore(IMemento memento){
		
		//Get the location
		Double x = Double.parseDouble(memento.getString("x"));
		Double y = Double.parseDouble(memento.getString("y"));
		
		_point = new Point2D(x,y);
		
		_width = Double.parseDouble(memento.getString("width"));
		_height = Double.parseDouble(memento.getString("height"));
		
		
		String className = memento.getString("class");
		
		try {
			Class<?> toolClass = Class.forName(className);
			Constructor<?> ctor = toolClass.getConstructor();
			_tool = (Tool)ctor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(_tool.getClass().equals(TableTool.class)){
			IMemento tableMemento = memento.getChild("toolTable");
			_tableName = tableMemento.getString("name");
			_dataSourceUid = tableMemento.getString("dataSource");
		}
		
	}
	
	public Tool getTool(){
		return _tool;
	}
	
	public Point2D getPoint(){
		return _point;
	}
	
	public String getTableName(){
		return _tableName;
	}
	
	public String getTableDatasource(){
		return _dataSourceUid;
	}
	
	public double getWidth(){
		return _width;
	}
	
	public double getHeight(){
		return _height;
	}
	
	
	
}
