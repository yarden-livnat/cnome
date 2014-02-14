package edu.utah.sci.cyclist.core.ui.components;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

public class NumericField extends TextField {

	private IntegerProperty _valueProperty = new SimpleIntegerProperty();
	private int _minValue = Integer.MIN_VALUE;
	private int _maxValue = Integer.MAX_VALUE;
	
	public NumericField() {
		this(0);
	}
	
	public NumericField(int value) {
		super();
		
		setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				valueProperty().set(Integer.parseInt(getText()));
			}
		});
		
		setValue(value);
	}
	
	public void setMinValue(int value) {
		_minValue = value;
		if (getValue() < _minValue)
			setValue(_minValue);
	}
	
	public void setMaxValue(int value) {
		_maxValue = value;
		if (getValue() > _maxValue)
			setValue(_maxValue);
	}
	
	public IntegerProperty valueProperty() {
		return _valueProperty;
	}
	
	public int getValue() {
		return valueProperty().get();
	}
	
	public void setValue(int value) {
		value = Math.max(_minValue, Math.min(value, _maxValue));
		valueProperty().set(value);
		setText(Integer.toString(value));
	}
	
	@Override
    public void replaceText(int start, int end, String text) {
        if (text.matches("[0-9]")) {
            super.replaceText(start, end, text);                     
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (!text.matches("[0-9]")) {
            super.replaceSelection(text);
        }
    }
}
