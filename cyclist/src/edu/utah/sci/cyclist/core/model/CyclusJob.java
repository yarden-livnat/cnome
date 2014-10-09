package edu.utah.sci.cyclist.core.model;


import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.json.JsonObject;

public class CyclusJob {
	public enum Status { INIT, SUBMITTED, COMPLETED, FAILED, LOADING, READY }
	
	private JsonObject _info;
	private ObjectProperty<Status> _status = new SimpleObjectProperty<>(Status.INIT);
	private ObjectProperty<LocalDateTime> _dateSumbitted = new SimpleObjectProperty<>();
	private ObjectProperty<LocalDateTime> _lastChecked = new SimpleObjectProperty<>();
	private StringProperty _alias = new SimpleStringProperty();
	private String _inputFilePath = "";
	
	
	public CyclusJob(String path) {
		_inputFilePath = path;
	}	
	
	public String getPath() {
		return _inputFilePath;
	}
	
	public void updateInfo(JsonObject info) {
		_info = info;
		System.out.println("set status to "+info.getString("Status"));
		String s = info.getString("Status");
		if ("complete".equals(s)) 
			setStatus(Status.COMPLETED);
		else if ("failed".equals(s))
			setStatus(Status.FAILED);
		else 
			setStatus(Status.SUBMITTED);
	}
	
	public void setInfo(JsonObject info) {
		_info = info;
		if (getAlias() == null)
			setAlias(getId().substring(0,4));
	}
	
	public void setStatus(String s) {
		System.out.println("set status to "+s);;
		if ("complete".equals(s)) 
			setStatus(Status.COMPLETED);
		else if ("failed".equals(s))
			setStatus(Status.FAILED);
		else if ("loading".equals(s))
			setStatus(Status.LOADING);
		else if ("ready".equals(s))
			setStatus(Status.READY);
		else
			setStatus(Status.SUBMITTED);
	}
	
	
	public String getId() {
		return _info.getString("Id");
	}
	
	public StringProperty aliasProperty() {
		return _alias;
	}
	
	public String getAlias() {
		return aliasProperty().get();
	}
	
	public void setAlias(String alias) {
		aliasProperty().set(alias);
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
