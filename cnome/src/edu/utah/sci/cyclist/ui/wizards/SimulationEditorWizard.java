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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.model.Simulation;

public class SimulationEditorWizard extends VBox {
	
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
		_dialog.setTitle("Delete Simulation");
		_dialog.setHeight(100);
		
		
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, simulation) );
	}

		// * * * Create scene creates the GUI * * * //
		private Scene createScene(final Stage dialog, final Simulation simulation) {

			VBox vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setAlignment(Pos.CENTER);
			vbox.setMinWidth(200);
			vbox.setPadding(new Insets(5));
			
			Label lbl = new Label("Delete simulation \"" + simulation.getAlias() + "\" ?");
			HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER);
			hbox.setSpacing(5);
			hbox.setPadding(new Insets(5));
			
			Button delete = new Button("Delete");
			Button cancel = new Button("Cancel");
			hbox.getChildren().addAll(delete,cancel);
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
			
			vbox.getChildren().addAll(lbl,hbox);
			
			// Create the scene
			Scene scene = new Scene(vbox);
			
			scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
			return scene;
		}	
}