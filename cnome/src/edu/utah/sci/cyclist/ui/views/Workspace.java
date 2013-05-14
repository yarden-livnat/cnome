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
package edu.utah.sci.cyclist.ui.views;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.ui.View;
import edu.utah.sci.cyclist.ui.components.ViewBase;
import edu.utah.sci.cyclist.ui.tools.Tool;
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
//				System.out.println("workspace over: \n\tsrc:"+event.getSource()+"\n\ttarget: "+event.getTarget());
				if (event.getTarget() == _pane) {
					DnD.LocalClipboard clipboard = getLocalClipboard();
					if (clipboard.hasContent(DnD.TOOL_FORMAT) || clipboard.hasContent(DnD.TABLE_FORMAT)) 
					{
						event.acceptTransferModes(TransferMode.COPY);
					} 
					
					event.consume();
				}
			}
		});	
		
		setOnDragEntered(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {		
//			System.out.println("workspace enter: \n\tsrc:"+event.getSource()+"\n\ttarget: "+event.getTarget());
//				event.consume();
			}
		});
		
		setOnDragExited(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
//				System.out.println("workspace exit");
				// do nothing
//				event.consume();
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
					} else if (clipboard.hasContent(DnD.TABLE_FORMAT)) {
						Table table = clipboard.get(DnD.TABLE_FORMAT, Table.class);
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