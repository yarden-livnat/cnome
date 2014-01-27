package edu.utah.sci.cyclist.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.model.Table;

public class SelectionModel {
	
//	private List<Entry> _list = new ArrayList<Entry>();
	private Map<selectedTypes,List<Entry>> _listsMap = new HashMap<selectedTypes,List<Entry>>();
	private Closure.V2<Table, Boolean> _onSelectTableAction = null;
	private Entry _explicitSelection = null;
	
	//Possible types of objects that the entries contain.
	public enum selectedTypes{TABLE,SIMULATION};
	
	public List<Entry> getEntries(selectedTypes type) {
		//return _list;
		return _listsMap.get(type);
	}
	
	public void setOnSelectTableAction(Closure.V2<Table, Boolean> action) {
		_onSelectTableAction = action;
	}
	
	public void selectTableAction(Table table, Boolean remote) {
		if (_onSelectTableAction != null) {
			_onSelectTableAction.call(table, remote);
		}
	}
	
	public void addTable(Table table, boolean remote, boolean active, boolean remoteActive) {
		if(_listsMap.get(selectedTypes.TABLE) == null){
			_listsMap.put(selectedTypes.TABLE, new ArrayList<Entry>());
		}
		//_list.add(new Entry(table, remote, active, remoteActive));
		_listsMap.get(selectedTypes.TABLE).add(new Entry(table, remote, active, remoteActive));
	}
	
	public void removeTable(Table table) {
		Entry entry = getEntry(table);
		if (entry != null) {
			//_list.remove(entry);
			_listsMap.get(selectedTypes.TABLE).remove(entry);
		}
	}
	
	public Entry getEntry(Table table) {
		List<Entry> list = _listsMap.get(selectedTypes.TABLE);
		for (Entry entry : list)
			if ((Table)entry.object == table)
				return entry;
		return null;
	}
	
	public List<Entry> getTableRecords() {
		List<Entry> records = new ArrayList<>();
		List<Entry> list = _listsMap.get(selectedTypes.TABLE);
		if(list != null){
			for (Entry entry : list) {
				records.add(entry.clone());
			}
		}
		return records;
	}
	
	public List<Entry> getRemotes() {
		List<Entry> records = new ArrayList<>();
		List<Entry> list = _listsMap.get(selectedTypes.TABLE);
		for (Entry entry : list)
			if (entry.remote)
				records.add(entry);
		return records;
	}
	
	public Table getSelected() {
		return null;
	}
	
	public boolean isExplicit() {
		return _explicitSelection != null;
	}
	
	/**
	 * Select at table 
	 */
	public void selectTable(Table table, boolean active) {
		// ignore here.
	}
	
	/*
	 * The UI has changed
	 */
	public void tableSelected(Table table, boolean active) {
		Entry entry = getEntry(table);
		if (active) _explicitSelection = entry;
		else if (entry.active) _explicitSelection = null;
	}
	
	public void setRemoteTables(List<SelectionModel.Entry> list) {
		for (Entry entry : list) {
			addTable((Table)entry.object, entry.remote, entry.active, entry.remoteActive);
		}
	}
	
	public Boolean IsRemoteActive(Table table){
		Entry entry = getEntry(table);
		if(entry != null){
			return entry.remoteActive;
		}
		return false;
	}
	
	public class Entry {
		//Table table;
		Object object;
		boolean remote;
		boolean active;
		boolean remoteActive;
		
		public Entry(/*Table table*/ Object object, boolean remote, boolean active, boolean remoteActive) {
			//this.table = table;
			this.object = object;
			this.remote = remote;
			this.active = active;
			this.remoteActive = remoteActive;
		}
		
		public Entry clone() {
			//return new Entry(table, remote, active, remoteActive);
			return new Entry(object, remote, active, remoteActive);
		}
	}
}

