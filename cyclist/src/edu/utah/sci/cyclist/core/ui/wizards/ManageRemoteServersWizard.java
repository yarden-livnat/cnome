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
package edu.utah.sci.cyclist.core.ui.wizards;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Cyclist;

public class ManageRemoteServersWizard extends VBox {
	
	private Stage dialog;
	private ObservableList<String> _selection =   FXCollections.observableArrayList();
	private ObservableList<String> _serversList;
	
	public ObservableList<String> show(Window window) {
		dialog.initOwner(window);
		dialog.show();
		dialog.setX(window.getX() + (window.getWidth() - dialog.getWidth())*0.5);
		dialog.setY(window.getY() + (window.getHeight() - dialog.getHeight())*0.5);
		return _selection;
	}
	
	public ManageRemoteServersWizard(List<String> serversList) {
	
		_serversList = FXCollections.observableArrayList(serversList);
		
		dialog = new Stage();
		dialog.setTitle("Manage remote servers list");
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
		
		ListView<String>  serversList = new ListView<String>();
		VBox.setVgrow(serversList, Priority.ALWAYS);
		serversList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		serversList.setItems(_serversList);
		
		HBox buttons = new HBox();
		buttons.setSpacing(10);
		buttons.setPadding(new Insets(5));
		buttons.setAlignment(Pos.CENTER_RIGHT);
		
		Button cancel = new Button("Cancel");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialog.close();
			}
		});
		
		Button delete = new Button("Delete");
		delete.setDefaultButton(true);
		delete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {	
				_selection.setAll(serversList.getSelectionModel().getSelectedItems());
				dialog.close();
			}
		});
		
		buttons.getChildren().addAll(cancel,delete);
			
		HBox.setHgrow(buttons,  Priority.ALWAYS);
		
		VBox vBox = new VBox();
		vBox.setSpacing(5);
		vBox.setPadding(new Insets(5));
		vBox.getChildren().addAll(new Text("Remote servers list:"), serversList,buttons);
		
		Scene scene = new Scene(vBox,300,250);
		
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
		return scene;
	}
	
	// records relative x and y co-ordinates.
	class Delta { double x, y; }
}
