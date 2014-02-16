package edu.utah.sci.cyclist.neup.ui.views.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import edu.utah.sci.cyclist.core.ui.components.CycistToggleButton;
import edu.utah.sci.cyclist.core.ui.components.Spring;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import edu.utah.sci.cyclist.neup.model.Transaction;
import edu.utah.sci.cyclist.neup.ui.views.FlowView;


class FlowNode extends Pane {
	private int _direction;
	private Object _value;
	private String _type;
	private boolean _explicit;
	
	private ObservableList<Transaction> _transactions = FXCollections.observableArrayList();
	private FilteredList<Transaction> _activeTransactions = new FilteredList<>(_transactions);
	
	private DoubleProperty _anchorXProperty = new SimpleDoubleProperty();
	private DoubleProperty _anchorYProperty = new SimpleDoubleProperty();
	
	private Consumer<FlowNode> _onOpen;
	private Consumer<FlowNode> _onClose;
	private Consumer<FlowNode> _onSelect;
	
	private List<Connector> _connectors = new ArrayList<>();
	
	private VBox _vbox;
	private Label _button;
	
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
		
		build(type);
		
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
		_explicit = value;
		if (value) {
			_vbox.getStyleClass().add("node-explicit");
			_button.setGraphic(GlyphRegistry.get(AwesomeIcon.TIMES, "10px"));
		}
		else {
			_vbox.getStyleClass().remove("node-explicit");
			_button.setGraphic(GlyphRegistry.get(AwesomeIcon.EXTERNAL_LINK, "10px"));
		}
	}
	
	public int getDirection() {
		return _direction;
	}
	
	
	public boolean isSRC() {
		return _direction == Flow.SRC;
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
	
	public void release() {	
	}
	
	private void build(String label) {
		_vbox = new VBox();
		_vbox.getStyleClass().add("flow-node");
		
		// header
		HBox header = new HBox();
		header.getStyleClass().add("node-header");
		
		Label _select = new Label("", GlyphRegistry.get(AwesomeIcon.BAR_CHART_ALT, "10px"));
		_select.setVisible(false);
		
		_button = new Label();
		_button.setVisible(false);
			
		header.getChildren().addAll(
				_select,
				new Spring(),
				_button);
		
		_vbox.getChildren().add(header);
			
		Text body = new Text(_value.toString());
		body.getStyleClass().add("node-body");
		_vbox.getChildren().add(body);
	
		getChildren().add(_vbox);
		
		/*
		 * add listeners
		 */
		_select.setOnMouseClicked(e->{
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
			_select.setVisible(true);
			_button.setVisible(true);
		});
		
		_vbox.setOnMouseExited(e->{
			_select.setVisible(false);
			_button.setVisible(false);
		});
	}
}