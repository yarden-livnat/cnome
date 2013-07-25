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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.event.ui.CyclistDropEvent;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.View;
import edu.utah.sci.cyclist.ui.components.ViewBase;
import edu.utah.sci.cyclist.ui.components.WorkspacePanelArea;
import edu.utah.sci.cyclist.ui.panels.TitledPanel;
import edu.utah.sci.cyclist.ui.tools.Tool;

public class Workspace extends ViewBase implements View {

	public static final String WORKSPACE_ID = "workspace";
	private static final String PATH_TITLE = "Working Path: ";
	
	private Pane _pane;
	private WorkspacePanelArea _filtersPane;
	private Pane _statusPane;
	private double _savedDivider = 0.9;
	private Label _pathLabel;
	
	private ViewBase _maximizedView = null;
	
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
		super(true);
		build();
	}
	
	private void build() {
		getStyleClass().add("workspace");
		setTitle("Workspace");
		setPadding(new Insets(5, 10, 5, 10));

		_pane = new Pane();
		_filtersPane = new WorkspacePanelArea();
		
		final SplitPane splitPane = new SplitPane();
		splitPane.setId("hiddenSplitter");
		splitPane.setOrientation(Orientation.HORIZONTAL);
		splitPane.getItems().addAll(_pane, _filtersPane);
		splitPane.setDividerPosition(0, 1);
		
		SplitPane.setResizableWithParent(_filtersPane, false);
		
		_filtersPane.visibleProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean visible) {
				if (visible) {
					splitPane.setDividerPosition(0, _savedDivider);
				} else {
					_savedDivider = splitPane.getDividerPositions()[0];
					splitPane.setDividerPosition(0, 1);
				}
				
			}
		});
		
		_pathLabel = new Label(PATH_TITLE);
		addBar(_pathLabel);
		
//		BorderPane borderPane = new BorderPane();

//		_statusPane = new HBox();
//		
//		borderPane.setRight(_filtersPane);
//		borderPane.setBottom(_statusPane);
//		borderPane.setCenter(_pane);
		
		Rectangle clip = new Rectangle(0, 0, 100, 100);
		clip.widthProperty().bind(_pane.widthProperty());
		clip.heightProperty().bind(_pane.heightProperty());
		_pane.setClip(clip);
		_pane.getStyleClass().add("workspace-pane");
		
		//setContent(borderPane, true /* allowMove */);
		setContent(splitPane);
			
		enableDragging(false);
		
		setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				if (event.getTarget() == _pane) {
					DnD.LocalClipboard clipboard = getLocalClipboard();
					if (clipboard.hasContent(DnD.TOOL_FORMAT) || clipboard.hasContent(DnD.TABLE_FORMAT)) 
					{
						event.acceptTransferModes(TransferMode.COPY);
						event.consume();
					} else if (clipboard.hasContent(DnD.FIELD_FORMAT)) {
						event.acceptTransferModes(TransferMode.MOVE);
						event.consume();
					}
					
					
				}
			}
		});	
		
//		setOnDragEntered(new EventHandler<DragEvent>() {
//			public void handle(DragEvent event) {		
////				event.consume();
//			}
//		});
//		
//		setOnDragExited(new EventHandler<DragEvent>() {
//			public void handle(DragEvent event) {
//				// do nothing
////				event.consume();
//			}
//		});
		
		setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				
				boolean status = false;
				DnD.LocalClipboard clipboard = getLocalClipboard();
				if (event.getGestureSource() != this) {
					if ( clipboard.hasContent(DnD.TOOL_FORMAT)) {
						Tool tool =  clipboard.get(DnD.TOOL_FORMAT, Tool.class);
						if (_onToolDrop != null)
							_onToolDrop.call(tool, event.getX()-_pane.getLayoutX(), event.getY()-_pane.getLayoutY());
						status = true;
					} else if (clipboard.hasContent(DnD.TABLE_FORMAT)) {
						Table table = clipboard.get(DnD.TABLE_FORMAT, Table.class);
						if (_onShowTable != null) {
							_onShowTable.call(table, event.getX()-_pane.getLayoutX(), event.getY()-_pane.getLayoutY());
						}
						status = true;
					} else if (clipboard.hasContent(DnD.FIELD_FORMAT)) {
						// accept but don't do anything. 
						// This allows fields to be remove from other places without causing the DnD to fail
						status = true;
					}
				}
				if (status) {
					event.setDropCompleted(status);
					event.consume();
				}
			}
		});
		
		heightProperty().addListener(_resizedHandler);
		widthProperty().addListener(_resizedHandler);
	}
	
	
	private InvalidationListener _resizedHandler = new InvalidationListener() {
		
		@Override
		public void invalidated(Observable observable) {
			if (_maximizedView != null) {
				Bounds b = _pane.getLayoutBounds();
				_maximizedView.setPrefSize(b.getWidth(), b.getHeight());
			}
			
		}
	};
	
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}
	
	public void selectView(View view) {
		ViewBase node = (ViewBase) view;
		node.toFront();
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
					_maximizedView = null;
					
				} else {
					_viewPos.x = view.getTranslateX();
					_viewPos.y = view.getTranslateY();
					_viewPos.width = view.getWidth();
					_viewPos.height = view.getHeight();
		
					view.setTranslateX(0);
					view.setTranslateY(0);
					Bounds b = _pane.getLayoutBounds();
					view.setPrefSize(b.getWidth(), b.getHeight());
					
					view.toFront();
					
					view.setMaximized(true);
					_maximizedView = view;
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
	
	
	public void addPanel(TitledPanel panel) {
		_filtersPane.add(panel);
	}
	
	public void showPanel(TitledPanel panel, boolean show) {
		_filtersPane.show(panel, show);
	}
	
	public void removePanel(TitledPanel panel) {
		_filtersPane.remove(panel);
	}
	
	public void setWorkDirPath(String path){
		_pathLabel.setText(PATH_TITLE+path);
	}
	
	private ViewPos _viewPos = new ViewPos();
}

class ViewPos {
	public double x;
	public double y;
	public double width;
	public double height;
}