package edu.utah.sci.cyclist.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utah.sci.cyclist.model.Table;

public class SingleSelection extends SelectionModel {
	//private Entry _current = null;
	
	//Saves the current selection For each selected type.
	private Map<selectedTypes,Entry> _currentMap = new HashMap<selectedTypes,Entry>();
	
	@Override
	public Table getSelected() {
//		return _current == null ? null : _current.table;
		return _currentMap.get(selectedTypes.TABLE)== null ? null : (Table)_currentMap.get(selectedTypes.TABLE).object;
	}
	
	@Override
	public void addTable(Table table, boolean remote, boolean active, boolean remoteActive) {
		super.addTable(table, remote, false, remoteActive);
		
		if (active) {
			if (_currentMap.get(selectedTypes.TABLE) == null) {
				selectTable(table, true);
			} else if (!remote) {
				selectTable(table, true);
			} else {
				// select a remote only if no local table is active
			}
		}
	}
	
	
	@Override
	public void removeTable(Table table) {
		selectTable(table, false);
		super.removeTable(table);
	}
	
	/**
	 * 
	 */
	public void selectTable(Table table, boolean active) {
		Entry entry = getEntry(table);
		Entry current = _currentMap.get(selectedTypes.TABLE);
		//System.out.println("   SingleSelectionModel: selectTable: "+table.getName()+"  active:"+active+(entry.active == active? "   ignore": ""));
		
		if (entry== null || entry.active == active) {
			// ignore
		} else if (active) {
			if (current != null) {
				if (!current.remote && entry.remote) {
					// ignore.  
					// switch from a local to a remote on on user explicit request (tableSelected) 
					entry.remoteActive = true;
					return;
				} else {
					current.active = false;
					selectTableAction((Table)current.object, false);
				}
			}
			current = entry;
			entry.active = true;
			_currentMap.put(selectedTypes.TABLE,current);
			selectTableAction(table, true);
		} else /* unselect */ {
			entry.active = false;
			if (entry == current) {
				//current = null;
				_currentMap.remove(selectedTypes.TABLE);
			}
			selectTableAction(table, false);
			
			// TODO: select a default one from the remote?
		}
	}
	
	/**
	 * a table status was changed via a button click
	 */
	@Override
	public void tableSelected(Table table, boolean active) {
		//System.out.println("tableSelected: "+table.getName()+"  active:"+active);
		Entry entry = getEntry(table);
		Entry current = _currentMap.get(selectedTypes.TABLE);
		
		if (entry == null || entry.active == active) {
			// ignore
		} else if (active) {
			if (current != entry && current != null) {
				current.active = false;
				selectTableAction((Table)current.object, false);
			}
			current = entry;
			current.active = true;
			// inform the world this table is now active.
			selectTableAction((Table)current.object, true);
		} else /* not active */ {
			current.active = false;
			current = null;
			_currentMap.remove(selectedTypes.TABLE);
			
			// check if there is a remoteActive
			for (Entry remoteEntry : getRemotes()) {
				if (remoteEntry.remoteActive) {
					selectTable((Table)remoteEntry.object, true);
					break;
				}
			}
		}
		//Update the current selected table in the map.
		if(current != null){
			_currentMap.put(selectedTypes.TABLE,current);
		}
	}
	
	@Override
	public void setRemoteTables(List<SelectionModel.Entry> list) {
		super.setRemoteTables(list);
		
		// select the first active entry
		for (Entry entry : list) {
			if (entry.remoteActive) {
				selectTable((Table)entry.object, true);
				break;
			}
		}
	}
}