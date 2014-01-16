package edu.utah.sci.cyclist.ui;

import javafx.collections.ObservableList;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Table;

public interface CyclistView extends View {
	void setOnTableDrop(Closure.V1<Table> action);
	void setOnTableRemoved(Closure.V1<Table> action);
	void setOnTableSelectedAction(Closure.V2<Table, Boolean> action);
	void setOnSelectAction(Closure.V0 action);
	void setOnShowFilter(Closure.V1<Filter> action);
	void setOnRemoveFilter(Closure.V1<Filter> action);
	
	void addTable(Table table, boolean remote, boolean active);
	void removeTable(Table table);
	void selectTable(Table table, boolean value);
	
	ObservableList<Filter> remoteFilters();
}
