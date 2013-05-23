package edu.utah.sci.cyclist.ui.wizards;

import java.io.File;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ListViewBuilder;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableViewBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.TextBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;
import javafx.stage.Window;
import javafx.util.Callback;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Table;

public class TableEditorWizard extends VBox {
	
	// GUI elements
	private Stage                       _dialog;
	private ObjectProperty<Table> selection = new SimpleObjectProperty<>();
	private TextField _aliasField;
	private TextField _remoteField;
	private TextField _localField;
	private TextField _rowsField;
	private ListView<Field> _schemaView; 
	private ToggleGroup _remoteLocalGroup;
	private RadioButton _remoteRadio;
	private RadioButton _localRadio;
	
	
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
		return selection;
	}
		
	// * * * Create the dialog
	private void createDialog(Table tableProperty){
		_dialog = StageBuilder.create()
				.title("Edit Data Table")
				.build();
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, tableProperty) );	
	}

		// * * * Create scene creates the GUI * * * //
		private Scene createScene(final Stage dialog, final Table table) {

			// * * * Alias box * * * 
			HBox aliasBox = HBoxBuilder.create()
					.spacing(5)
					.alignment(Pos.CENTER)
					.children(
							TextBuilder.create().text("Alias:").build(),
							_aliasField = TextFieldBuilder.create()
							.prefWidth(150)
							.text(table.getAlias()).build()
							)
					.build();
			
			// * * * Datasource box * * * 
			GridPane grid;
			_remoteLocalGroup = new ToggleGroup();
			VBox dataBox = VBoxBuilder.create()
					.spacing(5)
					.alignment(Pos.TOP_LEFT)
					.children(
							TextBuilder.create().text("Data source:").build(),
							grid = GridPaneBuilder.create()
							.alignment(Pos.CENTER)
							.build()
					
							)		
					.build();
			
			_remoteField = TextFieldBuilder.create()
					                       .prefWidth(150)
					                       .text(table.getDataSource().getName())
					                       .editable(false)
					                       .build();
			_localField = TextFieldBuilder.create()
                                          .prefWidth(150)
                                          .build();
	
			grid.add(_remoteRadio = new RadioButton(), 0, 0);
			grid.add(TextBuilder.create().text("Remote:").build(), 1, 0);
			grid.add(_remoteField,2, 0);
			
			grid.add(_localRadio = new RadioButton(),0, 1);
			grid.add(TextBuilder.create().text("Local:").build(), 1, 1);
			grid.add(_localField,2, 1);
			
			grid.add(ButtonBuilder.create()
			.text("...")
			.onAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FileChooser chooser = new FileChooser();
					chooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("SQLite files (*.sqlite)", "*.sqlite") );
					File file = chooser.showOpenDialog(null);
					if (file != null)
						_localField.setText(file.getPath());
				}
			})
			.build(), 3, 1);
			
			_remoteRadio.setToggleGroup(_remoteLocalGroup);
			_localRadio.setToggleGroup(_remoteLocalGroup);
			_remoteRadio.setSelected(true);
				
			
			// * * * Schema Box * * * 
			VBox schemaBox = VBoxBuilder.create()
					.spacing(5)
					.alignment(Pos.CENTER_LEFT)
					.children(
							TextBuilder.create().text("Schema:").build(),
							HBoxBuilder.create()
							.children(TextBuilder.create().text("# of Rows:").build(),
									_rowsField = TextFieldBuilder.create()
				                     .prefWidth(150)
				                     .editable(false)
			                      	.text(new Integer(table.getNumRows()).toString()).build()
									)
							.build(),
							_schemaView = ListViewBuilder.<Field>create()
							.items(FXCollections.observableList(table.getFields()))
							.maxHeight(100)
							.build()
							)
					.build();		
			
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
			
			// The vertical layout of the whole wizard
			VBox header = VBoxBuilder.create()
					.spacing(10)
					.padding(new Insets(5))
					.children(aliasBox,
							dataBox,
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
				
		private void updateTable(Table table) {
			
			// Get the alias
			table.setAlias(_aliasField.getText());
			
			// Get the datasource
			// TODO: what to do with local source?
			String localSource = "";
			if(_localRadio.selectedProperty().get())
				localSource = _localField.getText();
						
		}		
}