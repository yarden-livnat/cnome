package edu.utah.sci.cyclist.core.model;


public class DateTimeStructure implements FieldStructure {
	public static final String LABELS[]  = {"Year", "Quarter", "Month", "Day"}; 
	public static final String FUNCTIONS[]  = {"year", "quarter", "month", "day"}; 
	
	@Override
	public String[] getLabels() {
		return LABELS;
	}

	@Override
	public String[] getFunctions() {
		return FUNCTIONS;
	}
}
