package edu.utah.sci.cyclist.ui.wizards;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ListViewBuilder;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxListCell;
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
import javafx.util.Callback;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.components.DatasourceSelector;
import edu.utah.sci.cyclist.ui.components.Spring;

public class TableEditorWizard extends VBox {
	
	// GUI elements
	private Stage                 _dialog;
	private ObjectProperty<Table> _selection = new SimpleObjectProperty<>();
	private TextField _aliasField;
	private TextField _localField;
	private ListView<Field>    _schemaView; 
	private DatasourceSelector _selector;
	
	// * * * Constructor creates a new stage * * * //
	public TableEditorWizard(Table table) {	
		createDialog(table);
	}

	// * * * Show the dialog * * * //
	public ObjectProperty<Table> show(Window window) {
		_dialog.initOwner	(window);
		_dialog.show();
		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
		return _selection;
	}
		
	// * * * Create the dialog
	private void createDialog(Table tableProperty){	
		_dialog = StageBuilder.create()
				.title("Edit Data Table")
				.height(300)
				.build();
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, tableProperty) );	
	}

		// * * * Create scene creates the GUI * * * //
		private Scene createScene(final Stage dialog, final Table table) {

			
			// * * * Datasource selector * * * 
			_selector = new DatasourceSelector(table);
			
			
			// * * * Schema Box * * * 
			VBox schemaBox = VBoxBuilder.create()
					.spacing(5)
					.alignment(Pos.CENTER_LEFT)
					.maxHeight(Double.MAX_VALUE)
					.children(
							TextBuilder.create().text("Select Fields:").build(),
//							_schemaView = ListViewBuilder.create(Field.class) // Java 8
							_schemaView = ListViewBuilder.<Field>create() // Java 7
							.items(FXCollections.observableList(table.getFields()))
							.maxHeight(Double.MAX_VALUE)
						//	.prefHeight(100)	
							.build()							)
					.build();		
		
		
			VBox.setVgrow(_schemaView, Priority.ALWAYS);
			
			Callback<Field, ObservableValue<Boolean>> getProperty = new Callback<Field, ObservableValue<Boolean>>() {
	            @Override
	            public BooleanProperty call(Field field) {
	            	// TODO	 make getSelectedProperty meaningful
	                return field.getSelectedProperty();
	            }	           
	        };
			
			 Callback<ListView<Field>, ListCell<Field>> forListView = CheckBoxListCell.forListView(getProperty);
			 _schemaView.setCellFactory(forListView);
			
			// * * *  The ok/cancel buttons * * * 
			Button ok;
			HBox buttonsBox = HBoxBuilder.create()
					.spacing(10)
					.alignment(Pos.CENTER_RIGHT)
					.padding(new Insets(5))
					.children(	
							
							// Remove
							ButtonBuilder.create()
							.text("Delete")
							.onAction(new EventHandler<ActionEvent>() {	
								@Override
								public void handle(ActionEvent arg0) {
									Table newTable = new Table("DELETE_ME");								
									_selection.setValue(newTable);
									_dialog.hide();
								};
							})
							.build(),
							
							//
							new Spring(),
							
							
						// Cancel
						ButtonBuilder.create()
							.text("Cancel")
							.onAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent arg0) {
									_dialog.hide();
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
									_dialog.hide();
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
					.children(TextBuilder.create().text("Name: " + table.getName()).build(),
							_selector,
							schemaBox,
							buttonsBox)
					.build();
			
			VBox.setVgrow(schemaBox, Priority.ALWAYS);
			
			// Create the scene
			Scene scene = new Scene(
					VBoxBuilder.create()
					.spacing(5)
					.padding(new Insets(5))
					.id("datatable-wizard")
					.children(header)
					.build()
					);

				
			VBox.setVgrow(schemaBox, Priority.ALWAYS);
			scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
			return scene;
		}
				
		private void updateTable(Table table) {
			
			// Get the alias
			table.setAlias(_selector.getAlias());
			
			// Get the data source location and subset
			table.setSourceLocation(_selector.getSourceLocation());
			table.setDataSubset(_selector.getDataSubset());		
		}		
}