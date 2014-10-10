package edu.utah.sci.cyclist.core.util;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.apache.log4j.Logger;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import edu.utah.sci.cyclist.core.model.Blob;
import edu.utah.sci.cyclist.core.model.CyclistDatasource;
import edu.utah.sci.cyclist.core.model.Simulation;

public class LoadSqlite {

	LoadSqlite() {	
	}
	
	public static ListProperty<Simulation> load(String path, Window window) {
		ListProperty<Simulation> list = new SimpleListProperty<Simulation>();
		CyclistDatasource ds = createDataSource(path);
		if(ds != null){
			Boolean updateReuired = SimulationTablesPostProcessor.isDbUpdateRequired(ds);
			if(updateReuired){
				updateDB(ds, window, list);
			} else {	
				loadSimulations(ds, list);
			}
		}
		return list;
	}
	
	private static CyclistDatasource createDataSource(String path) {
		Logger log = Logger.getLogger(LoadSqlite.class);
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			log.warn("Can not load sqlite driver", e);
			return null;
		}

		CyclistDatasource ds = new CyclistDatasource();
		ds.setURL("jdbc:sqlite:/"+path);
		
		Properties p = ds.getProperties();
		p.setProperty("driver", "sqlite");
		p.setProperty("type", "SQLite");
		p.setProperty("path", path);
		p.setProperty("name", Paths.get(path).getFileName().toString());
		
		return ds;	
	}
	
    private static void updateDB(CyclistDatasource ds, Window window, ListProperty<Simulation> list) {
		Action response = Dialogs.create()
			.owner(window)
			.title("SQLite loader")
			.masthead(null)
			.message("Database needs postprocessing, which may take long time.\nContinue ?")
			.actions(Dialog.Actions.OK, Dialog.Actions.NO)
			.showConfirm();
		
		if (response == Dialog.Actions.OK) {			
			Service<Boolean> service = new Service<Boolean>() {
				@Override
				protected Task<Boolean> createTask() {
					SimulationTablesPostProcessor postProcessor = new SimulationTablesPostProcessor();
					return postProcessor.process(ds);
				}
			};
			
			Dialogs.create()
				.owner(window)
				.title("SQLite loader")
				.masthead("Updating database")
				.showWorkerProgress(service);
			
			service.start();
			service.setOnSucceeded( new EventHandler<WorkerStateEvent>() {
				
				@Override
				public void handle(WorkerStateEvent event) {
					loadSimulations(ds, list);
					
				}
			});
			
		} else {
		}		
	}
    
	private static final String SIMULATION_ID_QUERY = "SELECT DISTINCT SimID, initialYear, initialMonth, Duration FROM Info order by SimID";

    private static void loadSimulations(CyclistDatasource ds, ListProperty<Simulation> list) {
    	List<Simulation> simulations = new ArrayList<>();
		try (Connection conn = ds.getConnection()) {
			Blob simulationId = null;
			Simulation simulation = null;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SIMULATION_ID_QUERY);
			
			while (rs.next()) {
				simulationId = new Blob(rs.getBytes(1));
				if(simulationId != null ){
					simulation = new Simulation(simulationId);
					simulation.setDataSource(ds);
					simulation.setAlias(simulationId.toString().substring(2, 6));
					simulation.setStartYear(rs.getInt(2));
					simulation.setStartMonth(rs.getInt(3));
					simulation.setDuration(rs.getInt(4));
					simulations.add(simulation);
				}
			}
			
			if(simulations.size()>1){
				for(int i=0;i<simulations.size();i++){
					Simulation sim = simulations.get(i);
					sim.setAlias(sim.getAlias()+"-"+(i+1));
				}
			}
    			
		}catch(SQLSyntaxErrorException e){
			System.out.println("Table for SimId doesn't exist");
		}
		catch (Exception e) {
			System.out.println("Get simulation failed");
		}finally{
			ds.releaseConnection();
		}
		
		list.set(FXCollections.observableArrayList(simulations));
	}
}
