package edu.utah.sci.cyclist.ui.views;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.FieldProperties;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.model.Table.Row;
import edu.utah.sci.cyclist.ui.components.DropArea;
import edu.utah.sci.cyclist.ui.components.ViewBase;

public class ChartView extends ViewBase {
	public static final String TITLE = "Chart";

	private XYChart<Object,Object> _chart;
	private Series<Object, Object> _series;
	
	private BorderPane _pane;
	private DropArea _xArea;
	private DropArea _yArea;
	
	private String _xAxisType = "";
	private String _yAxisType = "";
	
	private Table _currentTable = null;
	private ListProperty<Row> _items = new SimpleListProperty<>();
	
	public ChartView() {
		super();
		build();
	}
	
	@Override
	public void selectTable(Table table, boolean active) {
		super.selectTable(table, active);

		if (!active) {
			if (table == _currentTable) 
				invalidateChart();
			return;
		}
		
		if (table != _currentTable) {
			invalidateChart();
			_currentTable = table;
		}
		
		fetchData();
	}
	
	private void invalidateChart() {
		_pane.setCenter(null);
		_chart = null;
	}
	
	private void fetchData() {
		if (_currentTable != null && _xArea.getFields().size() == 1 && _yArea.getFields().size() == 1) {
			if (_chart == null) 
				createChart();
			Field[] fields = {_xArea.getFields().get(0), _yArea.getFields().get(0)};
			_items.bind(_currentTable.getRows(fields, 100));
		}
	}
	
	
	private void assignData(ObservableList<Row> list) {
		ObservableList<XYChart.Data<Object, Object>> data = FXCollections.observableArrayList();
		for (Row row : list) {
			data.add(new XYChart.Data<Object, Object>(row.value[0], row.value[1]));
		}

		_series = new XYChart.Series<Object, Object>();
		_series.dataProperty().set(data);
		
		// clear chart data
		((XYChart)_chart).setData(FXCollections.observableArrayList());
		_chart.getData().add(_series);
		
//		if (_chart.getData().size() == 0) 
//			_chart.getData().add(_series);
//		else
//			_chart.getData().set(0, _series);
	}
	
	private void createChart() {
		Field xField = _xArea.getFields().get(0);
		Field yField = _yArea.getFields().get(0);
		
		Axis xAxis;
		_xAxisType = xField.getString(FieldProperties.ROLE);
		if (_xAxisType.equals(FieldProperties.VALUE_MEASURE)) {
			NumberAxis axis = new NumberAxis();
			axis.forceZeroInRangeProperty().set(false);
			xAxis = axis;
			// TODO: deal with time
		}
		else 
			xAxis = new CategoryAxis();
		xAxis.setLabel(xField.getName());
		
		Axis yAxis;
		_yAxisType = yField.getString(FieldProperties.ROLE);
		if (_yAxisType.equals(FieldProperties.VALUE_MEASURE)) {
			NumberAxis axis = new NumberAxis();
			axis.forceZeroInRangeProperty().set(false);
			yAxis = axis;
		}
		else 
			yAxis = new CategoryAxis();
		yAxis.setLabel(yField.getName());
	
		LineChart<Object,Object> chart = new LineChart<Object, Object>(xAxis, yAxis);
		chart.setCreateSymbols(false);
		chart.setLegendVisible(false);
		
//		_series = new XYChart.Series<Object, Object>();
//		chart.getData().add(_series);
		
		_chart = chart;
		
		_pane.setCenter(_chart);
	}

		
	private Node createControl() {
		GridPane grid = GridPaneBuilder.create()
					.hgap(5)
					.vgap(5)
					.padding(new Insets(0, 0, 0, 0))
					.build();
		grid.getColumnConstraints().add(new ColumnConstraints(10));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().add(cc);
		
		_xArea = createControlArea(grid, "X", 0);
		_yArea = createControlArea(grid, "Y", 1);
				
		return grid;
	}
	
	private InvalidationListener _areaLister = new InvalidationListener() {
		
		@Override
		public void invalidated(Observable arg0) {			
			if (_xArea.getFields().size() == 0 || !_xArea.getFields().get(0).getString(FieldProperties.ROLE).equals(_xAxisType))
				invalidateChart();
			
			if (_yArea.getFields().size() == 0 || !_yArea.getFields().get(0).getString(FieldProperties.ROLE).equals(_yAxisType))
				invalidateChart();	
				
			fetchData();
		}
	};
	
	private DropArea createControlArea(GridPane grid, String title, int  row) {
		
		Text text = TextBuilder.create().text(title).styleClass("input-area-header").build();
		DropArea area = new DropArea(DropArea.Policy.SINGLE);
		area.getFields().addListener(_areaLister);
		grid.add(text, 0, row);
		grid.add(area, 1, row);
		
		return area;
	}
	
	private void build() {
		setTitle(TITLE);
		
		getStyleClass().add("chart-view");
		_pane = BorderPaneBuilder.create().prefHeight(200).prefWidth(300).build();
		_pane.setBottom(createControl());
		
		setContent(_pane);
		
		_items.addListener(new ChangeListener<ObservableList<Row>>() {

			@Override
			public void changed(
					ObservableValue<? extends ObservableList<Row>> observable,
					ObservableList<Row> oldValue, ObservableList<Row> newValue) {
				if (newValue == null) return;
				
				assignData(newValue);
			}
		});
	}
}
