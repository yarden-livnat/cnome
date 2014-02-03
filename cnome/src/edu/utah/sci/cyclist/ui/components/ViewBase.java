/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved.
 *
 * License for the specific language governing rights and limitations under Permission
 * is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: The above copyright notice 
 * and this permission notice shall be included in all copies  or substantial portions of the Software. 
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR 
 *  A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *     Yarden Livnat  
 *     Kristi Potter
 *******************************************************************************/
package edu.utah.sci.cyclist.ui.components;

import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.ui.View;

public class ViewBase extends BorderPane implements View {
	
	public static final double EDGE_SIZE = 3;
	
	public enum Edge { TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, NONE };
	
	private static final Cursor[] _cursor = {
		Cursor.N_RESIZE, Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.W_RESIZE, Cursor.NW_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.SE_RESIZE, Cursor.DEFAULT
	};
	
	private HBox _header;
	private Spring _spring;
	private HBox _actionsArea;
	private Button _closeButton;
	private Button _minmaxButton;
	
	private Label _title;
	
	private ObjectProperty<EventHandler<ActionEvent>> selectPropery = new SimpleObjectProperty<>();
	private boolean _toplevel;
	private boolean _maximized = false;
	private final Resize resize = new Resize();
	
	private boolean _enableDragging = true;
	
	// Actions
	private Closure.V0 _onSelectAction = null;
	
	/**
	 * Constructor
	 * A non toplevel default constructor
	 */
	public ViewBase() {	
		this(false);
	}
	
	/**
	 * Constructor
	 * @param toplevel specify if this view is a toplevel view that can not be moved or resized
	 */
	public ViewBase(boolean toplevel) {
		super();
		getStyleClass().add("view");
		
		// Header
		_header = new HBox();
		_header.setSpacing(2);
		_header.getStyleClass().add("header");
		_header.setAlignment(Pos.CENTER_LEFT);
		
		_title = new Label();
		_title.setPrefWidth(70);
		
		_actionsArea = new HBox();
		
		_minmaxButton = new Button();
		_minmaxButton.getStyleClass().add("flat-button");
		_minmaxButton.setGraphic(new ImageView(Resources.getIcon("maximize")));
		
		_closeButton = new Button();
		_closeButton.getStyleClass().add("flat-button");
		_closeButton.setGraphic(new ImageView(Resources.getIcon("close_view")));
		
		_spring = new Spring();
		
		_header.getChildren().addAll(
				_title,
				_spring,
				_actionsArea,
				_minmaxButton,
				_closeButton);
		
		if (toplevel) {
			_minmaxButton.setVisible(false);
			_minmaxButton.setManaged(false);
			_closeButton.setVisible(false);
			_closeButton.setManaged(false);
		}
		
		_toplevel = toplevel;
		
		setHeaderListeners();
		
		setTop(_header);
		setListeners();
	}
	
	public ViewBase clone() {
		return null;
	}
	
	public DnD.LocalClipboard getLocalClipboard() {
		return DnD.getInstance().getLocalClipboard();
	}
	
	public HBox getHeader() {
		return _header;
	}
	
	public void setTitle(String title) {
		_title.setText(title);
	}
	
	public boolean isToplevel() {
		return _toplevel;
	}
	
	public boolean isMaximized() {
		return _maximized;
	}
	
	public void setMaximized(boolean value) {
		if (_maximized != value) {
			_maximized = value;
			_minmaxButton.setGraphic(new ImageView(Resources.getIcon(value ? "restore" : "maximize")));
		}
	}
	
	/*
	 * Max/min button
	 */
	public ObjectProperty<EventHandler<ActionEvent>> onMinmaxProperty() {
		return _minmaxButton.onActionProperty();
	}
	
	public EventHandler<ActionEvent> getOnMinmax() {
		return _minmaxButton.getOnAction();
	}
	
	public void setOnMinmax(EventHandler<ActionEvent> handler) {
		_minmaxButton.setOnAction(handler);
	}
	
	/*
	 * Close 
	 */
	public ObjectProperty<EventHandler<ActionEvent>> onCloseProperty() {
		return _closeButton.onActionProperty();
	}
	
	public EventHandler<ActionEvent> getOnClose() {
		return _closeButton.getOnAction();
	}
	
	public void setOnClose(EventHandler<ActionEvent> handler) {
		_closeButton.setOnAction(handler);
	}
	
	/*
	 * Select
	 */
	public ObjectProperty<EventHandler<ActionEvent>> onSelectProperty() {
		return selectPropery;
	}
	
	public EventHandler<ActionEvent> getOnSelect() {
		return selectPropery.get();
	}
	
	public void setOnSelect(EventHandler<ActionEvent> handler) {
		selectPropery.set(handler);
	}	
	
	/*
	 * Actions 
	 */
	public void setOnSelectAction(Closure.V0 action) {
		_onSelectAction = action;
	}

	public void addBar(Node bar) {
		addBar(bar, HPos.LEFT);
	}
	
	public void addBar(Node bar, HPos pos) {
		_header.getChildren().add(_header.getChildren().indexOf(_spring)+(pos == HPos.RIGHT ? 1 : 0), bar);
	}
	
	/*
	 * Content
	 */
	
	protected void setContent(Parent node) {
		setContent(node, true);
	}
	
	protected void setContent(Parent node, boolean canMove) {
		if (canMove)
			node.setOnMouseMoved(_onMouseMove);
		
		setCenter(node);
		VBox.setVgrow(node, Priority.NEVER);
	}
	

	/*
	 * 
	 */
	protected void addActions(List<Node> actions) {
		_actionsArea.getChildren().addAll(actions);
	}
	
	protected void setActions(List<Node> actions) {
		_actionsArea.getChildren().clear();
		addActions(actions);
	}
	
	
	public void select() { 
		if (_onSelectAction != null) 
			_onSelectAction.call();
	}
	
	protected void enableDragging(Boolean value) {
		_enableDragging = value;
	}
	
	/*
	 * Listeners
	 */
	private void setHeaderListeners() {
		final Delta delta = new Delta();
		
		_header.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				delta.x = getTranslateX() - event.getSceneX();
				delta.y = getTranslateY() - event.getSceneY();
				select();
				event.consume();
			}
		});
		
		_header.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!_enableDragging) return;
				
//				Parent parent = view.getParent();
//				double maxX = parent.getLayoutBounds().getMaxX() - getWidth();				
//				double maxY = parent.getLayoutBounds().getMaxY() - getHeight();
//				System.out.println("parent maxY:"+parent.getLayoutBounds().getMaxY()+"  h:"+getHeight());
//				System.out.println("delta.y: "+delta.y+"  event.sy: "+event.getSceneY()+"  maxY:"+maxY);
//				System.out.println("x: "+Math.min(Math.max(0, delta.x + event.getSceneX()), maxX)+"  y:"+Math.min(Math.max(0, delta.y+event.getSceneY()), maxY));
//				setTranslateX(Math.min(Math.max(0, delta.x+event.getSceneX()), maxX)) ;
//				setTranslateY(Math.min(Math.max(0, delta.y+event.getSceneY()), maxY));
				
				setTranslateX(delta.x+event.getSceneX()) ;
				setTranslateY(delta.y+event.getSceneY());
				event.consume();
			}
			
		});	
		
		EventHandler<MouseEvent> eh = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				select();
				event.consume();
			}
		};
		
		_header.setOnMouseClicked(eh);
		setOnMouseClicked(eh);
	}
		
	private void setListeners() {	
		setOnMouseMoved(_onMouseMove);
		setOnMousePressed(_onMousePressed);
		setOnMouseDragged(_onMouseDragged);
		setOnMouseExited(_onMouseExited);
	}
	
	private Edge getEdge(MouseEvent event) {
		double x = event.getX();
		double y = event.getY();
		double right = getWidth() - EDGE_SIZE;
		double bottom = getHeight() - EDGE_SIZE;
		
		Edge edge = Edge.NONE;
		
		if (x < EDGE_SIZE) {
			if (y < EDGE_SIZE) edge = Edge.TOP_LEFT;
			else if (bottom < y) edge = Edge.BOTTOM_LEFT;
			else edge = Edge.LEFT;
		} 
		else if (right < x) {
			if (y < EDGE_SIZE) edge = Edge.TOP_RIGHT;
			else if (bottom < y) edge = Edge.BOTTOM_RIGHT;
			else edge = Edge.RIGHT;			
		}
		else if (y < EDGE_SIZE) edge = Edge.TOP;
		else if (bottom < y) edge = Edge.BOTTOM;
		
		return edge;
	}
	
	private EventHandler<MouseEvent> _onMouseMove = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			Edge edge = getEdge(event);
			Cursor c = _cursor[edge.ordinal()];
			if (getCursor() != c)
				setCursor(c);
		}
	};
	
	private EventHandler<MouseEvent> _onMousePressed = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			resize.edge = getEdge(event);
			if ( resize.edge != Edge.NONE) {
				resize.x = getTranslateX();
				resize.y = getTranslateY();
				resize.width = getWidth();
				resize.height = getHeight();
				resize.sceneX = event.getSceneX();
				resize.sceneY = event.getSceneY() ;
			}
		}
	};
	
	private EventHandler<MouseEvent> _onMouseDragged = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
        	if (resize.edge == Edge.NONE) {
        		return;
        	}
        	
        	setMaximized(false);
        	
        	double dx = resize.sceneX - event.getSceneX();
        	double dy = resize.sceneY - event.getSceneY();
        	
        	// top/bottom
        	if (resize.edge == Edge.TOP || resize.edge == Edge.TOP_LEFT || resize.edge == Edge.TOP_RIGHT) {
        		setTranslateY(resize.y-dy);
        		setPrefHeight(resize.height+dy);
        	} else if (resize.edge == Edge.BOTTOM || resize.edge == Edge.BOTTOM_LEFT || resize.edge == Edge.BOTTOM_RIGHT){
        		//setTranslateY(resize.y+dy);
        		setPrefHeight(resize.height-dy);           		
        	}
        	
        	// left/right
        	if (resize.edge == Edge.TOP_LEFT || resize.edge == Edge.LEFT || resize.edge == Edge.BOTTOM_LEFT) {
        		setTranslateX(resize.x-dx);
        		setPrefWidth(resize.width+dx);
        	} else if (resize.edge == Edge.TOP_RIGHT || resize.edge == Edge.RIGHT || resize.edge == Edge.BOTTOM_RIGHT){
        		setPrefWidth(resize.width-dx);
        	}
        	
        	event.consume();
        }
	};
	
	private EventHandler<MouseEvent> _onMouseExited = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			setCursor(Cursor.DEFAULT);
		}
	};
	
	
	class Delta {
		public double x;
		public double y;
	}

	class Resize {
		public ViewBase.Edge edge;
		public double x;
		public double y;
		public double width;
		public double height;
		public double sceneX;
		public double sceneY;
	}	
}




