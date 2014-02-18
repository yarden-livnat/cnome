package edu.utah.sci.cyclist.neup.ui.views.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import edu.utah.sci.cyclist.core.event.Pair;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

public class FlowChart extends VBox {
	private static final String NET_CHART_LABEL = "Net Flow";
	private static final String COMMULATIVE_CHART_LABEL = "Commulative Flow";
	
	private HBox _header;
	private Pane _glass;
	private Line _line;
	private Label _popup;
	
	private LineChart<Number, Number> _chart;
	private NumberAxis _xAxis;
	private NumberAxis _yAxis;
	private double _scale = 1;
	private boolean _opened = true;
	private String _type;
	private int _movingTime;
	private int _upperBound;
	
	private Map<FlowNode, ChartInfo> _info = new HashMap<>();
	
	private IntegerProperty _timeProperty = new SimpleIntegerProperty();
	
	public class ChartInfo {
		public Label title;
		public ObservableList<Pair<Integer, Double>> values;
		public XYChart.Series<Number, Number> series;
		public double scale;
		public int last;
	}
	
	public FlowChart() {
		super();
		build();
	}
	
	public IntegerProperty timeProperty() {
		return _timeProperty;
	}
	
	private void selectChartType(String value) {
		_type = value;
		double s = 1;
		for (ChartInfo info : _info.values()) {
			s = Math.max(s, computeScale(info.values));
		}
		if (s != _scale) {
			_scale = s;
			updateYAxis();
		}
		updateAll();
	}
	
	public void add(FlowNode node, String title, ObservableList<Pair<Integer, Double>> values) {
		if (values.size() == 0) return;
		
		int last = values.get(values.size()-1).v1;
		if (last > _upperBound) {
			_upperBound = last;
		}
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		Color c = node.getColor();
		String style = c.toString().replace("0x", "#");
		series.nodeProperty().addListener(o->{
			series.getNode().setStyle("-fx-stroke:"+style);
		});

		double scale = computeScale(values); // relative to the current chart type
		
		if (scale > _scale) {
			updateScale(scale);
		}
		updateSeries(series, values);

		ChartInfo info = new ChartInfo();
		info.values = values;
		info.title = new Label(title);
		info.title.setStyle("-fx-background-color:"+style);
		info.title.getStyleClass().add("header");
		info.series = series;
		info.scale = scale;
		info.last = last;
		_info.put(node, info);
		
		_header.getChildren().add(info.title);
		_chart.getData().add(series);
		_line.setVisible(true);
	}
	
	public void remove(FlowNode node) {
		ChartInfo info = _info.remove(node);
		if (info == null) {
			_line.setVisible(false);
			return;
		}
				
		_header.getChildren().remove(info.title);
		_chart.getData().remove(info.series);
		double s = 1;
		_upperBound = 0;
		for (ChartInfo entry : _info.values()) {
			s = Math.max(entry.scale, s);
			_upperBound = Math.max(_upperBound, entry.last);
		}
		if (s != _scale) {
			updateScale(s);
			updateYAxis();
		}
	}
	
	private void updateAll() {
		for (ChartInfo info : _info.values()) {
			updateSeries(info.series, info.values);
		}
	}
	
	private void updateScale(double value) {
		_scale = value;
		System.out.println("scale: "+_scale);
		updateYAxis();
		updateAll();
	}

	private void updateYAxis() {
		String label = _scale == 1 ? "kg"
				: _scale == 1000 ? "x 1000 kg"
				: String.format("x %.0e kg", _scale);
	
		_yAxis.setLabel(label);
	}

	
	private double computeScale(ObservableList<Pair<Integer, Double>> values) {
		double max = 0;
		if (_type.equals(COMMULATIVE_CHART_LABEL)) {
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
	
	private void updateSeries(XYChart.Series<Number, Number> series, ObservableList<Pair<Integer, Double>> values) {
		series.getData().clear();

		if (_type.equals(COMMULATIVE_CHART_LABEL)) {
			double sum = 0;
			for (Pair<Integer, Double> value : values) {
				sum += value.v2/_scale;
				series.getData().add(new XYChart.Data<Number, Number>(value.v1, sum));
			}
		} else {
			double prev = 0;
			for (Pair<Integer, Double> value : values) {
				double v = value.v2/_scale;
				series.getData().add(new XYChart.Data<Number, Number>(value.v1, v-prev));
				prev = v;
			}
		}
	}

			
	private void build() {
		getStyleClass().add("fchart");
		
		buildHeader();
		
		getChildren().addAll(
			_header,
			buildChart()
		);
		
		addListeners();
	}
	
	private void buildHeader() {
		_header = new HBox();
		_header.getStyleClass().add("infobar");
		_header.setStyle("-fx-padding: 0, 0, 0, 2px");
		
		Label caret = new Label("", GlyphRegistry.get(AwesomeIcon.CARET_DOWN));
		
		ChoiceBox<String> type = new ChoiceBox<>();
		type.getStyleClass().add("choice");
		type.getItems().addAll(COMMULATIVE_CHART_LABEL, NET_CHART_LABEL);
	
		_header.getChildren().addAll(caret, type);
		
		caret.setOnMouseClicked(e->{
			_opened = !_opened;
			if (_opened) {
				caret.setGraphic(GlyphRegistry.get(AwesomeIcon.CARET_DOWN));
			} else {
				caret.setGraphic(GlyphRegistry.get(AwesomeIcon.CARET_RIGHT));			
			}
			_chart.setVisible(_opened);
			_chart.setManaged(_opened);
		});
		
		type.valueProperty().addListener(e->{
			selectChartType(type.getValue());
		});
		
		type.setValue(COMMULATIVE_CHART_LABEL);
	}
	

	private Node buildChart() {
		_xAxis = new NumberAxis();
		_xAxis.setLabel("time");
		
		_yAxis = new NumberAxis();
		_yAxis.setLabel("Cummulative");
		
		_chart = new LineChart<>(_xAxis, _yAxis);
		_chart.getStyleClass().add("chart");
		_chart.setCreateSymbols(false);
		
		_line = new Line();
		_line.getStyleClass().add("timeline");
		_line.endXProperty().bind(_line.startXProperty());
		_line.startYProperty().bind(_yAxis.layoutYProperty().add(10));
		_line.endYProperty().bind(_line.startYProperty().add(_yAxis.heightProperty()));
		_line.setVisible(false);
	
		
		_popup = new Label();
		_popup.getStyleClass().add("popup");
		_popup.setVisible(false);
		_popup.layoutXProperty().bind(_line.startXProperty().add(10));
		_glass = new Pane();
		_glass.getChildren().addAll(_line, _popup);
		
		_xAxis.layoutBoundsProperty().addListener(e->{
			updateLine();
		});
		StackPane stack = new StackPane();
		stack.getChildren().addAll( _chart, _glass);
		
		VBox.setVgrow(stack, Priority.ALWAYS);
		return stack;
	}
	
	private void updateLine() {
		double time = _timeProperty.get();
		double x = _xAxis.getDisplayPosition(time);
		Point2D p = _glass.sceneToLocal(_xAxis.localToScene(x, 0));
		_line.setStartX(p.getX());
	}
	private void addListeners() {
		_timeProperty.addListener(o->{
			updateLine();
		});
		
		_line.setOnMousePressed(e->{
			_popup.setText(Integer.toString(_timeProperty.get()));
			_popup.setLayoutY(e.getY()-10);
			_popup.setVisible(true);
			_movingTime = _timeProperty.get();
		});
		
		
		
		_line.setOnMouseDragged(e->{
			
			_popup.setLayoutY(e.getY()-10);
			Point2D p = new Point2D(e.getSceneX(), e.getSceneY());
			Number v = _xAxis.getValueForDisplay(_xAxis.sceneToLocal(p).getX());
			int i = v.intValue();
			i = Math.max(0, Math.min(_upperBound, i));
			if (i>0 && i <= _upperBound) {
				_line.setStartX(e.getX());
				_popup.setText(Integer.toString(i));
				_movingTime = i;
			}
		});
		
		_line.setOnMouseReleased(e->{
			_popup.setVisible(false);
			_timeProperty.set(_movingTime);
		});
	}
}