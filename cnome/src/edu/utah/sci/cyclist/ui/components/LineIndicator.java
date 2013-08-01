package edu.utah.sci.cyclist.ui.components;

import org.mo.closure.v1.Closure;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.model.Indicator;

public class LineIndicator extends Group {
	private Indicator _indicator;
	private ObjectProperty<XYChart<?,?>> _chartProperty = new SimpleObjectProperty<>();
	private Pane _pane;
	private Line _line;
	private double _dragX;
	private Closure.V1<LineIndicator> _onRemoveAction = null;
	
	public Indicator getIndicator() {
		return _indicator;
	}
	
	public Node getNode() {
		return _line;
	}
	
	public DoubleProperty xProperty() {
		return _line.startXProperty();
	}
	
	public ObjectProperty<XYChart<?,?>> chartProperty() {
		return _chartProperty;
	}
	
	public XYChart<?,?> getChart() {
		return _chartProperty.get();
	}
	
	public void setOnRemoveAction(Closure.V1<LineIndicator> action) {
		_onRemoveAction = action;
	}
	
	public LineIndicator(final Indicator indicator, Pane parent) {
		_indicator = indicator;
		_pane = parent;
		
		_line = new Line();
		_line.setStrokeWidth(2);
		_line.setStroke(Color.GRAY);
		parent.getChildren().add(_line);
		
		indicator.hoverProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean newValue) {
				_line.setStroke(newValue ? Color.BLUE : Color.GRAY);	
			}
		});
		
		_line.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				indicator.setHover(true);
			}
		});
		
		_line.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				indicator.setHover(false);
			}
		});
			
		_line.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				indicator.setSelected(!indicator.getSelected());
			}
		});
		
		_line.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.isSecondaryButtonDown() && _onRemoveAction != null) {
					_onRemoveAction.call(LineIndicator.this);
				}
								
			}
		});
		
		_line.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isShiftDown()) {
					Dragboard db = _line.startDragAndDrop(TransferMode.COPY);
					
					DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
					clipboard.put(DnD.INDICATOR_FORMAT, Indicator.class, indicator);
					ClipboardContent content = new ClipboardContent();
					content.putString("_indicator");
					content.putImage(Resources.getIcon("_indicator"));
					db.setContent(content);
				} else { 
					_dragX = event.getX();
					_line.setOnMouseDragged(_dragLineHandler);
					indicator.setSelected(true);
				}
			}
		});
		
		
		indicator.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number prevValue, Number newValue) {
				setXAxis(newValue.doubleValue());	
			}
		});
	
		_chartProperty.addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable arg0) {
				moveLine();
			}
		});
		
		parent.widthProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable arg0) {
				moveLine();	
			}
		});
	}
	
	private void moveLine() {
		if (getChart() != null) {
			setXAxis(_indicator.getValue());
			setYAxis();
			_line.setVisible(true);
		} else {
			_line.setVisible(false);
		}
	}
	
	EventHandler<MouseEvent> _dragLineHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (_dragX != event.getX()) {
				Axis<?> axis = getChart().getXAxis();
				
				if (axis instanceof NumberAxis) {
					NumberAxis x = (NumberAxis) axis;
					Number v = x.getValueForDisplay(x.sceneToLocal(event.getSceneX(), event.getSceneY()).getX());
					_indicator.setValue(v.doubleValue());
				}
				
				_dragX = event.getX();
			}
		}
	};
	
	
	private void setXAxis(double value) {
		if (getChart() == null) return;
		
		Axis<?> axis = getChart().getXAxis();
		if (axis != null && axis instanceof NumberAxis) {
			NumberAxis x = (NumberAxis) axis;
			if (value < x.getLowerBound()) {
				value = x.getLowerBound();
			} else if (x.getUpperBound() < value) {
				value = x.getUpperBound();
			}
			double pos = x.getDisplayPosition(value);
			
			 Point2D p = _pane.sceneToLocal(x.localToScene(pos, 0));
			
			_line.setStartX(p.getX());
			_line.setEndX(p.getX());
		}
	}
		
	private void setYAxis() {
		if (getChart() == null) return;
		Axis<?> y = getChart().getYAxis();
		if (y == null) return;
		
		Bounds b = y.localToScene(y.getBoundsInLocal());
		Bounds gb = _pane.sceneToLocal(b);
		
		_line.setStartY(gb.getMinY());
		_line.setEndY(gb.getMaxY());
	}
}

