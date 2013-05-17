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
	
	private Table _currentTable = null;
	private ListProperty<Row> _items = new SimpleListProperty<>();
	
	public ChartView() {
		super();
		build();
	}
	
	@Override
	public void selectTable(Table table, boolean active) {
		super.selectTable(table, active);

		if (!active && table != _currentTable) {
			// ignore
			return;
		}
		
		_currentTable = table;
		fetchData();
	}
	
	
	private void fetchData() {
		if (_currentTable != null && _xArea.getFields().size() == 1 && _yArea.getFields().size() == 1) {
			Field[] fields = {_xArea.getFields().get(0), _yArea.getFields().get(0)};
			_items.bind(_currentTable.getRows(fields, 100));
		}
	}
	
	private void build() {
		setTitle(TITLE);
		
		createChart();
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
	
	private void assignData(ObservableList<Row> list) {
		ObservableList<XYChart.Data<Object, Object>> data = FXCollections.observableArrayList();
		for (Row row : list) {
			data.add(new XYChart.Data<Object, Object>(row.value[0], row.value[1]));
		}
		_series.dataProperty().set(data);
//		_chart.getData().get(0).dataProperty().set(data);
	}
	
	private void createChart() {
//		Field xField = _xArea.getFields().get(0);
//		Field yField = _yArea.getFields().get(0);
		
//		if (xField.get(FieldProperties.ROLE).equals(FieldProperties.VALUE_MEASURE)) {
		Axis xAxis = new NumberAxis();
		Axis yAxis = new NumberAxis();
		
		_series = new XYChart.Series<Object, Object>();
	
		_chart = new LineChart<Object, Object>(xAxis, yAxis);
		_chart.getData().add(_series);
	}
	
//	private Axis<?> createAxis(Field field) {
//		Axis<?> axis;
//		
//		if (field.get(FieldProperties.ROLE).equals(FieldProperties.VALUE_MEASURE)) {
//			axis = new NumberAxis();
//		} else {
//			axis = new CategoryAxis();
//		}
//		
//		return axis;
//	}
		
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
			System.out.println("changed");
			if (_xArea.getFields().size() > 0 && _yArea.getFields().size() > 0) {
				createChart();
				_pane.setCenter(_chart);
				fetchData();
			}
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
}
