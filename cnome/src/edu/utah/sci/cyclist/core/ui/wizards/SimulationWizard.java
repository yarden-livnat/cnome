package edu.utah.sci.cyclist.core.ui.wizards;
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
import java.util.List;

import javafx.beans.property.ObjectProperty;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import edu.utah.sci.cyclist.core.model.CyclistDatasource;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

/*
 * Class to allow the user to create or edit a data table.
 * Also controls the creation/editing of data sources.
 */
public class SimulationWizard extends TilePane {

	// GUI elements
	private Stage                       _dialog;
	private ListView<CyclistDatasource> _sourcesView;
	private List<String> 				_simulationsIds;
//	private ImageView                   _statusDisplay;
	private Label						_status;
	private ListView<String>            _simulationsView;
	private TextField                   _aliasField;
	
	private ObservableList<CyclistDatasource> _sources = FXCollections.observableArrayList();
	
	// DataType elements
	private CyclistDatasource     _current;
	private ObservableList<Simulation> _selection =  FXCollections.observableArrayList();
	private static final String SIMULATION_ID_QUERY = "SELECT DISTINCT SimID FROM SimulationTimeInfo order by SimID";
	
	// * * * Constructor creates a new stage * * * //
	public SimulationWizard() {
		createDialog();
	}
	
	// * * * Set the existing data sources in the combo box * * * //
	public void setItems(final ObservableList<CyclistDatasource> sources) {
		_sources = sources;
		_sourcesView.setItems(_sources);
		_sourcesView.getSelectionModel().selectFirst();
	}
			
	// * * * Show the dialog * * * //
	public  ObservableList<Simulation>  show(Window window) {

		 _dialog.initOwner(window);
		 _dialog.show();	
		 
		// TODO: hopefully in JAVA 8 moving this to be BEFORE the show() will make it not flash, but at the moment it doesn't work
		// Moves window to be in the middle of the main window 
		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
		 	
		return _selection;
	}
	
	// * * * Create the dialog
	private void createDialog(){
		
		_dialog = new Stage();
		_dialog.setTitle("Simulations");
		
		
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog) );	
	
		System.out.println("changed" + _sourcesView.getSelectionModel().selectedItemProperty()) ;
	}
	
	// * * * Create scene creates the GUI * * * //
	private Scene createScene(final Stage dialog) {
			
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
		
//		_statusDisplay = new ImageView();
		_status = new Label();
		
		connectionBox.getChildren().addAll(dataSrcHbox,conHBox,_status /*_statusDisplay*/);
		
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
//		_statusDisplay = new ImageView();
		conHBox.getChildren().addAll(selectionButton, _status/*_statusDisplay*/);
		
		_sourcesView.setId("datasources-list");
		_sourcesView.setMaxSize(100, 100);

		// Keep track of the currently selected data source
		_sourcesView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<CyclistDatasource>() {
					public void changed(ObservableValue<? extends CyclistDatasource> ov, 
							CyclistDatasource old_val, CyclistDatasource new_val) {
						
						_current = _sourcesView.getSelectionModel().getSelectedItem();
//						_statusDisplay.setImage(null);
						_status.setGraphic(null);	
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
		
		VBox simulationBox = new VBox();
		simulationBox.setSpacing(5);
		simulationBox.setPadding(new Insets(5));
		simulationBox.setMaxHeight(Double.MAX_VALUE);
		
		Label simLbl =  new Label("Select Simulation:");
		simLbl.setAlignment(Pos.CENTER);
		simLbl.setFont(new Font(12));
		simLbl.setPadding(new Insets(3,0,0,0));
		_simulationsView = new ListView<>();
		_simulationsView.setPrefWidth(250);
		_simulationsView.setDisable(true);
		_simulationsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		_simulationsView.setMaxHeight(Double.MAX_VALUE);
		
		final HBox aliasHbox = new HBox();
		aliasHbox.setSpacing(10);
		aliasHbox.setAlignment(Pos.CENTER_LEFT);
		aliasHbox.setPadding(new Insets(5));
		
		Label aliasLbl =  new Label("Alias:");
		aliasLbl.setAlignment(Pos.CENTER);
		aliasLbl.setFont(new Font(12));
		aliasLbl.setPadding(new Insets(3,0,0,0));
		
		_aliasField = new TextField();
		_aliasField.setPrefWidth(210);
		_aliasField.setMinHeight(20);
		
		aliasHbox.getChildren().addAll(aliasLbl, _aliasField);
		aliasHbox.setDisable(true);
			
		simulationBox.getChildren().addAll(simLbl,_simulationsView, aliasHbox);
		simulationBox.disableProperty().bind(_sourcesView.getSelectionModel().selectedItemProperty().isNull());
		VBox.setVgrow(_simulationsView,  Priority.ALWAYS);
		VBox.setVgrow(aliasHbox,  Priority.ALWAYS);
		
		_simulationsView.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent e) {
				if(_simulationsView.getSelectionModel().getSelectedItems().size() >1 || _simulationsView.getSelectionModel().getSelectedItems().size()==0 ){
					aliasHbox.setDisable(true);
				} else{
					aliasHbox.setDisable(false);
				}
			}
		});
		
		
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
				updateSimulation();
				dialog.hide();
			};
		});
		
		buttonsBox.getChildren().addAll(btnCancel,btnOk);	
		HBox.setHgrow(buttonsBox,  Priority.ALWAYS);
		
		btnOk.disableProperty().bind(_simulationsView.getSelectionModel().selectedItemProperty().isNull());
	
		
		// Create the scene
		VBox wizardVbox = new VBox();
		wizardVbox.setSpacing(5);
		wizardVbox.setPadding(new Insets(5));
		wizardVbox.setPrefHeight(300);
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
		_simulationsView.getItems().clear();
		if(_simulationsView.isDisabled()){
			_simulationsView.setDisable(false);
		}
		
		try (Connection conn = ds.getConnection()) {
			_status.setGraphic(GlyphRegistry.get(AwesomeIcon.CHECK));//"FontAwesome|OK"));
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SIMULATION_ID_QUERY);
			while (rs.next()) {
				 String simulationId = rs.getString("SimID");
				 _simulationsView.getItems().add(simulationId);
			}
			
			/* ****************JUST FOR TESTING - WE BE DELETED AFTER TESTING!!! ************* */
			_simulationsView.getItems().add("first demo simulation");
			_simulationsView.getItems().add("second demo simulation");
			_simulationsView.getItems().add("third demo simulation");
			/* ****************JUST FOR TESTING - WE BE DELETED AFTER TESTING!!! ************* */
			
		}catch(SQLSyntaxErrorException e){
			System.out.println("Table for SimID doesn't exist");
		}
		catch (Exception e) {
			_status.setGraphic(GlyphRegistry.get(AwesomeIcon.WARNING));//"FontAwesome|WARNING"));
		}
	}
	
	private void updateSimulation() {
		_simulationsIds = (List<String>)_simulationsView.getSelectionModel().getSelectedItems();
		_selection.clear();
		for(String simId:_simulationsIds){
			Simulation simulation = new Simulation(simId);
			simulation.setDataSource(_current);
			String alias =_aliasField.getText();
			if(alias == null){
				alias = "null";
			}else if(alias.isEmpty()){
				alias = "emtpy";
			}
			simulation.setAlias(_aliasField.getText().isEmpty()?simId:_aliasField.getText());
			_selection.add(simulation);
		}
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
