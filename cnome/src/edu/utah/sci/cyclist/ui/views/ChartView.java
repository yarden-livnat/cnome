package edu.utah.sci.cyclist.ui.views;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
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
	
	public ChartView() {
		super();
		build();
	}
	
	
	private void build() {
		setTitle(TITLE);
		
		createChart();
		getStyleClass().add("chart-view");
		VBox vbox = VBoxBuilder.create()
						.children(
								_chart,
								createControl()
							)
						.build();
		setContent(vbox);
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
		
		createControlArea(grid, "X", 0);
		createControlArea(grid, "Y", 1);
				
		return grid;
	}
	
	private void createControlArea(GridPane grid, String title, int  row) {
		
		Text text = TextBuilder.create().text(title).styleClass("input-area-header").build();
		
		grid.add(text, 0, row);
		grid.add(new DropArea(DropArea.Policy.SINGLE), 1, row);
	}
}
