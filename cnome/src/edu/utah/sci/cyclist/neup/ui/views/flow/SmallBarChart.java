package edu.utah.sci.cyclist.neup.ui.views.flow;

import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;

public class SmallBarChart<X, Y> extends BarChart<X, Y> {

	public SmallBarChart(Axis<X> xAxis, Axis<Y> yAxis) {
		super(xAxis, yAxis);
	}
	
	/*
	 *  override the hard coded defaults in Chart.(non-Javadoc)
	 * @see javafx.scene.chart.Chart#computeMinHeight(double)
	 */
	
	@Override protected double computeMinHeight(double width) { return 10; }
    
	@Override protected double computeMinWidth(double height) { return 20; }

//    @Override protected double computePrefWidth(double height) { return 500.0; }
//
//    @Override protected double computePrefHeight(double width) { return 400.0; }

}
