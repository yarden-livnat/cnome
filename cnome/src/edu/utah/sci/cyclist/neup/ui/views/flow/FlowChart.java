package edu.utah.sci.cyclist.neup.ui.views.flow;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import edu.utah.sci.cyclist.neup.model.Transaction;
import javafx.beans.InvalidationListener;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class FlowChart extends Pane {

	private FacilityNode _node;
	private VBox _vbox;
	private Function<Transaction, Object> _typeFunc = t->t.iso;
	
	private InvalidationListener _listener = o->{
		refresh();
	};
	
	public FlowChart(FacilityNode node) {
		_node = node;
		
		build();
		init();
	}
	
	public void setType(Function<Transaction, Object> func) {
		_typeFunc = func; 
		refresh();
	}
	
	private void refresh() {
		_vbox.getChildren().clear();
		
		Map<Object, Double> map = new HashMap<>();
		for (Transaction t : _node.getActiveTransactions()) {
			Object type = _typeFunc.apply(t);
			Double sum = map.get(type);
			if (sum == null) 
				sum = new Double(0);
			map.put(type, sum+t.quantity*t.fraction );
		}
		
		for (Object type : map.keySet()) {
			Label l = new Label(String.format("%s: %.2e kg", type.toString(), map.get(type)));
			_vbox.getChildren().add(l);
		}
	}
	
	private void init() {
		_node.getActiveTransactions().addListener(_listener);
	}
	
	private void build() {
		getStyleClass().add("fchart");
		setWidth(50);
		setHeight(30);	
		
		_vbox = new VBox();
		getChildren().add(_vbox);
	}
}
