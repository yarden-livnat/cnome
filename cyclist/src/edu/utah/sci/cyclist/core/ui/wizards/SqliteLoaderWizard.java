/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved.
 *
 * License for the specific language governing rights and limitations under Permission
 * is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: The above copyright notice 
 * and this permission notice shall be included in all copies  or substantial portions of the Software. 
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR 
 *  A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *     Yarden Livnat  
 *     Kristin Potter
 *******************************************************************************/
package edu.utah.sci.cyclist.core.ui.wizards;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.core.model.Blob;
import edu.utah.sci.cyclist.core.model.CyclistDatasource;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.ui.components.SQLitePage;

public class SqliteLoaderWizard extends VBox {
	
	private Stage _dialog;
	private String _fileName = "";
	private VBox _errorMessageBox = null;
	private VBox _vbox = null;
	private ObjectProperty<Simulation> _selection = new SimpleObjectProperty<Simulation>();
	
	
	private static final String SIMULATION_ID_FIELD_NAME = "SimID";
	private static final String SIMULATION_ID_QUERY = "SELECT DISTINCT " + SIMULATION_ID_FIELD_NAME  +" FROM Info order by SimID";
	private static String SIMULATION_INFO_QUERY = "select initialYear, initialMonth, Duration from Info where SimID=?";
	
	public ObjectProperty<Simulation> show(Window window) {
		_dialog.initOwner(window);
		_dialog.show();
		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
		return _selection;
	}
	
	public SqliteLoaderWizard() {	
		createDialog();
	}
	
	private void createDialog(){
		_dialog = new Stage();
		_dialog.setTitle("Load Sqlite");
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog) );	
		_dialog.centerOnScreen();
	}
	
	private Scene createScene(final Stage dialog) {
		HBox pane = new HBox();  //set spacing;
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(5));
		pane.setSpacing(10);
		pane.setMinWidth(250);
		
		TextField path = new TextField();
		path.setPrefWidth(250);
		
		Button button = new Button("...");
		button.setFont(new Font(15));
		button.getStyleClass().add("flat-button");
		button.setOnAction(new EventHandler<ActionEvent>() {
				 @Override
				 public void handle(ActionEvent event) {
					 FileChooser chooser = new FileChooser();
					 chooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("SQLite files (*.sqlite)", "*.sqlite") );
					 File file = chooser.showOpenDialog(null);
					 if (file != null){
						 path.setText(file.getPath());
						 _fileName = file.getName();
					 }
				 }
		});
		
		
		pane.getChildren().addAll(new Text("Path:"),path,button);
	
		HBox.setHgrow(path, Priority.ALWAYS);
		
		HBox buttons = new HBox();
		buttons.setSpacing(10);
		buttons.setPadding(new Insets(5));
		buttons.setAlignment(Pos.CENTER_RIGHT);
		
		Button cancel = new Button("Cancel");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialog.close();
			}
		});
		
		Button ok = new Button("Ok");
		ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Simulation simulation = null;
				CyclistDatasource ds = getDataSource(path.getText());
				if(ds != null){
					simulation = getSimulation(ds);
				}
				if(simulation != null){
					_selection.set(simulation);
					dialog.close();
				}
			}
		});
		
		buttons.getChildren().addAll(cancel,ok);
			
		HBox.setHgrow(buttons,  Priority.ALWAYS);
		
		_errorMessageBox = new VBox();
		_errorMessageBox.setSpacing(15);
		_errorMessageBox.setPadding(new Insets(10));
		_errorMessageBox.setAlignment(Pos.CENTER);
		
		Button dismiss = new Button("Dismiss");
		dismiss.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				_selection.set(null);
				dialog.close();
			}
		});
		
		HBox btn = new HBox();
		btn.setSpacing(10);
		btn.setPadding(new Insets(5));
		btn.setAlignment(Pos.CENTER_RIGHT);
		
		btn.getChildren().add(dismiss);
		
		Text errorText = new Text("database contains more than one simulation - load failed");
		
		_errorMessageBox.getChildren().addAll(errorText, btn);
		
		_vbox = new VBox();
		_vbox.setSpacing(5);
		_vbox.setPadding(new Insets(5));
		_vbox.getChildren().addAll(pane,buttons);
		
		Scene scene = new Scene(_vbox);
		
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
		return scene;
	}
	
	/*
	 * Get the url from the jdbc and sqlite file path,
	 * for the creation of data source.
	 * @return String = the generated url.
	 */
	private  String getURL(String path) {
		if(!path.isEmpty()){
			return "jdbc:sqlite:/"+path;
		}else{
			return "";
		}
	}
	
	/*
	 * Gets the path of a Sqlite file and creates its Cyclist data source.
	 * @param path - the path of the Sqlite file
	 * @return CyclistDatasource = the datasource created from the given path.
	 */
	private  CyclistDatasource getDataSource(String path) {
		Logger log = Logger.getLogger(SQLitePage.class);
		CyclistDatasource ds = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			log.warn("Can not load sqlite driver", e);
		}

		String url = getURL(path);
		if(!url.isEmpty()){
			ds = new CyclistDatasource();
			ds.setURL(getURL(path));
			Properties p = ds.getProperties();
			p.setProperty("driver", "sqlite");
			p.setProperty("type", "SQLite");
			p.setProperty("path", path);
			String name = path;
			p.setProperty("name", name.substring(name.lastIndexOf("/")+1));
		}
		return ds;	
	}
	
	/*
	 * Retrieves a simulation from the specified data source.
	 * If there is more than one simulation - displays an error message and returns null.
	 * @param CyclistDatasource ds - the data source to look for the simulation.
	 * @return Simulation = the simulation found in the specified data source.
	 */
	private Simulation getSimulation(CyclistDatasource ds){
		try (Connection conn = ds.getConnection()) {
			int numOfSims = 0;
			Blob simulationId = null;
			Simulation simulation = null;
			Statement stmt = conn.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(SIMULATION_ID_QUERY);
			while (rs.next()) {
				 simulationId = new Blob(rs.getBytes(SIMULATION_ID_FIELD_NAME));
				 numOfSims++;
			}
			
			if(numOfSims==1 && simulationId != null ){
				simulation = new Simulation(simulationId);
				simulation.setDataSource(ds);
				String alias = _fileName.substring(0,_fileName.indexOf(".sqlite"));
				simulation.setAlias(alias);
				fetchSimulationInfo(simulation, conn);
			}else{
				_vbox.getChildren().clear();
				_vbox.getChildren().add(_errorMessageBox);
			}
			return simulation;
			
		}catch(SQLSyntaxErrorException e){
			System.out.println("Table for SimId doesn't exist");
			return null;
		}
		catch (Exception e) {
			System.out.println("Get simulation failed");
			return null;
		}finally{
			ds.releaseConnection();
		}
	}
	
	/*
	 * Gets additional simulation details.
	 * @param Simulation sim - the simulation to it's details.
	 * @param Connection conn - the connection to the data source of the simulation.
	 */
	private void fetchSimulationInfo(Simulation sim, Connection conn) {
			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement(SIMULATION_INFO_QUERY);
				stmt.setBytes(1, sim.getSimulationId().getData());
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					sim.setStartYear(rs.getInt(1));
					sim.setStartMonth(rs.getInt(2));
					sim.setDuration(rs.getInt(3));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
