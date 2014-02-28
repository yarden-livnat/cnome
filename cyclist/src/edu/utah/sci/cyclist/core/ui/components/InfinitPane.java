package edu.utah.sci.cyclist.core.ui.components;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class InfinitPane extends BorderPane {

	private ScrollBar _hbar;
	private ScrollBar _vbar;
	
	public InfinitPane() {
		super();
		
		build();
	}

	public void setContent(final Pane node) {
		setCenter(node);
		
		node.getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Node> c) {
				while (c.next()) {
					if (c.wasAdded()) {
						for (Node node : c.getAddedSubList()) {
							addListeners(node);
						}
					} else if (c.wasRemoved()) {
						for (Node node : c.getAddedSubList())
							removeListeners(node);
					}
				}	
			}
			
		});
		
		node.widthProperty().addListener(e->{
			double w = node.getWidth();
			if (w > _hbar.getMax()) _hbar.setMax(w);
			_hbar.setVisibleAmount(w);
		});
		
		node.heightProperty().addListener(e->{
			double h = node.getHeight();
			if (h > _vbar.getMax()) _vbar.setMax(h);
			_vbar.setVisibleAmount(h);
		});
	}
	
	private void build() {
		setStyle("-fx-background-color: lightblue");
		_hbar = new ScrollBar();
		setBottom(_hbar);
		
		_vbar = new ScrollBar();
		_vbar.setOrientation(Orientation.VERTICAL);
		setRight(_vbar);
		
		Pane p = new Pane();
		setCenter(p);
		
		getChildren().addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Node> c) {
				while (c.next()) {
					if (c.wasAdded()) {
						for (Node node : c.getAddedSubList()) {
							addListeners(node);
						}
					} else if (c.wasRemoved()) {
						for (Node node : c.getAddedSubList())
							removeListeners(node);
					}
				}	
			}
			
		});
		
		p.widthProperty().addListener(e->{
			double w = p.getWidth();
			if (w > _hbar.getMax()) _hbar.setMax(w);
			_hbar.setVisibleAmount(w);
		});
		
		p.heightProperty().addListener(e->{
			double h = p.getHeight();
			if (h > _vbar.getMax()) _vbar.setMax(h);
			_vbar.setVisibleAmount(h);
		});
		
//		Rectangle clip = new Rectangle(0, 0, 100, 100);
//		clip.widthProperty().bind(widthProperty().subtract(_vbar.widthProperty()));
//		clip.heightProperty().bind(heightProperty().subtract(_hbar.heightProperty()));
//		_pane.setClip(clip);
		addListeners();
	}
	
	private void addListeners() {
		_hbar.valueProperty().addListener(e->{
			System.out.println("h:"+_hbar.getValue());
			getCenter().setTranslateX(_hbar.getValue());
		});
		
		_vbar.valueProperty().addListener(e->{
			System.out.println("v:"+_vbar.getValue());
			getCenter().setTranslateY(_vbar.getValue());
		});
	}
	
	
	private InvalidationListener hResizeListener = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
			DoubleProperty p = (DoubleProperty) observable;
			double x = p.getValue();
			if (x < _hbar.getMin()) _hbar.setMin(x);
			else if (x > _hbar.getMax()) _hbar.setMax(x);
		}
	};
	
	private InvalidationListener vResizeListener = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
			DoubleProperty p = (DoubleProperty) observable;
			double y = p.getValue();
			if (y < _vbar.getMin()) _vbar.setMin(y);
			else if (y > _vbar.getMax()) _vbar.setMax(y);
		}
	};
	
	private void addListeners(Node node) {
		node.layoutXProperty().addListener(hResizeListener);
		node.layoutYProperty().addListener(hResizeListener);
	}
	
	private void removeListeners(Node node) {
		node.layoutXProperty().removeListener(vResizeListener);
		node.layoutYProperty().removeListener(vResizeListener);
	}

}
