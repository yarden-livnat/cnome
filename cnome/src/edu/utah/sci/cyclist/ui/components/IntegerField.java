package edu.utah.sci.cyclist.ui.components;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class IntegerField extends TextField {

	private IntegerProperty _value;
	private int _min;
	private int _max;
	
	public int getValue() { 
		return _value.getValue(); 
	}
	
	public void setValue(int value) { 
		_value.setValue(value); 
	}
	
	public IntegerProperty valueProperty() {
		return _value;
	}
	
	public IntegerField() {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE);
	}
	public IntegerField(int min, int max, Integer value)  {
		_min = min;
		_max = max;
		
		_value = new SimpleIntegerProperty(value);
		
		if (value != Integer.MIN_VALUE)
			setText(value.toString());
		
		addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (!"0123456789".contains(event.getCharacter())) {
					event.consume();
				}
				
			}
		});
		
		textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (newValue == null || "".equals(newValue)) {
					_value.setValue(Integer.MIN_VALUE);
				}
				
			}
		});
		
		_value.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				if (newValue == null) {
					setText(null);
				} else {
					
				}
				
				
			}
		});
	}
}
