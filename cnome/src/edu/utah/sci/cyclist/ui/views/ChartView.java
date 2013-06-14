package edu.utah.sci.cyclist.ui.views;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.util.converter.TimeStringConverter;

import org.mo.closure.v0.Closure;

import utils.QueryBuilder;
import edu.utah.sci.cyclist.model.DataType;
import edu.utah.sci.cyclist.model.DataType.Classification;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.model.DataType.Type;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.model.Table.Row;
import edu.utah.sci.cyclist.ui.components.DropArea;
import edu.utah.sci.cyclist.ui.components.IntegerField;
import edu.utah.sci.cyclist.ui.components.ViewBase;

public class ChartView extends ViewBase {
	public static final String TITLE = "Chart";

	enum ViewType { CROSS_TAB, BAR, LINE, SCATTER_PLOT, GANTT, NA }
	
	enum MarkType { TEXT, BAR, LINE, SHAPE, GANTT, NA }
	
	
	private ViewType _viewType;
	private MarkType _markType;
	
	private XYChart<Object,Object> _chart;
	
	private BorderPane _pane;
	private DropArea _xArea;
	private DropArea _yArea;
	
	private DataType.Role _xAxisType = DataType.Role.MEASURE;
	private DataType.Role _yAxisType = DataType.Role.MEASURE;
	
	private ObjectProperty<Table> _currentTableProperty = new SimpleObjectProperty<>();
	private ListProperty<Row> _items = new SimpleListProperty<>();
	
	private IntegerField _limitEntry;
//	private int _limit = 1000;
	
	private List<Closure.R1<Object, Object>> _convert;
	
	public ChartView() {
		super();
		build();
	}
	
	public Table getCurrentTable() {
		return _currentTableProperty.get();
	}
	
	@Override
	public void selectTable(Table table, boolean active) {
		super.selectTable(table, active);

		if (!active) {
			if (table == getCurrentTable()) 
				invalidateChart();
			return;
		}
		
		if (table != getCurrentTable()) {
			invalidateChart();
			_currentTableProperty.set(table);
		}
		
		fetchData();
	}
	
	private void invalidateChart() {
		_pane.setCenter(null);
		_chart = null;
		setCurrentTask(null);
	}
	
	private void fetchData() {
		if (getCurrentTable() != null && _xArea.getFields().size() == 1 && _yArea.getFields().size() > 0) {
			if (!_xArea.isValid() || !_yArea.isValid())
				return;
			
			if (_chart == null) 
				createChart();
			if (_chart != null) {
				QueryBuilder builder = 
							getCurrentTable().queryBuilder()
							.field(_xArea.getFields().get(0))
							.fields(_yArea.getFields())
							.limit(_limitEntry.getValue());
				System.out.println("Query: "+builder.toString());
				Task<ObservableList<Row>> task = getCurrentTable().getRows(builder.toString());
				setCurrentTask(task);
				_items.bind(task.valueProperty());
			}
		}
	}
	
	
	private Closure.R1<Object, Object> defaultConvert = new Closure.R1<Object, Object>() {
		@Override
		public Object call(Object value) {
			return value;
		}
	};
	
	private Closure.R1<Object, Object> num2String = new Closure.R1<Object, Object>() {
		private NumberFormat format = NumberFormat.getInstance();
		
		@Override
		public String call(Object num) {
			if (num instanceof Number)
				return format.format((Number)num);
			else if (num instanceof String)
				return (String)num;
			else
				return "error";
		}
	};
	
	private Closure.R1<Object, Object> date2int = new Closure.R1<Object, Object>() {		
		@Override
		public Object call(Object date) {
			return ((Date)date).getTime();
		}
	};
	
	private Closure.R1<Object, Object> determineConversionFunction(Role role, Type type) {
		if (role == Role.DIMENSION) {
			switch (type) {
			case NUMERIC: 
				return num2String;
			case DATE:
			case DATETIME:
				return date2int;
			default:
				return defaultConvert;
			}
		} else {
			switch (type) {
			case DATE:
			case DATETIME:
				return date2int;
			default:
				return defaultConvert;
			}
		}
	}
		
	private void determineConvertions() {
		_convert = new ArrayList<>();
		
		Field x = _xArea.getFields().get(0);
		_convert.add(determineConversionFunction(_xAxisType, x.getType()));
		
		for (Field y : _yArea.getFields()) {
			_convert.add(determineConversionFunction(_yAxisType, y.getType()));
		}
	}
	
	private Object[][] convertData(ObservableList<Row> list) {
		Object[][] data; 
		
		if (list == null || list.size() == 0) {
			// ignore
			data = new Object[0][];
		} else {
			int cols = list.get(0).value.length;
			data = new Object[cols][];
			data[0] = convertDataCol(list, 0, _xArea.getFields().get(0).getClassification());
			for (int col=1; col<cols; col++) {
				data[col] = convertDataCol(list, col, _yArea.getFields().get(col-1).getClassification());
			}
		}
		
		return data;
	}
	
	private Object[] convertDataCol(ObservableList<Row> list, int col, Classification classification) {
		NumberFormat format = NumberFormat.getInstance();
		
		int n = list.size();
		Object[] data = new Object[n];
		
		if (n > 0) {
			Object item = list.get(0).value[col];
			
			System.out.println(col+": "+classification+"   type:"+item.getClass());
			
			switch (classification) {
			case C:
				// axis is classification.
				if (item instanceof String) {
					// no conversion is required
					for (int r=0; r<n; r++)
						data[r] = list.get(r).value[col];
				} else if (item instanceof Number) {
					for (int r=0; r<n; r++)
						data[r] = format.format(list.get(r).value[col]);
				} else {
					System.out.println("item type:"+item.getClass());
				}
				break;
			case Cdate:
				for (int r=0; r<n; r++)
					data[r] = ((Date)list.get(r).value[col]).getTime();
				break;
			case Qi:
			case Qd:
				for (int r=0; r<n; r++)
					data[r] = list.get(r).value[col];
				break;
			}
		}
		return data;
	}
	
	@SuppressWarnings("unchecked")
	private <T> void setMinMax( Axis<?> axis, Object[] data, T Klass) {
		if (axis instanceof NumberAxis) {
			NumberAxis numAxis = (NumberAxis) axis;
			
			Comparable<T> from = (Comparable<T>) data[0];
			Comparable<T> to = from;
			
			for (Object o : data) {
				T num = (T) o;
				if (from.compareTo(num) == 1) from = (Comparable<T>) num;
				else if (to.compareTo(num) == -1) to = (Comparable<T>) num;
			}
			
			double v0 = ((Number)from).doubleValue();
			double v1 = ((Number)to).doubleValue();
			int scale = (int)Math.floor(Math.log10(Math.min(Math.abs(v0), Math.abs(v1))));
			scale = scale - (scale %3);
			double factor = Math.pow(10, scale);
			if (scale > 3) {
				for (int i=0; i<data.length; i++) 
					data[i] = (Double)data[i]/factor;
				v0 /= factor;
				v1 /= factor;
				
				numAxis.setLabel(numAxis.getLabel()+" * 1e"+scale);
			}
			
			
			numAxis.setLowerBound(v0);
			numAxis.setUpperBound(v1);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void assignData(ObservableList<Row> list) {
		System.out.println("fetched "+list.size()+" data points");
		
		long t0 = System.currentTimeMillis();
		((XYChart)_chart).setData(FXCollections.observableArrayList());
		
		Object[][] data = convertData(list);
		
		int cols = _yArea.getFields().size()+1;
		int rows = Math.min(20, list.size());
		
		List<XYChart.Series<Object, Object>> s = new ArrayList<>(); 
		
		// compute min/max
//		setMinMax(_chart.getXAxis(), data[0], data[0].getClass());
//		setMinMax(_chart.getYAxis(), data[1], data[0].getClass());
				
		for (int col=1; col<cols; col++) {
			ObservableList<XYChart.Data<Object, Object>> seriesData = FXCollections.observableArrayList();
			for (int row=0; row<rows; row++) {
				seriesData.add(new XYChart.Data<Object, Object>(data[0][row], data[col][row]));
			}
			
			for (int i=0; i<rows; i++) {
				System.out.println(seriesData.get(i));
			}
			XYChart.Series<Object, Object> series = new XYChart.Series<Object, Object>();
			series.setName(_yArea.getFieldTitle(col-1));
			series.dataProperty().set(seriesData);
			s.add(series);			
		}
		
		long t1 = System.currentTimeMillis();
		_chart.getData().addAll(s);
		long t2 = System.currentTimeMillis();
		System.out.println("conversion: "+(t1-t0)/1000.0+"  assignment: "+(t2-t1)/1000.0);
	}
	
	private Field getXField() {
		return _xArea.getFields().get(0);
	}
	
	private Field getYField() {
		return _yArea.getFields().get(0);
	}
	
	private boolean isPaneType(Classification x, Classification y, Classification c1, Classification c2) {
		return (x == c1 && y == c2) || (x==c2 && y==c1); 
	}
	
	private void determineViewType(Classification x, Classification y) {
		
		if (isPaneType(x, y, Classification.C, Classification.C)) {
			_viewType = ViewType.CROSS_TAB;
			_markType = MarkType.TEXT;
		} else if (isPaneType(x, y, Classification.Qd, Classification.C)) {
			_viewType = ViewType.BAR;
			_markType = MarkType.BAR;
		} else if (isPaneType(x, y, Classification.Qd, Classification.Cdate)) {
			_viewType = ViewType.LINE;
			_markType = MarkType.LINE;
		} else if (isPaneType(x, y, Classification.Qd, Classification.Qd)) {
			_viewType = ViewType.SCATTER_PLOT;
			_markType = MarkType.SHAPE;
		} else if (isPaneType(x, y, Classification.Qi, Classification.C)) {
			_viewType = ViewType.BAR;
			_markType = MarkType.BAR;
		} else if (isPaneType(x, y, Classification.Qi, Classification.Qd)) {
			_viewType = ViewType.LINE;
			_markType = MarkType.LINE;
		}else if (isPaneType(x, y, Classification.Qi, Classification.Qi)) {
			_viewType = ViewType.SCATTER_PLOT;
			_markType = MarkType.SHAPE;
		} else {
			_viewType = ViewType.NA;
			_markType = MarkType.NA;
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createChart() {
		Axis xAxis = createAxis(getXField(), _xArea.getFieldTitle(0));
		_xAxisType = xAxis instanceof CategoryAxis ? Role.DIMENSION : Role.MEASURE;
		
		Axis yAxis = createAxis(getYField(), _yArea.getFields().size() == 1 ? _yArea.getFieldTitle(0) : "");
		_yAxisType = yAxis instanceof CategoryAxis ? Role.DIMENSION : Role.MEASURE;


		determineViewType(getXField().getClassification(), getYField().getClassification()); 
		switch (_viewType) {
		case CROSS_TAB:
			_chart = null;
			break;
		case BAR:
			_chart = new BarChart<>(xAxis,  yAxis);
			break;
		case LINE:
			LineChart<Object,Object> lineChart = new LineChart<Object, Object>(xAxis, yAxis);
//			lineChart.setCreateSymbols(false);
			_chart = lineChart;
			break;
		case SCATTER_PLOT:
			_chart = new ScatterChart<>(xAxis, yAxis);
			break;
		case GANTT:
			_chart = null;
			break;
		case NA:
			_chart = null;
		}
		 
//		chart.setCreateSymbols(false);
//		chart.setLegendVisible(false);
//		
		if (_chart != null) {
			_chart.setAnimated(false);
//			_chart.setCache(true);
			_pane.setCenter(_chart);
		} else {
			Text text = new Text("Unsupported fields combination");
			_pane.setCenter(text);
		}
	}

	@SuppressWarnings("rawtypes")
	private Axis createAxis(Field field, String title) {
		Axis axis =  null;
		System.out.println("field clasification: "+field.getClassification());
		switch (field.getClassification()) {

		case C:
			CategoryAxis c = new CategoryAxis();
			axis = c;
			break;
		case Cdate:
			NumberAxis cd = new NumberAxis();
			cd.forceZeroInRangeProperty().set(false);
			NumberAxis.DefaultFormatter f = new NumberAxis.DefaultFormatter(cd) {
				TimeStringConverter converter = new TimeStringConverter("dd-MM-yyyy");
				@Override
				public String toString(Number n) {
					return converter.toString(new Date(n.longValue()));
				}
			};
			cd.setTickLabelFormatter(f);
			axis = cd;
			break;
		case Qd:
			NumberAxis t = new NumberAxis();
			t.forceZeroInRangeProperty().set(false);
			axis = t;
			break;
		case Qi:
			NumberAxis a = new NumberAxis();
			a.forceZeroInRangeProperty().set(false);
			axis = a;
		}
		
		axis.setLabel(title);
		
		return axis;
	}
		
	
	private void build() {
		setTitle(TITLE);
		getStyleClass().add("chart-view");
		
		// Limit box
		_limitEntry = new IntegerField(1, Integer.MAX_VALUE, 1000);
		_limitEntry.setEditable(true);
		_limitEntry.setPromptText("unlimited");
		_limitEntry.setPrefColumnCount(8);
		
		_limitEntry.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
//				_limit = newValue.intValue();
				System.out.println("limit changed: "+ newValue.intValue());
				fetchData();	
			}
		});
		
		addBar(_limitEntry, HPos.RIGHT);
		
		// main view
		_pane = BorderPaneBuilder.create().prefHeight(200).prefWidth(300).build();
		Rectangle clip = new Rectangle(0, 0, 100, 100);
		clip.widthProperty().bind(_pane.widthProperty());
		clip.heightProperty().bind(_pane.heightProperty());
		_pane.setClip(clip);
		_pane.setBottom(createControl());
		
		setContent(_pane);
		
		_items.addListener(new ChangeListener<ObservableList<Row>>() {

			@Override
			public void changed(
					ObservableValue<? extends ObservableList<Row>> observable,
					ObservableList<Row> oldValue, ObservableList<Row> newValue) {
				
				if (newValue != null) {
					assignData(newValue);
				}
			}
		});
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
		public void invalidated(Observable observable) {			
//			if (_xArea.getFields().size() == 0 || !_xArea.getFields().get(0).getRole().equals(_xAxisType))
//				invalidateChart();
//			
//			if (_yArea.getFields().size() == 0 || !_yArea.getFields().get(0).getRole().equals(_yAxisType))
//				invalidateChart();	
			invalidateChart();		
			if (getCurrentTable() == null) {
				DropArea area = (DropArea) observable;
				if (area.getFields().size() == 1) {
					if (getOnTableDrop() != null)
						getOnTableDrop().call(area.getFields().get(0).getTable());
				}
			}
			fetchData();
		}
	};
	
	private DropArea createControlArea(GridPane grid, String title, int  row, DropArea.Policy policy) {
		
		Text text = TextBuilder.create().text(title).styleClass("input-area-header").build();
		DropArea area = new DropArea(policy);
		area.tableProperty().bind(_currentTableProperty);
		area.addListener(_areaLister);
		grid.add(text, 0, row);
		grid.add(area, 1, row);
		
		return area;
	}
	
}
