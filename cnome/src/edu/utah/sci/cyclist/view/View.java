package edu.utah.sci.cyclist.view;

import java.util.List;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.model.Table;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface View {
	ObjectProperty<EventHandler<ActionEvent>> onMinmaxProperty();
	ObjectProperty<EventHandler<ActionEvent>> onCloseProperty();
	
	void setOnTableDrop(Closure.V1<Table> action);
	void setOnTableSelected(Closure.V1<Table> action);
	
	void setTables(List<Table> list, Table table);
	void addTable(Table table, boolean local);
	void addTable(Table table, boolean local, boolean activate);
	void tableSelected(Table table);
}
