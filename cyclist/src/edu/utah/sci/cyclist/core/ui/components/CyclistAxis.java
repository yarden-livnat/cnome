package edu.utah.sci.cyclist.core.ui.components;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


public class CyclistAxis extends NumAxis {
	public enum Mode {LINEAR, LOG}
	
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
		super();
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

	
	@Override
	protected Object autoRange(double minValue, double maxValue, double length, double labelSize) {
		double[] range = (double [])super.autoRange(minValue, maxValue, length, labelSize);
		if (getMode() == Mode.LOG) {		
			if (range[1] < 1) {
				range[1] = 1;
			}
			if (isForceZeroInRange()) {
            	range[0] = 1;
            } else {
            	range[0] = Math.max(range[0], 1);
            }
		}
        return range;  
    }
	
	@Override
	protected void setRange(Object range, boolean animate) {
		if (getMode() == Mode.LINEAR) {
			super.setRange(range, animate);
		} else {
        	if (range != null) {
        		double[] drange = (double[]) range;
        		double lowerBound = drange[0];
    			double upperBound = drange[1];
    			setLowerBound(lowerBound);
    			setUpperBound(upperBound);
//    			if (lowerBound< 1) {
//    				lowerBound = 1;
//    			}
    			if (animate) {
    				System.out.println("setRange animate not implemented yet.");
    			}
    		}
		}
	}

//	@Override
//	protected Object getRange() {
//		if (getMode() == Mode.LINEAR) 
//			return super.getRange();
//
//		return new double[] { getLowerBound(), getUpperBound() };
//	}

	@Override
	protected List<Number> calculateTickValues(double length, Object range) {
		if (getMode() == Mode.LINEAR) 
			return super.calculateTickValues(length, range);
			
		List<Number> tickPositions = new ArrayList<>();
		if (range != null) {
			double[] drange = (double[]) range;
			double lowerBound = drange[0];
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
			double lowerBound = drange[0];
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
		
		double logLowerBound = Math.log10(getLowerBound());
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
		
		if (value.doubleValue() == 0) return 0;
		
		double logValue = value.doubleValue() > 0 ? Math.log10(value.doubleValue()) : 0;
		double lowerBound = getLowerBound();
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
