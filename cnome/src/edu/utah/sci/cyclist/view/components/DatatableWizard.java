package edu.utah.sci.cyclist.view.components;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ListViewBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
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
import edu.utah.sci.cyclist.model.Table;
 

/*
 * Class to allow the user to create or edit a data table.
 * Also controls the creation/editing of data sources.
 */
public class DatatableWizard extends VBox {

	// GUI elements
	private Stage                       _dialog;
	private ListView<CyclistDatasource> _sourceList;
	private ListView<String>            _tableList;
	private TextField                   _nameField;
	private ImageView                   _statusDisplay;
	private Button                      _addButton;
	private Button                      _editButton;
	private Button                      _removeButton;
	
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
	
	// * * * Return the add/edit/remove buttons for the controller
	public Button getAddSourceButton(){ return _addButton; }
	public Button getEditSourceButton(){ return _editButton; }
	public Button getRemoveSourceButton(){ return _removeButton; }
		
	// * * * Get the current data source
	public CyclistDatasource getCurrentDataSource(){return _current; }
	
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
			
		// Get the name of the table, if we have one
		String tableName = table.getName();
		if (tableName == null) tableName = "";
		
		// The user-specified name of the table
		HBox nameBox = HBoxBuilder.create()
				.spacing(5)
				.alignment(Pos.CENTER)
				.children(
						TextBuilder.create().text("Name:").build(),
						_nameField = TextFieldBuilder.create()
						.prefWidth(150)
						.text(tableName)
						.build())
						.build();		
			
		Button selectionButton;
		
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
										TextBuilder.create().text("Data Connection").build(),
										_sourceList = ListViewBuilder.<CyclistDatasource>create()
										.maxHeight(100)
										.build())  
										.build(),

										// Add/Edit/Remove Buttons
										VBoxBuilder.create()
										.spacing(5)
										.alignment(Pos.CENTER)
										.children(
												_addButton = ButtonBuilder.create()
												.text("Add")
												.minWidth(75)
												.build(),
												_editButton = ButtonBuilder.create()
												.text("Edit")
												.minWidth(75)
												.build(),
												_removeButton = ButtonBuilder.create()
												.text("Remove")
												.minWidth(75)
												.build()
												).build()
								).build(),

								// Select the connection settings box
								HBoxBuilder.create()
								.spacing(10)
								.padding(new Insets(5))
								.alignment(Pos.CENTER)
								.children(
										// Select the connection
										selectionButton = ButtonBuilder.create()
										.text("Select Connection")
										.onAction(new EventHandler<ActionEvent>() {
											@Override
											public void handle(ActionEvent arg0) {
												selectConnection(_current);
											};
										})
										.build(),
										_statusDisplay = ImageViewBuilder.create().build()
										).build()

						).build();

		// Keep track of the currently selected data source
		_sourceList.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<CyclistDatasource>() {
					public void changed(ObservableValue<? extends CyclistDatasource> ov, 
							CyclistDatasource old_val, CyclistDatasource new_val) {
						_current = _sourceList.getSelectionModel().getSelectedItem();
					}
				});

		// Disable edit/remove until we have something selected
		_editButton.disableProperty().bind( _sourceList.getSelectionModel().selectedItemProperty().isNull());
		_removeButton.disableProperty().bind( _sourceList.getSelectionModel().selectedItemProperty().isNull());
		selectionButton.disableProperty().bind( _sourceList.getSelectionModel().selectedItemProperty().isNull());

		// The connection schema		
		VBox schemaBox = VBoxBuilder.create()
				.spacing(1)
				.padding(new Insets(5))
				.children(	
						TextBuilder.create().text("Select Schema Table:").build(),
						_tableList = ListViewBuilder.<String>create()
						.maxHeight(100)
						.build()						
						).build();
		_tableList.disableProperty().bind(_sourceList.getSelectionModel().selectedItemProperty().isNull());
			
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
								table.setName(_nameField.getText());
								table.setDataSource(_current);
								table.setTableName((String) _tableList.getSelectionModel().getSelectedItem());
								selection.setValue(table);
								dialog.hide();
							};
						})
						.build()	
				)
				.build();	
		HBox.setHgrow(buttonsBox,  Priority.ALWAYS);
		ok.disableProperty().bind(_tableList.getSelectionModel().selectedItemProperty().isNull());

		
		// The vertical layout of the whole wizard
		VBox header = VBoxBuilder.create()
				.spacing(10)
				.padding(new Insets(5))
				.children(nameBox, 
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

	// * * * Set the existing data sources in the combo box * * * //
	public void setItems(final ObservableList<CyclistDatasource> sources) {

		_sourceList.getItems().clear();
		// Set the sources in the combo box
		for(int i = 0; i < sources.size(); i++)
			_sourceList.getItems().add(sources.get(i));	
		
		sources.addListener(new ListChangeListener<CyclistDatasource>(){

			@Override
			public void onChanged(ListChangeListener.Change<? extends CyclistDatasource> arg0) {
				_sourceList.getItems().clear();
				// Set the sources in the combo box
				for(int i = 0; i < sources.size(); i++)
					_sourceList.getItems().add(sources.get(i));	
			}	
		});
	}
			
	// * * * Show the dialog * * * //
	public ObjectProperty<Table> show(Window window) {
		 _dialog.initOwner(window);
		 _dialog.show();
		 return selection;
	}
	
	private void selectConnection(CyclistDatasource ds) {
		System.out.println("Test Connection");

		try (Connection conn = ds.getConnection()) {
			System.out.println("connection ok");
			_statusDisplay.setImage(Resources.getIcon("ok"));
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				_tableList.getItems().add(rs.getString(3));
			}
			
		} catch (Exception e) {
			//System.out.println("connection failed");
			_statusDisplay.setImage(Resources.getIcon("error"));
		}
	}
	
}
