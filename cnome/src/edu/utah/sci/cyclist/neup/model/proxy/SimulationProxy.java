package edu.utah.sci.cyclist.neup.model.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.neup.model.Facility;
import edu.utah.sci.cyclist.neup.model.Transaction;

public class SimulationProxy {
	private Simulation _sim;
	private ObservableList<Facility> _facilities;
	
	public static final String SELECT_FACILITIES =
			"SELECT ID, ModelType, Prototype, InstitutionID, RegionID FROM Facilities where SimID=?";
	
	public static final String SELECT_TRANSACTIONS =
			 "SELECT * FROM MaterialFlow, Facilities "
			+ " WHERE" 
			+ "     MaterialFlow.SimID = ?"
			+ " and Time = ? "
			+ " and MaterialFlow.SimID = Facilities.SimID"
			+ " and %s = Facilities.ID "
			+"  and Facilities.%s = ?";
	
	public static final String SELECT_TRANSACTIONS_1 =
			 "SELECT * FROM MaterialFlow LEFT JOIN Facilities "
			+ " on (MaterialFlow.SimID = Facilities.SimID)"
			+ " WHERE" 
			+ "     MaterialFlow.SimID = ?"
			+ " and Time = ? "
			+ " and %s = Facilities.ID "
			+"  and Facilities.%s = ?";

	
	
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
			try (PreparedStatement stmt = conn.prepareStatement(SELECT_FACILITIES)) {
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
			String query = String.format(SELECT_TRANSACTIONS, forward? "SenderID" : "ReceiverID", type);
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
}
