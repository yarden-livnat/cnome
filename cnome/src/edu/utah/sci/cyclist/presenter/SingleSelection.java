package edu.utah.sci.cyclist.presenter;

import java.util.List;

import edu.utah.sci.cyclist.model.Table;

public class SingleSelection extends SelectionModel {
	private Entry _current = null;
	
	@Override
	public Table getSelected() {
		return _current == null ? null : _current.table;
	}
	
	@Override
	public void addTable(Table table, boolean remote, boolean active, boolean remoteActive) {
		super.addTable(table, remote, false, remoteActive);
		
		if (active) {
			if (_current == null) {
				selectTable(table, true);
			} else if (!remote) {
				selectTable(table, true);
			} else {
				// select a remote only if no local table is active
			}
		}
	}
	
	/**
	 * 
	 */
	public void selectTable(Table table, boolean active) {
		Entry entry = getEntry(table);
		//System.out.println("   SingleSelectionModel: selectTable: "+table.getName()+"  active:"+active+(entry.active == active? "   ignore": ""));
		if (entry.active == active) {
			// ignore
		} else if (active) {
			if (_current != null) {
				if (!_current.remote && entry.remote) {
					// ignore.  
					// switch from a local to a remote on on user explicit request (tableSelected) 
					entry.remoteActive = true;
					return;
				} else {
					_current.active = false;
					selectTableAction(_current.table, false);
				}
			}
			_current = entry;
			entry.active = true;
			selectTableAction(table, true);
		} else /* unselect */ {
			entry.active = false;
			if (entry == _current) {
				_current = null;
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
		if (entry.active == active) {
			// ignore
		} else if (active) {
			if (_current != entry && _current != null) {
				_current.active = false;
				selectTableAction(_current.table, false);
			}
			_current = entry;
			_current.active = true;
			// inform the world this table is now active.
			selectTableAction(_current.table, true);
		} else /* not active */ {
			_current.active = false;
			_current = null;
			
			// check if there is a remoteActive
			for (Entry remoteEntry : getRemotes()) {
				if (remoteEntry.remoteActive) {
					selectTable(remoteEntry.table, true);
					break;
				}
			}
		}
	}
	
	@Override
	public void setRemoteTables(List<SelectionModel.Entry> list) {
		super.setRemoteTables(list);
		
		// select the first active entry
		for (Entry entry : list) {
			if (entry.remoteActive) {
				selectTable(entry.table, true);
				break;
			}
		}
	}
}