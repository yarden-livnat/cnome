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
package edu.utah.sci.cyclist.model;

import edu.utah.sci.cyclist.controller.IMemento;

public class WorkDirectory {
	
	private String _path;
	private int _id;
	
	public WorkDirectory() {
		this(-1,"");
	}
	
	public WorkDirectory(int id, String path) {
		_id = id;
		_path = path.replace("\\", "/");
	}
	
	/** Saves the work directory */
	public void save(IMemento memento) {
		// Set the path
		memento.putString("path", getPath());
		
		memento.putInteger("id", getId());
	}
	
	/** Restores the work directory */
	public void restore(IMemento memento){
	
		// Get the name
		setId(memento.getInteger("id"));
		
		 // Get the value of the directory
		setPath(memento.getString("path"));
	}
	
	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;	
	}
	
	public String getPath(){
		return _path;
	}
	
	public void setPath(String path){
		//If in windows environment - change all the backslash to slash.
		_path = path.replace("\\", "/");
	}
}