package edu.utah.sci.cyclist.core.ui.views;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.converter.TimeStringConverter;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.log4j.Logger;
import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.event.ui.FilterEvent;
import edu.utah.sci.cyclist.core.model.CyclistDatasource;
import edu.utah.sci.cyclist.core.model.DataType.Classification;
import edu.utah.sci.cyclist.core.model.DataType.Role;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Filter;
import edu.utah.sci.cyclist.core.model.Indicator;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.model.TableRow;
import edu.utah.sci.cyclist.core.ui.components.CyclistViewBase;
import edu.utah.sci.cyclist.core.ui.components.DistanceIndicator;
import edu.utah.sci.cyclist.core.ui.components.DropArea;
import edu.utah.sci.cyclist.core.ui.components.LineIndicator;
import edu.utah.sci.cyclist.core.ui.components.ViewBase;
import edu.utah.sci.cyclist.core.ui.panels.SchemaPanel;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import edu.utah.sci.cyclist.core.util.QueryBuilder;

public class ChartView extends CyclistViewBase {
	public static final String TITLE = "Chart";
	static Logger log = Logger.getLogger(ChartView.class);

	enum ViewType { CROSS_TAB, BAR, LINE, SCATTER_PLOT, GANTT, NA }

	enum MarkType { TEXT, BAR, LINE, SHAPE, GANTT, NA }


	private ViewType _viewType;
	private boolean _active = true;

	//        private MarkType _markType;

	private ObjectProperty<XYChart<Object,Object>> _chartProperty = new SimpleObjectProperty<>();

	private ObservableList<Indicator> _indicators = FXCollections.observableArrayList();
	private Map<Indicator, LineIndicator> _lineIndicators = new HashMap<>();
	private List<DistanceIndicator> _distanceIndicators = new ArrayList<>();

	private Closure.V0 _onDuplicate = null;
	
	private MapSpec _spec;

	private BorderPane _pane;
	private DropArea _xArea;
	private DropArea _yArea;
	private DropArea _lodArea;
	//        private DropArea _colorArea;
	//        private DropArea _shapeArea;
	//        private DropArea _sizeArea;
	//        private DropArea _indicatorArea;

	private ObjectProperty<Table> _currentTableProperty = new SimpleObjectProperty<>();
	private ListProperty<TableRow> _items = new SimpleListProperty<>();
	private ObjectProperty<Boolean> _forceZeroProperty = new SimpleObjectProperty<>();

	private StackPane _stackPane;
	private Pane _glassPane;

	//Saves the latest results from the database, organized by keys.
	private  List<Map<MultiKey, SeriesData>> _lastSubLists = new ArrayList<Map<MultiKey, SeriesData>>();

	public ChartView() {
		super();
		build();
	}

	@Override 
	public ViewBase clone() {
		ChartView copy = new ChartView();
		copy.setActive(false);

		return copy;
	}

	public void copy(ChartView other) {
		for (Field field : other._xArea.getFields()) {
			_xArea.getFields().add(field.clone());
		}

		for (Field field : other._yArea.getFields()) {
			_yArea.getFields().add(field.clone());
		}

		for (Field field : other._lodArea.getFields()) {
			_lodArea.getFields().add(field.clone());
		}


		getFiltersArea().copy(other.getFiltersArea());

	}

	public void setActive(boolean state) {
		_active = state;
		if (_active) {
			invalidateChart();
			fetchData();
		}
	}

	public void setOnDuplicate(Closure.V0 action) {
		_onDuplicate = action;
	}


	public ObjectProperty<XYChart<Object,Object>> chartProperty() {
		return _chartProperty;
	}

	public XYChart<Object,Object> getChart() {
		return _chartProperty.get();
	}

	public void setChart(XYChart<Object,Object> chart) {
		_chartProperty.set(chart);
	}
	public Table getCurrentTable() {
		return _currentTableProperty.get();
	}

	public ObjectProperty<Boolean> forceZeroProperty() {
		return _forceZeroProperty;
	}

	public Boolean getForceZero() {
		return _forceZeroProperty.get();
	}

	public void setForceZero(Boolean value) {
		_forceZeroProperty.set(value);
	}
	
	public void updateSimulationData(){
		fetchData();
	}
	
	public void removeSimulationData(){
		invalidateChart();
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

	@Override
	public void selectSimulation(Simulation sim, boolean active) {
		super.selectSimulation(sim, active);	
		updateFilters();
	}
	
	private void updateFilters() {
		
		CyclistDatasource ds = getAvailableDatasource();
		
		if (ds != null)
		for (Filter filter : filters()) {
			filter.setDatasource(ds);
		}
	}
	
	private void invalidateChart() {
		if (_stackPane.getChildren().size() > 1) {
			_stackPane.getChildren().remove(0);
		}

		setChart(null);
		setCurrentTask(null);
	}

	private void fetchData() {
		if (!_active) return;

		if (getCurrentTable() != null && _xArea.getFields().size() == 1 && _yArea.getFields().size() > 0) {
			if (!_xArea.isValid() || !_yArea.isValid())
				return;

			if (getChart() == null) 
				createChart();

			if (getChart() != null) {
				List<Field> fields = new ArrayList<>();
				List<Field> aggregators = new ArrayList<>();
				List<Field> grouping = new ArrayList<>();

				Table table = getCurrentTable();

				int n = 0;
				for (Field field : _xArea.getFields()) {
					if (table.hasField(field)) {
						if (field.getRole() == Role.DIMENSION || field.getRole() == Role.INT_TIME)
							fields.add(field);
						else
							aggregators.add(field);
						n++;
					}
				}

				// is there at least one valid x field? 
				if (n == 0) return;

				n=0;
				for (Field field : _yArea.getFields()) {
					if (table.hasField(field)) {
						if (field.getRole() == Role.DIMENSION || field.getRole() == Role.INT_TIME)
							fields.add(field);
						else
							aggregators.add(field);
						n++;
					}
				}

				// is there at least one valid y field?
				if (n == 0) return;

				for (Field field : _lodArea.getFields()) {
					if (table.hasField(field)) {
						grouping.add(field);
					}
				}

				List<Filter> filtersList = new ArrayList<Filter>();

				//Check the filters current validity
				for(Filter filter : filters()){
					if(getCurrentTable().hasField(filter.getField())){
						filtersList.add(filter);
					}
				}

				//Check the remote filters current validity
				for(Filter filter : remoteFilters()){
					if(getCurrentTable().hasField(filter.getField())){
						filtersList.add(filter);
					}
				}

				// build the query
				QueryBuilder builder = 
						getCurrentTable().queryBuilder()
						.fields(fields)
						.aggregates(aggregators)
						.grouping(grouping)
						.filters(filtersList);
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
	            Simulation currentSim = getCurrentSimulation();
				CyclistDatasource ds = currentSim != null ? currentSim.getDataSource() : null;
				
				Task<ObservableList<TableRow>> task = new Task<ObservableList<TableRow>>() {
					@Override
					protected ObservableList<TableRow> call() throws Exception {
						return getCurrentTable().getRows(ds, builder.toString());
					}
				};
				
				setCurrentTask(task);
				
				Thread th = new Thread(task);
				th.setDaemon(true);
				th.start();
				
				_items.bind(task.valueProperty());
			}
		}
	}



	//        public final double MIN_BAR_WIDTH = 2;
	//        
	//        
	//        private void updateAxes(List<XYChart.Series<Object, Object>> graphs) {
	//                Axis<? extends Object> axis =  _chart.getXAxis();
	//                if (axis instanceof NumberAxis) {
	//
	//                        XYChart.Series<Object, Object> series = graphs.get(0);
	//                        XYChart.Data<Object, Object> entry = series.getData().get(0);
	//                        if (entry.getXValue() instanceof Number) {
	//                                Double min = (Double) entry.getXValue();
	//                                Double max = min;
	//                                for (XYChart.Data<Object, Object> item : series.getData()) {                                
	//                                        Double value = (Double) item.getXValue();
	////                                        item.setXValue(-(Double) item.getXValue());
	//                                        if (value < min) min = value;
	//                                        else if (max < value) max = value;
	//                                }
	//                                
	//                                if (max < 0) {
	//                                        NumberAxis numAxis = (NumberAxis) axis;
	//                                        numAxis.setUpperBound(max);
	//                                        numAxis.setLowerBound(min);
	//                                }
	//                        }
	//                }
	//                
	//                axis =  _chart.getYAxis();
	//                if (axis instanceof NumberAxis) {
	//                        XYChart.Series<Object, Object> series = graphs.get(0);
	//                        XYChart.Data<Object, Object> entry = series.getData().get(0);
	//                        if (entry.getYValue() instanceof Number) {
	//                                Double min = (Double) entry.getYValue();
	//                                Double max = min;
	//                                for (XYChart.Data<Object, Object> item : series.getData()) {                                
	//                                        Double value = (Double) item.getYValue();
	//                                        if (value < min) min = value;
	//                                        else if (max < value) max = value;
	//                                }
	//                                
	//                                if (max < 0) {
	//                                        NumberAxis numAxis = (NumberAxis) axis;
	//                                        numAxis.setUpperBound(max);
	//                                }
	//                        }
	//                }        
	//        }


	//        @SuppressWarnings("unchecked")
	//        private <T> void updateAxis( Axis<?> axis, Object[] data, T Klass) {
	//                if (axis instanceof NumberAxis) {
	//                        NumberAxis numAxis = (NumberAxis) axis;
	//                        
	//                        Comparable<T> from = (Comparable<T>) data[0];
	//                        Comparable<T> to = from;
	//                        
	//                        for (Object o : data) {
	//                                T num = (T) o;
	//                                if (from.compareTo(num) == 1) from = (Comparable<T>) num;
	//                                else if (to.compareTo(num) == -1) to = (Comparable<T>) num;
	//                        }
	//                        
	//                        double v0 = ((Number)from).doubleValue();
	//                        double v1 = ((Number)to).doubleValue();
	//                        int scale = (int)Math.floor(Math.log10(Math.min(Math.abs(v0), Math.abs(v1))));
	//                        scale = scale - (scale %3);
	//                        double factor = Math.pow(10, scale);
	//                        if (scale > 3) {
	//                                for (int i=0; i<data.length; i++) 
	//                                        data[i] = (Double)data[i]/factor;
	//                                v0 /= factor;
	//                                v1 /= factor;
	//                                
	//                                numAxis.setLabel(numAxis.getLabel()+" * 1e"+scale);
	//                        }
	//                        
	//                        
	//                        numAxis.setLowerBound(v0);
	//                        numAxis.setUpperBound(v1);
	//                } else { // CategoryAxis
	////                        CategoryAxis ca = (CategoryAxis) axis;
	////                        // ensure there is enough space for the bars
	////                        Set<Object> names = new HashSet<>();
	////                        for (Object obj : data) {
	////                                names.add(obj);
	////                        }
	////                        int n = names.size();
	////                        double w = (axis.getWidth() - ca.getCategorySpacing()*(n-1))/n;
	////                        if (w < MIN_BAR_WIDTH) {
	////                                ca.setCa
	////                        }
	//                        
	//                }
	//        }


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

	private void assignData(MapSpec spec, ObservableList<TableRow> list) {
		if (list.size() == 0) {
			System.out.println("no data");
			return;
		}

		System.out.println("data has "+list.size()+" rows");
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


		//                Axis<?> axis = getChart().getYAxis();
		//                if (axis instanceof NumberAxis) {
		//                        NumberAxis y = (NumberAxis) axis;
		//                         SeriesData sd = getFirst(sublists.get(0));
		//                         double min =  ((Number) sd.points.get(0).y).doubleValue();
		//                         for (SeriesDataPoint p : sd.points) {
		//                                 double v = ((Number) p.y).doubleValue();
		//                                 if (v < min) min = v;
		//                         }
		//                         double s0 = Math.floor(Math.log10(min));
		//                         double s1 = Math.floor(s0/3)*3;
		//                         double s2 = s1 -3;
		//                         if (s2 > 0) {
		//                                 System.out.println("Adjust axis format");
		//                                 NumberFormat nf = NumberFormat.getInstance();
		//                                 nf.setMaximumIntegerDigits(4);
		//                                 NumberStringConverter nsc = new NumberStringConverter(nf);
		//                                 y.setTickLabelFormatter(nsc);
		//                         }
		//                         
		//                }
		if(getChart() != null && getChart().getData() != null){
			getChart().getData().addAll(graphs);
		}
		//                updateAxes(graphs);
	}


	//        private SeriesData getFirst(Collection<SeriesData> collection) {
	//                return collection.iterator().next();
	//        }
	//        
	private List<SeriesData> splitToSeriesData(MapSpec spec, ObservableList<TableRow> list) {
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
				for (TableRow row : list) {
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
		_lastSubLists.clear();

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
		_lastSubLists.add(map);
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
			if(data.points.get(0) != null && data.points.get(0).x.getClass() == Date.class){
				for (SeriesDataPoint p : data.points) {
						p.x = ((Date)p.x).getTime();
				}
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
			if(data.points.get(0) != null && data.points.get(0).y.getClass() == Date.class){
				for (SeriesDataPoint p : data.points) {
						p.y = ((Date)p.y).getTime();
				}
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
			//                        _markType = MarkType.TEXT;
		} else if (isPaneType(x, y, Classification.Qd, Classification.C)) {
			_viewType = ViewType.BAR;
			//                        _markType = MarkType.BAR;
		} else if (isPaneType(x, y, Classification.Qd, Classification.Cdate)) {
			_viewType = ViewType.LINE;
			//                        _markType = MarkType.LINE;
		} else if (isPaneType(x, y, Classification.Qd, Classification.Qd)) {
			_viewType = ViewType.SCATTER_PLOT;
			//                        _markType = MarkType.SHAPE;
		} else if (isPaneType(x, y, Classification.Qi, Classification.C)) {
			_viewType = ViewType.BAR;
			//                        _markType = MarkType.BAR;
		} else if (isPaneType(x, y, Classification.Qi, Classification.Qd)) {
			_viewType = ViewType.LINE;
			//                        _markType = MarkType.LINE;
		} else if (isPaneType(x, y, Classification.Qi, Classification.Qi)) {
			_viewType = ViewType.SCATTER_PLOT;
			//                        _markType = MarkType.SHAPE;
		} else if (isPaneType(x, y, Classification.C, Classification.Cdate)) {
			_viewType = ViewType.SCATTER_PLOT;
		} else {
			_viewType = ViewType.NA;
			//                        _markType = MarkType.NA;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createChart() {
		System.out.println("create new chart");
		Axis xAxis = createAxis(getXField(), _xArea.getFieldTitle(0));

		Axis yAxis = createAxis(getYField(), _yArea.getFields().size() == 1 ? _yArea.getFieldTitle(0) : "");

		determineViewType(getXField().getClassification(), getYField().getClassification()); 
		switch (_viewType) {
		case CROSS_TAB:
			setChart(null);
			break;
		case BAR:
			BarChart bar = new BarChart<>(xAxis,  yAxis);
			System.out.println("gaps: "+bar.getBarGap()+"  "+bar.getCategoryGap());
			bar.setBarGap(1);
			bar.setCategoryGap(4);
			setChart(bar);
			break;
		case LINE:
			LineChart<Object,Object> lineChart = new LineChart<Object, Object>(xAxis, yAxis);
			//                        lineChart.setCreateSymbols(false);
			setChart(lineChart);
			break;
		case SCATTER_PLOT:
			setChart(new ScatterChart<>(xAxis, yAxis));
			break;
		case GANTT:
			setChart(null);
			break;
		case NA:
			setChart(null);

		}

		//                chart.setCreateSymbols(false);
		//                chart.setLegendVisible(false);
		//                
		if (getChart() != null) {
			getChart().setAnimated(false);
			getChart().setHorizontalZeroLineVisible(false);
			getChart().setVerticalZeroLineVisible(false);
			System.out.println("zero _line: "+getChart().horizontalZeroLineVisibleProperty().get()+"  "+getChart().verticalZeroLineVisibleProperty().get());
			//                        getChart().setCache(true);
			//                        _pane.setCenter(getChart());
			_stackPane.getChildren().add(0, getChart());
		} else {
			Text text = new Text("Unsupported fields combination");
			_stackPane.getChildren().add(0, text);
			//_pane.setCenter(text);
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
			cd.forceZeroInRangeProperty().bind(forceZeroProperty());
			if(field.getRole() != Role.INT_TIME)
			{
				NumberAxis.DefaultFormatter f = new NumberAxis.DefaultFormatter(cd) {
					TimeStringConverter converter = new TimeStringConverter("dd-MM-yyyy");
					@Override
					public String toString(Number n) {
						return converter.toString(new Date(n.longValue()));
					}
				};
				cd.setTickLabelFormatter(f);
			}
			axis = cd;
			break;
		case Qd:
			NumberAxis t = new NumberAxis();
			t.forceZeroInRangeProperty().bind(forceZeroProperty());
			axis = t;
			break;
		case Qi:
			NumberAxis a = new NumberAxis();
			a.forceZeroInRangeProperty().bind(forceZeroProperty());
			axis = a;
		}

		axis.setLabel(title);

		return axis;
	}

	long t0;

	private void build() {
		setTitle(TITLE);

		// main view
		_pane = new BorderPane();
		_pane.setPrefSize(600, 300);

		Rectangle clip = new Rectangle(0, 0, 100, 100);
		clip.widthProperty().bind(_pane.widthProperty());
		clip.heightProperty().bind(_pane.heightProperty());
		_pane.setClip(clip);

		// Glass pane for indicators
		_glassPane = new Pane();
		// _glassPane.setStyle("-fx-background-color: rgba(200, 200, 200, 0.1)");
		setupGlassPaneListeners();

		_stackPane = new StackPane();
		_stackPane.getChildren().add(_glassPane);

		_pane.setCenter(_stackPane);
		_pane.setBottom(createControl());

		setContent(_pane);

		// add actions
		setupActions();

		_items.addListener(new ChangeListener<ObservableList<TableRow>>() {

			@Override
			public void changed(
					ObservableValue<? extends ObservableList<TableRow>> observable,
							ObservableList<TableRow> oldValue, ObservableList<TableRow> newValue) {

				if (newValue != null) {
					long t1 = System.currentTimeMillis();
					assignData(_spec, newValue);
					long t2 = System.currentTimeMillis();
					System.out.println("assigned data: "+(t2-t1)/1000+"secs");
				}
			}
		});

		_indicators.addListener(new ListChangeListener<Indicator>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Indicator> change) {
				while (change.next()) {
					// remove
					for (Indicator indicator : change.getRemoved()) {
						LineIndicator lineIndicator = _lineIndicators.remove(indicator);
						if (lineIndicator != null) {
							_glassPane.getChildren().remove(lineIndicator.getNode());
						}
					}

					// add
					for (final Indicator indicator : change.getAddedSubList()) {
						final LineIndicator li = new LineIndicator(indicator, _glassPane);
						li.chartProperty().bind(chartProperty());
						li.setOnRemoveAction(new Closure.V1<LineIndicator>() {

							@Override
							public void call(LineIndicator li) {
								_indicators.remove(li.getIndicator());
							}

						});
						_lineIndicators.put(indicator, li);
						indicator.selectedProperty().addListener( new ChangeListener<Boolean>() {

							@Override
							public void changed(ObservableValue<? extends Boolean> arg0,
									Boolean prevValue, Boolean select) {
								if (select) {
									showDistances(indicator);
								} else {
									clearDistances();                                                                                
								}
							}
						});
					}
				}
			}
		});

		filters().addListener(new ListChangeListener<Filter>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Filter> change) {
				boolean update = false;
				
				CyclistDatasource ds = getAvailableDatasource();
				
				while (change.next()) {
					for (Filter f : change.getRemoved()) {
						update = update || f.isActive();
						f.removeListener(_filterListener);
					}
					for (Filter f : change.getAddedSubList()) {
						//For a new filter - add the current data source as its data source.
						f.setDatasource(ds);
							
						update = update || f.isActive();
						f.addListener(_filterListener);
					}
				}

				if (update) {
					invalidateChart();
					fetchData();
				}
			}
		});

		remoteFilters().addListener(new ListChangeListener<Filter>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Filter> change) {
				while (change.next()) {
					for (Filter filter : change.getRemoved()) {
						filter.removeListener(_filterListener);
					}
					for (Filter filter : change.getAddedSubList()) {
						filter.addListener(_filterListener);
					}
				}
				invalidateChart();
				fetchData();
			}
		});


		forceZeroProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				// FIXME: seems to be a bug in JavaFX.
				// The chart does not refresh itself if the axis forceRangeZero changes.
				// This is a hack to force the chart to redraw.
				if (getChart() != null) {
					ObservableList<Series<Object, Object>> list = getChart().getData();
					getChart().setData(null);
					getChart().setData(list);
				}
			}
		});
		forceZeroProperty().set(false);
	}


	private void setupActions() {
		final Button options = new Button("", GlyphRegistry.get(AwesomeIcon.CARET_DOWN));
		options.getStyleClass().add("flat-button");

		// create menu
		final ContextMenu contextMenu = new ContextMenu();

		MenuItem item = new MenuItem("Add indicator");
		item.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				createIndicator();
			}
		});

		contextMenu.getItems().add(item);

		final MenuItem forceZeroItem = new MenuItem("Force Zero", GlyphRegistry.get(AwesomeIcon.CHECK));
		forceZeroItem.getGraphic().visibleProperty().bind(forceZeroProperty());
		contextMenu.getItems().add(forceZeroItem);

		forceZeroItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				setForceZero(!getForceZero());
			}
		});

		MenuItem duplicateItem = new MenuItem("Duplicate chart");
		duplicateItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (_onDuplicate != null) {
					_onDuplicate.call();
				}
			}
		});

		contextMenu.getItems().add(duplicateItem);

		options.setOnMousePressed(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				contextMenu.show(options, Side.BOTTOM, 0, 0);
			}
		});

		List<Node> actions = new ArrayList<>();
		actions.add(options);
		addActions(actions);

		chartProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				if(chartProperty().get() != null){
					Axis<?> yAxis = chartProperty().get().getYAxis();
					if (! (yAxis instanceof NumberAxis)){
						forceZeroItem.setVisible(false);
					} else{
						forceZeroItem.setVisible(true);
					}
				}
			}
		});


		forceZeroProperty().set(false);
	}

	private void createIndicator() {
		Indicator indicator = new Indicator();

		if (getChart() != null) {
			Axis<?> axis = getChart().getXAxis();
			if (axis instanceof NumberAxis) {
				NumberAxis x = (NumberAxis) axis;
				double value = (x.getUpperBound() - x.getLowerBound())/2;
				indicator.valueProperty().set(value);
			}

		}
		_indicators.add(indicator);
	}

	private Node createControl() {
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setPadding(new Insets(0, 0, 0, 0));

		grid.getColumnConstraints().add(new ColumnConstraints(10));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setHgrow(Priority.SOMETIMES);
		grid.getColumnConstraints().add(cc);

		//                grid.getColumnConstraints().add(new ColumnConstraints(17));

		grid.getColumnConstraints().add(new ColumnConstraints(20));
		cc = new ColumnConstraints();
		cc.setHgrow(Priority.SOMETIMES);
		grid.getColumnConstraints().add(cc);

		_xArea = createControlArea(grid, "X", 0, 0, 1, DropArea.Policy.SINGLE, DropArea.AcceptedRoles.ALL);
		_yArea = createControlArea(grid, "Y", 1, 0, 1, DropArea.Policy.MULTIPLE, DropArea.AcceptedRoles.ALL);
		_lodArea = createControlArea(grid, "Group by", 0, 2, 2, DropArea.Policy.MULTIPLE, DropArea.AcceptedRoles.DIMENSION);
		//                _lodArea = createControlArea(grid, "LOD", 0, 3, 2, DropArea.Policy.MULTIPLE, DropArea.AcceptedRoles.DIMENSION);
		//                _indicatorArea = createIndicatorArea(grid, "Ind", 1, 2, DropArea.Policy.MULTIPLE);
		//                Button swapButton = new Button("", new ImageView(Resources.getIcon("swap")));
		//                swapButton.getStyleClass().add("flat-button");
		//                swapButton.setOnAction(new EventHandler<ActionEvent>() {
		//                        
		//                        @Override
		//                        public void handle(ActionEvent arg0) {
		//                                if(_xArea.getFields().size() == 1 && _lodArea.getFields().size() == 1){
		//                                   Field lodField = _lodArea.getFields().get(0);
		//                                   Field xField = _xArea.getFields().get(0);
		//                                   if(lodField.getClassification() == Classification.C && xField.getClassification() == Classification.C ){
		//                                	   _lodArea.getFields().removeAll(_lodArea.getFields());
		//                                	   //_xArea.getChildren().removeAll(_xArea.getChildren());
		//                                	   _xArea.getFields().removeAll(_xArea.getFields());
		//                                	   _xArea.getFields().add(lodField);
		//                                	   _lodArea.getFields().add(xField);
		//                                   }
		//                                   
		//                                }
		//                        }
		//                });
		//                
		//                grid.add(swapButton, 2, 0, 1, 1);

		return grid;
	}

	/*
	 * Name: createDragAndDropModes
	 * Returns: Map<Class<?>, TransferMode[]>
	 * Description: Maps for each possible drag and drop source the accepted transfer modes.
	 */
	private Map<Class<?>, TransferMode[]> createDragAndDropModes() {
		Map<Class<?>, TransferMode[]> sourcesTransferModes = new HashMap<Class<?>, TransferMode[]>();
		sourcesTransferModes.put(DropArea.class, new TransferMode[]{TransferMode.MOVE});
		sourcesTransferModes.put(SchemaPanel.class, TransferMode.COPY_OR_MOVE);
		return sourcesTransferModes;
	}
	private void showDistances(Indicator selected) {
		clearDistances();

		Axis<?> axis = getChart().getXAxis();
		NumberAxis.DefaultFormatter formater = new NumberAxis.DefaultFormatter((NumberAxis)axis);
		LineIndicator current = _lineIndicators.get(selected);
		double y = 10;
		for (LineIndicator to : _lineIndicators.values()) {
			if (to.getIndicator() != selected) {
				DistanceIndicator di = new DistanceIndicator(current, to, y, formater);
				_distanceIndicators.add(di);
				_glassPane.getChildren().add(di);
				y += 10;
			}
		}
	}

	private void clearDistances() {
		for (DistanceIndicator di : _distanceIndicators)
			_glassPane.getChildren().remove(di);
		_distanceIndicators.clear();
	}


	private InvalidationListener _filterListener = new InvalidationListener() {

		@Override
		public void invalidated(Observable o) {
			Filter f = (Filter) o;
			System.out.println("filter changed: "+f.getName());
			//If possible - take data directly from memory instead of quering the database.
			if(handleLODFilters(f))
			{
				f.setValid(true);
			}else{
				invalidateChart();
				fetchData();
			}
		}
	};

	private InvalidationListener _areaListener = new InvalidationListener() {

		@Override
		public void invalidated(Observable observable) {                        
			invalidateChart();
			DropArea area = (DropArea) observable;
			if (getCurrentTable() == null) {
				if (area.getFields().size() == 1) {
					if (getOnTableDrop() != null)
						getOnTableDrop().call(area.getFields().get(0).getTable());
				}
			}
			updatePreOccupiedFields(area);
			fetchData();
		}
	};


	/* Name: updatePreOccupiedFields
	 * This method prevents the same field to appear in more than one drop area.
	 * Each drop area gets the list of fields already used by the other drop areas.
	 * When there is a change in the fields of one of the drop areas, update all the other drop area with the updated fields list. */

	private void updatePreOccupiedFields(DropArea area)
	{
		if (area == _lodArea){
			List<Field> newList = new ArrayList<Field>(_lodArea.getFields());
			newList.addAll(_yArea.getFields());
			_xArea.updatePreOccupiedField(newList);

			newList.clear();
			newList.addAll(_lodArea.getFields());
			newList.addAll(_xArea.getFields());
			_yArea.updatePreOccupiedField(newList);


		} else if(area == _xArea){
			List<Field> newList = new ArrayList<Field>(_xArea.getFields());
			newList.addAll(_lodArea.getFields());
			_yArea.updatePreOccupiedField(newList);

			newList.clear();
			newList.addAll(_xArea.getFields());
			newList.addAll(_yArea.getFields());
			_lodArea.updatePreOccupiedField(newList);


		} else if(area == _yArea){
			List<Field> newList = new ArrayList<Field>(_yArea.getFields());
			newList.addAll(_lodArea.getFields());
			_xArea.updatePreOccupiedField(newList);

			newList.clear();
			newList.addAll(_yArea.getFields());
			newList.addAll(_xArea.getFields());
			_lodArea.updatePreOccupiedField(newList);
		}
	}

	/* Name: "isInLodArea"
	 * Checks that the field is from the LOD drop area (physically contained or has the same name and table as the field in the lod area  */
	private Boolean isInLodArea(Field field){
		for(Field lodField : _lodArea.getFields()){
			if(_xArea.getFields().contains(field) || _yArea.getFields().contains(field))
			{
				return false;
			}else if (_lodArea.getFields().contains(field) || (lodField.getName().equals(field.getName()) && lodField.getTable().getName().equals(field.getTable().getName())) ){
				return true;
			}
		}
		return false;
	}

	/* Name: "filterItemsExistInMap"
	 * Checks that all the selected values of a given filter appear as keys in the given map */
	private Boolean filterItemsExistInMap(Filter currentFilter, Map<MultiKey, SeriesData> map ){

		for(Object item:currentFilter.getSelectedValues()){
			String filterItem = item.toString();
			Boolean filterWasFound = false;
			for ( MultiKey key : map.keySet() ) {
				if(Arrays.asList(key.getKeys()).contains(filterItem)){ 
					filterWasFound = true;
					break;
				}
			}
			if(!filterWasFound){
				return false;
			}
		}
		return true;
	}

	/* Name: "handleLODFilters"
	 * When a filter based on a LOD field is applied, no need to query the database again
	 * Since the data already exists and organized by keys based on the LOD field, it's enough to hide/unhide the data   
	 * under the corresponding key */ 
	private Boolean handleLODFilters(Filter currentFilter){

		//Verify that the filter which has been updated is category and if from a LOD field.
		//Otherwise - return false and fetch the data with the SQL query.
		if(currentFilter.getField().getClassification() != Classification.C || !isInLodArea(currentFilter.getField())){

			//Set all the LOD filters validity to false - to include them in the query.
			//Since they are not build with the query builder, their validity is not set automatically.
			for(Filter filter : filters()){
				if(filter.getField().getClassification() == Classification.C && isInLodArea(filter.getField())){
					filter.setValid(false);
				}
			}
			for(Filter filter : remoteFilters()){
				if(filter.getField().getClassification() == Classification.C && isInLodArea(filter.getField())){
					filter.setValid(false);
				}
			}
			return false;
		}

		if(_lastSubLists != null){
			getChart().getData().clear();
			List<XYChart.Series<Object, Object>> graphs = new ArrayList<>();

			for (Map<MultiKey, SeriesData> map : _lastSubLists) {

				//First check that all the selected items in the filter exist in "_lastSubLists", otherwise - need to fetch them with SQL query.
				//It happens when a filter based on LOD field has one or more unchecked items and then a filter based on non-LOD field is applied.
				// It queries the database and the returned results are missing the LOD unchecked values.
				if(!filterItemsExistInMap(currentFilter, map)){
					return false;
				}


				for (Map.Entry<MultiKey, SeriesData> entry : map.entrySet()) {
					Boolean addValues = true;
					MultiKey key = entry.getKey();
					if(isKeyInFilter(key,filters())){
						addValues = isKeyInFilter(key,remoteFilters());
					}else{
						addValues = false;
					}
					if(addValues){
						convertData(entry.getValue(), _spec);
						graphs.add(createChartSeries(entry.getValue(), _spec));
					}
				}
				getChart().getData().addAll(graphs);
			}
			return true;
		}
		return false;
	}

	/* Name: "isKeyInFilter"
	 * parameter: MultiKey, key to check if included in the filters list.
	 * parameter: List<Filter>, List of the currently applied filters.
	 * Gets a key and checks if it appears in the filters list */ 
	private Boolean isKeyInFilter(MultiKey key, List<Filter> filters ) {
		for(Filter filter : filters){
			Boolean filterFound=false;
			//ignores filters which are not in the LOD area or are not under C classification.
			if(!(filter.getField().getClassification() == Classification.C) || !isInLodArea(filter.getField())){
				continue;
			}
			for(Object item: filter.getSelectedValues()){
				String filterKey = item.toString();
				if(Arrays.asList(key.getKeys()).contains(filterKey)){
					filterFound=true;
					break;
				}
			}
			//If none of the current filter values exists in the tested points series - it is no use to continue to check the other filters
			//Just return false, so the calling method continues to the next set of points.
			if(!filterFound){
				return false;
			}
		}
		return true;

	}


	/*Name: setAreaFiltersListeners 
	 * This method handles fields which are connected to a filter
	 * If the field SQL function has changed the filter has to be changed accordingly 
	 * and the filter panel has to be adjusted. */
	private void setAreaFiltersListeners(DropArea area) {
		area.setOnAction(new EventHandler<FilterEvent>() {

			@Override
			public void handle(FilterEvent event) {

				Filter filter = event.getFilter();
				if(event.getEventType() == FilterEvent.DELETE){
					filters().remove(filter);
					getOnRemoveFilter().call(event.getFilter());
				}else if(event.getEventType() == FilterEvent.SHOW){
					filters().add(filter);
					getOnShowFilter().call(event.getFilter());
				}
			}
		});
	}

	private DropArea createControlArea(GridPane grid, String title, int  row, int col, int colspan, DropArea.Policy policy, DropArea.AcceptedRoles acceptedRoles) {                
		Text text = new Text(title);
		text.getStyleClass().add("input-area-header");

		DropArea area = new DropArea(policy, acceptedRoles);
		area.tableProperty().bind(_currentTableProperty);
		area.addListener(_areaListener);

		//Sets for the drop area all the possible drag and drop sources and their accepted transfer modes.
		Map<Class<?>, TransferMode[]> sourcesTransferModes = createDragAndDropModes();
		area.setDragAndDropModes(sourcesTransferModes);

		//Let the filters area know when the selected table is changed.
		getFiltersArea().tableProperty().bind(_currentTableProperty);

		setAreaFiltersListeners(area);

		grid.add(text, col, row);
		grid.add(area, col+1, row, colspan, 1);

		return area;
	}

//	private DropArea createIndicatorArea(GridPane grid, String title, int  row, int col, DropArea.Policy policy, DropArea.AcceptedRoles acceptedRoles) {
//		Text text = new Text(title);
//		text.getStyleClass().add("input-area-header");
//
//		DropArea area = new DropArea(policy, acceptedRoles);
//		area.tableProperty().bind(_currentTableProperty);
//		area.addListener(_areaListener);
//
//		Button addButton = new Button("+");
//		addButton.getStyleClass().add("flat-button");
//		addButton.setOnAction(new EventHandler<ActionEvent>() {
//
//			@Override
//			public void handle(ActionEvent arg0) {
//				createIndicator();
//			}
//		});
//
//		grid.add(text, col, row);
//		grid.add(area, col+1, row);
//		grid.add(addButton, col+2, row);
//
//		return area;
//	}

	private void setupGlassPaneListeners() {
		_glassPane.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (getLocalClipboard().hasContent(DnD.INDICATOR_FORMAT)) {
					event.acceptTransferModes(TransferMode.COPY);
					event.consume();
				}
			}
		});

		_glassPane.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Indicator indicator = getLocalClipboard().get(DnD.INDICATOR_FORMAT, Indicator.class);
				_indicators.add(indicator);
			}
		});
	}
	
	/*
	 * Tries to find an available data source either from the current table or the current simulation.
	 * @return CyclistDatasource - the data source which has been found.
	 */
	private CyclistDatasource getAvailableDatasource(){
		Table currentTable = getCurrentTable();
		Simulation currentSim = getCurrentSimulation();
		
		CyclistDatasource ds = (currentTable != null && currentTable.getDataSource()!= null) ? 
				currentTable.getDataSource() : 
				currentSim != null ? currentSim.getDataSource() : null;
		return ds;
				
	}

	/*Name: removeFilterFromDropArea
	 * If a filter is removed - check if it is connected to a numeric field. By searching this field in the drop areas.
	 * If so - change also the field display to indicate that is doesn't connect to a filter anymore */
	@Override 
	public void removeFilterFromDropArea(Filter filter){
		_xArea.removeFilterFromGlyph(filter);
		_yArea.removeFilterFromGlyph(filter);
	}

	private class FieldInfo {
		Field field;
		int index;

		public FieldInfo(Field field, int index) {
			this.field = field;
			this.index = index;
		}
	}

}