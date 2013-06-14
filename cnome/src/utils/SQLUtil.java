package utils;

import java.util.HashMap;
import java.util.Map;

import edu.utah.sci.cyclist.model.DataType;


public class SQLUtil {

	private static Map<Integer, DataType.Type> _sql2Field = new HashMap<>();
	private static Map<String, DataType.Type> _name2Field = new HashMap<>();
		
	
	public static DataType.Type fromSQL(int type) {
		DataType.Type result = _sql2Field.get(type);
		return result != null ? result : DataType.Type.NA;
	}
	
	public static DataType.Type fromSQL(String name) {
		DataType.Type result = _name2Field.get(name);
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
		_sql2Field.put(java.sql.Types.BLOB, 		DataType.Type.NA);
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
		_name2Field.put("BLOB", 			DataType.Type.NA);
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
