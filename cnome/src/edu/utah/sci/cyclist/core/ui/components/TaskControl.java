package edu.utah.sci.cyclist.core.ui.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import edu.utah.sci.cyclist.core.Resources;

public class TaskControl extends HBox {

	private ObjectProperty<Task<?>> _taskProperty = new SimpleObjectProperty<>();
	private Task<?> _task;
	private ProgressIndicator _indicator;
	private ImageView _imageView; 
	private Tooltip _msg = new Tooltip();
	
	public TaskControl() {
		build();
	}
	
	public ObjectProperty<Task<?>> taskProperty() {
		return _taskProperty;
	}
		
	public void setTask(Task<?> task) {
		if (_task != null && _task.isRunning()) {
			_task.cancel();
		}
		
		_task = task;
		_imageView.setVisible(false);
		
		if (_task == null) {
			_indicator.visibleProperty().unbind();
			_indicator.setVisible(false);
			_msg.textProperty().unbind();
			_msg.setText("");
		} else {
			_indicator.visibleProperty().bind(_task.runningProperty());		
			_task.setOnFailed(new EventHandler<WorkerStateEvent>() {
				
				@Override
				public void handle(WorkerStateEvent event) {
					_imageView.setVisible(true);
				}
			});
			
			
			_indicator.setOnMouseClicked(new EventHandler<Event>() {
	
				@Override
				public void handle(Event event) {
					System.out.println("Canceling task: "+_task.cancel());				
				}
			});
		
			_msg.textProperty().bind(_task.messageProperty());
		}
		
	}
	
	public void clear() {
		_imageView.setVisible(false);
	}
	
	private void build() {
		_indicator = new ProgressIndicator(-1);
		_indicator.setPrefSize(14, 14);
		_indicator.setVisible(false);
		_indicator.setStyle("-fx-background-color: #fffeec");
		_imageView = new ImageView(Resources.getIcon("error"));
		
		getChildren().addAll(_indicator, _imageView);
		
		Tooltip.install(this, _msg);
		
		_imageView.setVisible(false);
	}
}
