package edu.utah.sci.cyclist.ui.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import edu.utah.sci.cyclist.Resources;

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
		HBoxBuilder.create()
			.children(
				_indicator = ProgressIndicatorBuilder.create()
					.progress(-1)
					.maxWidth(20)
					.maxHeight(20)
					.visible(false)
					.build(),
				_imageView = new ImageView(Resources.getIcon("error"))
				)
			.applyTo(this);
		
		Tooltip.install(this, _msg);
		
		_imageView.setVisible(false);
	}
}
