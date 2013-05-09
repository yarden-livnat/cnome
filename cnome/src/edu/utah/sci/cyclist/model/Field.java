package edu.utah.sci.cyclist.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.utah.sci.cyclist.controller.IMemento;
import edu.utah.sci.cyclist.controller.XMLMemento;

public class Field {

	private String _name;
	private Map<String, Object> _properties = new HashMap<>();

	public Field(){
		this("");
	}

	public Field(String name) {
		this._name = name;
	}

	// Save this field
	public void save(IMemento memento) {
	
		memento.putString("name", _name);
		
		// Set things saved in the properties map
		Set<String> keys = _properties.keySet();
		for(String key: keys){
			
			// Get the value associated with this key
			Object value = _properties.get(key);

			// Create an entry memento
			IMemento entryMemento = memento.createChild("entry");
			entryMemento.putString("key", key);
			
			// If the value is null, record it
			if(value == null)
				entryMemento.putTextData("null");	
			else{

				// Put the type of the class
				entryMemento.putString("class", value.getClass().toString());

				// Save integers or strings as strings
				if(value.getClass().toString().equals(String.class.toString()) || 
						value.getClass().toString().equals(Integer.class.toString()))
					entryMemento.putTextData(value.toString());
				// TODO/FIXME: save some sort of Factory-ID
				else{

					System.out.println("Table:save() NEED TO CHECK FOR SAVE-ABLE OBJECTS!!");
					IMemento valueMemento = entryMemento.createChild("value");
					valueMemento.putString("value-ID", value.toString());		
				}
			}
		}
	}
	
	// Restore this field
	public void restore(IMemento memento) {
		
		// Get the name of the field
		_name = memento.getString("name");
		
		// Get the entries in the field
		IMemento[] entries = memento.getChildren("entry");
		for(IMemento entry:entries){
			
			// Get the key of the object
			String key = entry.getString("key");
						
			// Get the class of the object
			String classType = entry.getString("class");
							
			// If we have a string
			if(classType.equals(String.class.toString())){
				String value = entry.getTextData();
				set(key, value);
			}
			// If we have an Integer
			else if(classType.equals(Integer.class.toString())){
				Integer value = Integer.parseInt(entry.getTextData());
				set(key, value);
			}	
			else{
				System.out.println("Field:load() NEED TO IMPLEMENT OBJECT FACTORIES!!");
			}	
		}		
	}

	public String getName() {
		return _name;
	}


	public String getDataTypeName() {
		return (String) _properties.get(FieldProperties.DATA_TYPE_NAME);
	}

	public void set(String property, Object value) {
		_properties.put(property, value);
	}

	public Object get(String property) {
		return _properties.get(property);
	}
	
	public String getString(String property) {
		return (String) get(property);
	}
	
	public int getInt(String property) {
		return (int) get(property);
	}
	
	public String toString() {
		return _name;
	}
}