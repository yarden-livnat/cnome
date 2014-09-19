package edu.utah.sci.cyclist.core.util;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.function.Function;

import edu.utah.sci.cyclist.core.model.Blob;
import edu.utah.sci.cyclist.core.model.Nuclide;


public class SQLUtil {

	  // TODO: replace with an abstract factory and register these functions.
	  //       will need both the function and a condition for selecting it (based on the rmd and col num)
	  static private Function<Object, Object> noop = o->{return o;};
	  static private Function<Object, Object> nuclide =  o -> { return new Nuclide(o); };
	  static private Function<Object, Object> blob = o -> { return new Blob((byte[]) o); };
	  
	  
	  private SQLUtil() {
	  }

	  static public Function<Object, Object>[] factories(ResultSetMetaData rmd) throws SQLException {
		int cols = rmd.getColumnCount();
		
		@SuppressWarnings("unchecked")
	    Function<Object, Object> func[] = new Function[cols];
		try {	 
		  for (int c = 0; c < cols; c++) {
			String name = rmd.getColumnName(c + 1).toLowerCase();
			if (name.equals("nucid"))
			  func[c] = nuclide;
			else if (rmd.getColumnType(c + 1) == Types.BLOB)
			  func[c] = blob;
			else
			  func[c] = noop;
		  }
		} catch (SQLException e) {
		  System.out.println("Error parsing sql meta data: "+e.getMessage());
		  for (int c=0; c<cols; c++) {
			func[c] = noop;
		  }
		}

		return func;
	  }
}
