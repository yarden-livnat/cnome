/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.view.components;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.view.View;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.event.ui.CyclistDropEvent;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.tool.Tool;

public class Workspace extends ViewBase implements View {

	public static final String WORKSPACE_ID = "workspace";
	
	private Pane _pane;
	private Closure.V3<Tool, Double, Double> _onToolDrop = null;
	private Closure.V3<Table, Double, Double> _onShowTable = null;
	
	public void setOnToolDrop(Closure.V3<Tool, Double, Double> action) {
		_onToolDrop = action;
	}
	
	public void setOnShowTable(Closure.V3<Table, Double, Double> action) {
		_onShowTable = action;
	}
	
	// -- Properties
	
	// OnToolDrop
	private ObjectProperty<EventHandler<CyclistDropEvent>> _propertyOnToolDrop = new SimpleObjectProperty<EventHandler<CyclistDropEvent>>();
	
	public final ObjectProperty<EventHandler<CyclistDropEvent>> onToolDropPropery() {
		return _propertyOnToolDrop;
	}
	
	public final void setOnToolDrop(EventHandler<CyclistDropEvent> eventHandler) {
		_propertyOnToolDrop.set(eventHandler);
	}
	
	public final EventHandler<CyclistDropEvent> getOnToolDrop() {
		return _propertyOnToolDrop.get();
	}
	
	
	/**
	 * Constructor
	 */
	public Workspace() {
		super();
		getStyleClass().add("workspace");
		setTitle("Workspace");
		setPadding(new Insets(5, 10, 5, 10));

		_pane = new Pane();
		_pane.getStyleClass().add("workspace-pane");
		
		setContent(_pane);
				
		setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				DnD.LocalClipboard clipboard = getLocalClipboard();
				if (clipboard.hasContent(DnD.TOOL_FORMAT) || clipboard.hasContent(DnD.DATA_SOURCE_FORMAT)) 
				{
					event.acceptTransferModes(TransferMode.COPY);
				} 
				
				event.consume();
			}
		});	
		
		setOnDragEntered(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {		
//				System.out.println("workspace enter: \n\tsrc:"+event.getSource()+"\n\ttarget: "+event.getTarget());
				event.consume();
			}
		});
		
		setOnDragExited(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
//				System.out.println("worspace exit");
				// do nothing
				event.consume();
			}
		});
		
		setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				DnD.LocalClipboard clipboard = getLocalClipboard();
				if (event.getGestureSource() != this) {
					if ( clipboard.hasContent(DnD.TOOL_FORMAT)) {
						Tool tool =  clipboard.get(DnD.TOOL_FORMAT, Tool.class);
						if (_onToolDrop != null)
							_onToolDrop.call(tool, event.getX(), event.getY());
					} else if (clipboard.hasContent(DnD.DATA_SOURCE_FORMAT)) {
						Table table = clipboard.get(DnD.DATA_SOURCE_FORMAT, Table.class);
						if (_onShowTable != null) {
							_onShowTable.call(table, event.getX(), event.getY());
						}
					}
				}
				event.setDropCompleted(true);
				
				event.consume();
			}
		});
		
	}
	
	
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}
	
	public void addView(final ViewBase view) {
		_pane.getChildren().add(view);
		
		view.setOnSelect(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {	
				view.toFront();
			}
		});
		
		view.setOnMinmax(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (view.isMaximized()) {
					view.setTranslateX(_viewPos.x);
					view.setTranslateY(_viewPos.y);
					view.setPrefSize(_viewPos.width, _viewPos.height);
					
					view.setMaximized(false);
					
				} else {
					_viewPos.x = view.getTranslateX();
					_viewPos.y = view.getTranslateY();
					_viewPos.width = view.getWidth();
					_viewPos.height = view.getHeight();
		
					view.setTranslateX(0);
					view.setTranslateY(0);
					Bounds b = getLayoutBounds();
					view.setPrefSize(b.getWidth(), b.getHeight());
					
					view.toFront();
					
					view.setMaximized(true);
				}
					
			}
		});
		
	}
	
	/**
	 * will be called by the workspace presenter
	 * @param view
	 */
	public void removeView(ViewBase view) {
		view.setOnSelect(null);
		_pane.getChildren().remove(view);
	}
	
	private ViewPos _viewPos = new ViewPos();
}

class ViewPos {
	public double x;
	public double y;
	public double width;
	public double height;
}