package edu.utah.sci.cyclist.view.components;

import java.io.File;
import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceBoxBuilder;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.DirectoryChooser;
import javafx.stage.DirectoryChooserBuilder;
import javafx.stage.Popup;
import javafx.stage.Window;

public class CopyOfWorkspaceWizard extends VBox {
	
	private Popup popup = null;
	private ComboBox<String> cb;
	private String selection = null;
	
	public String getSelection() {
		return selection;
	}
	
	public void showWizard(Window window) {
		if (popup == null) {
			popup = new Popup();
			popup.getContent().add(this);
		}
		popup.show(window);
	}
	
	public void setItems(ObservableList<String> items) {
		cb.setItems(items);
		if (items.size() > 0)
			selection = items.get(0);
	}
	
	public CopyOfWorkspaceWizard() {
		getStyleClass().add("workspace-wizard");
		
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
//							.items(list)
							.value(selection)
							.build(),
						ButtonBuilder.create()
							.text("...")
							.onAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									DirectoryChooser chooser = DirectoryChooserBuilder.create()
											.title("Select directory")
											.build();
									if (cb.getValue() != null) {
										chooser.setInitialDirectory(new File(cb.getValue()));
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
										selection = null;
										popup.hide();
									}
								})
								.build(),
							ButtonBuilder.create()
								.text("Ok")
								.onAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										selection = cb.getValue();
										popup.hide();
									}
								})
								.build()
					)
					.build();
		
		HBox.setHgrow(buttons,  Priority.ALWAYS);
		getChildren().addAll(header, pane, buttons);
	}
	
	
}
