package edu.utah.sci.cyclist.neup.ui.views.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import edu.utah.sci.cyclist.core.ui.components.TaskControl;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import edu.utah.sci.cyclist.neup.model.Transaction;


class FlowNode extends Pane {
	private int _direction;
	private Object _value;
	private String _type;
	private boolean _explicit;
	private String _color;
	private BooleanProperty _selected = new SimpleBooleanProperty(false);
	private BooleanProperty _hover = new SimpleBooleanProperty(false);

	
	private ObservableList<Transaction> _transactions = FXCollections.observableArrayList();
	private FilteredList<Transaction> _activeTransactions = new FilteredList<>(_transactions);
	
	private DoubleProperty _anchorXProperty = new SimpleDoubleProperty();
	private DoubleProperty _anchorYProperty = new SimpleDoubleProperty();
	
	private Consumer<FlowNode> _onOpen;
	private Consumer<FlowNode> _onClose;
	private Consumer<FlowNode> _onSelect;
	
	private List<Connector> _connectors = new ArrayList<>();
	
	private VBox _vbox;
	private ToggleIcon _button;
	private TaskControl _ctrl;
	private Label _graphIcon;
	
	/*
	 * Properties 
	 */

	public FilteredList<Transaction> getActiveTransactions() {
		return _activeTransactions;
	}
	
	public ReadOnlyDoubleProperty anchorXProperty() {
		return _anchorXProperty;
	}
	
	public double getAnchorX() {
		return anchorXProperty().get();
	}
	
	public ReadOnlyDoubleProperty anchorYProperty() {
		return _anchorYProperty;
	}
	
	public double getAnchorY() {
		return anchorYProperty().get();
	}

	public void setOnOpen(Consumer<FlowNode> func) {
		_onOpen = func;
	}
	
	public void setOnClose(Consumer<FlowNode> func) {
		_onClose = func;
	}
	
	public void setOnSelect(Consumer<FlowNode> func) {
		_onSelect = func;
	}
	
	/**
	 * Constructor
	 * @param type
	 * @param value
	 * @param direction
	 * @param explicit
	 */
	public FlowNode(String type, Object value, int direction, boolean explicit) {
		super();
		
		_type = type;
		_value = value;
		_direction = direction;
		
		build(type, value);
		
		if (direction == FlowView.SRC) 
			_anchorXProperty.bind( translateXProperty().add(widthProperty()));
		else
			_anchorXProperty.bind(translateXProperty());	
		_anchorYProperty.bind( layoutYProperty().add(translateYProperty()).add(heightProperty().divide(2)));
		
		this.setExplicit(explicit);
	}	
	
	/*
	 * access function
	 * 
	 */
	
	public void setColor(Color color) {
		_color = color.toString().replace("0x", "#"); 
	}
	
	public Object getValue() {
		return _value;
	}
	
	public boolean isExplicit() {
		return _explicit;
	}
	
	public boolean isImplicit() {
		return !_explicit;
	}
	
	public void setExplicit(boolean value) {
		if (_explicit == value) return;
		
		_explicit = value;
		_button.select(value);
		if (value) {
			_vbox.getStyleClass().add("node-explicit");
		}
		else {
			_vbox.getStyleClass().remove("node-explicit");
		}
	}
	
	public int getDirection() {
		return _direction;
	}
	
	
	public boolean isSRC() {
		return _direction == FlowView.SRC;
	}
	
	public String getType() {
		return _type;
	}
	
	public void setTransactions(List<Transaction> list) {
		_transactions.setAll(list);
	}
	
	public void addConnector(Connector c) {
		_connectors.add(c);
	}
	
	public void removeConnector(Connector c) {
		_connectors.remove(c);
	}
	
	public List<Connector> getConnectors() {
		return _connectors;
	}
	
	public void setTask(Task<?> task) {
		_ctrl.setTask(task);
	}
	
	public void release() {	
	}
	
	public void setSelected(boolean value) {
		if (_selected.get() != value) {
			_selected.set(value);
			_graphIcon.setStyle("-fx-background-color:"+(value?_color: "transparent"));
		}
//		if (value) {
//			RotateTransition rt = new RotateTransition(Duration.millis(3000),_graphIcon);
//			rt.setByAngle(360);
//			rt.setCycleCount(4);
//			rt.play();
//		}
	}
	
	public boolean isSelected() {
		return _selected.get();
	}
	
	public String getName() {
		return _value instanceof String ?
			_value.toString()
			:
			_type+" = "+_value.toString();
	}
	
	private void build(String type, Object value) {
//		String label = value.toString();
//		if (!(value instanceof String))
//				label = type+":"+label;
//		
//		_color = Configuration.getInstance().getColor(label);
		
		_vbox = new VBox();
		_vbox.getStyleClass().add("flow-node");
		
		// header
		HBox header = new HBox();
		header.getStyleClass().add("node-header");
		header.setPrefWidth(75);
		
		Label icon = GlyphRegistry.get(AwesomeIcon.BAR_CHART_ALT, "10px");
		_graphIcon = new Label("", icon);
		_graphIcon.setStyle("-fx-padding: 1px");
		_graphIcon.visibleProperty().bind(_selected.or(_hover));
		_graphIcon.setPrefWidth(20);
		_button = new ToggleIcon(GlyphRegistry.get(AwesomeIcon.EXTERNAL_LINK, "10px"), GlyphRegistry.get(AwesomeIcon.TIMES, "10px"));
		_button.setVisible(false);
			
		_ctrl = new TaskControl();

		header.getChildren().addAll(
				_graphIcon,
				_ctrl,
				_button);
		header.setSpacing(2);
		HBox.setHgrow(_ctrl,  Priority.SOMETIMES);
		_vbox.getChildren().add(header);

			
		Text body = new Text(_value.toString());
		body.getStyleClass().add("node-body");
		_vbox.getChildren().add(body);
	
		getChildren().add(_vbox);
		
		/*
		 * add listeners
		 */
		_graphIcon.setOnMouseClicked(e->{
			if (_onSelect != null) 
				_onSelect.accept(this);
		});
		
		_button.setOnMouseClicked(e->{
			if (isExplicit()) {
				if (_onClose != null) _onClose.accept(FlowNode.this);
			} else {
				if (_onOpen != null) _onOpen.accept(FlowNode.this);
			}
		});
		
		_vbox.setOnMouseEntered(e->{
			_hover.set(true);
			_button.setVisible(true);
		});
		
		_vbox.setOnMouseExited(e->{
			_hover.set(false);
			_button.setVisible(false);
		});
	}
}