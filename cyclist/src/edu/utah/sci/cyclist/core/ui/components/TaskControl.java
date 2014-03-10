package edu.utah.sci.cyclist.core.ui.components;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

public class TaskControl extends HBox {
	static final Logger logger = LogManager.getLogger(TaskControl.class.getName());
	private ObjectProperty<Task<?>> _taskProperty = new SimpleObjectProperty<>();
	private Task<?> _task;
	private ProgressBar _indicator;
	private Button _status;
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
		_status.setVisible(false);
		_status.setManaged(false);
		
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
					_status.setVisible(true);
					_status.setManaged(true);
					String error = _task.getException().getLocalizedMessage();	
					_msg.setText(error.substring(0, error.indexOf("\n")));
					logger.warn("Error:"+_task.getException().getLocalizedMessage());
				}
			});
					
			_indicator.setOnMouseClicked(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					System.out.println("Canceling task: "+_task.cancel());				
				}
			});
		
//			_msg.setText("test");
//			_msg.textProperty().bind(_task.messageProperty());
		}
		
	}
	
	public void clear() {
		_status.setVisible(false);
		_status.setManaged(false);
	}
	
	private void build() {
		getStyleClass().add("actions-area");
		_indicator = new ProgressBar(-1);
		_indicator.setPrefSize(50, 12);
		_indicator.setVisible(false);
		_status = new Button();
		_status.getStyleClass().add("flat-button");
		_status.setGraphic(GlyphRegistry.get(AwesomeIcon.WARNING, "12px"));
		getChildren().addAll(_indicator, _status);
				
		_status.setVisible(false);
		_status.setManaged(false);
		
		Tooltip.install(_status, _msg);
	}
}
