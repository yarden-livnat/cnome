package edu.utah.sci.cyclist.ui.components;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

public class NumericField extends TextField {

	private IntegerProperty _valueProperty = new SimpleIntegerProperty();
	
	public NumericField() {
		this(0);
	}
	
	public NumericField(int value) {
		super(Integer.toString(value));
		
		setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				_valueProperty.set(Integer.parseInt(getText()));
			}
			
		});
	}
	
	public IntegerProperty valueProperty() {
		return _valueProperty;
	}
	
	public int getValue() {
		return _valueProperty.get();
	}
	
	public void setValue(int value) {
		_valueProperty.set(value);
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
