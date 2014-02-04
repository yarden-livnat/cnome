package edu.utah.sci.cyclist.ui.views;

import java.util.ArrayList;

import org.mo.closure.v1.Closure;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.event.dnd.DnDSource;
import edu.utah.sci.cyclist.model.DataType;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.components.CyclistViewBase;
import edu.utah.sci.cyclist.ui.components.MaterialFlowPane;

public class FlowView extends CyclistViewBase {
	public static final String ID = "flow-view";
	public static final String TITLE = "Flow";
	
	private MaterialFlowPane _pane;
	private Line _l1;
	private Line _l2;
	private ArrayList<Node> _nodes = new ArrayList<>();

	
	public FlowView() {
		super();
		build();
	}

	private void build() {
		setTitle(TITLE);
		getStyleClass().add("flow-view");
		this.setPrefWidth(400);
		this.setPrefHeight(300);
		// padding? spaceing?
		
		_l1 = new Line();
		_l1.getStyleClass().add("flow-line");
		_l2 = new Line();
		_l2.getStyleClass().add("flow-line");
		
		// components
		MenuBar menubar = createMenubar();
		
		_pane = new MaterialFlowPane();
		_pane.getChildren().addAll(_l1, _l2);
		_pane.setPrefSize(400, 300);
		
		VBox vbox = new VBox();
		vbox.getChildren().addAll(menubar, _pane);
		
		VBox.setVgrow(_pane, Priority.ALWAYS);
	
		setContent(vbox);
		
		addListeners();
	}
	
	
	private MenuBar createMenubar() {
		MenuBar menubar = new MenuBar();

		MenuItem item = new MenuItem("item");
		
		Menu menu = new Menu("Menu");
		menu.getItems().addAll(item);
		
		menubar.getMenus().add(menu);
		
		return menubar;
	}
	
	
	private void onWidthChanged() {
		double w = _pane.getWidth();
		
		_l1.setStartX(w/3);
		_l1.setEndX(w/3);
		
		_l2.setStartX(2*w/3);
		_l2.setEndX(2*w/3);
		
		for (Node node : _nodes) {
			node.setTranslateX(node.line.getStartX()-node.getWidth()/2);
		}
	}
	
	private void onHeightChanged() {
		System.out.println("height changed");
		double h = _pane.getHeight();
		
		_l1.setStartY(30);
		_l1.setEndY(h-30);
		
		_l2.setStartY(30);
		_l2.setEndY(h-30);
	}
	
	
	private Line _targetLine = null;
	
	private void setTargetLine(Line l) {
		if (_targetLine == l) return;
		if (_targetLine != null) {
			_targetLine.getStyleClass().remove("line-hover");
		}
		if (l != null)  {
			l.getStyleClass().add("line-hover");
		}
		_targetLine = l;
	}
	
	private void addListeners() {
		
		_pane.widthProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				onWidthChanged();
			}	
		});
		
		_pane.heightProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				onHeightChanged();
			}	
		});
		
		// DnD
		_pane.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				boolean accept = false;
				DnD.LocalClipboard clipboard = getLocalClipboard();
				DnDSource source = clipboard.get(DnD.SOURCE_FORMAT, DnDSource.class);
				if (source != null && source == DnDSource.VALUE) {
					if (clipboard.hasContent(DnD.VALUE_FORMAT)) {
						// accept if it near one of the lines
						double x = event.getX();
						if (Math.abs(x - _l1.getStartX()) < 10) {
							setTargetLine(_l1);
							accept = true;							
						} else if (Math.abs(x - _l2.getStartX()) < 10) {
							setTargetLine(_l2);
							accept = true;
						} else {
							setTargetLine(null);
						}
						if (accept) {
							event.acceptTransferModes(TransferMode.COPY);
							event.consume();
						}
					} 
				}
			}
		});
		
		_pane.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				boolean accept = false;
				DnD.LocalClipboard clipboard = getLocalClipboard();
				
				Object value = clipboard.get(DnD.VALUE_FORMAT, Object.class);
				Field field = clipboard.get(DnD.FIELD_FORMAT, Field.class);
				Table table = clipboard.get(DnD.TABLE_FORMAT, Table.class);
				
				addNode(value, field, table, _targetLine, event.getY());
				
				setTargetLine(null);
				
				accept = true;
				if (accept) {
					event.setDropCompleted(accept);
					event.consume();
				}
			}
		});

	}
	
	private void addNode(Object value, Field field, Table table, Line l, double y) {
		Node node = new Node(value, field, table, l);
		_nodes.add(node);
		_pane.getChildren().add(node);
		node.setTranslateX(l.getStartX() - 30); //node.getWidth()/2);
		node.setTranslateY(y);
	}
}

class Node extends Pane {
	public Object value;
	public Field field;
	public Table table;
	public Line line;
	private double mx;
	private double my;
	private VBox _vbox;
	private boolean _selected = false;
	
	public Node(Object value, Field field, Table table, Line line) {
		
		
		this.value = value;
		this.field = field;
		this.table = table;
		this.line = line;
		
		build();
		setListeners();
	}	
	
	private void build() {
		_vbox = new VBox();
		_vbox.getStyleClass().add("flow-node");
		if (field.getDataType().getType() != DataType.Type.TEXT) {
			Text header = new Text(field.getName()+":");
			header.getStyleClass().add("node-header");
			_vbox.getChildren().add(header);
		}
			
		Text body = new Text(value.toString());
		body.getStyleClass().add("node-body");
		_vbox.getChildren().add(body);
	
		getChildren().add(_vbox);
	}
	
	private void setListeners() {
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				_selected = !_selected;	
				if (_selected) {
					_vbox.getStyleClass().add("node-selected");

				} else {
					_vbox.getStyleClass().remove("node-selected");
				}
			
			}
		});
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
//				mx = event.getSceneX() - getTranslateX();
				my = event.getSceneY() - getTranslateY();
			}
		});
		
		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
//				setTranslateX(event.getSceneX()-mx);
				setTranslateY(event.getSceneY()-my);
			}
		});
	}
}
