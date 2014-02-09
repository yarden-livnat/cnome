package edu.utah.sci.cyclist.core.model.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.core.model.CyclistDatasource;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.model.TableRow;

public class TableProxy {
	private static final String GET_ROWS_QUERY = "select * from %s limit ?";
	
	private Table _table;
	
	public TableProxy(Table table) {
		_table = table;
	}
	
	public ObservableList<TableRow> getRows(final int n) {
		CyclistDatasource ds = _table.getDataSource();
		return getRows(ds, n);
	}
	
	public ObservableList<TableRow> getRows(CyclistDatasource ds, final int n) {
		return getRows(ds, n, false);
	}
	
	public ObservableList<TableRow> getRows(CyclistDatasource ds1, final int n, boolean force) {
		final CyclistDatasource ds = (!force && _table.getDataSource() != null) ?  _table.getDataSource(): ds1;
	
		List<TableRow> rows = new ArrayList<>();
		try {
			Connection conn = ds.getConnection();
			String query = String.format(GET_ROWS_QUERY, _table.getName());
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, n);
			
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData rmd = rs.getMetaData();
			
			int cols = rmd.getColumnCount();
			while (rs.next()) {
				TableRow row = new TableRow(cols);
				for (int i=0; i<cols; i++) {
					row.value[i] = rs.getObject(i+1);
				}
				rows.add(row);
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return FXCollections.observableList(rows);
	}
		
}
