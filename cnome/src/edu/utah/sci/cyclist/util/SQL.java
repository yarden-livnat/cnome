package edu.utah.sci.cyclist.util;

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
	public static final String DEFAULT_FUNCTION = "Avg";
	
	public enum Functions{VALUE,AVG,SUM,COUNT,COUNT_DISTINCT,MIN,MAX;
		public String toName() {
		   //only capitalize the first letter
		   String s = super.toString();
		   
		   int index = s.indexOf("_");
		   if(index >-1){
			   s = s.substring(0,index)+"("+s.substring(index+1,s.length())+")";
		   }
		   
		   return s.substring(0, 1) + s.substring(1).toLowerCase();
		}
		
		public String toLabel() {
			   String s = super.toString();
			   int index = s.indexOf("_");
			   if(index >-1){
				   s = s.substring(0,index);
			   }
			   
			   return s.substring(0, 1) + s.substring(1).toLowerCase();
		}
		
		public static Functions getEnum(String name) {
	        if(name == null)
	            throw new IllegalArgumentException();
	        for(Functions n : values())
	            if(n.toName().equals(name)) return n;
	        throw new IllegalArgumentException();
	    }
	};
	
	public static Function getFunction(String name) {
		for (Function f : FUNCTIONS) {
			if (f.getName().equals(name))
				return f;
		}
		return null;
	}
	
	static {
//		FUNCTIONS.add(new Function("", "NA") {
//			@Override
//			public String format(String col) {
//				return col;
//			}
//		});
		FUNCTIONS.add(new Function("Value", "") {
			@Override
			public String getLabel(String col) {
				return col;
			}
			
			@Override
			public String format(String col) {
				return col;
			}
		});
		
		FUNCTIONS.add(new Function("Avg", "AVG"));
		FUNCTIONS.add(new Function("Sum", "SUM"));
//		FUNCTIONS.add(new Function("Median", "MEDIAN"));
		FUNCTIONS.add(new Function("Count", "COUNT"));
		FUNCTIONS.add(new Function("Count(distinct)", "COUNT") {
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
