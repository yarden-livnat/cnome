package edu.utah.sci.cyclist.ui.views;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.panels.Panel;

public class FilterPanel extends Panel {

	private Filter _filter;
	private ListProperty<Object> _valuesProperty = new SimpleListProperty<>();
	private VBox _cbBox;
	
	public FilterPanel(Filter filter) {
		super(filter.getName());
		_filter = filter;
		
		configure();
	}
	
	public Filter getFilter() {
		return _filter;
	}
	
	private void configure() {
		switch (_filter.getClassification()) {
		case C:
			createList();
			break;
		case Cdate:
			break;
		case Qd:
			createRange();
			break;
		case Qi:
			createList();
		}
	}
	
	private void createList() {
		_cbBox = VBoxBuilder.create()
				.spacing(4)
				.build();
		setContent(_cbBox);
		
		_valuesProperty.addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable arg0) {
				_cbBox.getChildren().clear();
				if (_valuesProperty.get() != null) {
					_cbBox.getChildren().add(createAllEntry());
					for (Object item: _valuesProperty.get()) {
						_cbBox.getChildren().add(createEntry(item));
					}
				}
			}
		});
		
		_valuesProperty.bind(_filter.valuesProperty());
		
		
		if (!_filter.isValid()) {
			Field field = _filter.getField();
			Table table = field.getTable();
			
			Task<ObservableList<Object>> task = table.getFieldValues(field);		
			setTask(task);

			field.valuesProperty().bind(task.valueProperty());
		}
	}
	
	private Node createAllEntry() {
		CheckBox cb = new CheckBox("All");
//		cb.setSelected(true)
		cb.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldValue, Boolean newValue) {
				for (Node node : _cbBox.getChildren()) {
					((CheckBox) node).setSelected(newValue);
				}
				_filter.selectAll(newValue);
			}
		});
		return cb;
	}
	
	private Node createEntry(final Object item) {
		CheckBox cb = new CheckBox(item.toString());
		cb.setSelected(_filter.isSelected(item));
		cb.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldValue, Boolean newValue) {
				_filter.selectValue(item, newValue);		
			}
		});
		return cb;
	}
	
	
	private void createRange() {
		
	}
}
