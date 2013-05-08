package edu.utah.sci.cyclist.event.notification;

public class SimpleNotification extends CyclistNotification {

	private String _msg;
	
	public SimpleNotification(String type, String msg) {
		super(type);
		_msg = msg;
	}
	
	public String getMsg() {
		return _msg;
	}

}
