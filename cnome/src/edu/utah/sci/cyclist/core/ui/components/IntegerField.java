package edu.utah.sci.cyclist.core.ui.components;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;

public class IntegerField extends TextField {

	private IntegerProperty _valueProperty = new SimpleIntegerProperty();
	private int _minValue = Integer.MIN_VALUE;
	private int _maxValue = Integer.MAX_VALUE;
	
	private boolean _active = false;
	
	public IntegerField() {
		this(0);
	}
	
	public IntegerField(int value) {
		super();
		setOnAction(e->parseValue());
		setValue(value);
		
		_valueProperty.addListener(o->{
			setText(Integer.toString(getValue()));
			setActive(false);
		});
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
	}
	
	@Override
    public void replaceText(int start, int end, String text) {
        if (text.matches("[0-9]")) {
            super.replaceText(start, end, text);  
            setActive(true);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (!text.matches("[0-9]")) {
            super.replaceSelection(text);
            setActive(true);
        }
    }
    
    private void parseValue() {
    	try {
    		int n = Integer.parseInt(getText());
//    		valueProperty().set(n);
    		setValue(n);
    	} catch (Exception e) {
    		System.out.println("** Do something about an Illegle number");
    	}
    	
    }
    
    private void setActive(boolean value) {
    	if (value == _active) return;
    	
    	_active = value;
    	if (_active) 
    		setStyle("-fx-background:lightgray");
    	else
    		setStyle("-fx-background:white");
    }
	
}