package edu.utah.sci.cyclist.model.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.model.Simulation;
import edu.utah.sci.cyclist.neup.model.Facility;
import edu.utah.sci.cyclist.neup.model.Transaction;
import edu.utah.sci.cyclist.ui.views.flow.Node;

public class SimulationProxy {
	private Simulation _sim;
	private ObservableList<Facility> _facilities;
	
	public static final String SELECT_FACILITIES =
			"select ID, ModelType, Prototype from Facilities where SimID=?";
	
	public static final String SELECT_TRANSACTIONS =
			"SELECT Transactions.ReceiverID as nodeId, "
			+ "    Resources.Quantity as quantity, Resources.Units as units, "
			+ "    Compositions.IsoID as iso, Compositions.Quantity as fraction"
			+ " FROM Transactions, TransactedResources, Resources, Compositions, Facilities "
			+ " WHERE" 
			+ "    Transactions.SimID = ?"
			+ " and Transactions.SimID =TransactedResources.SimID"
			+ " and Transactions.SimID = Resources.SimID"
			+ " and Transactions.SimID = Compositions.SimID"
			+ " and Transactions.SimID = Facilities.SimID"
			+ " and Transactions.Time = ? "
			+ " and Transactions.%s = Facilities.ID "
			+"  and Facilities.%s = ?"
			+ " and Transactions.ID = TransactedResources.TransactionID "
			+ " and TransactedResources.ResourceID = Resources.ID "
			+ " and Resources.ID = Compositions.ID";
	
	
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
					Facility f = new Facility(rs.getInt("ID"), rs.getString("ModelType"), rs.getString("Prototype"));
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
	
	public ObservableList<Transaction> getTransactions(String type, String value, int timestep, boolean forward, int isotope) {
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
					int target = rs.getInt("nodeId");
					double quantity = rs.getDouble("quantity");
					String units = rs.getString("units");
					int iso = rs.getInt("iso");
					double fraction = rs.getDouble("fraction");
					Transaction tr = new Transaction(timestep, target, iso, fraction, quantity, units);
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
