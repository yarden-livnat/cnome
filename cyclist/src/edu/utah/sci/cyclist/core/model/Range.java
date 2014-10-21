package edu.utah.sci.cyclist.core.model;

public class Range {
	public final static Range INVALID_RANGE = new Range(0, -1);
	
	public final double min;
	public final double max;
	
	public Range(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	public boolean isValid() {
		return min <= max;
	}
}
