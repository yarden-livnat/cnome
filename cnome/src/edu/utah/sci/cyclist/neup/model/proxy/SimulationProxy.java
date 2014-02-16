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
import edu.utah.sci.cyclist.neup.model.Transaction;

public class SimulationProxy {
	private Simulation _sim;
	private ObservableList<Facility> _facilities;
	
	public static final String FACILITIES_QUERY =
			"SELECT ID, ModelType, Prototype, InstitutionID, RegionID FROM Facilities where SimID=?";
	
	public static final String TRANSACTIONS_QUERY =
			 "SELECT * "
			+ " FROM MaterialFlow, Facilities "
			+ " WHERE" 
			+ "     MaterialFlow.SimID = ?"
			+ " and Time = ? "
			+ " and MaterialFlow.SimID = Facilities.SimID"
			+ " and %s = Facilities.ID "
			+"  and Facilities.%s = ?";
	
	public static final String COMULATIVE_FLOW_QUERY =
			"SELECT time, sum(quantity*fraction) as amount"
			+ " FROM MaterialFlow, Facilities "
			+ " WHERE" 
			+ "     MaterialFlow.SimID = ?"
			+ " and MaterialFlow.SimID = Facilities.SimID"
			+ " and %s = Facilities.ID "
			+ " and Facilities.%s = ?"
			+ " GROUP BY time "
			+ " ORDER BY time";
	
	public static final String NET_FLOW_QUERY = 
		"SELECT Times.Time as time, ifnull(r.v,0)-ifnull(s.v,0) as vol "
		+ " FROM Times"
		+ "      left join "
		+ "			(SELECT Time, sum(Quantity*Fraction) as v "
		+ "   	   	   FROM MaterialFlow, Facilities "
		+ "            WHERE "
		+ "                MaterialFlow.SimID = ?"
		+ " 		   and MaterialFlow.SimID = Facilities.SimID"
		+ "            and SenderID = Facilities.ID "
		+ "            and Facilities.%s = ?"
		+ "		   	  GROUP BY Time) as s "
		+ "		on (Times.Time = s.Time) "
		+ "		left join "
		+ "		 	(SELECT Time, sum(Quantity*Fraction) as v "
		+ "   	   	   FROM MaterialFlow, Facilities "
		+ "            WHERE "
		+ "                MaterialFlow.SimID = ?"
		+ " 		   and MaterialFlow.SimID = Facilities.SimID"
		+ "            and ReceiverID = Facilities.ID "
		+ "            and Facilities.%s = ?"
		+ "		      GROUP BY Time) as r"
		+ "		on Times.Time = r.Time";


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
					Facility f = new Facility(rs.getInt("ID"), rs.getString("ModelType"), rs.getString("Prototype"),
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
			String query = String.format(TRANSACTIONS_QUERY, forward? "SenderID" : "ReceiverID", type);
			System.out.println("query: ["+query+"]");
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, _sim.getSimulationId());
				stmt.setInt(2, timestep);
				stmt.setString(3, value);
				
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Transaction tr = new Transaction();
					tr.sender = rs.getInt("SenderID");
					tr.receiver = rs.getInt("ReceiverID");
					tr.market = rs.getInt("MarketID");
					tr.commodity = rs.getString("Commodity");
					tr.price = rs.getDouble("Price");
					tr.iso = rs.getInt("IsoID");
					tr.quantity = rs.getDouble("Quantity");
					tr.fraction = rs.getDouble("Fraction");
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
	
	public ObservableList<Pair<Integer, Double>> getFlow(String type, String value, boolean forward) {
		List<Pair<Integer, Double>> list = new ArrayList<>();
		
		String query = String.format(COMULATIVE_FLOW_QUERY, forward? "SenderID" : "ReceiverID", type);
		System.out.println(query+"  ["+type+", "+value+"]");
		try (Connection conn = _sim.getDataSource().getConnection()) {
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, _sim.getSimulationId());
				stmt.setString(2, value);
				
				ResultSet rs = stmt.executeQuery();
				double sum = 0;
				while (rs.next()) {
					Pair<Integer, Double> p = new Pair<>();
					p.v1 = rs.getInt(1);
					sum += rs.getDouble(2);
					p.v2 = sum;
					list.add(p);
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
	
	public ObservableList<Pair<Integer, Double>> getNetFlow(String type, String value) {
		List<Pair<Integer, Double>> list = new ArrayList<>();
		
		String query = String.format(NET_FLOW_QUERY, type, type);
		System.out.println(query+"  ["+type+", "+value+"]");
		try (Connection conn = _sim.getDataSource().getConnection()) {
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, _sim.getSimulationId());
				stmt.setString(2, value);
				stmt.setString(3, _sim.getSimulationId());
				stmt.setString(4, value);				

				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Pair<Integer, Double> p = new Pair<>();
					p.v1 = rs.getInt(1);
					p.v2 = rs.getDouble(2);
					list.add(p);
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
