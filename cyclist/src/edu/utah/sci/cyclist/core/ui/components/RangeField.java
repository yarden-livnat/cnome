package edu.utah.sci.cyclist.core.ui.components;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.utah.sci.cyclist.neup.model.Range;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

public class RangeField extends TextField {
	
	private int _minValue = 0;
	private int _maxValue = 100;
	
	public enum Mode {TO, DURATION}
	
	private boolean _parsing = false;
	private boolean _changing = false;
	private Mode _mode;
	
	private IntegerProperty _fromProperty = new SimpleIntegerProperty(1);
	private IntegerProperty _toProperty = new SimpleIntegerProperty(1);
	private ObjectProperty<Range<Integer>> _rangeProperty = new SimpleObjectProperty<>();
	
	public ObjectProperty<Range<Integer>> rangeProperty() {
		return _rangeProperty;
	}
	
	public Range<Integer> getRange() {
		return rangeProperty().get();
	}
	
	public void setRange(Range<Integer> range) {
		rangeProperty().set(range);
		update();
	}
	
//	public IntegerProperty fromProperty() {
//		return _fromProperty;
//	}
//	
//	public void setFrom(int value) {
//		fromProperty().set(value);
//		System.out.println("from: "+value);
//		if (_mode == Mode.SINGLE) {
//			setTo(value);
//			setDuration(0);
//		}
//		update();
//	}
//	
//	public int getFrom() {
//		return fromProperty().get();
//	}
//	
//	public IntegerProperty toProperty() {
//		return _toProperty;
//	}
//	
//	public void setTo(int value) {
//		toProperty().set(value);
//		System.out.println("to: "+value);
//		if (_mode == Mode.TO) {
//			setDuration(getTo() - getFrom() +1);
//			update();
//		}
//	}
//	
//	public int getTo() {
//		return toProperty().get();
//	}
//	
//	public IntegerProperty durationProperty() {
//		return _durationProperty;
//	}
//	
//	public void setDuration(int value) {
//		durationProperty().set(value);
//		System.out.println("duration: "+value);
//		if (_mode == Mode.DURATION) {
//			setTo(getFrom()+getDuration()-1);
//			update();
//		}
//	}
//	
//	public int getDuration() {
//		return durationProperty().get();
//	}
	
	
	/**
	 * Constructor
	 */
	public RangeField() {
		getStyleClass().add("range-field");
		setOnAction(e->parse());
		rangeProperty().addListener(o->{
			update();
		});
		_mode = Mode.DURATION;
		setRange(new Range<Integer>(1, 1));
	}
	
	public void inc() {
		Range<Integer> r = getRange();
		Range<Integer> nr = new Range<>();
		nr.from = r.from+1;
		if (_mode == Mode.TO)
			nr.to = Math.max(nr.from, r.to);
		else // _mode == Mode.DURATION 
			nr.to = r.to+1;
		
		setRange(nr);
	}
	
	public void dec() {
		Range<Integer> r = getRange();
		if (r.from == _minValue) return;
		
		Range<Integer> nr = new Range<>();		
		nr.from = r.from-1;
		if (_mode == Mode.TO)
			nr.to = r.to;
		else // _mode == Mode.DURTATION
			nr.to = r.to-1;
		
		setRange(nr);
	}
	
	public void setMinValue(int value) {
		if (_minValue == value) return;
		
		_minValue = value;
		Range<Integer> r = getRange();
		if (r.from < _minValue) { 
			Range<Integer> nr = new Range<>(_minValue, r.to);
			if (_mode == Mode.DURATION)
				nr.to = _minValue+r.from-r.to;
			setRange(nr);
		}
	}
	
//	public void setMaxValue(int value) {
//		if (_maxValue == value) return;
//		_maxValue = value;
//		
//		if (getTo() > value) {
//			setTo(value);
//		}
//	}
	
	@Override
    public void replaceText(int start, int end, String text) {
        if (text == "" || text.matches("[0-9 +-]")) {
            super.replaceText(start, end, text);  
    		if (!_changing) {
    			_changing = true;
    			getStyleClass().add("changing");
    		}
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (text == "" || text.matches("[0-9 +-]")) {
            super.replaceSelection(text);
    		if (!_changing) {
    			_changing = true;
    			getStyleClass().add("changing");
    		}
        }
    }
    
    private void update() {
    	if (_parsing) return;
    	Range<Integer> r = getRange();
    	if (_mode == Mode.TO)
    		setText(String.format("%d-%d", r.from, r.to));
    	else {
    		int d = r.to - r.from;
    		if (d == 0) 
    			setText(String.format("%d", r.from));
    		else
    			setText(String.format("%d+%d", r.from, d+1));
    	}
    }
    
    private Pattern singlePattern = Pattern.compile("^([1-9][0-9]*)$");
    private Pattern multiplePattern = Pattern.compile("^([1-9][0-9]*) *([-+]) *([1-9][0-9]*)");
    
    private void parse() {
    	_parsing = true;
    	boolean ok = false;
    	try {
	    	Matcher matcher = singlePattern.matcher(getText());
	    	if (matcher.find()) {
	    		_mode = Mode.DURATION;
	    		int n = Integer.parseInt(matcher.group());
	    		setRange(new Range<>(n, n));
	    		ok = true;
	    	} else {
	    		matcher = multiplePattern.matcher(getText());
	    		if (matcher.find()) {
	    			int n1 = Integer.parseInt(matcher.group(1));
	    			int n2 = Integer.parseInt(matcher.group(3)); 

	    			if (matcher.group(2).equals("+")) {
	    				_mode = Mode.DURATION;
	    				setRange(new Range<>(n1, n1+n2-1));
	    				ok = true;
	    			} else if (n2 > n1) {
	    				_mode = Mode.TO;
	    				setRange(new Range<>(n1, n2));
	    				ok = true;
	    			} 
	    		}
	    	}
    		if (ok) {
    			getStyleClass().remove("changing");
    			_changing = false;
    		}
    	} catch (Exception e) {
    		System.out.println("RangeField parse error");
    	} finally {
    		_parsing = false;
    	}
    }
}
