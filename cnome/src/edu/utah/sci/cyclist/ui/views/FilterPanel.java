package edu.utah.sci.cyclist.ui.views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.panels.Panel;

public class FilterPanel extends Panel {

	private Filter _filter;
	
	public FilterPanel(Filter filter) {
		super(filter.getName());
		_filter = filter;
		
		configure();
	}
	
	private void configure() {
		switch (_filter.getClassification()) {
		case C:
			createList();
			break;
		case Cdate:
			break;
		case Qd:
			
			break;
		case Qi:
			createList();
		}
	}
	
	private void createList() {
		Field field = _filter.getField();
		Table table = field.getTable();
		
		Task<ObservableList<Object>> task = table.getFieldValues(field);		
		setTask(task);
		
		final VBox vbox = VBoxBuilder.create()
							.spacing(4)
							.build();
		setContent(vbox);
		
		task.valueProperty().addListener(new ChangeListener<ObservableList<Object>>() {

			@Override
			public void changed(
					ObservableValue<? extends ObservableList<Object>> observable,
					ObservableList<Object> oldList, ObservableList<Object> newList) {
				if (newList != null) {
					vbox.getChildren().clear();
//					vbox.getChildren().add(createAllEntry());
					
					for (Object item : newList) {
						vbox.getChildren().add(createEntry(item));
					}
				}
				
			}
		});
	}
	
//	private Node createAllEntry() {
//		CheckBox cb = new CheckBox("All");
//		cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
//
//			@Override
//			public void changed(ObservableValue<? extends Boolean> arg0,
//					Boolean oldValue, Boolean newValue) {
//				for ()
//			}
//		});
//		return cb;
//	}
	
	private Node createEntry(final Object item) {
		CheckBox cb = new CheckBox(item.toString());
		cb.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldValue, Boolean newValue) {
				_filter.selectValue(item, newValue);		
			}
		});
		return cb;
	}
	
}
