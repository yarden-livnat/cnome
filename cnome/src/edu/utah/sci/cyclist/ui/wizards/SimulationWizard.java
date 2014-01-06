package edu.utah.sci.cyclist.ui.wizards;
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
 *     Kristi Potter
 *******************************************************************************/

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Simulation;

/*
 * Class to allow the user to create or edit a data table.
 * Also controls the creation/editing of data sources.
 */
public class SimulationWizard extends TilePane {

	// GUI elements
	private Stage                       _dialog;
	private ListView<CyclistDatasource> _sourcesView;
	private String						_simulationId;
	private ImageView                   _statusDisplay;
	private ComboBox<String> 			_cmbSimulation;
	
	private ObservableList<CyclistDatasource> _sources = FXCollections.observableArrayList();
	
	// DataType elements
	private CyclistDatasource     _current;
	private ObjectProperty<Simulation> _selection =  new SimpleObjectProperty<>();
	private static final String SIMULATION_ID_QUERY = "SELECT DISTINCT SimID FROM SimulationTimeInfo order by SimID";
	
	// * * * Constructor creates a new stage * * * //
	public SimulationWizard() {
		createDialog(new Simulation());
	}
	
	// * * * Set the existing data sources in the combo box * * * //
	public void setItems(final ObservableList<CyclistDatasource> sources) {
		_sources = sources;
		_sourcesView.setItems(_sources);
		_sourcesView.getSelectionModel().selectFirst();
	}
			
	// * * * Show the dialog * * * //
	public  ObjectProperty<Simulation>  show(Window window) {

		 _dialog.initOwner(window);
		 _dialog.show();	
		 
		// TODO: hopefully in JAVA 8 moving this to be BEFORE the show() will make it not flash, but at the moment it doesn't work
		// Moves window to be in the middle of the main window 
		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
		 	
		return _selection;
	}
	
	// * * * Create the dialog
	private void createDialog(Simulation simulationProperty){
		
		_dialog = new Stage();
		_dialog.setTitle("Create Simulation");
		
		
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, simulationProperty) );	
	
		System.out.println("changed" + _sourcesView.getSelectionModel().selectedItemProperty()) ;
	}
	
	// * * * Create scene creates the GUI * * * //
	private Scene createScene(final Stage dialog, final Simulation simulation) {
			
		// * * * The connection settings group
		VBox connectionBox = new VBox();
		connectionBox.setSpacing(5);
		connectionBox.setAlignment(Pos.CENTER_LEFT);
		
		// Add, edit, or remove connection box
		HBox dataSrcHbox = new HBox();
		dataSrcHbox.setSpacing(5);
		dataSrcHbox.setAlignment(Pos.CENTER_LEFT);
		
		HBox conHBox = new HBox();
		conHBox.setSpacing(10);
		conHBox.setPadding(new Insets(5));
		conHBox.setAlignment(Pos.CENTER_LEFT);
		
		_statusDisplay = new ImageView();
		
		connectionBox.getChildren().addAll(dataSrcHbox,conHBox,_statusDisplay);
		
		// Sources box
		VBox srcVbox = new VBox();
		srcVbox.setSpacing(5);
		srcVbox.setAlignment(Pos.CENTER_LEFT);
		
		// Add/Edit/Remove Buttons
		VBox buttonsVbox = new VBox();
		buttonsVbox.setSpacing(5);
		buttonsVbox.setAlignment(Pos.CENTER);
		
		dataSrcHbox.getChildren().addAll(srcVbox,buttonsVbox);
		
		Text txt = new Text("Data Sources");
		_sourcesView = new ListView<CyclistDatasource>();
		
		srcVbox.getChildren().addAll(txt,_sourcesView);
		
		Button addButton = new Button("Add");
		addButton.setMinWidth(75);
		
		Button editButton = new Button("Edit");
		editButton.setMinWidth(75);
		
		Button removeButton = new Button("Remove");
		removeButton.setMinWidth(75);
		
		buttonsVbox.getChildren().addAll(addButton,editButton,removeButton);
		
		Button selectionButton = new Button("Connect");
		selectionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				selectConnection(_current);
			};
		});
		_statusDisplay = new ImageView();
		conHBox.getChildren().addAll(selectionButton,_statusDisplay);
		
		_sourcesView.setId("datasources-list");
		_sourcesView.setMaxSize(100, 100);

		// Keep track of the currently selected data source
		_sourcesView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<CyclistDatasource>() {
					public void changed(ObservableValue<? extends CyclistDatasource> ov, 
							CyclistDatasource old_val, CyclistDatasource new_val) {
						
						_current = _sourcesView.getSelectionModel().getSelectedItem();
						_statusDisplay.setImage(null);
						
					}
				});
		
		
	
		// Disable edit/remove until we have something selected
		editButton.disableProperty().bind( _sourcesView.getSelectionModel().selectedItemProperty().isNull());
		removeButton.disableProperty().bind( _sourcesView.getSelectionModel().selectedItemProperty().isNull());
		selectionButton.disableProperty().bind( _sourcesView.getSelectionModel().selectedItemProperty().isNull());
		
		// add button actions
		addButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				selectDatasource(new CyclistDatasource());		
			}	
		});	

		editButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				selectDatasource(_current);		
			}	
		});	
		
		removeButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {			
				_sources.remove(_current);		
			}	
		});	
		
		HBox simulationBox = new HBox();
		simulationBox.setSpacing(5);
		simulationBox.setPadding(new Insets(5));
		simulationBox.setMaxHeight(Double.MAX_VALUE);
		
		Label simLbl =  new Label("Select Simulation:");
		simLbl.setAlignment(Pos.CENTER);
		simLbl.setFont(new Font(12));
		simLbl.setPadding(new Insets(3,0,0,0));
		_cmbSimulation = new ComboBox<>();
		_cmbSimulation.setPrefWidth(200);
		_cmbSimulation.setDisable(true);
		simulationBox.getChildren().addAll(simLbl,_cmbSimulation);
		simulationBox.disableProperty().bind(_sourcesView.getSelectionModel().selectedItemProperty().isNull());
		HBox.setHgrow(_cmbSimulation,  Priority.ALWAYS);
		
		// The ok/cancel buttons
		HBox buttonsBox = new HBox();
		buttonsBox.setSpacing(10);
		buttonsBox.setAlignment(Pos.CENTER_RIGHT);
		buttonsBox.setPadding(new Insets(5));
		
		Button btnCancel = new Button("Cancel");
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				dialog.hide();
			};
		});
		Button btnOk = new Button("OK");
		btnOk.setOnAction(new EventHandler<ActionEvent>() {	
			@Override
			public void handle(ActionEvent arg0) {
				updateSimulation(simulation);
				_selection.setValue(simulation);
				dialog.hide();
			};
		});
		
		buttonsBox.getChildren().addAll(btnCancel,btnOk);	
		HBox.setHgrow(buttonsBox,  Priority.ALWAYS);
		
		btnOk.disableProperty().bind(_cmbSimulation.getSelectionModel().selectedItemProperty().isNull());
	
		
		// Create the scene
		VBox wizardVbox = new VBox();
		wizardVbox.setSpacing(5);
		wizardVbox.setPadding(new Insets(5));
		wizardVbox.setPrefHeight(250);
		wizardVbox.setId("datatable-wizard");
		Scene scene = new Scene(wizardVbox);
		wizardVbox.getChildren().addAll(connectionBox,simulationBox,buttonsBox);
		
		scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
	
		_sourcesView.getSelectionModel().selectFirst();
		
		// Return the scene
		return scene;
	}
	
	/**
	 * selectDataSource
	 */
	private void selectDatasource(CyclistDatasource datasource){
		DatasourceWizard wizard = new DatasourceWizard(datasource);
		ObjectProperty<CyclistDatasource> selection = wizard.show(_dialog.getScene().getWindow());
		selection.addListener(new ChangeListener<CyclistDatasource>(){
			@Override
			public void changed(ObservableValue<? extends CyclistDatasource> arg0, CyclistDatasource oldVal, CyclistDatasource newVal) {
				if (!_sourcesView.getItems().contains(newVal))
					_sourcesView.getItems().add(newVal);
				_sourcesView.getSelectionModel().select(newVal);
			}
		});
	}
	
	private void selectConnection(CyclistDatasource ds) {
		
		_cmbSimulation.getItems().clear();
		if(_cmbSimulation.isDisabled()){
			_cmbSimulation.setDisable(false);
		}
		
		try (Connection conn = ds.getConnection()) {
			_statusDisplay.setImage(Resources.getIcon("ok"));
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SIMULATION_ID_QUERY);
			while (rs.next()) {
				 String simulationId = rs.getString("SimID");
				 _cmbSimulation.getItems().add(simulationId);
			}
			_cmbSimulation.getSelectionModel().selectFirst();
			
		}catch(SQLSyntaxErrorException e){
			System.out.println("Table for SimID doesn't exist");
		}
		catch (Exception e) {
			_statusDisplay.setImage(Resources.getIcon("error"));
		}
	}
	
	private void updateSimulation(Simulation simulation) {
		_simulationId = (String)_cmbSimulation.getSelectionModel().getSelectedItem();
		simulation.setSimulationId(_simulationId);
		simulation.setDataSource(_current);
	}

	public CyclistDatasource getSelectedSource() {
		return _current;
	}

	public void setSelectedSource(CyclistDatasource source) {
		_current = source;	
		_sourcesView.getSelectionModel().select(_current);
	}
	
	public void setWorkDir(String workDir){
	}
	
}
