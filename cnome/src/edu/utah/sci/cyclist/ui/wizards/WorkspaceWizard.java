/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved.
 *
 * License for the specific language governing rights and limitations under Permission
 * is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: The above copyright notice 
 * and this permission notice shall be included in all copies  or substantial portions of the Software. 
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR 
 *  A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *     Yarden Livnat  
 *     Kristin Potter
 *******************************************************************************/
package edu.utah.sci.cyclist.ui.wizards;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Cyclist;

public class WorkspaceWizard extends VBox {
	
	private Stage dialog;
	private ComboBox<String> cb;
	private String current = null;
	private ObjectProperty<ObservableList<String>> _selection = new SimpleObjectProperty<ObservableList<String>>(FXCollections.<String>observableArrayList());
	private ObservableList<String> _selectionList = FXCollections.observableArrayList();
	private int _selectedAppIndex = 0;
	
	
	public ObservableList<String> show(Window window) {
		dialog.initOwner(window);
		dialog.show();
		dialog.setX(window.getX() + (window.getWidth() - dialog.getWidth())*0.5);
		dialog.setY(window.getY() + (window.getHeight() - dialog.getHeight())*0.5);
		_selection.set(_selectionList);
		return _selectionList;
	}
	
	public void setItems(ObservableList<String> items) {
		cb.setItems(items);
		if (items.size() > 0) {
			current = items.get(0);
			cb.setValue(current);
		}
	}
	
	public void setSelectedItem(int index)
	{
		_selectedAppIndex =  index;
	}
	
	public WorkspaceWizard() {
	
		/*dialog = StageBuilder.create()
				.title("Select Workspace Directory")
				.maxWidth(300).minWidth(300)
				.maxHeight(120).minHeight(110)
				.build();*/
		
		dialog = new Stage();
		dialog.setTitle("Select Workspace Directory");
		dialog.setMaxWidth(300);
		dialog.setMinWidth(300);
		dialog.setMaxHeight(120);
		dialog.setMinHeight(120);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.setScene( createScene(dialog) );
	    dialog.centerOnScreen();
		
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
		//Text header = TextBuilder.create()
		//		.id("workspace-wizard-header")
		//		.text("Select workspace directory")
		//		.build();
		
		HBox pane = new HBox();  //set spacing;
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(5));
		pane.setSpacing(10);
		
		Button btn = new Button("...");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("Select directory");
				
				if (cb.getValue() != null && cb.getValue() != "") {
					File dir = new File(cb.getValue());
					if(dir.isDirectory())
						chooser.setInitialDirectory(dir);									
				}
				File dir = chooser.showDialog(null);
				if (dir != null && !directoryExists(dir.getAbsolutePath())) { 
					cb.getItems().add(0, dir.getAbsolutePath());
					cb.setValue(cb.getItems().get(0));
					_selectionList.clear();
					_selectionList.add(cb.getValue());
				}
			}
		});
		
		pane.getChildren().addAll(cb = new ComboBox<>(),btn);
		
		/*HBox pane = HBoxBuilder.create()		
				.alignment(Pos.CENTER)
				.padding(new Insets(5))
				.spacing(10)
				.children(
//						cb = ComboBoxBuilder.create(String.class) // Java 8
//						cb = ComboBoxBuilder.<String>create()     // Java 7
//						.prefWidth(200)
//						.editable(true)
//						.value(current)
//						.build(),
						cb = new ComboBox<>(),
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
								if (dir != null && !directoryExists(dir.getAbsolutePath())) { 
									cb.getItems().add(0, dir.getAbsolutePath());
									cb.setValue(cb.getItems().get(0));
									_selectionList.clear();
									_selectionList.add(cb.getValue());
								}
							}
						})
						.build()
						)
						.build();*/
		HBox.setHgrow(cb, Priority.ALWAYS);
		
		HBox buttons = new HBox();
		buttons.setId("worksapce-wizard-buttons");
		buttons.setSpacing(10);
		buttons.setPadding(new Insets(5));
		buttons.setAlignment(Pos.CENTER_RIGHT);
		
		Button cancel = new Button("Cancel");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				current = null;
				dialog.close();
			}
		});
		
		Button ok = new Button("Ok");
		ok.setDefaultButton(true);
		ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(_selectionList.size() > 0 ){
					//It means the "..." button was pressed before the o.k button - new value should be added
					// to the general config file.
					_selectionList.add(cb.getValue());
				} else{
					//If an existing directory was chosen - add an empty string to the list.
					//It means -no need to add the value to the general config file, just mark it as last chosen.
					_selectionList.add("");
					_selectionList.add(cb.getValue());
				}
					
				dialog.close();
			}
		});
		
		buttons.getChildren().addAll(cancel,ok);
		
		
		/*HBox buttons = HBoxBuilder.create()
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
										//It means the "..." button was pressed before the o.k button - new value should be added
										// to the general config file.
										if(_selectionList.size() > 0 ){
											_selectionList.add(cb.getValue());
										} else{
											//If an existing directory was chosen - add an empty string to the list.
											//It means -no need to add the value to the general config file, just mark it as last chosen.
											_selectionList.add("");
											_selectionList.add(cb.getValue());
										}
											
										dialog.close();
									}
								})
								.build()
					)
					.build();*/
		
		HBox.setHgrow(buttons,  Priority.ALWAYS);
		
		VBox vBox = new VBox();
		vBox.setSpacing(5);
		vBox.setPadding(new Insets(5));
		vBox.setId("workspace-wizard");
		vBox.getChildren().addAll(pane,buttons);
		
		Scene scene = new Scene(vBox);
				/*VBoxBuilder.create()
					.spacing(5)
					.padding(new Insets(5))
					.id("workspace-wizard")
					//.children(header, pane, buttons)
					.children(pane, buttons)
					.build()
				);*/
		
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
		return scene;
	}
	
	/**
	 * Checks if the new chosen directory already exists in the combo box .
	 */
	private Boolean directoryExists(String directory)
	{
		ObservableList<String> list = cb.getItems();
		for(String item:list){
			if(item.equals(directory)){
				JFrame jFrame = new JFrame();
				jFrame.setAlwaysOnTop(true);
				JOptionPane.showMessageDialog(jFrame, "Directory already exists, please choose it from the list");
				return true;
			}
		}
		return false;
	}
	
	// records relative x and y co-ordinates.
	class Delta { double x, y; }
}
