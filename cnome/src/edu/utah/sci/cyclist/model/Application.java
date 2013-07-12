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

public class Application {
		
	private String _name;
	private String _directory;
	private Boolean _lastChosen;
	
	public Application() {
		this("","",false);
	}
	public Application(String name, String directory, Boolean lastChosen) {
		_name = name;
		_directory = directory;
		_lastChosen= lastChosen;
	}
	
	public Application(String name, String directory) {
		_name = name;
		_directory = directory;
	}
	
    // Save the workspace
	public void save(IMemento memento) {

		// Set the name
		memento.putString("name", getName());
		
		// Set the value
		memento.putString("value", getDirectory());
	}
	
	// Restore the table
	public void restore(IMemento memento){
	
		// Get the name
		setName(memento.getString("name"));
		
		 // Get the alias
		setDirectory(memento.getString("value"));	
	}
	
	/**
	 * Checks if the application already has the same directory.
	 */
	public Boolean applicationExists(IMemento memento, String directory){
		if (memento.getString("name").equals(directory)) 
			return true;
		return false;
	}

	
	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;	
	}
	
	public String getDirectory(){
		return _directory;
	}
	
	public void setDirectory(String directory){
		_directory = directory;
	}
	
	@Override
    public String toString() {
        return getName();
    }
}