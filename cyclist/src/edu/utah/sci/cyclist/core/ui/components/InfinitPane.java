package edu.utah.sci.cyclist.core.ui.components;

import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class InfinitPane extends BorderPane {

	private ScrollBar _hbar;
	private ScrollBar _vbar;
	private Pane _pane;

	public InfinitPane() {
		super();

		build();
	}

	public Pane getPane() {
		return _pane;
	}

	private void build() {
		_hbar = new ScrollBar();
		_hbar.setMin(0);
		_hbar.setMax(0);
		setBottom(_hbar);

		_vbar = new ScrollBar();
		_vbar.setOrientation(Orientation.VERTICAL);
		_vbar.setMin(0);
		_vbar.setMax(0);
		setRight(_vbar);

		_pane = new Pane();
		setCenter(_pane);

		Rectangle clip = new Rectangle(0, 0, 100, 100);
		clip.widthProperty().bind(_pane.widthProperty());
		clip.heightProperty().bind(_pane.heightProperty());
		_pane.setClip(clip);

		addListeners();
	}

	private void addListeners() {
		_pane.getChildren().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Node> c) {
				while (c.next()) {
					if (c.wasAdded()) {
						for (Node node : c.getAddedSubList()) {
							addListeners((Region)node);
							// force update
							adjust(r->r.getLayoutX(), r->r.getWidth(), _hbar);
							adjust(r->r.getLayoutY(), r->r.getHeight(), _vbar);
						}
					} 
				}	
			}		
		});

		_hbar.valueProperty().addListener(e->{
			for (Node node : _pane.getChildren()) {
				node.setTranslateX(-_hbar.getValue());
			}
		});

		_vbar.valueProperty().addListener(e->{
			for (Node node : _pane.getChildren()) {
				node.setTranslateY(-_vbar.getValue());
			}
		});
	}

	private void addListeners(final Node node) {
		node.layoutXProperty().addListener(new WeakInvalidationListener(hListener));
		node.layoutYProperty().addListener(new WeakInvalidationListener(vListener));
	}

	private InvalidationListener hListener = o->adjust(r->r.getLayoutX(), r->r.getWidth(), _hbar);
	private InvalidationListener vListener = o->adjust(r->r.getLayoutY(), r->r.getHeight(), _vbar);


	private void adjust(Function<Region, Double> pos, Function<Region, Double> len, ScrollBar sbar) {
		double w = len.apply(_pane);
		double min = 0;
		double max = 0;
		for (Node n : _pane.getChildren()) {
			Region r = (Region) n;
			double p = pos.apply(r);
			double l = len.apply(r);
			if (p < min) min = p;
			else if (p+l > w) max = p+l-w;
		}

		boolean change = min != sbar.getMin() || max != sbar.getMax();
		if (change) {
			if (min != sbar.getMin()) sbar.setMin(min);
			if (max != sbar.getMax()) sbar.setMax(max);

			sbar.setVisibleAmount( (sbar.getMax()-sbar.getMin()) * w/(w+sbar.getMax()-sbar.getMin()));
		}
	}
}
