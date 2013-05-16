package edu.utah.sci.cyclist.ui.views;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import edu.utah.sci.cyclist.ui.components.DropArea;
import edu.utah.sci.cyclist.ui.components.ViewBase;

public class ChartView extends ViewBase {
	public static final String TITLE = "Chart";

	private XYChart<?,?> _chart;
	private BorderPane _pane;
	private DropArea _xArea;
	private DropArea _yArea;
	
	public ChartView() {
		super();
		build();
	}
	
	
	private void build() {
		setTitle(TITLE);
		
		createChart();
		getStyleClass().add("chart-view");
//		VBox vbox = VBoxBuilder.create()
//						.children(
//								_pane = new Pane(),
//								createControl()
//							)
//						.build();
//		setContent(vbox);
		_pane = BorderPaneBuilder.create().build();
		_pane.setBottom(createControl());
		
		setContent(_pane);
	}
	
	private void createChart() {
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		
		LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);

		_chart = chart;
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
			System.out.println("changed");
			if (_xArea.getFields().size() > 0 && _yArea.getFields().size() > 0) {
				createChart();
				_pane.setCenter(_chart);
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
