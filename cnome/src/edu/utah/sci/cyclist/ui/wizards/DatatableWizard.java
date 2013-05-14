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
 *******************************************************************************/
package edu.utah.sci.cyclist.ui.wizards;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

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
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.FieldProperties;
import edu.utah.sci.cyclist.model.Schema;
import edu.utah.sci.cyclist.model.Table;
 

/*
 * Class to allow the user to create or edit a data table.
 * Also controls the creation/editing of data sources.
 */
public class DatatableWizard extends VBox {

	// GUI elements
	private Stage                       _dialog;
	private ListView<CyclistDatasource> _sourcesView;
	private ListView<String>            _tablesView;
	private ImageView                   _statusDisplay;
	
	private ObservableList<Table> _tables;
	private ObservableList<CyclistDatasource> _sources = FXCollections.observableArrayList();
	
	// Data elements
	private CyclistDatasource _current;
	private ObjectProperty<Table> selection = new SimpleObjectProperty<>(); 
	
	// * * * Constructor creates a new stage * * * //
	public DatatableWizard() {
		createDialog(new Table());
	}

	public DatatableWizard(Table tableProperty){
		createDialog(tableProperty);
	}
	
	// * * * Set the existing data sources in the combo box * * * //
	public void setItems(final ObservableList<CyclistDatasource> sources) {
		_sourcesView.setItems(sources);
		_sourcesView.getSelectionModel().selectFirst();
	}
			
	// * * * Show the dialog * * * //
	public ObjectProperty<Table> show(Window window) {
		 _dialog.initOwner(window);
		 _dialog.show();
		 return selection;
	}
	
	// * * * Create the dialog
	private void createDialog(Table tableProperty){
		
		_dialog = StageBuilder.create()
				.title("Create or Edit Data Table")
				.build();
		
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, tableProperty) );	
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
											.text("Data sources")
											.build(),
										_sourcesView = ListViewBuilder.<CyclistDatasource>create()
											.id("datasources-list")
											.maxHeight(100)
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
									})
									.build(),
									_statusDisplay = ImageViewBuilder.create().build()
									)
							.build()

					)
				.build();
		
		

		// Keep track of the currently selected data source
		_sourcesView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<CyclistDatasource>() {
					public void changed(ObservableValue<? extends CyclistDatasource> ov, 
							CyclistDatasource old_val, CyclistDatasource new_val) {
						_current = _sourcesView.getSelectionModel().getSelectedItem();
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
				.children(	
						TextBuilder.create().text("Select Schema Table:").build(),
						_tablesView = ListViewBuilder.<String>create()
						.maxHeight(100)
						.build()						
						).build();
		_tablesView.disableProperty().bind(_sourcesView.getSelectionModel().selectedItemProperty().isNull());
			
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
								selection.setValue(table);
								dialog.hide();
							};
						})
						.build()	
				)
				.build();	
		HBox.setHgrow(buttonsBox,  Priority.ALWAYS);
		ok.disableProperty().bind(_tablesView.getSelectionModel().selectedItemProperty().isNull());

		
		// The vertical layout of the whole wizard
		VBox header = VBoxBuilder.create()
				.spacing(10)
				.padding(new Insets(5))
				.children(
						connectionBox,
						schemaBox,
						buttonsBox)
				.build();	

		// Create the scene
		Scene scene = new Scene(
				VBoxBuilder.create()
				.spacing(5)
				.padding(new Insets(5))
				.id("datatable-wizard")
				.children(header)
				.build()
				);

			
		scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
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
			}
		});
	}
	
	

	private void selectConnection(CyclistDatasource ds) {
		try (Connection conn = ds.getConnection()) {
			_statusDisplay.setImage(Resources.getIcon("ok"));
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			ResultSetMetaData rmd = rs.getMetaData();
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
		
		table.setName(name); // _nameField.getText());
		table.setDataSource(_current);
		table.setProperty(Table.REMOTE_TABLE_NAME, name);
		table.extractSchema();
	}
	
}
