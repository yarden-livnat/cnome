package edu.utah.sci.cyclist.ui.views;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.util.converter.TimeStringConverter;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Field.Type;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.model.Table.Row;
import edu.utah.sci.cyclist.ui.components.DropArea;
import edu.utah.sci.cyclist.ui.components.ViewBase;

public class ChartView extends ViewBase {
	public static final String TITLE = "Chart";

	private StackPane _chartStack;
	private XYChart<Object,Object> _chartBottom;
	private XYChart<Object,Object> _chartTop;
		
	private BorderPane _pane;
	private DropArea _xArea;
	private DropArea _yArea;
	
	private Field.Role _xAxisType = Field.Role.NA;
	private Field.Role _yAxisType = Field.Role.NA;
	
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
		_chartBottom = null;
		_chartTop = null;
	}
	
	private void fetchData() {
		if (_currentTable != null && _xArea.getFields().size() == 1 && _yArea.getFields().size() > 0) {
			if (_chartBottom == null) 
				createBottomChart();
			if(_yArea.getFields().size() == 2 && _chartTop == null)
				createTopChart();
			List<Field> fields = new ArrayList<>();
			fields.add(_xArea.getFields().get(0));
			fields.addAll(_yArea.getFields());
			_items.bind(_currentTable.getRows(fields, 100));
		}
	}
	
	
	private void assignData(ObservableList<Row> list) {
		((XYChart)_chartBottom).setData(FXCollections.observableArrayList());
		if(_chartTop != null)
			((XYChart)_chartTop).setData(FXCollections.observableArrayList());

		int cols = _yArea.getFields().size();
		System.out.println("YAREA: " + _yArea.getFields().size());
		for (int col=0; col<cols; col++) {
			
			ObservableList<XYChart.Data<Object, Object>> data = FXCollections.observableArrayList();
			if (_xArea.getFields().get(0).getType() == Field.Type.TIME) {
				for (Row row : list) {
					Timestamp time = (Timestamp) row.value[0];
					data.add(new XYChart.Data<Object, Object>(time.getTime(), row.value[col+1]));
				}
			} else {
				for (Row row : list) {
					data.add(new XYChart.Data<Object, Object>(row.value[0], row.value[col+1]));
				}
			}
			
		
			XYChart.Series<Object, Object> series = new XYChart.Series<Object, Object>();
			series.setName(_yArea.getFields().get(col).getName());
			series.dataProperty().set(data);
		
			// !!!! DEAL WITH MORE THAN 2 DATAS ON TEH Y AXIS!!
			if(col == 0)
				_chartBottom.getData().add(series);
			else
				_chartTop.getData().add(series);
		
		}
		
		// Translate the top chart
		if(_chartTop != null){
			_chartBottom.getXAxis().setMinWidth(250);
			_chartTop.getXAxis().setMinWidth(250);
			_chartBottom.getXAxis().setMaxWidth(250);
			_chartTop.getXAxis().setMaxWidth(250);
			
			_chartTop.setTranslateX(_chartBottom.getYAxis().getBoundsInLocal().getWidth() + _chartBottom.getYAxis().getTickLength() );
		}
		
	}
	
	// Create the bottom chart (always present)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createBottomChart() {
		
		// Create the stack to hold the charts, set as center
		_chartStack = new StackPane();
		_pane.setCenter(_chartStack);		
				
		// Create the x and y axes
		Axis xAxis = createAxis(_xArea.getFields().get(0));
		Axis yAxis = createAxis(_yArea.getFields().get(0));	
		
		// Create the bottom chart
		LineChart<Object,Object> chartBottom = new LineChart<Object, Object>(xAxis, yAxis);
		chartBottom.setCreateSymbols(false);
		chartBottom.setLegendVisible(false);
		_chartBottom = chartBottom;
						
		// Add the bottom chart to the stack
		_chartStack.getChildren().add(_chartBottom);	
	}	
	
	// Create the top chart (only present when we have 2 fields in the y area)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createTopChart() {
		
		// Create the x and y axes (x axis is the same as the bottom chart)
		Axis xAxis = createAxis(_xArea.getFields().get(0));
		Axis yAxis = createAxis(_yArea.getFields().get(1));	
		yAxis.setSide(Side.RIGHT);
		
		// Create the top chart	    
		LineChart<Object,Object> chartTop = new LineChart<Object,Object>(xAxis,yAxis);
		chartTop.getStyleClass().add("top-line-chart");
		chartTop.setCreateSymbols(false);
		chartTop.setLegendVisible(false);		
		_chartTop = chartTop;

		System.out.println("Scale? " + _chartBottom.getScaleX());
		
		// Add the top chart to the stack
		_chartStack.getChildren().add(_chartTop);
	}

	@SuppressWarnings("rawtypes")
	private Axis createAxis(Field field) {
		Axis axis =  null;
		switch (field.getType()) {
		case INTEGER:
		case NUMERIC:
			NumberAxis a = new NumberAxis();
			a.forceZeroInRangeProperty().set(false);
			axis = a;
			break;
		case STRING:
			CategoryAxis c = new CategoryAxis();
			axis = c;
			break;
		case TIME:
			NumberAxis t = new NumberAxis();
			t.forceZeroInRangeProperty().set(false);
			NumberAxis.DefaultFormatter f = new NumberAxis.DefaultFormatter(t) {
				TimeStringConverter converter = new TimeStringConverter("dd-MM-yyyy");
				@Override
				public String toString(Number n) {
					return converter.toString(new Date(n.longValue()));
				}
			};
			t.setTickLabelFormatter(f);
			axis = t;
			break;
		case BOOLEAN:
		case NA:
			axis = new NumberAxis();
		}
		
		axis.setLabel(field.getName());
		
		return axis;
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
		
		_xArea = createControlArea(grid, "X", 0, DropArea.Policy.SINGLE);
		_yArea = createControlArea(grid, "Y", 1, DropArea.Policy.MULTIPLE);
				
		return grid;
	}
	
	private InvalidationListener _areaLister = new InvalidationListener() {
		
		@Override
		public void invalidated(Observable arg0) {			
			if (_xArea.getFields().size() == 0 || !_xArea.getFields().get(0).getRole().equals(_xAxisType))
				invalidateChart();
			
			if (_yArea.getFields().size() == 0 || !_yArea.getFields().get(0).getRole().equals(_yAxisType))
				invalidateChart();	
				
			fetchData();
		}
	};
	
	private DropArea createControlArea(GridPane grid, String title, int  row, DropArea.Policy policy) {
		
		Text text = TextBuilder.create().text(title).styleClass("input-area-header").build();
		DropArea area = new DropArea(policy);
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
