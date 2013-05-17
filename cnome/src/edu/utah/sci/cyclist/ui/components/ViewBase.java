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
 *******************************************************************************/
package edu.utah.sci.cyclist.ui.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleButtonBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.LineBuilder;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.View;

public class ViewBase extends BorderPane implements View {
	
	public static final double EDGE_SIZE = 2;
	
	public enum Edge { TOP, BOTTOM, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, NONE };
	
	private static final Cursor[] _cursor = {
		Cursor.N_RESIZE, Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.W_RESIZE, Cursor.NW_RESIZE, Cursor.NE_RESIZE, Cursor.SW_RESIZE, Cursor.SE_RESIZE, Cursor.DEFAULT
	};
	
	private Button _closeButton;
	private Button _minmaxButton;
	
	private Label _title;
	private ProgressIndicator _indicator;
	private ObjectProperty<EventHandler<ActionEvent>> selectPropery = new SimpleObjectProperty<>();
	
	private boolean _maximized = false;
	private HBox _actionsArea;
	private HBox _dataBar;
	private final Resize resize = new Resize();
	
	class ButtonEntry {
		public ToggleButton button;
		public Boolean remote;
		
		public ButtonEntry(ToggleButton button, Boolean remote) {
			this.button = button;
			this.remote = remote;
		}	
	}
	
	private Map<Table, ButtonEntry> _buttons = new HashMap<>();
	private int _numOfRemotes = 0;
	
	// Actions
	private Closure.V0 _onSelectAction = null;
	private Closure.V1<Table> _onTableDrop = null;
	private Closure.V2<Table, Boolean> _onTableSelectedAction = null;
	
	
	public ViewBase() {	
		super();
//		prefWidth(100);
//		prefHeight(100);
		getStyleClass().add("view");
		
		// Title
		HBox header = HBoxBuilder.create()
				.spacing(2)
				.styleClass("header")
				.alignment(Pos.CENTER_LEFT)
				.children(
					_title = LabelBuilder.create().prefWidth(60).build(),
					_indicator = ProgressIndicatorBuilder.create().progress(-1).maxWidth(8).maxHeight(8).visible(false).build(),
					_dataBar = HBoxBuilder.create()
						.id("databar")
						.styleClass("data-bar")
						.spacing(2)
						.minWidth(20)
						.children(
								LineBuilder.create().startY(0).endY(16).build()
							)
						.build(),
					new Spring(),
					_actionsArea = new HBox(),
					_minmaxButton = ButtonBuilder.create().styleClass("flat-button").graphic(new ImageView(Resources.getIcon("maximize"))).build(),
					_closeButton = ButtonBuilder.create().styleClass("flat-button").graphic(new ImageView(Resources.getIcon("close_view"))).build()
				)
				.build();
		setHeaderListeners(header);
		setDatasourcesListeners();
		
		setTop(header);
		setListeners();
	}
	
	public void setTitle(String title) {
		_title.setText(title);
	}
	
	public void setWaiting(boolean value) {
		_indicator.setVisible(value);
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
	
	public void setOnTableDrop(Closure.V1<Table> action) {
		_onTableDrop = action;
	}
	
	public void setOnTableSelectedAction(Closure.V2<Table, Boolean> action) {
		_onTableSelectedAction = action;
	}
	
	public void setOnSelectAction(Closure.V0 action) {
		_onSelectAction = action;
	}
	
	public DnD.LocalClipboard getLocalClipboard() {
		return DnD.getInstance().getLocalClipboard();
	}
	
	
	@Override
	public void addTable(final Table table, boolean remote, boolean active) {
		ToggleButton button = ToggleButtonBuilder.create()
				.styleClass("flat-toggle-button")
				//.graphic(new ImageView(Resources.getIcon("table")))
				.text(table.getName().substring(0, 1))
				.selected(active)
				.build();
		
		button.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean prevState, Boolean activate) {
				//System.out.println("ViewBase ["+getId()+"]>>> " + table.getName()+"  activate:"+activate);
				if (_onTableSelectedAction != null)
					_onTableSelectedAction.call(table, activate);
				//System.out.println("ViewBase ["+getId()+"]<< " + table.getName()+"  activate");
			}
		});
		
		_buttons.put(table, new ButtonEntry(button, remote));
		
		if (remote) {
			_dataBar.getChildren().add(_numOfRemotes, button);
			_numOfRemotes++;
		} else {
			_dataBar.getChildren().add(button);
		}
	}
	
	@Override
	public void removeTable(Table table) {
		ButtonEntry entry = _buttons.remove(table);
		_dataBar.getChildren().remove(entry.button);
		if (entry.remote)
			_numOfRemotes--;
	}
	
	@Override
	public void selectTable(Table table, boolean value) {
		_buttons.get(table).button.setSelected(value);
	}
	
	/*
	 * Content
	 */
	
	protected void setContent(Parent node) {
		node.setOnMouseMoved(_onMouseMove);
		
		setCenter(node);
		VBox.setVgrow(node, Priority.NEVER);
	}
	
	/*
	 * 
	 */
	protected void addActions(List<ButtonBase> actions) {
		_actionsArea.getChildren().addAll(actions);
	}
	
	protected void setActions(List<ButtonBase> actions) {
		_actionsArea.getChildren().clear();
		addActions(actions);
	}
	
	/*
	 * Listeners
	 */
	private void setHeaderListeners(HBox header) {
		final Delta delta = new Delta();
		
		header.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				delta.x = getTranslateX() - event.getSceneX();
				delta.y = getTranslateY() - event.getSceneY();
				if (_onSelectAction != null)
					_onSelectAction.call();
				
//				if (selectPropery.get() != null)
//					selectPropery.get().handle(new ActionEvent());
			}
		});
		
		header.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
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
			}
			
		});	
		
		EventHandler<MouseEvent> eh = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (_onSelectAction != null)
					_onSelectAction.call();
//				if (selectPropery.get() != null)
//					selectPropery.get().handle(new ActionEvent());
			}
		};
		
		header.setOnMouseClicked(eh);
		setOnMouseClicked(eh);
	}
	
	private void setDatasourcesListeners() {
		_dataBar.setOnDragEntered(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().getContent(DnD.TABLE_FORMAT) != null) {
					event.acceptTransferModes(TransferMode.COPY);
				}
				event.consume();
			}
		});
		
		_dataBar.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().getContent(DnD.TABLE_FORMAT) != null) {
					event.acceptTransferModes(TransferMode.COPY);
				}
				event.consume();
			}
		});
		
		_dataBar.setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				event.consume();
			}
		});
		
		_dataBar.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().getContent(DnD.TABLE_FORMAT) != null) {
					Table table = getLocalClipboard().get(DnD.TABLE_FORMAT, Table.class);
					if (_onTableDrop != null) {
						_onTableDrop.call(table);
					}
				}
				event.setDropCompleted(true);
				event.consume();
			}
		});
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
        		//setTranslateY(resize.y+dy);
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




