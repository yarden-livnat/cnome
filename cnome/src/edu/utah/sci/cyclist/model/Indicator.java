package edu.utah.sci.cyclist.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Indicator {
	private DoubleProperty _valueProperty = new SimpleDoubleProperty();
	
	public DoubleProperty valueProperty() { return _valueProperty; }
	public double getValue() { return _valueProperty.get(); }
	public void setValue(double v) { _valueProperty.set(v); }
}
