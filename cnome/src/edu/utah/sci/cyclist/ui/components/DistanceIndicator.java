package edu.utah.sci.cyclist.ui.components;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

public class DistanceIndicator extends Group {
	private LineIndicator _from;
	private LineIndicator _to;
	private Line _line;
	private Text _text;
	private StringConverter<Number> _formater;
	
	public DistanceIndicator(LineIndicator from, LineIndicator to, double y, StringConverter<Number> formater) {
		_formater = formater;
		
		_from = from;
		_to = to;
		
		_line = new Line();
		_line.setStroke(Color.DARKGRAY);
		_line.setStrokeWidth(0.5);
		
		_line.startXProperty().bind(_from.xProperty());
		_line.endXProperty().bind(_to.xProperty());
		_line.setStartY(y);
		_line.setEndY(y);
		
		_text = new Text();
		_text.setFont(new Font("Arial", 8));
		updateText();
		
		_from.xProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable arg0) {
				updateText();
			}
		});
		
		getChildren().addAll(_line, _text);
	}
	
	public LineIndicator getFrom() { return _from; }
	
	public LineIndicator getTo() { return _to; }
	
	private void updateText() {
		double dx = Math.abs(_from.getIndicator().getValue() - _to.getIndicator().getValue());
		_text.setText(_formater.toString(dx));
		
		double mid = (_from.xProperty().get()+_to.xProperty().get())/2;
		_text.setX(mid);
		_text.setY(_line.getStartY());
	}
}
