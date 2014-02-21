package edu.utah.sci.cyclist.neup.ui.views.flow;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import edu.utah.sci.cyclist.neup.model.Transaction;

public class FlowLine  {

	public final int SPACING = 10;
	public final int Y0 = 40;
	public final int GAP = 5;
	public final int BOTTOM_OFFSET = 10;
	public final int INFO_OFFSET = 120;

	private Pane _parent;
	private int _direction;
	private ObservableList<FlowNode> _nodes = FXCollections.observableArrayList();
//	private Map<FlowNode, FlowInfo> _infos = new HashMap<>();
	
	private Line _line;
	private ChoiceBox<String> _choiceBox;
	private DoubleProperty _infoOffset = new SimpleDoubleProperty();
	
	private IntegerProperty _infoMode = new SimpleIntegerProperty();
	private ObjectProperty<Function<Transaction, Object>> _aggregateFuncProperty = new SimpleObjectProperty<>();
	
	private DoubleProperty _centerX = new SimpleDoubleProperty();
	private DoubleProperty _startY = new SimpleDoubleProperty();
	private DoubleProperty _endY = new SimpleDoubleProperty();
	private DoubleProperty _infoX = new SimpleDoubleProperty();

	/*
	 * Properties
	 */
	
	public IntegerProperty charModeProperty() {
		return _infoMode;
	}
	
	public ObjectProperty<Function<Transaction, Object>> aggregationFunProperty() {
		return _aggregateFuncProperty;
	}
	
	public DoubleProperty centerXProperty() {
		return _centerX;
	}
	
	public DoubleProperty startYProperty() {
		return _startY;
	}
		
	public DoubleProperty endYProperty() {
		return _endY;
	}
	
	public DoubleProperty infoXProperty() {
		return _infoX;
	}
	
	public ReadOnlyDoubleProperty widthProperty() {
		return _choiceBox.widthProperty();
	}
	
	/**
	 * Constructor
	 * @param direction 
	 */
	public FlowLine(int direction, Pane parent) {
		_direction = direction;
		_parent = parent;
		_infoOffset.set(direction==FlowView.SRC ? -INFO_OFFSET : INFO_OFFSET);
		build();
		init();
	}

	public int getDirection() {
		return _direction;
	}
	
	public void addNode(final FlowNode node) {
		double y = _line.getStartY();
		if (_nodes.size() > 0) {
			FlowNode last = _nodes.get(_nodes.size()-1);
			y = last.getTranslateY()+last.getHeight()+SPACING;
		}
		
		node.translateXProperty().bind(centerXProperty().subtract(node.widthProperty().divide(2)));
		node.setTranslateY(y);
		
		addListeners(node);
		_nodes.add(node);
		_parent.getChildren().add(node);
		
//		FlowInfo info = new FlowInfo(node);
//		info.translateXProperty().bind(infoXProperty());
//		info.translateYProperty().bind(node.translateYProperty());
//		_infos.put(node, info);
//		_parent.getChildren().add(info);
	}

	public void removeNode(FlowNode node) {
		removeListeners(node);
		_nodes.remove(node);
		_parent.getChildren().remove(node);
		
		if (_nodes.isEmpty())
			_choiceBox.setValue(null);
	}

//	public void removeNodes(Predicate<FlowNode> pred) {
//		_nodes.stream()
//			.filter(pred)
//			.collect(Collectors.toList()).stream()
//				.forEach(n->removeNode(n));
//	}
	
	public ObservableList<FlowNode> getNodes() {
		return _nodes;
	}
	
	public List<FlowNode> selectNodes(Predicate<FlowNode> pred) {
		return _nodes.stream()
				.filter(pred)
				.collect(Collectors.toList());
	}
	
	public FlowNode findNode(Object value) {
		for (FlowNode node : _nodes) {
			if (node.getValue().equals(value))
				return node;
		}

		return null;
	}
	
	public double getCenter() {
		return centerXProperty().doubleValue();
	}

	public ObjectProperty<String> kindProperty() {
		return _choiceBox.valueProperty();
	}
	
	public String getKind() {
		return _choiceBox.getValue();
	}

	public void setKind(String value) {
		_choiceBox.setValue(value);
	}
	
	public void setKindItems(Set<String> items) {
		_choiceBox.getItems().addAll(items);
	}
	
	public ReadOnlyDoubleProperty widthPropery() {
		return _choiceBox.widthProperty();
	}
		
	private double _my;
	
	private void addListeners(FlowNode node) {
		node.setOnMousePressed(e->{
			_my = e.getSceneY() - node.getTranslateY();
		});
		
		node.setOnMouseDragged(e->{
			double py = e.getSceneY()-_my;
			if (py >= _line.getStartY() && py <= _line.getEndY()-node.getHeight())
				node.setTranslateY(py);
		});
	}
	
	private void removeListeners(FlowNode node) {
		node.setOnMousePressed(null);
		node.setOnMouseDragged(null);
	}
	
	
	private void init() {
//		_infoMode.addListener(new ChangeListener<Number>() {
//
//			@Override
//			public void changed(ObservableValue<? extends Number> observable,
//					Number oldValue, Number newValue) {
//				for (FlowInfo info : _infos.values()) {
//					info.setMode(_infoMode.get());
//				}
//			}
//		});
		
//		_aggregateFuncProperty.addListener(new ChangeListener<Function<Transaction, Object>>() {
//
//			@Override
//			public void changed(ObservableValue<? extends Function<Transaction, Object>> observable,
//					Function<Transaction, Object> oldValue, Function<Transaction, Object> newValue) {
//				for (FlowInfo info : _infos.values()) {
//					info.setType(_aggregateFuncProperty.get());
//				}
//			}
//		});
		
	}
	
	private void build() {
		_choiceBox = new ChoiceBox<>();
		_choiceBox.getStyleClass().add("choice");
		_choiceBox.translateXProperty().bind(centerXProperty().subtract(_choiceBox.widthProperty().divide(2)));
		_choiceBox.translateYProperty().bind(startYProperty());
		
		_line = new Line();
		_line.getStyleClass().add("line");
		_line.startXProperty().bind(centerXProperty());
		_line.endXProperty().bind(_line.startXProperty());
		
		_line.startYProperty().bind(_choiceBox.translateYProperty().add(_choiceBox.heightProperty().add(GAP)));
		_line.endYProperty().bind(endYProperty());

		_parent.getChildren().addAll(_choiceBox, _line);
		
	}
	
}
