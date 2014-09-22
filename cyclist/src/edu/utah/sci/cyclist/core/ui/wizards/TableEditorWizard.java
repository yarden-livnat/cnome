package edu.utah.sci.cyclist.core.ui.wizards;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.ui.components.DatasourceSelector;
import edu.utah.sci.cyclist.core.ui.components.Spring;

public class TableEditorWizard extends VBox {
	
	// GUI elements
	private Stage                 _dialog;
	private ObjectProperty<Table> _selection = new SimpleObjectProperty<>();
	private ListView<Field>    _schemaView; 
	private DatasourceSelector _selector;
	
	// * * * Constructor creates a new stage * * * //
	public TableEditorWizard(Table table) {	
		createDialog(table);
	}

	// * * * Show the dialog * * * //
	public ObjectProperty<Table> show(Window window, Bounds bounds) {
		_dialog.initOwner	(window);
		_dialog.show();
//		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
//		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
		_dialog.setX(window.getX()+bounds.getMaxX());
		_dialog.setY(window.getY()+bounds.getMinY());
		
		return _selection;
	}
		
	// * * * Create the dialog
	private void createDialog(Table tableProperty){	
		
	    _dialog = new Stage();
	    _dialog.setTitle("Edit Data Table");
	    _dialog.setHeight(300);
	    
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, tableProperty) );	
	}

		// * * * Create scene creates the GUI * * * //
		private Scene createScene(final Stage dialog, final Table table) {

			
			// * * * Datasource selector * * * 
			_selector = new DatasourceSelector(table);
			
			
			// * * * Schema Box * * * 
			VBox schemaBox = new VBox();
			schemaBox.setSpacing(5);
			schemaBox.setAlignment(Pos.CENTER_LEFT);
			schemaBox.maxHeight(Double.MAX_VALUE);
			
			_schemaView = new ListView<>();
			
			schemaBox.getChildren().addAll(new Text("Select Fields:"),_schemaView);
			
			_schemaView.setItems(FXCollections.observableList(table.getFields()));
			_schemaView.setMaxHeight(Double.MAX_VALUE);
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
			HBox buttonsBox = new HBox();


			buttonsBox.setSpacing(10);
			buttonsBox.setAlignment(Pos.CENTER_RIGHT);
			buttonsBox.setPadding(new Insets(5));
			
			// Cancel
			Button cancel = new Button("Cancel");
			cancel.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent arg0) {
									_dialog.hide();
								};
			});
			
			// OK
			Button ok = new Button("Ok");
			ok.setOnAction(new EventHandler<ActionEvent>() {	
								@Override
								public void handle(ActionEvent arg0) {
									updateTable(table);
									_selection.setValue(table);
									_dialog.hide();
								};
			});
			
			buttonsBox.getChildren().addAll(new Spring(),cancel,ok);	
			HBox.setHgrow(buttonsBox,  Priority.ALWAYS);
			
			// The vertical layout of the whole wizard
			VBox header = new VBox();
			header.setSpacing(10);
			header.setPadding(new Insets(5));
			header.getChildren().addAll(new Text("Name: " + table.getName()),_selector, schemaBox, buttonsBox);
			
			VBox.setVgrow(schemaBox, Priority.ALWAYS);
			
			// Create the scene
			VBox sceneVbox = new VBox();
			sceneVbox.setSpacing(5);
			sceneVbox.setPadding(new Insets(5));
			sceneVbox.setId("datatable-wizard");
			sceneVbox.getChildren().add(header);
			
			Scene scene = new Scene(sceneVbox);

				
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