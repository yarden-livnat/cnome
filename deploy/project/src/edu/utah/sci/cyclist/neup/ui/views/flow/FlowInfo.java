package edu.utah.sci.cyclist.neup.ui.views.flow;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import edu.utah.sci.cyclist.neup.model.Transaction;

public class FlowInfo extends Pane {
	public static final int CHART = 0;
	public static final int LIST = 1;
	
	private FlowNode _node;

	private Function<Transaction, Object> _typeFunc = t->t.nucid;
	
	private CategoryAxis _xAxis;
	private NumberAxis _yAxis;
	private BarChart<String, Number> _chart;
		
	private InvalidationListener _listener = o->{
		refresh();
	};
	
	public FlowInfo(FlowNode node) {
		_node = node;
		
		build();
		init();
	}
	
	public void setMode(int mode) {
		
	}
	
	public void setType(Function<Transaction, Object> func) {
		_typeFunc = func; 
		refresh();
	}
	
	private void refresh() {
//		_vbox.getChildren().clear();
		
		Map<Object, Double> map = new HashMap<>();
		for (Transaction t : _node.getActiveTransactions()) {
			Object type = _typeFunc.apply(t);
			Double sum = map.get(type);
			if (sum == null) 
				sum = new Double(0);
			map.put(type, sum+t.amount );
		}
		
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		for (Object type : map.keySet()) {
			System.out.println(type+":"+ map.get(type));
			series.getData().add(new XYChart.Data<String, Number>(type.toString(), map.get(type)));
		}
		_chart.getData().clear();
		_chart.getData().add(series);
		
//		for (Object type : map.keySet()) {
//			Label l = new Label(String.format("%s: %.2e kg", type.toString(), map.get(type)));
//			_vbox.getChildren().add(l);
//		}
	}
	
	private void init() {
		_node.getActiveTransactions().addListener(_listener);
	}
	
	private void build() {
		getStyleClass().add("finfo");
		
		buildChart();
		getChildren().add(_chart);
//		_vbox = new VBox();
//		getChildren().add(_vbox);
	}
	
	private Node buildChart() {
		_xAxis = new CategoryAxis();
//		_xAxis.setTickLabelsVisible(false);
		_xAxis.setTickMarkVisible(false);
		
		_yAxis = new NumberAxis();
		_yAxis.setTickLabelsVisible(false);
		_yAxis.setTickMarkVisible(false);
		_yAxis.setVisible(false);
		_yAxis.setMinorTickVisible(false);
		
		_chart = new SmallBarChart<>(_xAxis, _yAxis);
		_chart.getStyleClass().add("chart");
		_chart.setAnimated(false);

		return _chart;
	}
}
