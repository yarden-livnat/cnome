package edu.utah.sci.cyclist.view;

import edu.utah.sci.cyclist.event.ui.CyclistDropEvent;
import edu.utah.sci.cyclist.model.Table;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface View {
	ObjectProperty<EventHandler<ActionEvent>> onMinmaxProperty();
	ObjectProperty<EventHandler<ActionEvent>> onCloseProperty();
	ObjectProperty<EventHandler<CyclistDropEvent>> onDatasourceActionProperty();
	
	void addTable(Table table);
}
