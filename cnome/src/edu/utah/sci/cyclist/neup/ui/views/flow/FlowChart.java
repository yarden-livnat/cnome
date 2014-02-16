package edu.utah.sci.cyclist.neup.ui.views.flow;

import java.util.OptionalDouble;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import edu.utah.sci.cyclist.core.event.Pair;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

public class FlowChart extends VBox {
	public static final String CUMMULATIVE = "Cummulative Flow";
	
	private Label _title;
	private LineChart<Number, Number> _chart;
	private NumberAxis _xAxis;
	private NumberAxis _yAxis;
	private boolean _open = true;
		
	private ListProperty<Pair<Integer, Double>> _itemsProperty = new SimpleListProperty<>();
	
	/*
	 * Properties
	 */
	
	public ListProperty<Pair<Integer, Double>> items() { 
		return _itemsProperty;
	}
	
	public ObservableList<Pair<Integer, Double>> getItems() {
		return items().get();
	}
	
	public FlowChart() {
		super();
		
		build();
	}
	
	public void open(boolean value) {
		_open = value;
		if (_open) {
			_title.setGraphic(GlyphRegistry.get(AwesomeIcon.CARET_DOWN));
		} else {
			_title.setGraphic(GlyphRegistry.get(AwesomeIcon.CARET_RIGHT));			
		}
		_chart.setVisible(_open);
		_chart.setManaged(_open);
	}
	
	public void setTitle(String title) {
		_chart.setTitle(title);
	}
	
	private void plotData() {
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
	
		Double min = getItems().stream()
			.mapToDouble(i->i.v2)
			.min()
			.orElse(0);
		
		if (min < 0) min = -min;
		double f = Math.pow(10, 3*Math.floor(Math.log10(min)/3));
		
		System.out.println(String.format("min: %.2e  %e", min, f));
		for (Pair<Integer, Double> item : getItems()) {
			series.getData().add(new XYChart.Data<Number, Number>(item.v1, item.v2/f));
		}
		String label = f == 1 ? "kg" : String.format("x %.0e kg", f);
		_yAxis.setLabel(label);
		
		_chart.getData().clear();
		_chart.getData().add(series);
	}
	
	private void build() {
		getStyleClass().add("fchart");
		
//		_title = new Label(CUMMULATIVE, GlyphRegistry.get(AwesomeIcon.CARET_DOWN));
		buildChart();
	
		getChildren().addAll(
//			new Separator(),
//			_title,
			_chart
		);
		
		addListeners();
	}
	
	private void buildChart() {
		_xAxis = new NumberAxis();
		_xAxis.setLabel("time");
		
		_yAxis = new NumberAxis();
		_yAxis.setLabel("Cummulative");
		
		_chart = new LineChart<>(_xAxis, _yAxis);
		_chart.getStyleClass().add("chart");
		_chart.setCreateSymbols(false);
		
		VBox.setVgrow(_chart, Priority.ALWAYS);
	}
	
	private void addListeners() {
		_itemsProperty.addListener(
				(o, p, n)->{
					if (n != null) plotData();
				});
	}
	
	
	
}
