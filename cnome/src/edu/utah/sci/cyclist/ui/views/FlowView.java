package edu.utah.sci.cyclist.ui.views;

import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.event.dnd.DnDSource;
import edu.utah.sci.cyclist.model.DataType;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.components.CyclistViewBase;
import edu.utah.sci.cyclist.ui.components.ViewBase;

public class FlowView extends CyclistViewBase {
	public static final String ID = "flow-view";
	public static final String TITLE = "Flow";
	
	private Pane _pane;
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
		
		// components
		MenuBar menubar = createMenubar();
		
		_pane = new Pane();
		_pane.setPrefSize(400, 300);
		
		VBox vbox = new VBox();
		vbox.getChildren().addAll(menubar, _pane);
		
		VBox.setVgrow(_pane, Priority.ALWAYS);
	
		setContent(vbox);
		
		addListeners();
		
//		pane.widthProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable,
//					Number oldValue, Number newValue) {
//				redrawCanvas();
//			}
//		});
//		
//		pane.heightProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable,
//					Number oldValue, Number newValue) {
//				redrawCanvas();
//			}
//		});
//		
//		redrawCanvas();
	}
	
	
	private MenuBar createMenubar() {
		MenuBar menubar = new MenuBar();

		MenuItem item = new MenuItem("item");
		
		Menu menu = new Menu("Menu");
		menu.getItems().addAll(item);
		
		menubar.getMenus().add(menu);
		
		return menubar;
	}
	
	private void addListeners() {		
		// add listeners
		_pane.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				DnD.LocalClipboard clipboard = getLocalClipboard();
				DnDSource source = clipboard.get(DnD.SOURCE_FORMAT, DnDSource.class);
				if (source != null && source == DnDSource.VALUE) {
					if (clipboard.hasContent(DnD.VALUE_FORMAT)) {
						event.acceptTransferModes(TransferMode.COPY);
						event.consume();
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
				
				addNode(value, field, table, event.getX(), event.getY());
				
				accept = true;
				
				if (accept) {
					event.setDropCompleted(accept);
					event.consume();
				}
			}
		});

	}
	
	private void addNode(Object value, Field field, Table table, double x, double y) {
		Node node = new Node(value, field, table);
		node.setTranslateX(x);
		node.setTranslateY(y);
		_nodes.add(node);
		_pane.getChildren().add(node);
	}
}

class Node extends Pane {
	public Object value;
	public Field field;
	public Table table;
	private double mx;
	private double my;
	private VBox _vbox;
	private boolean _selected = false;
	
	public Node(Object value, Field field, Table table) {
		
		
		this.value = value;
		this.field = field;
		this.table = table;
		
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
				if (_selected) {
					getStyleClass().remove("selected");
				} else {
					getStyleClass().add("selected");
				}
				_selected = !_selected;				
			}
		});
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mx = event.getSceneX() - getTranslateX();
				my = event.getSceneY() - getTranslateY();
			}
		});
		
		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setTranslateX(event.getSceneX()-mx);
				setTranslateY(event.getSceneY()-my);
			}
		});
	}
}
