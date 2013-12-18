package edu.utah.sci.cyclist.ui.views;


import org.controlsfx.control.RangeSlider;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
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
import edu.utah.sci.cyclist.model.FieldProperties;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.model.Table.NumericRangeValues;
import edu.utah.sci.cyclist.ui.components.Spring;
import edu.utah.sci.cyclist.ui.panels.TitledPanel;
import edu.utah.sci.cyclist.util.SQL;

public class FilterPanel extends TitledPanel {

	private Filter _filter;
	private ListProperty<Object> _valuesProperty = new SimpleListProperty<>();
	private VBox _cbBox;
	private ProgressIndicator _indicator;
	private Button _closeButton;
	private Task<?> _task;
	private boolean _reportChange = true;
	private BooleanProperty _highlight = new SimpleBooleanProperty(false);
	MapProperty<Object, Object> _map = new SimpleMapProperty<>();
	private ObjectProperty<EventHandler<ActionEvent>> _closeAction = new SimpleObjectProperty<>();
			
	
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
		//super(filter.getName());
		super(getTitle(filter));
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
		return _closeAction;
	}
	
	public EventHandler<ActionEvent> getOnClose() {
		return _closeAction.get();
	}
	
	public void setOnClose(EventHandler<ActionEvent> handler) {
		_closeAction.set(handler);
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
		
		_closeButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				setTask(null);
				if(getOnClose() != null){
					getOnClose().handle(e);
				}
			}
		});
	
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
				System.out.println("highlight: "+newValue);
				if (newValue)
					getHeader().setStyle("-fx-background-color: #beffbf");
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
		_valuesProperty.addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable arg0) {
				populateValues();
				
			}
		});
		
		_valuesProperty.bind(_filter.valuesProperty());
		
		
		if (!_filter.isValid()) {
			Field field = _filter.getField();
			Table table = field.getTable();
			
			Task<ObservableList<Object>> task = table.getFieldValues(field);		
			setTask(task);

			field.valuesProperty().bind(task.valueProperty());
		} else {
			populateValues();
		}
	}
	
	private void populateValues() {
		_cbBox.getChildren().clear();
		if (_valuesProperty.get() != null) {
			_cbBox.getChildren().add(createAllEntry());
			for (Object item: _valuesProperty.get()) {
				_cbBox.getChildren().add(createEntry(item));
			}
		}
	}
	
	private Node createAllEntry() {
		CheckBox cb = new CheckBox("All");
		cb.setSelected(true);
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
	
	
	/* Name: createRange
	 * For numerical Fields - get the possible minimum and maximum values of the field 
	 * and creates a filter within that range */
	private void createRange() {
		
		_cbBox = new VBox();
		_cbBox.setSpacing(4);
		_cbBox.setPrefSize(USE_COMPUTED_SIZE,USE_COMPUTED_SIZE);
		setContent(_cbBox);
		_cbBox.setPadding(new Insets(0,8,0,8));
		
		_map.addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable arg0) {
				populateRangeValues();
			}
		});
		
		_map.bind(_filter.rangeValuesProperty());
		
		if (!_filter.isRangeValid()) {
			Field field = _filter.getField();
			Table table = field.getTable();
			
			Task<ObservableMap<Object,Object>> task = table.getFieldRange(field);
			setTask(task);
			field.rangeValuesProperty().bind(task.valueProperty());
		}
		else{
			populateRangeValues();
		}
	}
	
	/* Name: populateRangeValues
	 * Populates a filter panel for a "measure" type filter.
	 * In this case there would be a range slider which displays the possible values in the range 
	 * and the user can change the minimum and maximum values which are displayed */
	private void populateRangeValues() {
		_cbBox.getChildren().clear();
		if (_map.get() != null) {
			
			Double min = (Double) (_map.get(NumericRangeValues.MIN) != null ? _map.get(NumericRangeValues.MIN):0.0);
			Double max = (Double) (_map.get(NumericRangeValues.MAX) != null ? _map.get(NumericRangeValues.MAX):0.0);
			final RangeSlider rangeSlider = new RangeSlider(min,max,min,max);
			rangeSlider.setShowTickLabels(true);
			rangeSlider.setShowTickMarks(true);
			//rangeSlider.setOrientation(Orientation.VERTICAL);
			
			double currentWidth = this.widthProperty().doubleValue();
			_cbBox.setPrefWidth(0.95*currentWidth);
			
			double majorTicks = (rangeSlider.getMax()-rangeSlider.getMin())/4;
			rangeSlider.setMajorTickUnit(majorTicks);	
			final HBox hbox = new HBox();
			hbox.setSpacing(20);
			final TextField minTxt = new TextField();
			final TextField maxTxt = new TextField();
			hbox.getChildren().addAll(minTxt, maxTxt);
			
			 hbox.setPrefWidth(currentWidth);
			 hbox.setSpacing(currentWidth-120);
			
			minTxt.setPrefSize(80, 18);
			maxTxt.setPrefSize(80, 18);
			minTxt.setEditable(false);
			maxTxt.setEditable(false);
			minTxt.setAlignment(Pos.CENTER_LEFT);
			maxTxt.setAlignment(Pos.CENTER_LEFT);
			minTxt.setText(Double.toString(min));
			maxTxt.setText(Double.toString(max));

			_cbBox.getChildren().addAll(rangeSlider,hbox);
			
			//Listen to the width property of the parent - so the slider width can be changed accordingly.
			this.widthProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> observable, 
                        Number oldValue, Number newValue) {
						double width = newValue.doubleValue();
					    _cbBox.setPrefWidth(0.95*width);
				}
			});
			
			_cbBox.widthProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> observable, 
                        Number oldValue, Number newValue) {
						double width = newValue.doubleValue();
					    hbox.setPrefWidth(width);
					    hbox.setSpacing(width-160);
				}
			});
				
			
			// Change the filter's values according to the user new choice.
			rangeSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					_filter.selectMinMaxValues(rangeSlider.getLowValue(),rangeSlider.getHighValue());
				}
			}); 
			
			// Update the text field to show the current value on the slider.
			rangeSlider.setOnMouseDragged(new EventHandler<Event>() {
				public void handle(Event e) {
					minTxt.setText(Double.toString(rangeSlider.getLowValue()));
					maxTxt.setText(Double.toString(rangeSlider.getHighValue()));
				}
			});
				
		}
	}
	
	/* Name: getTitle
	 * Gets the filter name and function (if exists).
	 * If the filter is connected to a field, and the field has an aggregation function
	 * sets the filter panel header to display both the filter name and the function*/
	
	
	private static String getTitle(Filter filter) {
		String title = null;
		if (filter.getRole() == Role.DIMENSION) {
			title = filter.getName();
		} else {
			String funcName = filter.getField().get(FieldProperties.AGGREGATION_FUNC, String.class);
			if (funcName == null){
				funcName = filter.getField().get(FieldProperties.AGGREGATION_DEFAULT_FUNC, String.class);
				filter.getField().set(FieldProperties.AGGREGATION_FUNC, funcName);
			}
			SQL.Function func = SQL.getFunction(funcName);
			title = func.getLabel(filter.getName());
		}
		
		return title;
	}
}
