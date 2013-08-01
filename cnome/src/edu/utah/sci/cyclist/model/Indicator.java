package edu.utah.sci.cyclist.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Indicator {
	public static final String HOVER = "hover";
	public static final String SELECTED = "selected";
	
	private DoubleProperty _valueProperty = new SimpleDoubleProperty();
	private BooleanProperty _hoverProperty = new SimpleBooleanProperty(false);
	private BooleanProperty _selectedProperty = new SimpleBooleanProperty(false);
	
	
	public Indicator() {
		this(0);
	}
	
	public Indicator(double value) {
		_valueProperty.set(value);
	}
	
	public DoubleProperty valueProperty() { return _valueProperty; }
	public double getValue() { return _valueProperty.get(); }
	public void setValue(double v) { _valueProperty.set(v); }
	
	public BooleanProperty hoverProperty() { return _hoverProperty; }
	public boolean getHover() { return _hoverProperty.get(); }
	public void setHover(boolean value) { _hoverProperty.set(value); }
	
	public BooleanProperty selectedProperty() { return _selectedProperty; }
	public boolean getSelected() { return _selectedProperty.get(); }
	public void setSelected(boolean value) { _selectedProperty.set(value); }
	
}
