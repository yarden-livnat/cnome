package edu.utah.sci.cyclist.presenter;

import java.util.ArrayList;
import java.util.List;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.model.Table;

public class SelectionModel {
	private List<Entry> _list = new ArrayList<Entry>();
	private Closure.V2<Table, Boolean> _onSelectTableAction = null;
	private Entry _explicitSelection = null;
	
	public void setOnSelectTableAction(Closure.V2<Table, Boolean> action) {
		_onSelectTableAction = action;
	}
	
	public void selectTableAction(Table table, Boolean remote) {
		if (_onSelectTableAction != null) {
			_onSelectTableAction.call(table, remote);
		}
	}
	
	public void addTable(Table table, boolean remote, boolean active, boolean remoteActive) {
		_list.add(new Entry(table, remote, active, remoteActive));
	}
	
	public void removeTable(Table table) {
		Entry entry = getEntry(table);
		if (entry != null) {
			_list.remove(entry);
		}
	}
	
	public Entry getEntry(Table table) {
		for (Entry entry : _list)
			if (entry.table == table)
				return entry;
		return null;
	}
	
	public List<Entry> getTableRecords() {
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
	
	public Table getSelected() {
		return null;
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
			addTable(entry.table, entry.remote, entry.active, entry.remoteActive);
		}
	}
	
	public class Entry {
		Table table;
		boolean remote;
		boolean active;
		boolean remoteActive;
		
		public Entry(Table table, boolean remote, boolean active, boolean remoteActive) {
			this.table = table;
			this.remote = remote;
			this.active = active;
			this.remoteActive = remoteActive;
		}
		
		public Entry clone() {
			return new Entry(table, remote, active, remoteActive);
		}
	}
}

