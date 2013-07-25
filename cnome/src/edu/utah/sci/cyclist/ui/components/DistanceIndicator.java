package edu.utah.sci.cyclist.ui.components;

import javafx.scene.Node;
import javafx.scene.shape.Line;

public class DistanceIndicator {
	private LineIndicator _from;
	private LineIndicator _to;
	private Line _line;
	
	public DistanceIndicator(LineIndicator from, LineIndicator to) {
		_from = from;
		_to = to;
		
		_line = new Line();
	}
	
	public LineIndicator getFrom() { return _from; }
	
	public LineIndicator getTo() { return _to; }
	
	public Node getNode() { return _line; }
}
