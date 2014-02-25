package edu.utah.sci.cyclist.neup.model.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.core.event.Pair;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.neup.model.Facility;
import edu.utah.sci.cyclist.neup.model.Inventory;
import edu.utah.sci.cyclist.neup.model.Transaction;

public class SimulationProxy {
	private Simulation _sim;
	private ObservableList<Facility> _facilities;
	
	public static final String FACILITIES_QUERY =
			"SELECT AgentID, Implementation, Prototype, InstitutionID, RegionID FROM Facilities where SimId=?";
	
	public static final String TRANSACTIONS_QUERY =
			 "SELECT  SenderId, ReceiverId, Commodity, NucId, Quantity*MassFrac as Amount, Units"
			+ " FROM Transactions "
			+ "      JOIN Facilities on (Transactions.SimId = Facilities.SimId and Transactions.%s = Facilities.AgentID) "
			+ "      JOIN Resources on (Transactions.SimId = Resources.SimId and Transactions.ResourceId = Resources.ResourceId)"
			+ "      JOIN Compositions on (Transactions.SimId = Compositions.SimId and Compositions.StateId = Resources.StateId)"
			+ " WHERE" 
			+ "     Transactions.SimId = ?"
			+ " and Time = ? "
			+"  and Facilities.%s = ?";
	
	public static final String INVENTORY_QUERY = 
			"SELECT tl.Time as time, cmp.NucId as nucid, SUM(inv.Quantity*cmp.MassFrac) as amount"
			+ "	FROM "
			+ "		TimeList AS tl"
			+ "			INNER JOIN Inventories AS inv ON inv.StartTime <= tl.Time AND inv.EndTime > tl.Time "
			+ "			INNER JOIN Agents AS ag ON ag.AgentId = inv.AgentId "
			+ "			INNER JOIN Compositions AS cmp ON cmp.StateId = inv.StateId "
			+ "	WHERE"
			+ "		inv.SimId = cmp.SimId AND inv.SimId = ag.SimId"
			+ "		AND inv.SimId = ?"
			+ "		AND ag.%s = ?"
			+ "     AND tl.Time > 0 "
			+ "	GROUP BY tl.Time,cmp.NucId";
	
	public SimulationProxy(Simulation sim) {
		_sim = sim;
	}
	
	public ObservableList<Facility> getFacilities() {
		if (_facilities == null) {
			_facilities = fetchFacilities();
		}
		return _facilities;
	}
	
	public ObservableList<Facility> fetchFacilities() {
		List<Facility> list = new ArrayList<>();
		
		try (Connection conn = _sim.getDataSource().getConnection()) {
			try (PreparedStatement stmt = conn.prepareStatement(FACILITIES_QUERY)) {
				stmt.setString(1, _sim.getSimulationId());
				
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Facility f = new Facility(rs.getInt("AgentID"), rs.getString("Implementation"), rs.getString("Prototype"),
							rs.getInt("InstitutionID"), rs.getInt("RegionID"));
					list.add(f);
				}
			}		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			_sim.getDataSource().releaseConnection();
		}
		
		return FXCollections.observableList(list);
	}
	
	public ObservableList<Transaction> getTransactions(String type, String value, int timestep, boolean forward) {
		List<Transaction> list = new ArrayList<>();
		
		try (Connection conn = _sim.getDataSource().getConnection()) {
			String query = String.format(TRANSACTIONS_QUERY, forward? "SenderId" : "ReceiverId", type);
//			System.out.println("query: ["+query+"]");
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, _sim.getSimulationId());
				stmt.setInt(2, timestep);
				stmt.setString(3, value);
				
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Transaction tr = new Transaction();
					tr.sender = rs.getInt("SenderId");
					tr.receiver = rs.getInt("ReceiverId");
					tr.commodity = rs.getString("Commodity");
					tr.nucid = rs.getInt("NucId");
					tr.amount = rs.getDouble("Amount");
					tr.units = rs.getString("Units");
					
					list.add(tr);
				}
			}		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			_sim.getDataSource().releaseConnection();
		}
		
		System.out.println("retrieived "+list.size()+" transactions");
		return  FXCollections.observableList(list);
	}
	
	public ObservableList<Inventory> getInventory(String type, String value) {
		List<Inventory> list = new ArrayList<>();
		
		String query = String.format(INVENTORY_QUERY, type);
//		System.out.println(query+"  ["+type+", "+value+"]");
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			_sim.getDataSource().releaseConnection();
		}
		
		return FXCollections.observableArrayList(list);
	}
	
}
