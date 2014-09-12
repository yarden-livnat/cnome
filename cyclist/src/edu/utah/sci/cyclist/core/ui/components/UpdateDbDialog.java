package edu.utah.sci.cyclist.core.ui.components;

import edu.utah.sci.cyclist.Cyclist;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class UpdateDbDialog extends HBox {
	
	private Stage _dialog;
	
	/**
	 * Default constructor
	 **/
	public UpdateDbDialog() {
		createDialog();
	}
	
	/**
	 * Show the wizard * * * //
	 * @param window
	 */
	public void show(Window window) {
		_dialog.initOwner(window);
		_dialog.show();
		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
	}
	
	public void hide(){
		_dialog.hide();
	}
	
	/**
	 * Creates the dialog
	 */
	private void createDialog(){
		_dialog = new Stage();
		_dialog.setTitle("Update DB");
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog) );
		_dialog.centerOnScreen();
//		_dialog.initStyle(StageStyle.UNDECORATED);
		_dialog.initStyle(StageStyle.UTILITY);
	}
	
	private Scene createScene(final Stage dialog){
		
		Label txt = new Label("Please wait while database update is running");
		txt.setFont(new Font(15));
		txt.setMinHeight(75);
		txt.setPrefWidth(400);
		txt.setPadding(new Insets(5));
		txt.setAlignment(Pos.CENTER);
		
		
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.setPadding(new Insets(10));
		hbox.getChildren().add(txt);
		hbox.setAlignment(Pos.CENTER);
		HBox.setHgrow(txt, Priority.ALWAYS);
		
		Scene scene = new Scene(hbox,400,100);
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
		return scene;
	}
}
