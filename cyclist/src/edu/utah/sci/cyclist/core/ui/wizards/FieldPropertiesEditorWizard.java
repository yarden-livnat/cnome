package edu.utah.sci.cyclist.core.ui.wizards;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.core.model.DataType;
import edu.utah.sci.cyclist.core.model.DataType.Type;
import edu.utah.sci.cyclist.core.model.Field;

public class FieldPropertiesEditorWizard extends VBox {
	
	// GUI elements
	private Stage                 _dialog;
	private ObjectProperty<DataType> _selection = new SimpleObjectProperty<>();
	
//	private static final String DISCRETE = "Discrete";
//	private static final String CONTINOUS = "Continous";
	
	// * * * Constructor creates a new stage * * * //
	public FieldPropertiesEditorWizard(Field field) {	
		createDialog(field);
	}

	// * * * Show the dialog * * * //
	public ObjectProperty<DataType> show(Window window) {
		_dialog.initOwner	(window);
		_dialog.show();
		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
		return _selection;
	}
		
	// * * * Create the dialog
	private void createDialog(Field field){
        
		_dialog = new Stage();
		_dialog.setTitle("Edit Field Properties");
		_dialog.setHeight(200);
		
		
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, field) );
	}

		// * * * Create scene creates the GUI * * * //
		private Scene createScene(final Stage dialog, Field field) {
			
			VBox vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setAlignment(Pos.CENTER_LEFT);
			vbox.setMinWidth(250);
			vbox.setPadding(new Insets(5));
			vbox.getStyleClass().add("fields-wizard");
			
			Label name = new Label("Field Name: " + field.getName() );
			name.setAlignment(Pos.CENTER_LEFT);
			name.setFont(Font.font(null, FontWeight.BOLD, 12));
			
			HBox roleHbox = new HBox();
			roleHbox.setSpacing(5);
			roleHbox.setPadding(new Insets(5));
			roleHbox.setAlignment(Pos.CENTER_LEFT);
			roleHbox.setMinWidth(250);
			
			Label roleLbl = new Label("Distribution:");
			roleLbl.setMinWidth(60);
			
			ChoiceBox<String> role = new ChoiceBox<>();
			role.getStyleClass().add("choice");
			role.setMaxWidth(Double.MAX_VALUE);
			for(DataType.Role value: DataType.Role.values()){
				role.getItems().add(value.name());
			}
			
			roleHbox.getChildren().addAll(roleLbl,role);
			HBox.setHgrow(role, Priority.ALWAYS);
			
			HBox filterHbox = new HBox();
			filterHbox.setSpacing(5);
			filterHbox.setPadding(new Insets(5));
			filterHbox.setAlignment(Pos.CENTER_LEFT);
			filterHbox.setMinWidth(250);
			
			Label filterLbl = new Label("Filter:");
			filterLbl.setMinWidth(60);
			
			ChoiceBox<String> filter = new ChoiceBox<>();
			filter.getStyleClass().add("choice");
			filter.setMaxWidth(Double.MAX_VALUE);
			filter.getItems().addAll(DataType.FilterType.LIST.name(), DataType.FilterType.RANGE.name());
			
			filterHbox.getChildren().addAll(filterLbl,filter);
			HBox.setHgrow(filter, Priority.ALWAYS);
			
			
			HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER_RIGHT);
			hbox.setSpacing(10);
			hbox.setPadding(new Insets(5));
			
			Button ok = new Button("OK");
			Button cancel = new Button("Cancel");
			hbox.getChildren().addAll(ok,cancel);
			cancel.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					_dialog.hide();
				};
			});
			ok.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					DataType dataType = new DataType(field.getDataType());
					dataType.setRole(DataType.Role.valueOf(role.valueProperty().getValue()));
					dataType.setFilterType(DataType.FilterType.valueOf(filter.valueProperty().getValue()));
					
					_selection.set(dataType);
					_dialog.hide();
				};
			});
			
			vbox.getChildren().addAll(name, roleHbox, filterHbox, hbox);
			VBox.setVgrow(roleHbox, Priority.ALWAYS);
			
			// Create the scene
			Scene scene = new Scene(vbox);
			
			scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
			role.setValue(field.getDataType().getRole().name());
			filter.setValue(field.getDataType().getFilterType().name());
			if(field.getType() == Type.TEXT){
				role.setDisable(true);
				filter.setDisable(true);
				ok.setDisable(true);
			}
			
			return scene;
		}	
}