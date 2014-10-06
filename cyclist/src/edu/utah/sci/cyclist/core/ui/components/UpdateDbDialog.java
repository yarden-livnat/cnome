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
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public class UpdateDbDialog extends HBox {
	
	private Stage _dialog;
	
	private ObjectProperty<Boolean> _selection = new SimpleObjectProperty<>();
	private TextArea _statusText;
	private RotateTransition _animation;
	private VBox _body;
	private VBox _runningBox;
	
	/**
	 * Default constructor
	 **/
	public UpdateDbDialog(TextArea statusText, RotateTransition animation) {
		createDialog(statusText, animation);
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
	private void createDialog(TextArea statusText, RotateTransition animation){
		_dialog = new Stage();
		_dialog.setTitle("Update DB");
		_dialog.initModality(Modality.WINDOW_MODAL);
		_statusText = statusText;
		_animation = animation;
		_dialog.setScene( createScene(_dialog) );
		_dialog.centerOnScreen();
//		_dialog.initStyle(StageStyle.UNDECORATED);
		_dialog.initStyle(StageStyle.UTILITY);
	}
	
	private Scene createScene(final Stage dialog){
		
		Label txt = new Label("A Database update is required. \n" + 
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
		buttonsBox.getChildren().addAll(cancel,ok);
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
		
		_statusText.setPadding(new Insets(5));
		_statusText.setEditable(false);
		_statusText.setPrefWidth(400);
		_statusText.setPrefHeight(150);
				
		_body = new VBox();
		_body.setSpacing(5);
		_body.setAlignment(Pos.CENTER);
		_body.setPadding(new Insets(5));
		_body.getChildren().addAll(hbox,buttonsBox);
		
		VBox globalBody = new VBox();
		globalBody.getChildren().add(_body);
		
		Scene scene = new Scene(globalBody,300,150);
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
        
        _runningBox = new VBox();
        _runningBox.setSpacing(5);
        _runningBox.setAlignment(Pos.CENTER);
        
        Node icon;
        icon = GlyphRegistry.get(AwesomeIcon.REFRESH, "20px");
        
        _animation.setDuration(Duration.millis(1000)); 
        _animation.setNode(icon);
		_animation.setFromAngle(0);
		_animation.setByAngle(360);
		_animation.setCycleCount(Animation.INDEFINITE);
		_animation.setInterpolator(Interpolator.LINEAR);
		
		HBox animationBox = new HBox();
		animationBox.setPadding(new Insets(5));
		animationBox.setAlignment(Pos.CENTER_LEFT);
		animationBox.getChildren().add(icon);
		
		VBox.setVgrow(_runningBox, Priority.ALWAYS);
		VBox.setVgrow(_statusText, Priority.ALWAYS);
		VBox.setVgrow(_body, Priority.ALWAYS);
      
        _statusText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldVal, String newVal) {
				if(!newVal.isEmpty()){
					if(_body.getChildren().get(0) != _runningBox ){
						_body.getChildren().clear();
						_body.getChildren().add(_runningBox);
						_dialog.setWidth(500);
						_dialog.setHeight(200);
					}
					_statusText.setScrollTop(Double.MAX_VALUE);  //Doesn't work -javafx bug?
					_statusText.appendText(""); //Should fix the javafx bug, but it doesn't.
				}	
			}
		});
        
        _runningBox.getChildren().addAll(animationBox,new Text("Update info:"),_statusText);
		return scene;
	}
}
