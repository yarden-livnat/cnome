package edu.utah.sci.cyclist.neup.ui.views.inventory;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import edu.utah.sci.cyclist.core.ui.components.CyclistViewBase;

public class InventoryView extends CyclistViewBase {
	public static final String ID = "inventory-view";
	public static final String TITLE = "Inventory";

	private static final String NET_CHART_LABEL = "Net";
	private static final String COMMULATIVE_CHART_LABEL = "Commulative";
	
	private LineChart<Number, Number> _chart;
	private NumberAxis _xAxis;
	private NumberAxis _yAxis;
	
	public InventoryView() {
		super();		
		build();
	}
	
	private void selectChartType(String value) {
	
	}
	
	private void build() {
		setTitle(TITLE);
		getStyleClass().add("inventory");
	
		BorderPane pane = new BorderPane();
		pane.setLeft(buildCtrl());
		pane.setCenter(buildChart());
		
		setContent(pane);
	}
	
	private Node buildCtrl() {
		VBox vbox = new VBox();
		
		ChoiceBox<String> type = new ChoiceBox<>();
		type.getStyleClass().add("choice");
		type.getItems().addAll(COMMULATIVE_CHART_LABEL, NET_CHART_LABEL);
		type.valueProperty().addListener(e->{
			selectChartType(type.getValue());
		});
		
		type.setValue(COMMULATIVE_CHART_LABEL);
		
		vbox.getChildren().add(type);
		return vbox;
	}
	

	private Node buildChart() {
		_xAxis = new NumberAxis();
		_xAxis.setLabel("time");
		_xAxis.setAnimated(false);
		
		_yAxis = new NumberAxis();
		_yAxis.setLabel("Amount");
		_yAxis.setAnimated(false);
		
		_chart = new LineChart<>(_xAxis, _yAxis);
		_chart.getStyleClass().add("chart");
		_chart.setCreateSymbols(false);
		
		return _chart;
	}
	
}
