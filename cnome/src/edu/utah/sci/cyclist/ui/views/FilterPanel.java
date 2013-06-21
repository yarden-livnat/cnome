package edu.utah.sci.cyclist.ui.views;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.dnd.DnD;
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
	private BooleanProperty _highlight = new SimpleBooleanProperty(false);
			
	
	public BooleanProperty highlight() {
		return _highlight;
	}
	
	public void setHighlight(boolean value) {
		_highlight.set(value);
	}
	
	public boolean getHighlight() {
		return _highlight.get();
	}
	
	public FilterPanel(Filter filter) {
		super(filter.getName());
		_filter = filter;
		
		build();
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
	
	
	
	private void build() {
		setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		
		final HBox header = getHeader();
		
		_indicator = new ProgressIndicator();
		_indicator.setProgress(-1);
		_indicator.setMaxWidth(20);
		_indicator.setMaxHeight(20);
		_indicator.setVisible(false);
				
		_closeButton = new Button();
		_closeButton.getStyleClass().add("flat-button");
		_closeButton.setGraphic(new ImageView(Resources.getIcon("close_view")));
	
		header.getChildren().addAll(_indicator, new Spring(), _closeButton);		
		
		header.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Dragboard db = header.startDragAndDrop(TransferMode.MOVE);
				
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.FILTER_FORMAT, Filter.class, _filter);
				
				ClipboardContent content = new ClipboardContent();
				content.putString(_filter.getName());
				
				SnapshotParameters snapParams = new SnapshotParameters();
	            snapParams.setFill(Color.TRANSPARENT);
	            
	            content.putImage(header.getChildren().get(0).snapshot(snapParams, null));	
				
				db.setContent(content);
			}
		});
		
		header.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				_highlight.set(!_highlight.get());
			}
		});
		
		_highlight.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldValue, Boolean newValue) {
				System.out.println("highlight: "+oldValue+"  "+newValue);
				if (newValue)
					getHeader().setStyle("-fx-background-color: #808080");
				else
					getHeader().setStyle("-fx-background-color: #e0e0ef");
				
			}
		});
		
		
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
			createRange();
			break;
		case Qi:
			createList();
		}
	}
	
	private void createList() {
		_cbBox = new VBox();
		_cbBox.setSpacing(4);
		_cbBox.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		
		setContent(_cbBox);
//		VBox.setVgrow(_cbBox, Priority.ALWAYS);
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
