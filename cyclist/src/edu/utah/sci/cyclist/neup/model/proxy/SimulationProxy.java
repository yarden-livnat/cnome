package edu.utah.sci.cyclist.neup.model.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.neup.model.Facility;
import edu.utah.sci.cyclist.neup.model.Inventory;
import edu.utah.sci.cyclist.neup.model.Range;
import edu.utah.sci.cyclist.neup.model.Transaction;

public class SimulationProxy {
	private Simulation _sim;
	private ObservableList<Facility> _facilities;
	
	public static final String FACILITIES_QUERY =
			"SELECT AgentID, Spec, Prototype, InstitutionID, RegionID FROM Facilities where SimID=?";
	
	public static final String COMMODITY_QUERY = 
			"SELECT distinct(Commodity) FROM Transactions where SimID=?";
	
	public static final String TRANSACTIONS_QUERY =
			 "SELECT  SenderID, ReceiverID, Commodity, NucID, Quantity*MassFrac as Amount, Units"
			+ " FROM Transactions "
			+ "      JOIN Facilities on (Transactions.SimID = Facilities.SimID and Transactions.%s = Facilities.AgentID) "
			+ "      JOIN Resources on (Transactions.SimID = Resources.SimID and Transactions.ResourceID = Resources.ResourceID)"
			+ "      JOIN Compositions on (Transactions.SimID = Compositions.SimID and Compositions.QualID = Resources.QualID)"
			+ " WHERE" 
			+ "     Transactions.SimID = ?"
			+ " and Time >= ? and Time <= ? "
			+"  and Facilities.%s = ?";
	
	public static final String INVENTORY_QUERY = 
			"SELECT tl.Time as time, cmp.NucID as nucid, SUM(inv.Quantity*cmp.MassFrac) as amount"
			+ "	FROM "
			+ "		TimeList AS tl"
			+ "			INNER JOIN Inventories AS inv ON inv.StartTime <= tl.Time AND inv.EndTime > tl.Time "
			+ "			INNER JOIN Agents AS ag ON ag.AgentID = inv.AgentID "
			+ "			INNER JOIN Compositions AS cmp ON cmp.QualID = inv.QualID "
			+ "	WHERE"
			+ "		inv.SimID = cmp.SimID AND inv.SimID = ag.SimID"
			+ "		AND inv.SimID = ?"
			+ "		AND ag.%s = ?"
			+ "     AND tl.Time > 0 "
			+ "	GROUP BY tl.Time,cmp.NucID";
	
	public SimulationProxy(Simulation sim) {
		_sim = sim;
	}
	
	public ObservableList<Facility> getFacilities() throws SQLException {
		if (_facilities == null) {
			_facilities = fetchFacilities();
		}
		return _facilities;
	}
	
	public ObservableList<Facility> fetchFacilities() throws SQLException {
		List<Facility> list = new ArrayList<>();
		
		try (Connection conn = _sim.getDataSource().getConnection()) {
			try (PreparedStatement stmt = conn.prepareStatement(FACILITIES_QUERY)) {
				stmt.setString(1, _sim.getSimulationId());
				
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Facility f = new Facility(rs.getInt("AgentId"), rs.getString("Spec"), rs.getString("Prototype"),
							rs.getInt("InstitutionID"), rs.getInt("RegionID"));
					list.add(f);
				}
			}		
		} finally {
			_sim.getDataSource().releaseConnection();
		}
		
		return FXCollections.observableList(list);
	}
	
	public ObservableList<String> getCommodities() throws SQLException {
		List<String> list = new ArrayList<String>();
		try (Connection conn = _sim.getDataSource().getConnection()) {
			try (PreparedStatement stmt = conn.prepareStatement(COMMODITY_QUERY)) {
				stmt.setString(1,  _sim.getSimulationId());
			
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					list.add(rs.getString("Commodity"));
				}
			}
		} finally {
			_sim.getDataSource().releaseConnection();
		}
		return FXCollections.observableList(list);
	}
	
	public ObservableList<Transaction> getTransactions(String type, String value, Range<Integer> timerange, boolean forward) throws SQLException {
		List<Transaction> list = new ArrayList<>();
		try (Connection conn = _sim.getDataSource().getConnection()) {
			String query = String.format(TRANSACTIONS_QUERY, forward? "SenderID" : "ReceiverID", type);
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, _sim.getSimulationId());
				stmt.setInt(2, timerange.from);
				stmt.setInt(3, timerange.to);
				stmt.setString(4, value);
				
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Transaction tr = new Transaction();
					tr.sender = rs.getInt("SenderID");
					tr.receiver = rs.getInt("ReceiverID");
					tr.commodity = rs.getString("Commodity");
					tr.nucid = rs.getInt("NucId");
					tr.amount = rs.getDouble("Amount");
					tr.units = rs.getString("Units");
					
					list.add(tr);
				}
			}		
		} finally {
			_sim.getDataSource().releaseConnection();
		}
		
		System.out.println("retrieived "+list.size()+" transactions");
		return  FXCollections.observableList(list);
	}
	
	public ObservableList<Inventory> getInventory(String type, String value) throws SQLException {
		List<Inventory> list = new ArrayList<>();
		
		String query = String.format(INVENTORY_QUERY, type);
		System.out.println("query: "+query);
		try (Connection conn = _sim.getDataSource().getConnection()) {
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, _sim.getSimulationId());
				stmt.setString(2, value);				

				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Inventory i = new Inventory();
					i.time = rs.getInt(1);
					i.nucid = rs.getInt(2);
					i.amount = rs.getDouble(3);
					list.add(i);
				}
			}
		} finally {
			_sim.getDataSource().releaseConnection();
		}
		
		return FXCollections.observableArrayList(list);
	}
	
	public List<Inventory> getInventory2(String type, String value) throws SQLException {
		List<Inventory> list = new ArrayList<>();
		
		String query = String.format(INVENTORY_QUERY, type);
		System.out.println("query: "+query);
		try (Connection conn = _sim.getDataSource().getConnection()) {
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, _sim.getSimulationId());
				stmt.setString(2, value);				

				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Inventory i = new Inventory();
					i.time = rs.getInt(1);
					i.nucid = rs.getInt(2);
					i.amount = rs.getDouble(3);
					list.add(i);
				}
			}
		} finally {
			_sim.getDataSource().releaseConnection();
		}
		
		return list;
	}
}
