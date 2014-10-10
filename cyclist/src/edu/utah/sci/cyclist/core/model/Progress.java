package edu.utah.sci.cyclist.core.model;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Progress<T> {
	public DoubleProperty progress = new SimpleDoubleProperty();
	public StringProperty message = new SimpleStringProperty();
	public ObjectProperty<T> result = new SimpleObjectProperty<>();
	private Consumer<T> _onDone;
	
	
	private ChangeListener<T> listener = new ChangeListener<T>() {

		@Override
        public void changed(ObservableValue<? extends T> observable,
                T oldValue, T newValue) {
            result.removeListener(this);
            if (_onDone != null)
            	_onDone.accept(result.get());
            
        }
	};
	
	public Progress<T> then(Consumer<T> func) {
		_onDone = func;
		if (_onDone != null) {
			result.addListener(listener);
		} else {
			result.removeListener(listener);
		}
		return this;
	}
}
