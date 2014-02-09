package edu.utah.sci.cyclist.neup.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.event.dnd.DnDSource;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.ui.components.CyclistViewBase;
import edu.utah.sci.cyclist.core.ui.components.NumericField;
import edu.utah.sci.cyclist.neup.model.Facility;
import edu.utah.sci.cyclist.neup.model.Transaction;
import edu.utah.sci.cyclist.neup.model.proxy.SimulationProxy;

public class FlowView extends CyclistViewBase {
	public static final String ID = "flow-view";
	public static final String TITLE = "Flow";
	
	public static final int Y_OFFSET_TOP = 30;
	public static final int Y_OFFSET_BOTTOM = 20;
	
	public static final int SRC = 0;
	public static final int DEST = 1;
	
	// UI components
	private Pane _pane;
	private Line _line[] = new Line[2];
	private Line _srcLine;
	private Line _destLine;
	private Label _timestepLabel;
	private NumericField _timestepField;
	private Line _targetLine = null;
	
	// variables
	private Simulation _currentSim = null;
	private SimulationProxy _simProxy = null;
	private int _timestep = 1;
	private Map<Integer, Facility> _facilities = new HashMap<>();
	private Column _column[] = new Column[2];
	
	private Map<String, Function<Facility, Object>> kindFactory = new HashMap<>();
	
	public FlowView() {
		super();
		init();
		build();
	}

	@Override
	public void selectSimulation(Simulation sim, boolean active) {
		super.selectSimulation(sim, active);
		
		if (!active && sim != _currentSim) {
			return; // ignore
		}
		if (active) {
			_currentSim = sim;
		} else {
			_currentSim = null;
		}
		
		update();
	}
	

	private void update() {
		if (_currentSim == null) {
			_simProxy = null;
			return;
		}
		_simProxy = new SimulationProxy(_currentSim);
		
		fetchFacilities();
	}
	
	private void fetchFacilities() {
		// fetch facilities from the simulation
		Task<ObservableList<Facility>> task = new Task<ObservableList<Facility>>() {
			@Override
			protected ObservableList<Facility> call() throws Exception {
				return _simProxy.getFacilities();
			}
		};
		
		task.valueProperty().addListener( new ChangeListener<ObservableList<Facility>>() {

			@Override
			public void changed(
					ObservableValue<? extends ObservableList<Facility>> observable,
					ObservableList<Facility> oldValue,
					ObservableList<Facility> newValue) 
			{
				if (newValue != null) {
					_facilities = new HashMap<>();
					for (Facility f : newValue) {
						_facilities.put(f.getId(), f);
					}
					System.out.println("Flow: received facilities: "+newValue.size());
				}
			}
		});
		
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}
	
	private void init() {
		kindFactory.put("ModelType", f->f.model);
		kindFactory.put("Prototype", f->f.prototype);
		kindFactory.put("ID", f->f.id);
		
		_column[SRC] = new Column(SRC);
		_column[DEST] = new Column(DEST);
	}

	
	
	private Node createNode(String kind, Object value, int direction, boolean explicit) {
		Node node = new Node(kind, value, direction, explicit);
		
		return node;
	}
	
	private void addNode(Field field, Object value, int direction, double y, boolean explicit) {
		
		Column col = _column[direction];
		
		if (col.kind == null) {
			col.kind = field.getName();
			col.kindFunc = kindFactory.get(col.kind);
		} else if (!col.kind.equals(field.getName())) {
			System.out.println("Error: REJECT node of this kind");
			return;
		}
		
		Node node = col.findNode(value);
		if (node == null) {
			node = createNode(field.getName(), value, direction, explicit);
			node.setTranslateY(y);
			col.addNode(node);
			_pane.getChildren().add(node);
		} else {
			if (node.explicit || !explicit) {
				// nothing to do here
				return;
			}
		}
		
		node.explicit = explicit;
	
		if (explicit) {
			queryMaterialFlow(node);
		}
	}
	
	private void addRelatedNodes(Node node, ObservableList<Transaction> transactions,  int timestep) {
		node.setQuering(false);
		
		if (timestep != _timestep) {
			// user has already changed the current timestep so ignore this info;
			return;
		}
		
		node.transactions = transactions;
		
		int from = node.direction;
		int to = 1-from;
		
		if (_column[to].kind == null) {
			_column[to].kind = _column[from].kind;
			_column[to].kindFunc = _column[from].kindFunc;
			
		}
		Map<Object, List<Transaction>> groups = groupTransactions(transactions, _column[to].kindFunc);

		Column col = _column[to];
		for (Map.Entry<Object, List<Transaction>> entry : groups.entrySet()) {
			Object value = entry.getKey();
			Node target = col.findNode(value);
			if (target == null) {
				target = createNode(col.kind, value, to, false);
				target.explicit = false;
				//TODO: set the node's y
				col.addNode(target);
				
				_pane.getChildren().add(target);
			}
			
			Connector c = new Connector(node, target, entry.getValue());
			_pane.getChildren().add(c);
		}
	}
	
	
	private Map<Object, List<Transaction>> groupTransactions(List<Transaction> transactions, Function<Facility, Object> func) {
		Map<Object, List<Transaction>> map = new HashMap<>();
		
		for (Transaction t : transactions) {
			Facility f = _facilities.get(t.target);
			Object type = func.apply(f);
			List<Transaction> list = map.get(type);
			if (list == null) {
				list = new ArrayList<>();
				map.put(type, list);
			}
			list.add(t);
		}
		
		return map;
		
	}
	
	private void queryMaterialFlow(final Node node) {
		final int timestep = _timestep;
		final int isotope = 0; 
		node.setQuering(true);
		Task<ObservableList<Transaction>> task = new Task<ObservableList<Transaction>>() {

			@Override
			protected ObservableList<Transaction> call() throws Exception {
				return _simProxy.getTransactions(node.type, node.value.toString(), timestep, node.direction == SRC, isotope);
			}
			
		};
		
		task.valueProperty().addListener(new ChangeListener<ObservableList<Transaction>>() {

			@Override
			public void changed(
					ObservableValue<? extends ObservableList<Transaction>> observable,
					ObservableList<Transaction> oldValue,
					ObservableList<Transaction> newValue) 
			{
				if (newValue != null) {
					addRelatedNodes(node, newValue, timestep);
				}
			}
			
		});
		
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
		
		setCurrentTask(task);
		
		
	}
	
	/*
	 * DnD interactions
	 */
	
	private void changeTimestep(int value) {
		_timestep = value;
		System.out.println("t="+value+" TODO: update display");
	}
	
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
	
	
	private void build() {
		setTitle(TITLE);
		getStyleClass().add("flow-view");
		this.setPrefWidth(400);
		this.setPrefHeight(300);
		// padding? spaceing?
		
		_srcLine = new Line();
		_srcLine.getStyleClass().add("flow-line");
		_destLine = new Line();
		_destLine.getStyleClass().add("flow-line");
		_line[SRC] = _srcLine;
		_line[DEST] = _destLine;
		_column[SRC].line = _srcLine;
		_column[DEST].line = _destLine;
		
		// components
		MenuBar menubar = createMenubar();
		
		_timestepLabel= new Label("timestep:");
		
		_timestepField = new NumericField(_timestep);
		_timestepField.getStyleClass().add("flow-timestep");
		
		HBox hbox = new HBox();
		hbox.getStyleClass().add("flow-timestep-bar");
		hbox.getChildren().addAll(_timestepLabel, _timestepField);
		
		Rectangle clip = new Rectangle(0, 0, 100, 100);
		clip.widthProperty().bind(_pane.widthProperty());
		clip.heightProperty().bind(_pane.heightProperty());
		_pane.setClip(clip);
		_pane = new Pane();
		_pane.getChildren().addAll(_srcLine, _destLine);
		_pane.setPrefSize(400, 300);
		
		VBox vbox = new VBox();
		vbox.getChildren().addAll(menubar, hbox, _pane);
		
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
	
	
	/*
	 * Ensure nodes are aligned
	 */
	 private void onWidthChanged() {
		double w = _pane.getWidth();
		
		_srcLine.setStartX(w/3);
		_srcLine.setEndX(w/3);
		
		_destLine.setStartX(2*w/3);
		_destLine.setEndX(2*w/3);
		
		double px = _line[SRC].getStartX();
		for (Node node : _column[SRC].nodes) {
			node.setTranslateX(px-node.getWidth()/2);
		}
		
		px = _line[DEST].getStartX();
		for (Node node : _column[DEST].nodes) {
			node.setTranslateX(px-node.getWidth()/2);
		}
	}
	
	private void onHeightChanged() {
		double h = _pane.getHeight();
		
		_srcLine.setStartY(30);
		_srcLine.setEndY(h-30);
		
		_destLine.setStartY(30);
		_destLine.setEndY(h-30);
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
					// TODO: accept only certain fields
					
					if (clipboard.hasContent(DnD.VALUE_FORMAT)) {
						// accept if it near one of the lines
						double x = event.getX();
						if (Math.abs(x - _srcLine.getStartX()) < 10) {
							setTargetLine(_srcLine);
							accept = true;							
						} else if (Math.abs(x - _destLine.getStartX()) < 10) {
							setTargetLine(_destLine);
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
				DnD.LocalClipboard clipboard = getLocalClipboard();
				
				Object value = clipboard.get(DnD.VALUE_FORMAT, Object.class);
				Field field = clipboard.get(DnD.FIELD_FORMAT, Field.class);
//				Table table = clipboard.get(DnD.TABLE_FORMAT, Table.class);
				
				int direction = _targetLine == _line[0] ? SRC : DEST;
				addNode(field, value, direction, event.getY(), true);
				
				setTargetLine(null);
				
				event.setDropCompleted(true);
				event.consume();
			}
		});
		
		_timestepField.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				changeTimestep(_timestepField.getValue());
			}	
		});


	}
}


class Column {
	public int direction;
	public String kind = null;
	public Function<Facility, Object> kindFunc;
	public List<Node> nodes = new ArrayList<>();
	public Line line;
	
	public Column(int direction) {
		this.direction = direction;
	}
	
	public void addNode(final Node node) {
		nodes.add(node);
		node.setTranslateX(line.getStartX()); // will be centered once the width is known
		node.widthProperty().addListener( new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				node.setTranslateX(line.getStartX() - node.getWidth()/2);
			}
		});
	}
	
	public Node findNode(Object value) {
		for (Node node : nodes) {
			if (node.value == value)
				return node;
		}
		
		return null;
	}
}

class Connector extends CubicCurve {
	private Node _from;
	private Node _to;
	private List<Transaction> _transactions;
	
	public Connector(Node from, Node to, List<Transaction> transactions) {
		super();
		getStyleClass().add("connector");
		_from = from;
		_to = to;
		_transactions = transactions;
		
		startXProperty().bind(from.anchorXProperty);
		startYProperty().bind(from.anchorYProperty);
		
		controlX1Property().bind(startXProperty().add(40));
		controlY1Property().bind(startYProperty());
		
		controlX2Property().bind(endXProperty().subtract(40));
		controlY2Property().bind(endYProperty());
		
		endXProperty().bind(to.anchorXProperty);
		endYProperty().bind(to.anchorYProperty);		
	}
}


class Node extends Pane {
	public Object value;
	public String type;
	public boolean explicit;
	public int direction;
	public boolean quering = false;
	public List<Transaction> transactions = null;
	public List<Connector> connectors = new ArrayList();
	public DoubleProperty anchorXProperty = new SimpleDoubleProperty();
	public DoubleProperty anchorYProperty = new SimpleDoubleProperty();
	
	private double mx;
	private double my;
	private VBox _vbox;
	private boolean _selected = false;
	
	public Node(String type, Object value, int direction, boolean explicit) {
		this.type = type;
		this.value = value;
		this.direction = direction;
		this.explicit = explicit;
		
		build(type);
		setListeners();
		
		if (direction == FlowView.SRC) 
			anchorXProperty.bind( translateXProperty().add(widthProperty()));
		else
			anchorXProperty.bind( translateXProperty());
			
		anchorYProperty.bind( translateYProperty().add(heightProperty().divide(2)));
	}	
	
	public void setQuering(boolean value) {
		quering = value;
		// TODO: indicate to the user the node is (not) in query mode
	}
	
	public void addConnector(Connector connector) {
		connectors.add(connector);
//		connector.update(this, getTranslateX(), getTranslateY());
	}
	
	private void build(String label) {
		_vbox = new VBox();
		_vbox.getStyleClass().add("flow-node");
//		if (field.getDataType().getType() != DataType.Type.TEXT) {
			Text header = new Text(label+":");
			header.getStyleClass().add("node-header");
			_vbox.getChildren().add(header);
//		}
			
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
				
//				double nx = getTranslateX() + (direction == FlowView.SRC ? getWidth()/2 : -getWidth()/2);
//				double ny = getTranslateY();
//				System.out.println("node pos:"+nx+", "+ny);
//				for (Connector c : connectors) {
//					c.update(Node.this, nx, ny);
//				}
			}
		});
	}
}
