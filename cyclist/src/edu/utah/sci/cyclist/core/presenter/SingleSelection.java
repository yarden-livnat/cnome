package edu.utah.sci.cyclist.core.presenter;

import java.util.List;

public class SingleSelection<T> extends SelectionModel<T> {
	private Entry _current = null;
	
	@Override
	public T getSelected() {
		return _current == null ? null : _current.item;
	}
	
	@Override
	public void addItem(T item, boolean remote, boolean active, boolean remoteActive) {
		super.addItem(item, remote, false, remoteActive);
		
		if(active){
			if (_current == null) {
				selectItem(item, true);
			} else if (!remote) {
				selectItem(item, true);
			} else {
				// select a remote only if no local table is active
			}
		}
	}
	
	@Override
	public void removeItem(T item) {
		selectItem(item, false);
		super.removeItem(item);
	}
	
	/**
	 * 
	 */
	public void selectItem(T item, boolean active) {
		Entry entry = getEntry(item);
		//System.out.println("   SingleSelectionModel: selectTable: "+table.getName()+"  active:"+active+(entry.active == active? "   ignore": ""));
		if (entry== null || entry.active == active) {
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
					selectItemAction(_current.item, false);
				}
			}
			_current = entry;
			entry.active = true;
			selectItemAction(item, true);
		} else /* unselect */ {
			entry.active = false;
			if (entry == _current) {
				_current = null;
			}
			selectItemAction(item, false);
			
			// TODO: select a default one from the remote?
		}
	}
	
	/**
	 * an item status was changed via a button click
	 */
	@Override
	public void itemSelected(T item, boolean active) {
		//System.out.println("tableSelected: "+table.getName()+"  active:"+active);
		Entry entry = getEntry(item);
		
		if (entry == null || entry.active == active) {
			// ignore
		} else if (active) {
			if (_current != entry && _current != null) {
				_current.active = false;
				selectItemAction(_current.item, false);
			}
			_current = entry;
			_current.active = true;
			// inform the world this table is now active.
			selectItemAction(_current.item, true);
		} else /* not active */ {
			_current.active = false;
			_current = null;
			
			// check if there is a remoteActive
			for (Entry remoteEntry : getRemotes()) {
				if (remoteEntry.remoteActive) {
					selectItem(remoteEntry.item, true);
					break;
				}
			}
		}
	}
	
	@Override
	public void setRemoteItems(List<SelectionModel<T>.Entry> list) {
		super.setRemoteItems(list);
		
		// select the first active entry
		for (Entry entry : list) {
			if (entry.remoteActive) {
				selectItem(entry.item, true);
				break;
			}
		}
	}
}