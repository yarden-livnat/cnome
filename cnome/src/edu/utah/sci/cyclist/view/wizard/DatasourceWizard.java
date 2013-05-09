/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.view.wizard;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBuilder;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Pane;
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
import edu.utah.sci.cyclist.view.components.MySQLPage;
import edu.utah.sci.cyclist.view.components.SQLitePage;
import edu.utah.sci.cyclist.view.components.Spring;

/*
 *  Class to create or edit a data source
 */
public class DatasourceWizard extends VBox {
	
	// GUI elements
	private Stage                             _dialog;
	private ComboBox<String>                  _sourceBox;	
	private TextField                         _nameField;
	private ImageView                         _statusDisplay;
	private Map<String, DatasourceWizardPage> _panes;
	private DatasourceWizardPage              _currentPage;	
	
	private String current = null;

	// This will have to be changed to a data source
	private ObjectProperty<CyclistDatasource> selection = new SimpleObjectProperty<>();

	// * * * Default constructor creates new data source * * * //
	public DatasourceWizard() {
		createDialog(new CyclistDatasource());
	}

	// * * * Constructor that edits existing source * * * *//
	public DatasourceWizard(CyclistDatasource sourceProperty){
		createDialog(sourceProperty);		
	}
	
	// * * * Create the dialog * * * //
	private void createDialog(CyclistDatasource sourceProperty){
		_dialog = StageBuilder.create()
				.title("Create or Edit Data Source")
				.build();
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, sourceProperty) );
	}
		
	// * * * Show the wizard * * * //
	public ObjectProperty<CyclistDatasource> show(Window window) {
		_dialog.initOwner(window);
		_dialog.show();
		return selection;
	}
		
	// * * * Create scene creates the GUI * * * //
	private Scene createScene(final Stage dialog, final CyclistDatasource datasource) {
		
		// Get the name of the source, if we have one
		String sourceName = datasource.getName();
		if (sourceName == null) sourceName = "";
		
		
		// The user-specified name of the table
		HBox nameBox = HBoxBuilder.create()
				.spacing(5)
				.alignment(Pos.CENTER)
				.children(
						TextBuilder.create().text("Name:").build(),
						_nameField = TextFieldBuilder.create()
						.prefWidth(150)
						.text(sourceName)
						.build())
						.build();
			
		// The selector for type of connection
		final Pane pane = new Pane();
		pane.prefHeight(200);
		_panes = createPanes(datasource);
		
		ComboBox<String> cb = ComboBoxBuilder.<String>create()
				.prefWidth(200)
				.build();
		HBox.setHgrow(cb,  Priority.ALWAYS);
		cb.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				DatasourceWizardPage page = _panes.get(newValue);
				if (page != null) {
					pane.getChildren().clear();
					pane.getChildren().add(page.getNode());
					_currentPage = page;
				}
			}
		});
	
		cb.setItems(FXCollections.observableArrayList(_panes.keySet()));
		String type = datasource.getProperties().getProperty("type");
		if (type == null) type = "MySQL";
		cb.getSelectionModel().select(type);
		
		// The ok/cancel buttons
		Button ok;
		HBox buttonsBox = HBoxBuilder.create()
				.spacing(10)
				.alignment(Pos.CENTER_RIGHT)
				.padding(new Insets(5))
				.children(					
						// Test Connection
						ButtonBuilder.create()
						.text("Test Connection")
						.onAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								CyclistDatasource ds = _currentPage.getDataSource();
								testConnection(ds);
							};
						})
						.build(),
						_statusDisplay = ImageViewBuilder.create().build(),
						ProgressIndicatorBuilder.create().progress(-1).maxWidth(8).maxHeight(8).visible(false).build(),	
						new Spring(),						
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
								System.out.println("Create & return a new data source");
								
								CyclistDatasource ds = _currentPage.getDataSource();
								ds.getProperties().setProperty("name", _nameField.getText());
								selection.setValue(ds);
								
								//selection.set(sourceProperty);
								dialog.hide();
							};
						})
						.build()	
						)
						.build();
		HBox.setHgrow(buttonsBox,  Priority.ALWAYS);
		
		// Disable the ok button until we at least have a name field
		ok.disableProperty().bind(_nameField.textProperty().isNull().or(_nameField.textProperty().isEqualTo("")));

		// The vertical layout of the whole wizard
		VBox header = VBoxBuilder.create()
				.spacing(10)
				.padding(new Insets(5))
				.children(nameBox, 
						cb,
						pane, 
						buttonsBox)
						.build();	


		Scene scene = new Scene(
				VBoxBuilder.create()
					.spacing(5)
					.padding(new Insets(5))
					.id("datasource-wizard")
					.children(header)
					.build()
				);
		
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
		return scene;
	}
	
	private Map<String, DatasourceWizardPage> createPanes(CyclistDatasource ds) {
		Map<String, DatasourceWizardPage> panes = new HashMap<>();

		panes.put("MySQL", new MySQLPage(ds));
		panes.put("SQLite", new SQLitePage(ds));
		return panes;
	}

	private void testConnection(CyclistDatasource ds) {
		//System.out.println("Test Connection");

		try (Connection conn = ds.getConnection()) {
			//System.out.println("connection ok");
			_statusDisplay.setImage(Resources.getIcon("ok"));
		} catch (Exception e) {
			//System.out.println("connection failed");
			_statusDisplay.setImage(Resources.getIcon("error"));
		}
	}
}
