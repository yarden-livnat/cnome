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
package edu.utah.sci.cyclist.model;

import java.util.List;
import java.util.Vector;

import utils.SQLUtil;

import edu.utah.sci.cyclist.controller.IMemento;
import edu.utah.sci.cyclist.model.Field.Type;



public class Schema {

	private Vector<Field> _fields = new Vector<Field>();
	
	public Schema() {
		
	}
	
	// Save the schema
	public void save(IMemento memento) {

		// Create the child memento
		for(Field field: _fields){
			field.save(memento.createChild("field"));
		}
	}

	// Restore the schema
	public void restore(IMemento memento) {

		// Restore each field
		IMemento[] fields = memento.getChildren("field");
		for(IMemento field: fields){
			Field newField = new Field();
			newField.restore(field);
			addField(newField);
		}
	}
	
	public int size() {
		return _fields.size();
	}
	
	public Field getField(int index) {
		return _fields.get(index);
	}
	
	public Field getField(String name) {
		for (Field field : _fields)
			if (field.getName().equals(name)) 
				return field;
		return null;
	}
	
	public void addField(Field field) {
		_fields.add(field);
	}
	
	public void set(List<Field> list) {
		_fields.clear();
		_fields.addAll(list);
	}

	public void update() {
		for (Field field : _fields) {
			updateField(field);
		}
	}
	
	public void updateField(Field field) {
		// set field type
		int remote_type = (int) field.get(FieldProperties.REMOTE_DATA_TYPE);
		String remote_type_name = field.getString(FieldProperties.REMOTE_DATA_TYPE_NAME);
		
//		Field.Type type = SQLUtil.fromSQL(remote_type);
		Field.Type type = SQLUtil.fromSQL(remote_type_name);
		field.setType(type);
		field.set(FieldProperties.DATA_TYPE_NAME, remote_type_name);
		
		System.out.println("Field "+field.getName()+"  remote type:"+ remote_type_name+" ["+remote_type+"] type:"+type);
	
		Field.Role role = Field.Role.NA;
		switch (type) {
		case INTEGER:
		case NUMERIC:
			role = Field.Role.NUMERIC;
			break;
		case STRING:
		case TIME:
		case BOOLEAN:
			role = Field.Role.CATEGORICAL;
			break;
		default:
			role = Field.Role.NA;
		}
		
		field.setRole(role);
	}
	
	
}
