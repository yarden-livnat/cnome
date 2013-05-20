package utils;

import java.util.HashMap;
import java.util.Map;

import edu.utah.sci.cyclist.model.Field;


public class SQLUtil {

	private static Map<Integer, Field.Type> _sql2Field = new HashMap<>();
	private static Map<String, Field.Type> _name2Field = new HashMap<>();
		
	
	public static Field.Type fromSQL(int type) {
		Field.Type result = _sql2Field.get(type);
		return result != null ? result : Field.Type.NA;
	}
	
	public static Field.Type fromSQL(String name) {
		Field.Type result = _name2Field.get(name);
		return result != null ? result : Field.Type.NA;
	}
	
	static {
		_sql2Field.put(java.sql.Types.INTEGER, 		Field.Type.INTEGER);
		_sql2Field.put(java.sql.Types.BIGINT, 		Field.Type.INTEGER);
		_sql2Field.put(java.sql.Types.SMALLINT, 	Field.Type.INTEGER);
		_sql2Field.put(java.sql.Types.TINYINT, 		Field.Type.INTEGER);
		_sql2Field.put(java.sql.Types.DECIMAL, 		Field.Type.INTEGER);
		
		_sql2Field.put(java.sql.Types.REAL, 		Field.Type.NUMERIC);
		_sql2Field.put(java.sql.Types.DOUBLE, 		Field.Type.NUMERIC);
		_sql2Field.put(java.sql.Types.FLOAT, 		Field.Type.NUMERIC);
		
		_sql2Field.put(java.sql.Types.BOOLEAN, 		Field.Type.BOOLEAN);

		_sql2Field.put(java.sql.Types.CHAR, 		Field.Type.STRING);
		_sql2Field.put(java.sql.Types.VARCHAR, 		Field.Type.STRING);
		_sql2Field.put(java.sql.Types.LONGVARCHAR, 	Field.Type.STRING);
		_sql2Field.put(java.sql.Types.NCHAR, 		Field.Type.STRING);
		_sql2Field.put(java.sql.Types.VARCHAR, 		Field.Type.STRING);
		_sql2Field.put(java.sql.Types.LONGNVARCHAR, Field.Type.STRING);
		
		_sql2Field.put(java.sql.Types.DATE, 		Field.Type.TIME);
		_sql2Field.put(java.sql.Types.TIME, 		Field.Type.TIME);
		_sql2Field.put(java.sql.Types.TIMESTAMP, 	Field.Type.TIME);
		
		_sql2Field.put(java.sql.Types.BIT,	 		Field.Type.NA);
		_sql2Field.put(java.sql.Types.BLOB, 		Field.Type.NA);
		_sql2Field.put(java.sql.Types.DISTINCT, 	Field.Type.NA);
		_sql2Field.put(java.sql.Types.DATALINK, 	Field.Type.NA);
		_sql2Field.put(java.sql.Types.JAVA_OBJECT, 	Field.Type.NA);
		_sql2Field.put(java.sql.Types.LONGVARBINARY,Field.Type.NA);
		_sql2Field.put(java.sql.Types.NCLOB, 		Field.Type.NA);
		_sql2Field.put(java.sql.Types.NULL, 		Field.Type.NA);
		_sql2Field.put(java.sql.Types.OTHER, 		Field.Type.NA);
		_sql2Field.put(java.sql.Types.REF, 			Field.Type.NA);
		_sql2Field.put(java.sql.Types.SQLXML, 		Field.Type.NA);
		_sql2Field.put(java.sql.Types.STRUCT,		Field.Type.NA);
		_sql2Field.put(java.sql.Types.VARBINARY, 	Field.Type.NA);
		
		
		_name2Field.put("INTEGER", 			Field.Type.INTEGER);
		_name2Field.put("BIGINT", 			Field.Type.INTEGER);
		_name2Field.put("SMALLINT",			Field.Type.INTEGER);
		_name2Field.put("TINYINT", 			Field.Type.INTEGER);
		_name2Field.put("DECIMAL", 			Field.Type.INTEGER);
		
		_name2Field.put("REAL",				Field.Type.NUMERIC);
		_name2Field.put("DOUBLE", 			Field.Type.NUMERIC);
		_name2Field.put("FLOAT", 			Field.Type.NUMERIC);
		_name2Field.put("NUMERIC",			Field.Type.NUMERIC);    // sqlite return field type name as 'NUMERIC'
		
		_name2Field.put("BOOLEAN", 			Field.Type.BOOLEAN);
		_name2Field.put("BOOL", 			Field.Type.BOOLEAN);   	// sqlite return field type name as 'BOOL'. 

		_name2Field.put("TEXT",				Field.Type.STRING);     // sqlite return field type name as 'text'. it's not part of java.sql.Types
		_name2Field.put("CHAR",				Field.Type.STRING);
		_name2Field.put("VARCHAR", 			Field.Type.STRING);
		_name2Field.put("LONGVARCHAR", 		Field.Type.STRING);
		_name2Field.put("NCHAR", 			Field.Type.STRING);
		_name2Field.put("VARCHAR", 			Field.Type.STRING);
		_name2Field.put("LONGNVARCHAR", 	Field.Type.STRING);
		
		_name2Field.put("DATE", 			Field.Type.TIME);
		_name2Field.put("TIME", 			Field.Type.TIME);
		_name2Field.put("TIMESTAMP", 		Field.Type.TIME);
		_name2Field.put("DATETIME", 		Field.Type.TIME);      // sqlite return field type name as 'DATETIME'. 
		
		_name2Field.put("BIT",	 			Field.Type.NA);
		_name2Field.put("BLOB", 			Field.Type.NA);
		_name2Field.put("DISTINCT", 		Field.Type.NA);
		_name2Field.put("DATALINK", 		Field.Type.NA);
		_name2Field.put("JAVA_OBJECT", 		Field.Type.NA);
		_name2Field.put("LONGVARBINARY",	Field.Type.NA);
		_name2Field.put("NCLOB", 			Field.Type.NA);
		_name2Field.put("NULL", 			Field.Type.NA);
		_name2Field.put("OTHER", 			Field.Type.NA);
		_name2Field.put("REF", 				Field.Type.NA);
		_name2Field.put("SQLXML", 			Field.Type.NA);
		_name2Field.put("STRUCT",			Field.Type.NA);
		_name2Field.put("VARBINARY", 		Field.Type.NA);
	}
}
