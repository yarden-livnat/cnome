package edu.utah.sci.cyclist.view.components;

import java.io.File;

import edu.utah.sci.cyclist.Cyclist;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.TextBuilder;
import javafx.stage.DirectoryChooser;
import javafx.stage.DirectoryChooserBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;
import javafx.stage.Window;

public class DatatableWizard extends VBox {

	private Stage dialog;
	private ComboBox<String> cb;
	private TextField _name;
	private ImageView _status;
	private String current = null;
	
	// This will need to be changed to a data table
	private ObjectProperty<String> selection = new SimpleObjectProperty<>();

		
	
	
	// * * * Constructor creates a new stage * * * //
	public DatatableWizard() {
		createDialog(new String(""));
	}

	public DatatableWizard(String tableProperty){
		createDialog(tableProperty);		
	}
	
	// * * * Create the dialog * * * //
	private void createDialog(String tableProperty){
		
		dialog = StageBuilder.create()
				.title("Create or Edit Data Table")
				.maxWidth(250).minWidth(250)
			//	.maxHeight(100).minHeight(95)
				.build();
		
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setScene( createScene(dialog, tableProperty) );
			
	}
	
	
	// * * * Create scene creates the GUI * * * //
	private Scene createScene(final Stage dialog, String tableProperty) {
			
		// Get the name of the table, if we have one
		String tableName = tableProperty;
		if (tableName == null) tableName = "";

		// The name field
		
		// The user-specified name of the table
		HBox nameBox = HBoxBuilder.create()
				.spacing(5)
				.alignment(Pos.CENTER)
				.children(
						TextBuilder.create().text("Name:").build(),
						_name = TextFieldBuilder.create()
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
						cb = ComboBoxBuilder.<String>create()
						.build(),
						new Spring(),
						ButtonBuilder.create()
						.text("Add")
						.onAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								System.out.println("Data Source Wizard!");
							}
						})
						.build()
						).build();
		
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
					_status = ImageViewBuilder.create().build()
					).build();
		
		// The connection schema
		VBox schemaBox = VBoxBuilder.create()
				.spacing(1)
				//.alignment(Pos.CENTER)
				.padding(new Insets(5))
				.children(	
						TextBuilder.create().text("Schema Tables:").build(),
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
		
		
		// The name header
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
	
	// * * * Set the items * * * //
	public void setItems(ObservableList<String> items) {
		/*cb.setItems(items);
		if (items.size() > 0) {
			current = items.get(0);
			cb.setValue(current);
		}*/
		
		System.out.println("setItems Datatable Wizard");
		
	}
	
	// * * * Show the dialog * * * //
	public ObjectProperty<String> show(Window window) {
		 dialog.initOwner(window);
		 dialog.show();
		 return selection;
	}
	
}
