package edu.utah.sci.cyclist.neup.ui.views.flow;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.shape.CubicCurve;
import javafx.scene.text.Text;
import edu.utah.sci.cyclist.neup.model.Transaction;

public class Connector extends Group {
	public static final int CTRL_OFFSET = 40;
	
	private FacilityNode _from;
	private FacilityNode _to;
	private ObservableList<Transaction> _transactions;

	private String _units = "kg"; 

	private CubicCurve _curve;
	private Text _text;
	
	/*
	 * Listeners
	 */
	private InvalidationListener listener = new InvalidationListener() {		
		@Override
		public void invalidated(Observable observable) {
			setVisible(_transactions.size() > 0);
			computeAmount();
		}
	};
	
	public Connector(FacilityNode from, FacilityNode to, ObservableList<Transaction> transactions) {
		super();
		getStyleClass().add("connector");
		
		_from = from;
		_to = to;
		
		_transactions = transactions;
		
		build();
		addListeners();
	}
	
	public FacilityNode getFrom() {
		return _from;
	}
	
	public FacilityNode getTo() {
		return _to;
	}
	
	public void release() {
		_curve.startXProperty().unbind();
		_curve.startYProperty().unbind();
		_curve.endXProperty().unbind();
		_curve.endYProperty().unbind();
		_transactions.removeListener(listener);
	}
	
	private void build() {
		setStyle("-fx-backgound-cool: lightred");
		_curve = new CubicCurve();
		_curve.getStyleClass().add("connector");
		
		FacilityNode src = _from.isSRC() ? _from : _to;
		FacilityNode dest = _from.isSRC() ? _to : _from;
		
		
		_curve.startXProperty().bind(src.anchorXProperty().add(src.getParent().translateXProperty()));
		_curve.startYProperty().bind(src.anchorYProperty().add(src.getParent().translateYProperty()));

		_curve.controlX1Property().bind(_curve.startXProperty().add(CTRL_OFFSET));
		_curve.controlY1Property().bind(_curve.startYProperty());

		_curve.controlX2Property().bind(_curve.endXProperty().subtract(CTRL_OFFSET));
		_curve.controlY2Property().bind(_curve.endYProperty());

		_curve.endXProperty().bind(dest.anchorXProperty().add(dest.getParent().translateXProperty()));
		_curve.endYProperty().bind(dest.anchorYProperty().add(dest.getParent().translateYProperty()));	
		
		// text
		_text = new Text();
		_text.getStyleClass().add("connector-text");

		DoubleBinding px = new DoubleBinding() {
			{
				super.bind(_curve.startXProperty(), _curve.endXProperty(), _text.boundsInLocalProperty());
			}
			@Override
			protected double computeValue() {
				return (_curve.getStartX()+_curve.getEndX()-_text.getBoundsInLocal().getWidth())/2;
			}
		};

		DoubleBinding py = new DoubleBinding() {
			{
				super.bind(_curve.startYProperty(),_curve.endYProperty(), _text.boundsInLocalProperty());
			}
			@Override
			protected double computeValue() {
				return (_curve.getStartY()+_curve.getEndY()-_text.getBoundsInLocal().getHeight())/2;
			}
		};

		DoubleBinding r = new DoubleBinding() {
			{
				super.bind(_curve.startXProperty(), _curve.endXProperty(), _curve.startYProperty(), _curve.endYProperty());
			}
			@Override
			protected double computeValue() {
				return Math.atan2(_curve.getEndY()-_curve.getStartY(), _curve.getEndX()-_curve.getStartX()-CTRL_OFFSET) * 180/Math.PI;
			}
		};
		
		_text.translateXProperty().bind(px);
		_text.translateYProperty().bind(py);
		_text.rotateProperty().bind(r);
		
		getChildren().addAll(_curve, _text);
		
		computeAmount();
	}
	
	private void computeAmount() {
		if (_transactions.isEmpty()) return;
		
		double total = 0;
		for (Transaction t : _transactions) {
			total += t.quantity*t.fraction;
		}
		_text.setText(String.format("%.2e%s", total, _units));
//		System.out.println("connector: len:"+_transactions.size()+"   text:"+_text.getText());
	}
	
	
	private void addListeners() {
		_transactions.addListener(listener);
	}

	
}
