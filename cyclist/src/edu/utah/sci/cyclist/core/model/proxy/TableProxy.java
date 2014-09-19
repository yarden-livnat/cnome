package edu.utah.sci.cyclist.core.model.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import edu.utah.sci.cyclist.core.model.CyclistDatasource;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Nuclide;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.model.TableRow;
import edu.utah.sci.cyclist.core.util.SQLUtil;

public class TableProxy {
	
	private Table _table;
	
	public TableProxy(Table table) {
		_table = table;
	}
	
	public ObservableList<TableRow> getRows(String query) throws SQLException {
		return getRows(query, -1);
	}
	
	public ObservableList<TableRow> getRows(String query, int n) throws SQLException {
		CyclistDatasource ds = _table.getDataSource();
		return getRows(ds, query, n);
	}
	
	public ObservableList<TableRow> getRows(CyclistDatasource ds, String query) throws SQLException {
		return getRows(ds, query, -1);
	}
	
	public ObservableList<TableRow> getRows(CyclistDatasource ds, String query, int n) throws SQLException {
		return getRows(ds, query, n, false);
	}
	
	// TODO: for now the 'n' parameter is ignored
	public ObservableList<TableRow> getRows(CyclistDatasource ds1, final String query, final int n, boolean force) throws SQLException {
		final CyclistDatasource ds = (!force && _table.getDataSource() != null) ?  _table.getDataSource(): ds1;
	
		List<TableRow> rows = new ArrayList<>();
		try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()) {

			ResultSet rs = stmt.executeQuery(query);

			ResultSetMetaData rmd = rs.getMetaData();
			int cols = rmd.getColumnCount();
			Function<Object, Object> convert[] = SQLUtil.factories(rmd);
			
			while (rs.next()) {
				TableRow row = new TableRow(cols);
				for (int i=0; i<cols; i++) {
					row.value[i] = convert[i].apply(rs.getObject(i+1));
				}
				rows.add(row);
			}
		} catch (SQLException e) {
			System.out.println("Error parsing sql meta data: "+e.getMessage());
		} finally {
			ds.releaseConnection();
		}
		
		return FXCollections.observableList(rows);
	}

	
}
