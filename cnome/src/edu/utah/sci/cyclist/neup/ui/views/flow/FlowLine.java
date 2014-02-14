package edu.utah.sci.cyclist.neup.ui.views.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import edu.utah.sci.cyclist.neup.ui.views.FlowView.Node;

public class FlowLine extends Region {

	public final int SPACING = 10;
	public final int Y0 = 40;
	public final int GAP = 5;
	public final int BOTTOM_OFFSET = 10;

	private int _direction;
	private List<FacilityNode> _nodes = new ArrayList<>();
	
	private Line _line;
	private ChoiceBox<String> _choiceBox;

	public FlowLine(int direction) {
		_direction = direction;
		build();
	}

	public int getDirection() {
		return _direction;
	}
	
	public void addNode(final FacilityNode node) {
		double y = Y0;
		if (_nodes.size() > 0) {
			FacilityNode last = _nodes.get(_nodes.size()-1);
			y = last.getTranslateY()+last.getHeight()+SPACING;
		}
		
		node.translateXProperty().bind(widthProperty().subtract(node.widthProperty()).divide(2));
		node.setTranslateY(y);
		
		_nodes.add(node);
		getChildren().add(node);
	}

	public void removeNode(Node node) {
		_nodes.remove(node);
		getChildren().remove(node);
		if (_nodes.size() == 0) {
			setKind(null);
		}
	}

	public FacilityNode findNode(Object value) {
		for (FacilityNode node : _nodes) {
			if (node.getValue().equals(value))
				return node;
		}

		return null;
	}
	
	public String getKind() {
		return _choiceBox.getValue();
	}

	public void setKind(String value) {
		_choiceBox.setValue(value);
	}
	
	public double getCenter() {
		return localToParent(_line.getStartX(), 0).getX();
	}

	public ObjectProperty<String> kindProperty() {
		return _choiceBox.valueProperty();
	}
	
	public void setKindItems(Set<String> items) {
		_choiceBox.getItems().addAll(items);
	}
	
	public ReadOnlyDoubleProperty widthPropery() {
		return _choiceBox.widthProperty();
	}
	
	private void build() {
		getStyleClass().add("flow-line");

		_choiceBox = new ChoiceBox<>();
		_choiceBox.getStyleClass().add("choice");
		
		_line = new Line();
		_line.getStyleClass().add("line");
		_line.startXProperty().bind(widthProperty().multiply(0.5));
		_line.endXProperty().bind(_line.startXProperty());
		
		_line.startYProperty().bind(_choiceBox.translateYProperty().add(_choiceBox.heightProperty().add(GAP)));
		_line.endYProperty().bind(heightProperty());

		getChildren().addAll(_choiceBox, _line);
		
	}
	
}
