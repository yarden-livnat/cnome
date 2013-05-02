package edu.utah.sci.cyclist.view.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.ComboBoxBuilder;
import javafx.scene.control.ListViewBuilder;
import javafx.scene.control.Separator;
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
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Table;

/*
 * Class to allow the user to create or edit a data table.
 * Also controls the creation/editing of data sources.
 */
public class DatatableWizard extends VBox {

	// GUI elements
	private Stage            _dialog;
	private ComboBox<String> _sourceBox;
	private TextField        _nameField;
	private ImageView        _statusDisplay;
	private Button           _addButton;
	

	// Data elements
	private ObjectProperty<Table> selection = new SimpleObjectProperty<>(); 
	
	// * * * Constructor creates a new stage * * * //
	public DatatableWizard() {
		createDialog(new Table());
	}

	public DatatableWizard(Table tableProperty){
		createDialog(tableProperty);		
	}
	
	// * * * Return the add button for the controller * * * //
	public Button getAddSourceButton(){ return _addButton; }
		
	// * * * Create the dialog * * * //
	private void createDialog(Table tableProperty){
		
		_dialog = StageBuilder.create()
				.title("Create or Edit Data Table")
				.build();
		
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, tableProperty) );	
	}
	
	// * * * Create scene creates the GUI * * * //
	private Scene createScene(final Stage dialog, Table tableProperty) {
			
		// Get the name of the table, if we have one
		String tableName = tableProperty.getName();
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
		
		// The connection settings
		HBox connectionBox = HBoxBuilder.create()
				.spacing(5)
				.alignment(Pos.CENTER)
				.children(
						TextBuilder.create().text("Data Connection:").build(),
						_sourceBox = ComboBoxBuilder.<String>create()
						.prefWidth(200)
						.build(),
						new Spring(),
						_addButton = ButtonBuilder.create()
						.text("Add")
						.minWidth(75)
						.onAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								System.out.println("Data Source Wizard!");
							}
						})
						.build()
						).build();
		HBox.setHgrow(_sourceBox, Priority.ALWAYS);

		
		// Select the connection settings box
		HBox selectConnectionBox = HBoxBuilder.create()
				.spacing(10)
				.padding(new Insets(5))
				.children(
					// Select the connection
					ButtonBuilder.create()
						.text("Select Connection")
						.onAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								System.out.println("Get the connection");
							};
						})
						.build(),
					_statusDisplay = ImageViewBuilder.create().build()
					).build();
		
		// The connection schema
		VBox schemaBox = VBoxBuilder.create()
				.spacing(1)
				//.alignment(Pos.CENTER)
				.padding(new Insets(5))
				.children(	
						TextBuilder.create().text("Select Schema Table:").build(),
						ListViewBuilder.create()
						.maxHeight(100)
						.build()						
						).build();
		
			
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
								System.out.println("Create & return a new data table");
								dialog.hide();
							};
						})
						.build()	
				)
				.build();	
		HBox.setHgrow(buttonsBox,  Priority.ALWAYS);
		
		// The vertical layout of the whole wizard
		VBox header = VBoxBuilder.create()
				.spacing(10)
				.padding(new Insets(5))
				.children(nameBox, 
						new Separator(), 
						connectionBox,
						selectConnectionBox, 
						new Separator(),
						schemaBox,
						new Separator(),
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
			
			sources.addListener(new ListChangeListener<CyclistDatasource>(){

				@Override
				public void onChanged(ListChangeListener.Change<? extends CyclistDatasource> arg0) {
					// Set the sources in the combo box
					for(int i = 0; i < sources.size(); i++)
						_sourceBox.getItems().add(sources.get(i).getName());
				}
				
			});
			
			

			//if (sources.size() > 0) {
		//		current = items.get(0);
		//		cb.setValue(current);
		//	}	


		}
	
	
	// * * * Show the dialog * * * //
	public ObjectProperty<Table> show(Window window) {
		 _dialog.initOwner(window);
		 _dialog.show();
		 return selection;
	}
	
}
