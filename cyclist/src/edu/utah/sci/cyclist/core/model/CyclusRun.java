package edu.utah.sci.cyclist.core.model;


import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.json.JsonObject;

public class CyclusRun {
	public enum Status { INIT, SUMITTED, COMPLETED, FAILED }
	
	private JsonObject _info;
	private String _id;
	private ObjectProperty<Status> _status = new SimpleObjectProperty<>(Status.INIT);
	private ObjectProperty<LocalDateTime> _dateSumbitted = new SimpleObjectProperty<>();
	private ObjectProperty<LocalDateTime> _lastChecked = new SimpleObjectProperty<>();
	private String _inputFilePath = "";
	
	
	public CyclusRun(String path) {
		_inputFilePath = path;
	}	
	
	public String getPath() {
		return _inputFilePath;
	}
	
	public void updateInfo(JsonObject info) {
		setInfo(info);
	}
	
	public void setInfo(JsonObject info) {
		_info = info;
		System.out.println("set status to "+info.getString("Status"));
		String s = info.getString("Status");
		if ("complete".equals(s)) 
			setStatus(Status.COMPLETED);
		else if ("failed".equals(s))
			setStatus(Status.FAILED);
		else 
			setStatus(Status.SUMITTED);
	}
	
	public String getId() {
		return _info.getString("Id");
	}
	
	public ObjectProperty<Status> statusProperty() {
		return _status;
	}
	
	public Status getStatus() {
		return statusProperty().get();
	}
	
	public void setStatus(Status status) {
		statusProperty().set(status);
	}
	
	public ObjectProperty<LocalDateTime> dateSubmittedProperty() {
		return _dateSumbitted;
	}
	
	public LocalDateTime getDateSubmitted() {
		return dateSubmittedProperty().get();
	}
	
	public void setDateSubmitted(LocalDateTime time) {
		dateSubmittedProperty().set(time);
	}
		
	public ObjectProperty<LocalDateTime> lastCheckedProperty() {
		return _lastChecked;
	}
	
	public LocalDateTime getLastChecked() {
		return lastCheckedProperty().get();
	}
	
	public void setLastChecked(LocalDateTime time) {
		lastCheckedProperty().set(time);
	}
	
	
}
