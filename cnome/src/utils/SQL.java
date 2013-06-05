package utils;

import java.util.ArrayList;
import java.util.List;

public class SQL {
	
	public static class Function {
		private String _name;
		private String _label;
		
		public Function(String name, String label) {
			_name = name;
			_label = label;
		}
		
		public String getName() {
			return _name;
		}
		
		public String getLabel(String col) {
			return _label+"("+col+")";
		}
		
		public String format(String col) {
		 return _name+"("+col+")";
		}
	}
	
	public static final List<Function> FUNCTIONS = new ArrayList<>();
	public static final String DEFAULT_FUNCTION = "Sum";
	
	public static Function getFunction(String name) {
		for (Function f : FUNCTIONS) {
			if (f.getName().equals(name))
				return f;
		}
		return null;
	}
	
	static {
		FUNCTIONS.add(new Function("Avg", "AVG"));
		FUNCTIONS.add(new Function("Sum", "SUM"));
//		FUNCTIONS.add(new Function("Median", "MEDIAN"));
		FUNCTIONS.add(new Function("Count", "COUNT"));
		FUNCTIONS.add(new Function("Count(disinct)", "COUNT") {
			@Override
			public String format(String col) {
				return "Count(Distinct "+col+")";
			}
		});

		FUNCTIONS.add(new Function("Min", "MIN"));
		FUNCTIONS.add(new Function("Max", "MAX"));
		
//		FUNCTIONS.add(new Function("Std. Dev", "STDEV"));
//		FUNCTIONS.add(new Function("Variance", "VAE"));

	}
	
}
