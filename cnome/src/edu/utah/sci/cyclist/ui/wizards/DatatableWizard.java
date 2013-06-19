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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

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
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ListView;
import javafx.scene.control.ListViewBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.TextBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.components.DatasourceSelector;

/*
 * Class to allow the user to create or edit a data table.
 * Also controls the creation/editing of data sources.
 */
@SuppressWarnings("deprecation")
public class DatatableWizard extends TilePane {

	// GUI elements
	private Stage                       _dialog;
	private ListView<CyclistDatasource> _sourcesView;
	private ListView<String>            _tablesView;
	private ImageView                   _statusDisplay;
	
	private ObservableList<CyclistDatasource> _sources = FXCollections.observableArrayList();
	
	// DataType elements
	private CyclistDatasource     _current;
	private ObjectProperty<Table> _selection = new SimpleObjectProperty<>();
	private DatasourceSelector    _selector; 
	
	// * * * Constructor creates a new stage * * * //
	public DatatableWizard() {
		createDialog(new Table());
	}

	public DatatableWizard(Table tableProperty){
		createDialog(tableProperty);
	}
	
	// * * * Set the existing data sources in the combo box * * * //
	public void setItems(final ObservableList<CyclistDatasource> sources) {
		_sources = sources;
		_sourcesView.setItems(_sources);
		_sourcesView.getSelectionModel().selectFirst();
	}
			
	// * * * Show the dialog * * * //
	public ObjectProperty<Table> show(Window window) {

		 _dialog.initOwner(window);
		 _dialog.show();	
		 
		// TODO: hopefully in JAVA 8 moving this to be BEFORE the show() will make it not flash, but at the moment it doesn't work
		// Moves window to be in the middle of the main window 
		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
		 	
		return _selection;
	}
	
	// * * * Create the dialog
	private void createDialog(Table tableProperty){
		
		_dialog = StageBuilder.create()
				.title("Create Data Table")
				.build();
		
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, tableProperty) );	
	
		System.out.println("changed" + _sourcesView.getSelectionModel().selectedItemProperty()) ;
	}
	
	// * * * Create scene creates the GUI * * * //
	private Scene createScene(final Stage dialog, final Table table) {
		Button selectionButton;
		Button addButton;
		Button editButton;
		Button removeButton;
		
		// Get the name of the table, if we have one
		String tableName = table.getName();
		if (tableName == null) tableName = "";
			
		// * * * The connection settings group
		VBox connectionBox = VBoxBuilder.create()
				.spacing(5)
				.alignment(Pos.CENTER_LEFT)
				.children(

						// Add, edit, or remove connection box
						HBoxBuilder.create()
						.spacing(5)
						.alignment(Pos.CENTER_LEFT)
						.children(

								// Sources box
								VBoxBuilder.create()
								.spacing(5)
								.alignment(Pos.CENTER_LEFT)
								.children(
										TextBuilder.create()
										.text("Data Sources")
										.build(),
										//_sourcesView = ListViewBuilder.create(CyclistDatasource.class) // Java 8
										_sourcesView = ListViewBuilder.<CyclistDatasource>create()
										.id("datasources-list")
										.maxHeight(100)
										.minHeight(100)
										.build())  
										.build(),

										// Add/Edit/Remove Buttons
										VBoxBuilder.create()
										.spacing(5)
										.alignment(Pos.CENTER)
										.children(
												addButton = ButtonBuilder.create()
												.text("Add")
												.minWidth(75)
												.build(),
												editButton = ButtonBuilder.create()
												.text("Edit")
												.minWidth(75)
												.build(),
												removeButton = ButtonBuilder.create()
												.text("Remove")
												.minWidth(75)
												.build()
												)
												.build()
								).build(),

								// Select the connection settings box
								HBoxBuilder.create()
								.spacing(10)
								.padding(new Insets(5))
								.alignment(Pos.CENTER_LEFT)
								.children(
										// Select the connection
										selectionButton = ButtonBuilder.create()
										.text("Connect")
										.onAction(new EventHandler<ActionEvent>() {
											@Override
											public void handle(ActionEvent arg0) {
												selectConnection(_current);
											};
										}).build(),
										_statusDisplay = ImageViewBuilder.create().build()
										).build()
						).build();
		
		// Keep track of the currently selected data source
		_sourcesView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<CyclistDatasource>() {
					public void changed(ObservableValue<? extends CyclistDatasource> ov, 
							CyclistDatasource old_val, CyclistDatasource new_val) {
						
						_current = _sourcesView.getSelectionModel().getSelectedItem();
						_tablesView.getItems().clear();
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
		
		// The connection schema	
		VBox schemaBox = VBoxBuilder.create()
				.spacing(1)
				.padding(new Insets(5))
				.maxHeight(Double.MAX_VALUE)
				.children(	
						TextBuilder.create().text("Select Table:").build(),
						//_tablesView = ListViewBuilder.create(String.class) // Java 8
						_tablesView = ListViewBuilder.<String>create()
						.maxHeight(Double.MAX_VALUE)
						.build()						
						).build();
		VBox.setVgrow(_tablesView, Priority.ALWAYS);
		VBox.setVgrow(schemaBox, Priority.ALWAYS);
		schemaBox.disableProperty().bind(_sourcesView.getSelectionModel().selectedItemProperty().isNull());
	
		// The ok/cancel buttons
		Button ok;
		HBox buttonsBox = HBoxBuilder.create()
				.spacing(10)
				.alignment(Pos.CENTER_RIGHT)
				.padding(new Insets(5))
				.children(	
					// Cancel
					ButtonBuilder.create()
						.text("Cancel")
						.onAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								dialog.hide();
							};
						})
						.build(),

					// OK
					ok = ButtonBuilder.create()
						.text("Ok")
						.onAction(new EventHandler<ActionEvent>() {	
							@Override
							public void handle(ActionEvent arg0) {
								updateTable(table);
								_selection.setValue(table);
								dialog.hide();
							};
						})
						.build()	
				)
				.build();	
		HBox.setHgrow(buttonsBox,  Priority.ALWAYS);
		ok.disableProperty().bind(_tablesView.getSelectionModel().selectedItemProperty().isNull());
	
		
		// Create the scene
		Scene scene = new Scene(
				VBoxBuilder.create()
				.spacing(5)
				.padding(new Insets(5))
				.prefHeight(500)
				.id("datatable-wizard")
				.children(connectionBox, schemaBox, _selector = new DatasourceSelector(table), buttonsBox)
				.build()
				);			
		scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
		_selector.disableProperty().bind(_tablesView.getSelectionModel().selectedItemProperty().isNull());
		
	/*if(_current != null)
			_sourcesView.getSelectionModel().select(_current);
		else if(_sourcesView.getItems().size() == 1)
			_sourcesView.getSelectionModel().select(0);
		*/	
		_sourcesView.getSelectionModel().selectFirst();
		//_sourcesView.getSelectionModel().clearAndSelect(0);
	
		
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
		
		_tablesView.getItems().clear();
		
		try (Connection conn = ds.getConnection()) {
			_statusDisplay.setImage(Resources.getIcon("ok"));
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				_tablesView.getItems().add(rs.getString(3));
			}
			
		} catch (Exception e) {
			_statusDisplay.setImage(Resources.getIcon("error"));
		}
	}
	
	private void updateTable(Table table) {
		// for now table name is the same as the remote name
		String name = (String) _tablesView.getSelectionModel().getSelectedItem();
		
		table.setName(name); 
		table.setAlias(_selector.getAlias());
		table.setDataSource(_current);
		table.setLocalDatafile();
		table.setProperty(Table.REMOTE_TABLE_NAME, name);
		table.extractSchema();
	}

	public CyclistDatasource getSelectedSource() {
		return _current;
	}

	public void setSelectedSource(CyclistDatasource source) {
		_current = source;	
		_sourcesView.getSelectionModel().select(_current);
	}
	
	
	
}
