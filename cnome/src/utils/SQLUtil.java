package utils;

import java.util.HashMap;
import java.util.Map;

import edu.utah.sci.cyclist.model.Field;


public class SQLUtil {

	private static Map<Integer, Field.Type> _sql2Field = new HashMap<>();
	
	public static Field.Type fromSQL(int type) {
		return _sql2Field.get(type);
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
	}
}
