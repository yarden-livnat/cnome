package edu.utah.sci.cyclist.event.shared;


public class CyclistEvent {

	private String _type;
	private Object _src;
	private String _destId;
	
	public CyclistEvent(String type, Object src) {
		this(type, src, null);
	}
	public CyclistEvent(String type, Object src, String dest) {
		_type = type;
		_src = src;
		_destId = dest;
	}

	public String getType() {
		return _type;
	}
	
	public Object getSource() {
		return _src;
	}
	
	public String getDestID() {
		return _destId;
	}
	

}
