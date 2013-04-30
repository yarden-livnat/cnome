package edu.utah.sci.cyclist.view.components;

import java.io.File;

import edu.utah.sci.cyclist.Cyclist;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.DirectoryChooser;
import javafx.stage.DirectoryChooserBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DatasourceWizard extends VBox {

	
	private Stage dialog;
	private ComboBox<String> cb;
	private String current = null;

	
	// This will have to be changed to a data source
	private ObjectProperty<String> selection = new SimpleObjectProperty<>();
	
	public ObjectProperty<String> show(Window window) {
		 dialog.initOwner(window);
		 dialog.show();
		 return selection;
	}
	
	// * * * Constructor creates a new stage * * * //
	public DatasourceWizard() {
		dialog = new Stage(); 
		dialog.initModality(Modality.WINDOW_MODAL);
	   dialog.setTitle("Data Source Wizard");
	   // dialog.setScene( createScene(dialog) );
	}

	// * * * Create scene creates the GUI * * * //
	/*private Scene createScene(final Stage dialog) {
		
		
		Text header = TextBuilder.create()
				.id("workspace-wizard-header")
				.text("Create New Data Source")
				.build();
		
		String dsName = ds.getName();
		if (dsName == null) dsName = "";

		VBox header = VBoxBuilder.create()
				.spacing(5)
				.children(
					HBoxBuilder.create()
						.spacing(5)
						.children(
							TextBuilder.create().text("Name").build(),
							_name = TextFieldBuilder.create().prefWidth(150).text(dsName).build()
						)
						.build(),
					cb = ChoiceBoxBuilder.<String>create()
						.build()
				)
				.build();
		
		
		HBox pane = HBoxBuilder.create()
//				.prefWidth(250)
				
				.alignment(Pos.CENTER_RIGHT)
				.padding(new Insets(5))
				.spacing(10)
				.children(
						cb = ComboBoxBuilder.<String>create()
							.prefWidth(150)
							.editable(true)
							.value(current)
							.build(),
						ButtonBuilder.create()
							.text("...")
							.onAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									DirectoryChooser chooser = DirectoryChooserBuilder.create()
											.title("Select directory")
											.build();
									
									if (cb.getValue() != null && cb.getValue() != "") {
										File dir = new File(cb.getValue());
										if(dir.isDirectory())
											chooser.setInitialDirectory(dir);									
									}
									File dir = chooser.showDialog(null);
									if (dir != null) { 
										cb.getItems().add(0, dir.getAbsolutePath());
										cb.setValue(cb.getItems().get(0));
									}
										
								}
							})
							.build()
					)
				.build();
		HBox.setHgrow(cb, Priority.ALWAYS);
		
		HBox buttons = HBoxBuilder.create()
					.id("worksapce-wizard-buttons")
					.spacing(10)
					.padding(new Insets(5))
					.alignment(Pos.CENTER_RIGHT)
					.children(
							ButtonBuilder.create()
								.text("Cancel")
								.onAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										current = null;
										dialog.close();
									}
								})
								.build(),
							ButtonBuilder.create()
								.text("Ok")
								.defaultButton(true)
								.onAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										selection.set(cb.getValue());
										dialog.close();
									}
								})
								.build()
					)
					.build();
		
		HBox.setHgrow(buttons,  Priority.ALWAYS);
		
		Scene scene = new Scene(
				VBoxBuilder.create()
					.spacing(5)
					.padding(new Insets(5))
					.id("workspace-wizard")
					.children(header, pane, buttons)
					.build()
				);
		
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
        
        
		return scene;
	}
	*/
}
