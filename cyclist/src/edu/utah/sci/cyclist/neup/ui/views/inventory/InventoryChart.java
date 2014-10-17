package edu.utah.sci.cyclist.neup.ui.views.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;
import edu.utah.sci.cyclist.core.event.Pair;
import edu.utah.sci.cyclist.core.util.ColorUtil;
import edu.utah.sci.cyclist.neup.ui.views.inventory.InventoryView.AgentInfo;

public class InventoryChart extends VBox {

	public enum ChartType {
		INVENTORY, NET
	}
	
	public enum ChartMode {
		LINE, AREA, STACKED
	}
	
	private static int _id = 0;
	
	private static String _cssId = "InventoryChart-"+(_id++);
	
	private XYChart<Number, Number> _chart = null;
	private NumberAxis _xAxis;
	private NumberAxis _yAxis;
	private double _scale = 1;
	private ChartType _type = ChartType.INVENTORY;
	private int _upperBound;
	private ChartMode _mode = ChartMode.LINE;
	
	private Map<AgentInfo, ChartInfo> _items = new HashMap<>();
	
	public class ChartInfo {
		public Collection<Pair<Integer, Double>> values;
		public XYChart.Series<Number, Number> series;
		public double scale;
		public int last;
		public String style;
	}
	
	public InventoryChart() {
		super();
		build();
	}
	
	public void selectChartType(ChartType type) {
		_type = type;
		double s = 1;
		for (ChartInfo info : _items.values()) {
			s = Math.max(s, computeScale(info.values));
		}
		if (s != _scale) {
			_scale = s;
			updateYAxis();
		}
		updateAll();
	}
	
	public void add(AgentInfo entry) {
		ChartInfo info = _items.remove(entry);
		if (info != null) {
			_chart.getData().remove(info.series);
		}
		
		if (entry.series.size() == 0) return;
		
		int last = 0;
		for (Pair<Integer, Double> p : entry.series) {
			last = Math.max(last, p.v1);
		}
		if (last > _upperBound) {
			_upperBound = last;
		}
		
		final String style = ColorUtil.toString(entry.color).substring(0, 7)+"aa";
		XYChart.Series<Number, Number> series = createSeries(entry.series, style);
		double scale = computeScale(entry.series); // relative to the current chart type
		
		if (scale > _scale) {
			updateScale(scale);
		}
		
		info = new ChartInfo();
		info.values = entry.series;
		info.series = series;
		info.scale = scale;
		info.last = last;
		info.style = style;
		_items.put(entry, info);
		

	}
	
	public XYChart.Series<Number, Number> createSeries(Collection<Pair<Integer, Double>> data, String style) {
		final XYChart.Series<Number, Number> series = new XYChart.Series<>();
		
		series.nodeProperty().addListener(o->{
			Path fillPath;
			Path linePath;
			switch (_mode) {
			case LINE:	
				Node node = series.getNode();
				node.setStyle("-fx-stroke: "+style);
				break;
			case AREA:
				Group g1 = (Group) series.getNode();
				// Note: based on StackedAreaChart.java in JavaFX
				// unfortunately there isn't any official ways to do this
				fillPath = (Path)g1.getChildren().get(0);
				linePath = (Path)g1.getChildren().get(1);
				linePath.setStyle("-fx-stroke: "+style);
				fillPath.setStyle("-fx-fill: "+style);
				break;
			case STACKED:
				Group g2 = (Group) series.getNode();
				// Note: based on StackedAreaChart.java in JavaFX
				// unfortunately there isn't any official ways to do this
				fillPath = (Path)g2.getChildren().get(0);
				linePath = (Path)g2.getChildren().get(1);
				linePath.setStyle("-fx-stroke: "+style);
				fillPath.setStyle("-fx-fill: "+style);
				break;
			}

		});
	
		updateSeries(series, data);
		_chart.getData().add(series);
		return series;
	}
	
	public void remove(AgentInfo entry) {
		ChartInfo info = _items.remove(entry);
		if (info == null) {
			return;
		}
				
		_chart.getData().remove(info.series);
		double s = 1;
		_upperBound = 0;
		for (ChartInfo ci : _items.values()) {
			s = Math.max(ci.scale, s);
			_upperBound = Math.max(_upperBound, ci.last);
		}
		if (s != _scale) {
			updateScale(s);
			updateYAxis();
		}
	}
	
	private void updateAll() {
		for (ChartInfo info : _items.values()) {
			updateSeries(info.series, info.values);
		}
	}
	
	private void updateScale(double value) {
		_scale = value;
		updateYAxis();
		updateAll();
	}

	private void updateYAxis() {
		String label = _scale == 1 ? "kg"
				: _scale == 1000 ? "x 1000 kg"
				: String.format("x %.0e kg", _scale);
	
		_yAxis.setLabel(label);
	}

	private void updateColors() {
		_chart.applyCss();
		for (ChartInfo info : _items.values()) {
			Group g = (Group) info.series.getNode();
			// Note: based on StackedAreaChart.java in JavaFX
			// unfortunately there isn't any official ways to do this
			Path fillPath = (Path)g.getChildren().get(0);
			Path linePath = (Path)g.getChildren().get(1);
			linePath.setStyle("-fx-stroke: "+info.style);
			fillPath.setStyle("-fx-fill: "+info.style);
		}
	}
	
	private double computeScale(Collection<Pair<Integer, Double>> values) {
		double max = 0;
		if (_type == ChartType.INVENTORY) {
			double sum = 0;
			for (Pair<Integer, Double> value : values) {
				sum += Math.abs(value.v2);
			}
			max = sum;
		} else {
			double prev = 0;
			for (Pair<Integer, Double> value : values) {
				max = Math.max(max, Math.abs(value.v2-prev));
				prev = value.v2;
			}
		}
		if (max == 0) max = 1;
		double s = Math.pow(10, 3*Math.floor(Math.log10(max)/3));
		return s;
	}
	
	private void updateSeries(XYChart.Series<Number, Number> series, Collection<Pair<Integer, Double>> values) {
//		boolean updating = _chart.getData().contains(series);
//		if (updating)
//			_chart.getData().remove(series);
		
//		series.getData().clear();
		List<XYChart.Data<Number, Number>> list = new ArrayList<>();
		
		if (_type == ChartType.INVENTORY) {
			double sum = 0;
			for (Pair<Integer, Double> value : values) {;
//				sum += value.v2/_scale;
				sum = value.v2/_scale;
				list.add(new XYChart.Data<Number, Number>(value.v1, sum));
			}
		} else {
			double prev = 0;
			boolean first = true;
			for (Pair<Integer, Double> value : values) {
				double v = value.v2/_scale;
				if (first) {
					prev = v;
					first = false;
				}
				list.add(new XYChart.Data<Number, Number>(value.v1, v-prev));
				prev = v;
			}
		}
		
//		System.out.println("==values==");
//		for (XYChart.Data<Number, Number> i : list)
//			System.out.println(i.getXValue()+", "+i.getYValue());
		series.getData().setAll(list);
//		if (updating) {
//			_chart.getData().add(series);
//		}
	}

			
	public void setMode(ChartMode mode) {
		_mode = mode;
		switch (_mode) {
		case LINE:
			LineChart<Number, Number> lineChart = new LineChart<>(_xAxis, _yAxis);
			lineChart.getStyleClass().add("chart");
			lineChart.setCreateSymbols(false);
			lineChart.setLegendVisible(false);
			lineChart.setAnimated(false);
			setChart(lineChart);
			break;
		case AREA:
			AreaChart<Number, Number> areaChart = new AreaChart<>(_xAxis, _yAxis);
			areaChart.getStyleClass().add("chart");
			areaChart.setLegendVisible(false);
			areaChart.setAnimated(false);
			setChart(areaChart);
			break;
		case STACKED:
			StackedAreaChart<Number, Number> stackedAreaChart = new StackedAreaChart<>(_xAxis, _yAxis);
			stackedAreaChart.getStyleClass().add("chart");
			stackedAreaChart.setLegendVisible(false);
			stackedAreaChart.setAnimated(false);
			setChart(stackedAreaChart);
			break;
		}
		
		for (ChartInfo info : _items.values()) {
			info.series = createSeries(info.values, info.style);
		}
	}
	
	private void setChart(XYChart<Number, Number> chart) {
		getChildren().clear();
		VBox.setVgrow(chart, Priority.ALWAYS);
		getChildren().add(chart);
		_chart = chart;
	}
	
	private void build() {
		getStyleClass().add("fchart");

		buildChart();
		
		setFillWidth(true);
	}	

	private Node buildChart() {
		_xAxis = new NumberAxis();
		_xAxis.setLabel("time");
		_xAxis.setAnimated(false);
		
		_yAxis = new NumberAxis();
		_yAxis.setLabel("Amount");
		_yAxis.setAnimated(false);
		
		setMode(_mode);
		
		return _chart;
	}
}