package edu.utah.sci.cyclist.view.components;

import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBuilder;
import javafx.scene.input.MouseEvent;
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
import edu.utah.sci.cyclist.Cyclist;

public class WorkspaceWizard extends VBox {
	
	private Stage dialog;
	private ComboBox<String> cb;
	private String current = null;
	

	private ObjectProperty<String> selection = new SimpleObjectProperty<>();
	
	public ObjectProperty<String> show(Window window) {
		 dialog.initOwner(window);
		 dialog.show();
		 return selection;
	}
	
	public void setItems(ObservableList<String> items) {
		cb.setItems(items);
		if (items.size() > 0) {
			current = items.get(0);
			cb.setValue(current);
		}
	}
	
	public WorkspaceWizard() {
		dialog = new Stage(); 
		dialog.initModality(Modality.WINDOW_MODAL);
	   
	    dialog.setScene( createScene(dialog) );
	    
	    // allow the dialog to be dragged around.
	    final Node root = dialog.getScene().getRoot();
	    final Delta dragDelta = new Delta();
	    root.setOnMousePressed(new EventHandler<MouseEvent>() {
	      @Override public void handle(MouseEvent mouseEvent) {
	        // record a delta distance for the drag and drop operation.
	        dragDelta.x = dialog.getX() - mouseEvent.getScreenX();
	        dragDelta.y = dialog.getY() - mouseEvent.getScreenY();
	      }
	    });
	    root.setOnMouseDragged(new EventHandler<MouseEvent>() {
	      @Override public void handle(MouseEvent mouseEvent) {
	        dialog.setX(mouseEvent.getScreenX() + dragDelta.x);
	        dialog.setY(mouseEvent.getScreenY() + dragDelta.y);
	      }
	    });
	}
	
	private Scene createScene(final Stage dialog) {
		Text header = TextBuilder.create()
				.id("workspace-wizard-header")
				.text("Select workspace directory")
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
//										System.out.println("init dir:"+dir.getAbsolutePath()+"  "+dir.getPath()+"   "+dir.exists()+"    "+dir.isDirectory());
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
					.id("workspace-wizard")
					.children(header, pane, buttons)
					.build()
				);
		
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
		return scene;
	}
	
	// records relative x and y co-ordinates.
	 class Delta { double x, y; }
}
