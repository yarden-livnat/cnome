package edu.utah.sci.cyclist.core.ui.components;

import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public class UpdateDbDialog extends HBox {
	
	private Stage _dialog;
	
	private ObjectProperty<Boolean> _selection = new SimpleObjectProperty<>();
	private Label _statusLabel;
	private RotateTransition _animation;
	
	/**
	 * Default constructor
	 **/
	public UpdateDbDialog(Label statusLbl, RotateTransition animation) {
		createDialog(statusLbl, animation);
	}
	
	/**
	 * Show the wizard * * * //
	 * @param window
	 */
	public ObjectProperty<Boolean> show(Window window) {
		_dialog.initOwner(window);
		_dialog.show();
		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
		return _selection;
	}
	
	public void hide(){
		_dialog.hide();
	}
	
	/**
	 * Creates the dialog
	 */
	private void createDialog(Label statusLbl, RotateTransition animation){
		_dialog = new Stage();
		_dialog.setTitle("Update DB");
		_dialog.initModality(Modality.WINDOW_MODAL);
		_statusLabel = statusLbl;
		_animation = animation;
		_dialog.setScene( createScene(_dialog) );
		_dialog.centerOnScreen();
//		_dialog.initStyle(StageStyle.UNDECORATED);
		_dialog.initStyle(StageStyle.UTILITY);
	}
	
	private Scene createScene(final Stage dialog){
		
		Label txt = new Label("A Database update may be required. \n" + 
							  "This operation can take a long time. \n" +
							  "Continue?");
//		txt.setFont(new Font(15));
		txt.setMinHeight(75);
		txt.setPrefWidth(250);
		txt.setPadding(new Insets(5));
		txt.setAlignment(Pos.CENTER);
		
		
		Button ok = new Button("OK");
		Button cancel = new Button("Cancel");
		
		HBox buttonsBox = new HBox();
		buttonsBox.setSpacing(10);
		buttonsBox.setPadding(new Insets(5));
		buttonsBox.getChildren().addAll(ok,cancel);
		buttonsBox.setAlignment(Pos.CENTER_RIGHT);
		
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				_selection.set(false);
				dialog.hide();
			};
		});
		
		ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				_selection.set(true);
			};
		});
			
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.setPadding(new Insets(5));
		hbox.getChildren().add(txt);
		hbox.setAlignment(Pos.CENTER);
		HBox.setHgrow(txt, Priority.ALWAYS);
		
		_statusLabel.setPadding(new Insets(5));
		_statusLabel.setAlignment(Pos.CENTER);
		_statusLabel.setPrefWidth(350);
//		_statusLabel.setFont(new Font(15));
		
		
		VBox body = new VBox();
		body.setSpacing(5);
		body.setAlignment(Pos.CENTER);
		body.setPadding(new Insets(5));
		body.getChildren().addAll(hbox,buttonsBox);
		
//		Scene scene = new Scene(body,400,100);
		Scene scene = new Scene(body,400,150);
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
        
        Node icon;
        icon = GlyphRegistry.get(AwesomeIcon.REFRESH, "20px");
        
        _animation.setDuration(Duration.millis(1000)); 
        _animation.setNode(icon);
		_animation.setFromAngle(0);
		_animation.setByAngle(360);
		_animation.setCycleCount(Animation.INDEFINITE);
		_animation.setInterpolator(Interpolator.LINEAR);
      
        _statusLabel.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldVal, String newVal) {
				if(!newVal.isEmpty()){
					if(body.getChildren().get(0) != _statusLabel ){
						body.getChildren().removeAll(body.getChildren());
						body.getChildren().addAll(_statusLabel, icon);
//						animation.play();
					}
					if(newVal.equals("Done!")){
//						animation.stop();
//						body.getChildren().remove(icon);
					}
					
				}
			}
		});
		return scene;
	}
}
