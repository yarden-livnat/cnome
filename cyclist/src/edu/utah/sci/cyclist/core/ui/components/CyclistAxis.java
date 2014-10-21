package edu.utah.sci.cyclist.core.ui.components;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;


public class CyclistAxis extends CyclistNumberAxis {
	public enum Mode {LINEAR, LOG}
	public static final double EPS = 1e-1;
	
	private double _posMin = EPS;
	
	private ObjectProperty<Mode> _mode = new ObjectPropertyBase<Mode>(Mode.LINEAR) {
		 @Override protected void invalidated() {
			invalidateRange();
            requestAxisLayout();
	     }

        @Override
        public Object getBean() {
            return CyclistAxis.this;
        }

        @Override
        public String getName() {
            return "mode";
        }
	};
	
	public ObjectProperty<Mode> mode() {
		return _mode;
	}
	
	public Mode getMode() {
		return _mode.get();
	}
	
	public void setMode(Mode m) {
		_mode.set(m);
	}
	
	

	public CyclistAxis() {
		this(Mode.LINEAR);
	}
	
	public CyclistAxis(Mode mode) {
		super();
		setMode(mode);
		// A hack: JavaFX doesn't redraw if forceZeroInRange changes
		forceZeroInRangeProperty().addListener(new InvalidationListener() {	
			@Override
			public void invalidated(Observable observable) {
				invalidateRange();
	            requestAxisLayout(); 			
	        }
		});
	}
	
	public CyclistAxis(double from, double to, double tickUnit) {
		super(from, to, tickUnit);
		
		// A hack: JavaFX doesn't redraw if forceZeroInRange changes
		forceZeroInRangeProperty().addListener(new InvalidationListener() {	
			@Override
			public void invalidated(Observable observable) {
				invalidateRange();
	            requestAxisLayout(); 			
	        }
		});
	}

	
	@Override public void invalidateRange(List<Number> data) {
		if (getMode() == Mode.LOG) {
            if (data.isEmpty()) {
                _posMin = EPS;;
            } else {
                _posMin = Double.MAX_VALUE;
                for(Number dataValue: data) {
                	double v = dataValue.doubleValue();
                	if (v >0) _posMin = Math.min(_posMin, v);
                }
           }
		}
        super.invalidateRange(data);
    }

	@Override
	protected List<Number> calculateTickValues(double length, Object range) {
		if (getMode() == Mode.LINEAR) 
			return super.calculateTickValues(length, range);
			
		List<Number> tickPositions = new ArrayList<>();
		if (range != null) {
			double[] drange = (double[]) range;
			double lowerBound = _posMin;
			double upperBound = drange[1];
			int logLowerBound = (int) Math.floor(Math.log10(lowerBound));
			int logUpperBound = (int) Math.floor(Math.log10(upperBound));
	
			for (int i=logLowerBound; i<= logUpperBound; i++) {
				tickPositions.add(Math.pow(10, i));
			}
			double scale = Math.pow(10, logUpperBound);
			double top = (int)(upperBound/scale)*scale;
			tickPositions.add(top);
		}
		return tickPositions;
	}

	@Override
	protected List<Number> calculateMinorTickMarks() {
		if (getMode() == Mode.LINEAR)
			return super.calculateMinorTickMarks();
		
		List<Number> minorTickMarksPositions = new ArrayList<>();
		double[] drange = (double[]) getRange();
		if (drange != null) {
			double lowerBound = _posMin;
			double upperBound = drange[1];
			int logLowerBound = (int) Math.floor(Math.log10(lowerBound));
			int logUpperBound = (int) Math.floor(Math.log10(upperBound)+1);
			int minorTickMarkCount = 10; //getMinorTickCount();
				
			for (int i = logLowerBound; i < logUpperBound; i++) {
				for (int j = 1; j < 10; j += (10 / minorTickMarkCount)) {
					double value = j * Math.pow(10, i);
					minorTickMarksPositions.add(value);
				}
			}
		}
		return minorTickMarksPositions;
	}
	
//	@Override
//	protected String getTickMarkLabel(Number value) {
//
////		if (getMode() == Mode.LINEAR)
////			return super.getTickMarkLabel(value);
////		
////		return value.doubleValue() <= 1000 ?
////			String.format("%1$3.0f", value) :
////			String.format("%1$3.0g", value);
//			
//		String str = "";
//		if (getMode() == Mode.LINEAR)
//			str = super.getTickMarkLabel(value);
//		else 
//    		str =  value.doubleValue() <= 1000 ?
//    			String.format("%1$3.0f", value) :
//    			String.format("%1$3.0g", value);
//		
//		System.out.println("Tick: "+value+": "+str);
//		return str;
//	}
	
	@Override
	public Number getValueForDisplay(double displayPosition) {
		if (getMode() == Mode.LINEAR)
			return super.getValueForDisplay(displayPosition);
		
		double logLowerBound = Math.log10(_posMin); //getLowerBound());
		double delta = Math.log10(getUpperBound()) - logLowerBound ;
		if (getSide().isVertical()) {
			return Math.pow(10, (((displayPosition - getHeight()) / -getHeight()) * delta) + logLowerBound);
		} else {
			return Math.pow(10, (((displayPosition / getWidth()) * delta) + logLowerBound));
		}
	}
 
	@Override
	public double getDisplayPosition(Number value) {
		if (getMode() == Mode.LINEAR)
			return super.getDisplayPosition(value);
		
		double v = Math.max(value.doubleValue(), _posMin); 
		
		double logValue = Math.log10(v);
		double lowerBound = _posMin;
		double logLowerBound = Math.log10(lowerBound);
		double delta = Math.log10(getUpperBound()) - logLowerBound ;
		double deltaV = logValue - logLowerBound;
		if (getSide().isVertical()) {
			return (1. - ((deltaV) / delta)) * getHeight();
		} else {
			return ((deltaV) / delta) * getWidth();
		}
	}

}
