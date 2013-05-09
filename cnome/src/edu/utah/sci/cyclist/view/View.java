package edu.utah.sci.cyclist.view;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.model.Table;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface View {
	ObjectProperty<EventHandler<ActionEvent>> onMinmaxProperty();
	ObjectProperty<EventHandler<ActionEvent>> onCloseProperty();
	
	void setOnTableDrop(Closure.V1<Table> action);
	
	void addTable(Table table, boolean local);
}
