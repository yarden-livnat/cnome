package edu.utah.sci.cyclist.ui.views;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

import org.apache.commons.collections.keyvalue.MultiKey;

import edu.utah.sci.cyclist.model.DataType.Classification;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.model.Table.Row;
import edu.utah.sci.cyclist.ui.components.DropArea;
import edu.utah.sci.cyclist.ui.components.IntegerField;
import edu.utah.sci.cyclist.ui.components.ViewBase;
import edu.utah.sci.cyclist.util.QueryBuilder;

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
	private DropArea _lodArea;
	private DropArea _colorArea;
	private DropArea _shapeArea;
	private DropArea _sizeArea;
	
	private ObjectProperty<Table> _currentTableProperty = new SimpleObjectProperty<>();
	private ListProperty<Row> _items = new SimpleListProperty<>();
	
	private IntegerField _limitEntry;
	
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
	
	private MapSpec _spec;
	private class FieldInfo {
		Field field;
		int index;
		
		public FieldInfo(Field field, int index) {
			this.field = field;
			this.index = index;
		}
	}
	
	private void invalidate() {
		_chart = null;
	}
	
	private void fetchData() {
		if (getCurrentTable() != null && _xArea.getFields().size() == 1 && _yArea.getFields().size() > 0) {
			if (!_xArea.isValid() || !_yArea.isValid())
				return;
			
			if (_chart == null) 
				createChart();
			
			if (_chart != null) {
				//
				List<Field> fields = new ArrayList<>();
				List<Field> aggregators = new ArrayList<>();
				List<Field> grouping = new ArrayList<>();
				
				for (Field field : _xArea.getFields()) {
					if (field.getRole() == Role.DIMENSION)
						fields.add(field);
					else
						aggregators.add(field);
				}
				
				for (Field field : _yArea.getFields()) {
					if (field.getRole() == Role.DIMENSION)
						fields.add(field);
					else
						aggregators.add(field);
				}
				
				for (Field field : _lodArea.getFields()) {
					grouping.add(field);
				}
				
				// build the query
				QueryBuilder builder = 
							getCurrentTable().queryBuilder()
							.fields(fields)
							.aggregates(aggregators)
							.grouping(grouping)
							.filters(filters())
							.filters(remoteFilters())
							.limit(_limitEntry.getValue());
				System.out.println("Query: "+builder.toString());
				
				List<Field> order = builder.getOrder();
				
				_spec = new MapSpec();
				for (Field field : _xArea.getFields()) {
					_spec.xFields.add(new FieldInfo(field, order.indexOf(field)));
				}
				for (Field field : _yArea.getFields()) {
					_spec.yFields.add(new FieldInfo(field, order.indexOf(field)));
				}
				
				for (Field field : _lodArea.getFields()) {
					_spec.lod.add(new FieldInfo(field, order.indexOf(field)));
				}
				Task<ObservableList<Row>> task = getCurrentTable().getRows(builder.toString());
				setCurrentTask(task);
				_items.bind(task.valueProperty());
			}
		}
	}

	
	
	public final double MIN_BAR_WIDTH = 2;
	
	
//	private void updateAxes(List<XYChart.Series<Object, Object>> graphs) {
//		Axis<? extends Object> axis =  _chart.getXAxis();
//		if (axis instanceof NumberAxis) {
//
//			XYChart.Series<Object, Object> series = graphs.get(0);
//			XYChart.Data<Object, Object> entry = series.getData().get(0);
//			if (entry.getXValue() instanceof Number) {
//				Double min = (Double) entry.getXValue();
//				Double max = min;
//				for (XYChart.Data<Object, Object> item : series.getData()) {				
//					Double value = (Double) item.getXValue();
////					item.setXValue(-(Double) item.getXValue());
//					if (value < min) min = value;
//					else if (max < value) max = value;
//				}
//				
//				if (max < 0) {
//					NumberAxis numAxis = (NumberAxis) axis;
//					numAxis.setUpperBound(max);
//					numAxis.setLowerBound(min);
//				}
//			}
//		}
//		
//		axis =  _chart.getYAxis();
//		if (axis instanceof NumberAxis) {
//			XYChart.Series<Object, Object> series = graphs.get(0);
//			XYChart.Data<Object, Object> entry = series.getData().get(0);
//			if (entry.getYValue() instanceof Number) {
//				Double min = (Double) entry.getYValue();
//				Double max = min;
//				for (XYChart.Data<Object, Object> item : series.getData()) {				
//					Double value = (Double) item.getYValue();
//					if (value < min) min = value;
//					else if (max < value) max = value;
//				}
//				
//				if (max < 0) {
//					NumberAxis numAxis = (NumberAxis) axis;
//					numAxis.setUpperBound(max);
//				}
//			}
//		}	
//	}
	
	
//	@SuppressWarnings("unchecked")
//	private <T> void updateAxis( Axis<?> axis, Object[] data, T Klass) {
//		if (axis instanceof NumberAxis) {
//			NumberAxis numAxis = (NumberAxis) axis;
//			
//			Comparable<T> from = (Comparable<T>) data[0];
//			Comparable<T> to = from;
//			
//			for (Object o : data) {
//				T num = (T) o;
//				if (from.compareTo(num) == 1) from = (Comparable<T>) num;
//				else if (to.compareTo(num) == -1) to = (Comparable<T>) num;
//			}
//			
//			double v0 = ((Number)from).doubleValue();
//			double v1 = ((Number)to).doubleValue();
//			int scale = (int)Math.floor(Math.log10(Math.min(Math.abs(v0), Math.abs(v1))));
//			scale = scale - (scale %3);
//			double factor = Math.pow(10, scale);
//			if (scale > 3) {
//				for (int i=0; i<data.length; i++) 
//					data[i] = (Double)data[i]/factor;
//				v0 /= factor;
//				v1 /= factor;
//				
//				numAxis.setLabel(numAxis.getLabel()+" * 1e"+scale);
//			}
//			
//			
//			numAxis.setLowerBound(v0);
//			numAxis.setUpperBound(v1);
//		} else { // CategoryAxis
////			CategoryAxis ca = (CategoryAxis) axis;
////			// ensure there is enough space for the bars
////			Set<Object> names = new HashSet<>();
////			for (Object obj : data) {
////				names.add(obj);
////			}
////			int n = names.size();
////			double w = (axis.getWidth() - ca.getCategorySpacing()*(n-1))/n;
////			if (w < MIN_BAR_WIDTH) {
////				ca.setCa
////			}
//			
//		}
//	}
	
	
	class MapSpec {
		List<FieldInfo> xFields = new ArrayList<>();
		List<FieldInfo> yFields = new ArrayList<>();
		List<FieldInfo> color = new ArrayList<>();
		List<FieldInfo> lod = new ArrayList<>();
		int cols;
		
		public int numX() { return xFields.size(); }
		public int numY() { return yFields.size(); }
		public int cols() { return numX() + numY() + color.size()+lod.size(); }
	}
	
	class SeriesDataPoint {
		Object x;
		Object y;
		Object [] attribues;
	}
	
	class SeriesData {
		Field x;
		Field y;
		List<SeriesDataPoint> points = new ArrayList<>();
	}
	
	private void assignData(MapSpec spec, ObservableList<Row> list) {
		if (list.size() == 0) {
			System.out.println("no data");
			return;
		}
		
		// separate to (x,y,attributes) lists
		List<SeriesData> lists = splitToSeriesData(spec, list);
		
		// separate to sublists based on attributes
		List<Collection<SeriesData>> sublists = new ArrayList<>();
		for (SeriesData sd : lists) {
			sublists.add(createSubList(sd));
		}
		
		// create visual representation
		List<XYChart.Series<Object, Object>> graphs = new ArrayList<>();
		for (Collection<SeriesData> collection : sublists) {
			for (SeriesData sd : collection) {
				convertData(sd, spec);
				
				graphs.add(createChartSeries(sd, spec));
			}
		}
		
		
		_chart.getData().addAll(graphs);
//		updateAxes(graphs);
	}
	
	private List<SeriesData> splitToSeriesData(MapSpec spec, ObservableList<Row> list) {
		List<SeriesData> all = new ArrayList<>();
		
		int nx = spec.numX();
		int ny = spec.numY();
		int cols = spec.cols();
		
		boolean hasAttributes = cols > nx+ny;
		
		for (FieldInfo xInfo : spec.xFields) {
			int ix = xInfo.index;
		
			for (FieldInfo yInfo : spec.yFields) {
				int iy = yInfo.index;
			
				SeriesData series = new SeriesData();
				series.x = xInfo.field;
				series.y = yInfo.field;
				for (Row row : list) {
					SeriesDataPoint p = new SeriesDataPoint();
					p.x = row.value[ix];
					p.y = row.value[iy];
					p.attribues = hasAttributes ? Arrays.copyOfRange(row.value, nx+ny, cols) : null;
					
					series.points.add(p);
				}
				
				all.add(series);
			}
		}
		return all;
	}
	
	
	private Collection<SeriesData> createSubList(SeriesData data) {	
		if (data.points == null || data.points.size() == 0 ||data.points.get(0).attribues == null) {
			List<SeriesData> result = new ArrayList<>();
			result.add(data);
			return result;
		}
		
		Map<MultiKey, SeriesData> map = new HashMap<>();
		
		for (SeriesDataPoint point : data.points) {
			MultiKey key = new MultiKey(point.attribues, false);
			SeriesData sd = map.get(key);
			if (sd == null) {
				sd = new SeriesData();
				sd.x = data.x;
				sd.y = data.y;
				
				map.put(key, sd);
			}
			sd.points.add(point);
		}
		
		System.out.println("attributes:"+map.keySet());
		return map.values();
	}
	

	private void convertData(SeriesData data, MapSpec spec) {
		NumberFormat numFormater = NumberFormat.getInstance();
		
		if (data.points == null || data.points.size() == 0) return;
		
		Object firstItem = data.points.get(0);
		// convert x
		switch (data.x.getClassification()) {
			case C:
				if (firstItem instanceof String) {
					// ignore
				} else if (firstItem instanceof Number) {
					for (SeriesDataPoint p : data.points) {
						p.x = numFormater.format(p.x);
					}
				}
				break;
			case Cdate:
				for (SeriesDataPoint p : data.points) {
					p.x = ((Date)p.x).getTime();
				}
				break;
			case Qi:
			case Qd:
				// ignore
		}
		
		// convert y
		switch (data.y.getClassification()) {
			case C:
				if (firstItem instanceof String) {
					// ignore
				} else if (firstItem instanceof Number) {
					for (SeriesDataPoint p : data.points) {
						p.y = numFormater.format(p.y);
					}
				}
				break;
			case Cdate:
				for (SeriesDataPoint p : data.points) {
					p.y = ((Date)p.y).getTime();
				}
				break;
			case Qi:
			case Qd:
				// ignore
		}				
	}
	
	private XYChart.Series<Object, Object> createChartSeries(SeriesData sd, MapSpec spec) {
		ObservableList<XYChart.Data<Object, Object>> xyData = FXCollections.observableArrayList();
		for (SeriesDataPoint p : sd.points) {
			xyData.add(new XYChart.Data<Object, Object>(p.x, p.y, p.attribues));
		}
		
		XYChart.Series<Object, Object> series = new XYChart.Series<Object, Object>();
		SeriesDataPoint p = sd.points.get(0);
		String label = "";
		if (p.attribues != null && p.attribues.length > 0) {
			label = createAttributesLabel(p);
		}
		series.setName(label);
		series.dataProperty().set(xyData);
		return series;
		
	}
	
	private String createAttributesLabel(SeriesDataPoint p) {
		
		int n = p.attribues.length;
		if (n==1)
			return p.attribues[0].toString();
		
		StringBuilder builder = new StringBuilder("[").append(p.attribues[0]);
		for (int i=1; i<n; i++)
			builder.append(",").append(p.attribues[i]);
		builder.append("]");
		return builder.toString();
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
		
		Axis yAxis = createAxis(getYField(), _yArea.getFields().size() == 1 ? _yArea.getFieldTitle(0) : "");

		determineViewType(getXField().getClassification(), getYField().getClassification()); 
		switch (_viewType) {
		case CROSS_TAB:
			_chart = null;
			break;
		case BAR:
			BarChart bar = new BarChart<>(xAxis,  yAxis);
			System.out.println("gaps: "+bar.getBarGap()+"  "+bar.getCategoryGap());
			bar.setBarGap(1);
			bar.setCategoryGap(4);
			_chart = bar;
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
			_chart.setHorizontalZeroLineVisible(false);
			_chart.setVerticalZeroLineVisible(false);
			System.out.println("zero line: "+_chart.horizontalZeroLineVisibleProperty().get()+"  "+_chart.verticalZeroLineVisibleProperty().get());
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
		_limitEntry.setPrefColumnCount(4);
		
		_limitEntry.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				System.out.println("limit changed: "+ newValue.intValue());
				_chart = null;
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
					//assignData(newValue);
					assignData(_spec, newValue);
				}
			}
		});
		
		filters().addListener(new ListChangeListener<Filter>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Filter> change) {
				System.out.println("filters list changed");
				while (change.next()) {
					for (Filter f : change.getRemoved()) {
						f.removeListener(_filterListener);
					}
					for (Filter f : change.getAddedSubList()) {
						f.addListener(_filterListener);
					}
				}
				invalidate();
				fetchData();
			}
		});
		
		remoteFilters().addListener(new ListChangeListener<Filter>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Filter> change) {
				System.out.println("remote filters list changed");
				while (change.next()) {
					for (Filter filter : change.getRemoved()) {
						filter.removeListener(_filterListener);
					}
					for (Filter filter : change.getAddedSubList()) {
						filter.addListener(_filterListener);
					}
				}
				invalidate();
				fetchData();
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
		cc.setHgrow(Priority.SOMETIMES);
		grid.getColumnConstraints().add(cc);
		
		grid.getColumnConstraints().add(new ColumnConstraints(20));
		cc = new ColumnConstraints();
		cc.setHgrow(Priority.SOMETIMES);
		grid.getColumnConstraints().add(cc);
		
		_xArea = createControlArea(grid, "X", 0, 0, DropArea.Policy.SINGLE);
		_yArea = createControlArea(grid, "Y", 1, 0, DropArea.Policy.MULTIPLE);
		_lodArea = createControlArea(grid, "LOD", 0, 2, DropArea.Policy.MULTIPLE);
				
		return grid;
	}
	
	private InvalidationListener _filterListener = new InvalidationListener() {
		
		@Override
		public void invalidated(Observable o) {
			Filter f = (Filter) o;
			System.out.println("filter cahnged: "+f.getName());
			invalidate();
			fetchData();
		}
	};
	
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
	
	private DropArea createControlArea(GridPane grid, String title, int  row, int col, DropArea.Policy policy) {
		
		Text text = TextBuilder.create().text(title).styleClass("input-area-header").build();
		DropArea area = new DropArea(policy);
		area.tableProperty().bind(_currentTableProperty);
		area.addListener(_areaLister);
		grid.add(text, col, row);
		grid.add(area, col+1, row);
		
		return area;
	}
	
}
