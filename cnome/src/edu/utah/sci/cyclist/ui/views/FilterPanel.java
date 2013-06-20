package edu.utah.sci.cyclist.ui.views;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.components.Spring;
import edu.utah.sci.cyclist.ui.panels.TitledPanel;

public class FilterPanel extends TitledPanel {

	private Filter _filter;
	private ListProperty<Object> _valuesProperty = new SimpleListProperty<>();
	private VBox _cbBox;
	private ProgressIndicator _indicator;
	private Button _closeButton;
	private Task<?> _task;
	private boolean _reportChange = true;
	
	public FilterPanel(Filter filter) {
		super(filter.getName());
		_filter = filter;
		
		configure();
	}
	
	public Filter getFilter() {
		return _filter;
	}
	
	public void setTask(Task<?> task) {
		if (_task != null && _task.isRunning()) {
			_task.cancel();
		}
		
		_task = task;
		
		if (_task == null) {
			_indicator.visibleProperty().unbind();
			_indicator.setVisible(false);
		} else {
			_indicator.visibleProperty().bind(_task.runningProperty());	
			_indicator.setOnMouseClicked(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					System.out.println("Canceling task: "+_task.cancel());				
				}
			});
		}
	}
	
	/*
	 * Close 
	 */
	public ObjectProperty<EventHandler<ActionEvent>> onCloseProperty() {
		return _closeButton.onActionProperty();
	}
	
	public EventHandler<ActionEvent> getOnClose() {
		return _closeButton.getOnAction();
	}
	
	public void setOnClose(EventHandler<ActionEvent> handler) {
		_closeButton.setOnAction(handler);
	}
	
	private void configure() {
		HBox header = getHeader();
		header.getChildren().addAll(
			_indicator = ProgressIndicatorBuilder.create()
				.progress(-1)
				.maxWidth(20)
				.maxHeight(20)
				.visible(false)
				.build(),
			new Spring(),
			_closeButton = ButtonBuilder.create()
				.styleClass("flat-button")
				.graphic(new ImageView(Resources.getIcon("close_view")))
				.build() 
		);
				
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
				
				_reportChange = false;
				for (Node node : _cbBox.getChildren()) {
					((CheckBox) node).setSelected(newValue);
				}
				_reportChange = true;
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
				if (_reportChange)
					_filter.selectValue(item, newValue);		
			}
		});
		return cb;
	}
	
	
	private void createRange() {
		
	}
}
