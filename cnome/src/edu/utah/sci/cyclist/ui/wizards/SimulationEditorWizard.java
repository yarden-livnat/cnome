package edu.utah.sci.cyclist.ui.wizards;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.model.Simulation;

public class SimulationEditorWizard extends VBox {
	
	private TextField _aliasField;
	// GUI elements
	private Stage                 _dialog;
	private ObjectProperty<Simulation> _selection = new SimpleObjectProperty<>();
	// * * * Constructor creates a new stage * * * //
	public SimulationEditorWizard(Simulation simulation) {	
		createDialog(simulation);
	}

	// * * * Show the dialog * * * //
	public ObjectProperty<Simulation> show(Window window) {
		_dialog.initOwner	(window);
		_dialog.show();
		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
		return _selection;
	}
		
	// * * * Create the dialog
	private void createDialog(Simulation simulation){	

		_dialog = new Stage();
		_dialog.setTitle("Edit Simulation");
		_dialog.setHeight(150);
		
		
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, simulation) );
	}

		// * * * Create scene creates the GUI * * * //
		private Scene createScene(final Stage dialog, final Simulation simulation) {

			VBox vbox = new VBox();
			vbox.setSpacing(10);
			vbox.setAlignment(Pos.CENTER);
			vbox.setMinWidth(300);
			vbox.setPadding(new Insets(5));
			
			HBox aliasHbox = new HBox();
			aliasHbox.setSpacing(10);
			aliasHbox.setAlignment(Pos.CENTER_LEFT);
			aliasHbox.setPadding(new Insets(5));
			
			Label aliasLbl =  new Label("Alias:");
			aliasLbl.setAlignment(Pos.CENTER);
			aliasLbl.setFont(new Font(12));
			aliasLbl.setPadding(new Insets(3,0,0,0));
			
			_aliasField = new TextField();
			_aliasField.setPrefWidth(210);
			_aliasField.setMinHeight(20);
			
			aliasHbox.getChildren().addAll(aliasLbl, _aliasField);
			
			HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER);
			hbox.setSpacing(10);
			hbox.setPadding(new Insets(5));
			
			Button ok  = new Button("OK");
			Button delete = new Button("Delete");
			Button cancel = new Button("Cancel");
			hbox.getChildren().addAll(ok,delete,cancel);
			cancel.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					_dialog.hide();
				};
			});
			delete.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					_selection.set(new Simulation("delete"));
					_dialog.hide();
				};
			});
			ok.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					Simulation simulation = new Simulation("edit");
					simulation.setAlias(_aliasField.getText());
					_selection.set(simulation);
					_dialog.hide();
				};
			});
			
			vbox.getChildren().addAll(aliasHbox, hbox);
			
			// Create the scene
			Scene scene = new Scene(vbox);
			
			scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
			return scene;
		}	
}