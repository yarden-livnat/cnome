package edu.utah.sci.cyclist.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.core.model.DataType;
import edu.utah.sci.cyclist.core.model.DataType.Type;

public class SQL {
	static Logger log = Logger.getLogger(SQL.class);
	
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
	
	
	/*
	 * 
	 */
	
	private static Map<Integer, DataType.Type> _sql2Field = new HashMap<>();
	private static Map<String, DataType.Type> _name2Field = new HashMap<>();
		
	
	public static DataType.Type fromSQL(int type) {
		DataType.Type result = _sql2Field.get(type);
		return result != null ? result : DataType.Type.NA;
	}
	
	public static DataType.Type fromSQL(String name) {
		String s = name;
		if (s.contains("("))
			s = s.substring(0, s.indexOf("("));
		DataType.Type result = _name2Field.get(s);
		if (result == null || result == Type.NA)
			log.warn("**Unknown datatype:"+name);
		return result != null ? result : DataType.Type.NA;
	}
	
	static {
		_sql2Field.put(java.sql.Types.INTEGER, 		DataType.Type.NUMERIC);
		_sql2Field.put(java.sql.Types.BIGINT, 		DataType.Type.NUMERIC);
		_sql2Field.put(java.sql.Types.SMALLINT, 	DataType.Type.NUMERIC);
		_sql2Field.put(java.sql.Types.TINYINT, 		DataType.Type.NUMERIC);
		_sql2Field.put(java.sql.Types.DECIMAL, 		DataType.Type.NUMERIC);
		
		_sql2Field.put(java.sql.Types.REAL, 		DataType.Type.NUMERIC);
		_sql2Field.put(java.sql.Types.DOUBLE, 		DataType.Type.NUMERIC);
		_sql2Field.put(java.sql.Types.FLOAT, 		DataType.Type.NUMERIC);
		
		_sql2Field.put(java.sql.Types.BOOLEAN, 		DataType.Type.BOOLEAN);

		_sql2Field.put(java.sql.Types.CHAR, 		DataType.Type.TEXT);
		_sql2Field.put(java.sql.Types.VARCHAR, 		DataType.Type.TEXT);
		_sql2Field.put(java.sql.Types.LONGVARCHAR, 	DataType.Type.TEXT);
		_sql2Field.put(java.sql.Types.NCHAR, 		DataType.Type.TEXT);
		_sql2Field.put(java.sql.Types.VARCHAR, 		DataType.Type.TEXT);
		_sql2Field.put(java.sql.Types.LONGNVARCHAR, DataType.Type.TEXT);
		
		_sql2Field.put(java.sql.Types.DATE, 		DataType.Type.DATE);
		_sql2Field.put(java.sql.Types.TIME, 		DataType.Type.DATETIME);
		_sql2Field.put(java.sql.Types.TIMESTAMP, 	DataType.Type.DATETIME);
		
		_sql2Field.put(java.sql.Types.BIT,	 		DataType.Type.NA);
		_sql2Field.put(java.sql.Types.BLOB, 		DataType.Type.TEXT);
		_sql2Field.put(java.sql.Types.DISTINCT, 	DataType.Type.NA);
		_sql2Field.put(java.sql.Types.DATALINK, 	DataType.Type.NA);
		_sql2Field.put(java.sql.Types.JAVA_OBJECT, 	DataType.Type.NA);
		_sql2Field.put(java.sql.Types.LONGVARBINARY,DataType.Type.NA);
		_sql2Field.put(java.sql.Types.NCLOB, 		DataType.Type.NA);
		_sql2Field.put(java.sql.Types.NULL, 		DataType.Type.NA);
		_sql2Field.put(java.sql.Types.OTHER, 		DataType.Type.NA);
		_sql2Field.put(java.sql.Types.REF, 			DataType.Type.NA);
		_sql2Field.put(java.sql.Types.SQLXML, 		DataType.Type.NA);
		_sql2Field.put(java.sql.Types.STRUCT,		DataType.Type.NA);
		_sql2Field.put(java.sql.Types.VARBINARY, 	DataType.Type.NA);
		
		
		_name2Field.put("INTEGER", 			DataType.Type.NUMERIC);
		_name2Field.put("INT", 				DataType.Type.NUMERIC);
		_name2Field.put("BIGINT", 			DataType.Type.NUMERIC);
		_name2Field.put("SMALLINT",			DataType.Type.NUMERIC);
		_name2Field.put("TINYINT", 			DataType.Type.NUMERIC);
		_name2Field.put("DECIMAL", 			DataType.Type.NUMERIC);
		
		_name2Field.put("REAL",				DataType.Type.NUMERIC);
		_name2Field.put("DOUBLE", 			DataType.Type.NUMERIC);
		_name2Field.put("FLOAT", 			DataType.Type.NUMERIC);
		_name2Field.put("MEASURE",			DataType.Type.NUMERIC);    // sqlite return field type name as 'MEASURE'
		
		_name2Field.put("BOOLEAN", 			DataType.Type.BOOLEAN);
		_name2Field.put("BOOL", 			DataType.Type.BOOLEAN);   	// sqlite return field type name as 'BOOL'. 

		_name2Field.put("TEXT",				DataType.Type.TEXT);     // sqlite return field type name as 'text'. it's not part of java.sql.Types
		_name2Field.put("CHAR",				DataType.Type.TEXT);
		_name2Field.put("VARCHAR", 			DataType.Type.TEXT);
		_name2Field.put("LONGVARCHAR", 		DataType.Type.TEXT);
		_name2Field.put("NCHAR", 			DataType.Type.TEXT);
		_name2Field.put("VARCHAR", 			DataType.Type.TEXT);
		_name2Field.put("LONGNVARCHAR", 	DataType.Type.TEXT);
		
		_name2Field.put("DATE", 			DataType.Type.DATE);
		_name2Field.put("TIME", 			DataType.Type.DATETIME);
		_name2Field.put("TIMESTAMP", 		DataType.Type.DATETIME);
		_name2Field.put("DATETIME", 		DataType.Type.DATETIME);      // sqlite return field type name as 'DATETIME'. 
		
		_name2Field.put("BIT",	 			DataType.Type.NA);
		_name2Field.put("BLOB", 			DataType.Type.TEXT);
		_name2Field.put("DISTINCT", 		DataType.Type.NA);
		_name2Field.put("DATALINK", 		DataType.Type.NA);
		_name2Field.put("JAVA_OBJECT", 		DataType.Type.NA);
		_name2Field.put("LONGVARBINARY",	DataType.Type.NA);
		_name2Field.put("NCLOB", 			DataType.Type.NA);
		_name2Field.put("NULL", 			DataType.Type.NA);
		_name2Field.put("OTHER", 			DataType.Type.NA);
		_name2Field.put("REF", 				DataType.Type.NA);
		_name2Field.put("SQLXML", 			DataType.Type.NA);
		_name2Field.put("STRUCT",			DataType.Type.NA);
		_name2Field.put("VARBINARY", 		DataType.Type.NA);
	}
	
}
