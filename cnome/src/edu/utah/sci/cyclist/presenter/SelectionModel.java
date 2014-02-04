package edu.utah.sci.cyclist.presenter;

import java.util.ArrayList;
import java.util.List;
import org.mo.closure.v1.Closure;

public class SelectionModel<T> {
	
	private List<Entry> _list = new ArrayList<Entry>();
	private Closure.V2<T, Boolean> _onSelectItemAction = null;
	private Entry _explicitSelection = null;
	
	//Possible types of objects that the entries contain.
	public enum selectedTypes{TABLE,SIMULATION};
	
	public List<Entry> getEntries() {
		return _list;
	}
	
	public void setOnSelectItemAction(Closure.V2<T, Boolean> action) {
		_onSelectItemAction = action;
	}
	
	public void selectItemAction(T selectedItem, Boolean remote) {
		if (_onSelectItemAction != null) {
			_onSelectItemAction.call(selectedItem, remote);
		}
	}
	
	public void addItem(T item, boolean remote, boolean active, boolean remoteActive) {
		_list.add(new Entry(item, remote, active, remoteActive));
	}
	
	public void removeItem(T item) {
		Entry entry = getEntry(item);
		if (entry != null) {
			_list.remove(entry);
		}
	}
	
	public Entry getEntry(T item) {
		for (Entry entry : _list)
			if (entry.item == item)
				return entry;
		return null;
	}
	
	public List<Entry> getItemRecords() {
		List<Entry> records = new ArrayList<>();
		for (Entry entry : _list) {
			records.add(entry.clone());
		}
		return records;
	}
	
	public List<Entry> getRemotes() {
		List<Entry> records = new ArrayList<>();
		for (Entry entry : _list)
			if (entry.remote)
				records.add(entry);
		return records;
	}
	
	public T getSelected() {
		return null;
	}
	
	public boolean isExplicit() {
		return _explicitSelection != null;
	}
	
	/**
	 * Select at table 
	 */
	public void selectItem(T item, boolean active) {
		// ignore here.
	}
	
	/*
	 * The UI has changed
	 */
	public void itemSelected(T item, boolean active) {
		Entry entry = getEntry(item);
		if (active) _explicitSelection = entry;
		else if (entry.active) _explicitSelection = null;
	}
	
	public void setRemoteItems(List<SelectionModel<T>.Entry> list) {
		for (Entry entry : list) {
			addItem(entry.item, true/*entry.remote*/, entry.active, entry.remoteActive);
		}
	}
	
	public Boolean isRemoteActive(T item){
		Entry entry = getEntry(item);
		if(entry != null){
			return entry.remoteActive;
		}
		return false;
	}
	
	public class Entry {
		//Table table;
		T 		item;
		boolean remote;
		boolean active;
		boolean remoteActive;
		
		public Entry(/*Table table*/ T item, boolean remote, boolean active, boolean remoteActive) {
			//this.table = table;
			this.item = item;
			this.remote = remote;
			this.active = active;
			this.remoteActive = remoteActive;
		}
		
		public Entry clone() {
			//return new Entry(table, remote, active, remoteActive);
			return new Entry(item, remote, active, remoteActive);
		}
	}
}

