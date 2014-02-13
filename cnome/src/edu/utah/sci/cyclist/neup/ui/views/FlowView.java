package edu.utah.sci.cyclist.neup.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.ui.components.CyclistViewBase;
import edu.utah.sci.cyclist.core.ui.components.NumericField;
import edu.utah.sci.cyclist.core.ui.components.Spring;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import edu.utah.sci.cyclist.neup.model.Facility;
import edu.utah.sci.cyclist.neup.model.Transaction;
import edu.utah.sci.cyclist.neup.model.proxy.SimulationProxy;

public class FlowView extends CyclistViewBase {
	public static final String ID = "flow-view";
	public static final String TITLE = "Material Flow";
	
	public static final int Y_OFFSET_TOP = 30;
	public static final int Y_OFFSET_BOTTOM = 20;
	
	public static final int SRC = 0;
	public static final int DEST = 1;
	
	// UI components
	private Pane _pane;
	private Label _timestepLabel;
	private NumericField _timestepField;
	private int _targetLine = -1;
	private Label _forward;
	private Label _backward;
	
	// variables
	private Simulation _currentSim = null;
	private SimulationProxy _simProxy = null;
	private int _timestep = 2;
	private Map<Integer, Facility> _facilities = new HashMap<>();
	private Column _column[] = new Column[2];
	
	private Map<String, Function<Facility, Object>> kindFactory = new HashMap<>();
	
	public FlowView() {
		super();
		setSupportsFiltering(false);
		setSupportsTables(false);
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
		kindFactory.put("InstitutionID", f->f.intitution);
		kindFactory.put("RegionID", f->f.region);
	}

	
	
	private Node createNode(String kind, Object value, int direction, boolean explicit) {
		Node node = new Node(kind, value, direction, explicit);
		return node;
	}
	
	private void addNode(Field field, Object value, int direction, double y, boolean explicit) {	
		Column col = _column[direction];
		
		if (col.getKind() == null) {
			col.setKind(field.getName());
		} else if (!col.getKind().equals(field.getName())) {
			System.out.println("Error: REJECT node of this kind");
			return;
		}
		
		Node node = col.findNode(value);
		if (node == null) {
			node = createNode(field.getName(), value, direction, explicit);
			col.addNode(node);
			_pane.getChildren().add(node);
		} else {
			if (node.getExplicit() || !explicit) {
				// nothing to do here
				return;
			}
		}
		
		node.setExplicit(explicit);
	
		if (explicit) {
			queryMaterialFlow(node);
		}
	}
	

	private void removeNode(Node node) {
		Iterator<Connector> i = node.connectors.iterator();
		while (i.hasNext()) {
			Connector c = i.next();
			Node other = c.from == node ? c.to : c.from;
			if (!other.getExplicit()) {
				_pane.getChildren().remove(c);
				_pane.getChildren().remove(c.text);
				other.connectors.remove(c);
				if (!other.getExplicit() && other.connectors.isEmpty()) {
					_pane.getChildren().remove(other);
					_column[other.direction].removeNode(other);
				}	
				i.remove();
			}
		}
		
		if (node.connectors.isEmpty()) {
			_column[node.direction].removeNode(node);
			_pane.getChildren().remove(node);	
		} else 
			node.setExplicit(false);
	}
	
	private void openNode(Node node) {
		queryMaterialFlow(node);
		node.setExplicit(true);
	}
	
	private void addRelatedNodes(Node node, List<Transaction> transactions,  int timestep) {
		node.setQuering(false);
		
		if (timestep != _timestep) {
			// user has already changed the current timestep so ignore this info;
			return;
		}
		
		node.transactions = transactions;
		
		int from = node.direction;
		int to = 1-from;
		
		if (_column[to].getKind() == null) {
			_column[to].setKind(_column[from].getKind());			
		}

		Map<Object, List<Transaction>> groups = groupTransactions(transactions, 
				node.direction == SRC ? t->t.receiver : t->t.sender,
						_column[to].kindFunc);

		Column col = _column[to];
		for (Map.Entry<Object, List<Transaction>> entry : groups.entrySet()) {
			Object value = entry.getKey();
			Node target = col.findNode(value);
			if (target == null) {
				target = createNode(col.getKind(), value, to, false);
				//TODO: set the node's y
				col.addNode(target);
				
				_pane.getChildren().add(target);
			}
			
			Connector c;
			if (node.direction == SRC) 
				c= new Connector(node, target, entry.getValue());
			else
				c= new Connector(target, node, entry.getValue());

			_pane.getChildren().add(c);
			_pane.getChildren().add(c.text);
		}
	}
	
	private void changeColumnKind(int direction) {
		Column col = _column[direction];
		Column other = _column[1-direction];
		
		// remove all connections and nodes from the named column
		for (Node node : col.nodes) {
			for (Connector c : node.connectors) {
				_pane.getChildren().remove(c);
				_pane.getChildren().remove(c.text);
			}
			node.connectors.clear();
			_pane.getChildren().remove(node);
		}
		col.nodes.clear();
		
		// remove all connections and implicit nodes from the other column
		Iterator<Node> i = other.nodes.iterator();
		while (i.hasNext()) {
			Node node = i.next();	
			for (Connector c : node.connectors) {
				_pane.getChildren().remove(c);
				_pane.getChildren().remove(c.text);
			}
			node.connectors.clear();
			if (!node.getExplicit()) {
				_pane.getChildren().remove(node);
				i.remove();
			}
		}
		
		//
		for (Node node : other.nodes) {
			addRelatedNodes(node, node.transactions, _timestep);
		}
		
		
	}
	
	
	private Map<Object, List<Transaction>> groupTransactions(List<Transaction> transactions, Function<Transaction, Integer> targetFunc, Function<Facility, Object> func) {
		Map<Object, List<Transaction>> map = new HashMap<>();
		
		for (Transaction t : transactions) {
			Facility f = _facilities.get(targetFunc.apply(t));
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
	
	private void queryMaterialFlow(final List<Node> nodes) {
		final int timestep = _timestep;
		final int isotope = 0; 
		nodes.stream().forEach(n->n.setQuering(true));
		Task<ObservableMap<Node, List<Transaction>>> task = new Task<ObservableMap<Node, List<Transaction>>>() {

			@Override
			protected ObservableMap<Node,List<Transaction>> call() throws Exception {
				Map<Node, List<Transaction>> map = new HashMap<>();
				for (Node node : nodes) {
					List<Transaction> list = _simProxy.getTransactions(node.type, node.value.toString(), timestep, node.direction == SRC, isotope);
					map.put(node, list);
				}
				
				return FXCollections.observableMap(map);
			}
			
		};
		
		task.valueProperty().addListener(new ChangeListener<ObservableMap<Node, List<Transaction>>>() {

			@Override
			public void changed(
					ObservableValue<? extends ObservableMap<Node, List<Transaction>>> observable,
					ObservableMap<Node, List<Transaction>> oldValue,
					ObservableMap<Node, List<Transaction>> map) 
			{
				if (map != null) {
					for (Node node : map.keySet())
						addRelatedNodes(node, map.get(node), timestep);
					// remove empty implicit nodes
					for(Iterator<Node> i = _column[SRC].nodes.iterator(); i.hasNext();) {
						Node node = i.next();
						if (!node.getExplicit() && node.connectors.size() == 0) {
							i.remove();
							_pane.getChildren().remove(node);
						}
					}
					for(Iterator<Node> i = _column[DEST].nodes.iterator(); i.hasNext();) {
						Node node = i.next();
						if (!node.getExplicit() && node.connectors.size() == 0) {
							i.remove();
							_pane.getChildren().remove(node);
						}
					}
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
		List<Node> list = new ArrayList<>();
		
		_timestep = value;
		
		// remove all connectors 
		Column src = _column[SRC];
		Column dest = _column[DEST];
		
		// remove all connections 
		for (Node node : src.nodes) {
			for (Connector c : node.connectors) {
				_pane.getChildren().remove(c);
				_pane.getChildren().remove(c.text);
			}
			node.connectors.clear();
			node.transactions.clear();
			if (node.getExplicit())
				list.add(node);
		}
		
		// remove all connections and implicit nodes from the other column
		for (Node node : dest.nodes) {
			for (Connector c : node.connectors) {
				_pane.getChildren().remove(c);
				_pane.getChildren().remove(c.text);
			}
			node.connectors.clear();
			node.transactions.clear();
			if (node.getExplicit())
				list.add(node);
		}
		
		queryMaterialFlow(list);		
	}
	
	private void setTargetLine(int line) {
		if (_targetLine == line) return;
		if (_targetLine != -1) {
			_column[_targetLine].line.getStyleClass().remove("line-hover");
		}
		if (line != -1)  {
			_column[line].line.getStyleClass().add("line-hover");
		}
		_targetLine = line;
	}
	
	private void build() {
		setTitle(TITLE);
		getStyleClass().add("flow-view");
		this.setPrefWidth(400);
		this.setPrefHeight(300);
	
		_column[SRC] = new Column(SRC);
		_column[DEST] = new Column(DEST);
			
		// components
		_timestepLabel= new Label("time step:");	
		_timestepField = new NumericField(_timestep);
		_timestepField.getStyleClass().add("timestep");
		_timestepField.setMinValue(0);	
				
		_forward = GlyphRegistry.get(AwesomeIcon.CARET_RIGHT, "14px");
		_backward = GlyphRegistry.get(AwesomeIcon.CARET_LEFT, "14px");

		_forward.getStyleClass().add("flat-button");
		_backward.getStyleClass().add("flat-button");
		
		HBox header = new HBox();
		header.getStyleClass().add("infobar");
		header.getChildren().addAll(_timestepLabel, _timestepField, _backward, _forward);
		
		_pane = new Pane();
		_pane.getStyleClass().add("pane");
		_pane.getChildren().addAll(_column[SRC].choiceBox, _column[SRC].line, _column[DEST].choiceBox,_column[DEST].line);
		_pane.setPrefSize(400, 300);
		Rectangle clip = new Rectangle(0, 0, 100, 100);
		clip.widthProperty().bind(_pane.widthProperty());
		clip.heightProperty().bind(_pane.heightProperty());
		_pane.setClip(clip);
		
		VBox vbox = new VBox();
		vbox.getChildren().addAll(header, _pane);
		
		VBox.setVgrow(_pane, Priority.ALWAYS);
	
		_column[SRC].line.startXProperty().bind(_pane.widthProperty().multiply(0.25));
		_column[SRC].line.endXProperty().bind(_pane.widthProperty().multiply(0.25));
		_column[SRC].line.setStartY(40);
		_column[SRC].line.endYProperty().bind(_pane.heightProperty().subtract(20));
		
		_column[SRC].choiceBox.translateXProperty().bind(_pane.widthProperty().multiply(0.25)
				.subtract(_column[SRC].choiceBox.widthProperty().divide(2)));
		_column[SRC].choiceBox.setTranslateY(10);
		
		_column[DEST].line.startXProperty().bind(_pane.widthProperty().multiply(0.75));
		_column[DEST].line.endXProperty().bind(_pane.widthProperty().multiply(0.75));
		_column[DEST].line.setStartY(40);
		_column[DEST].line.endYProperty().bind(_pane.heightProperty().subtract(20));	
		_column[DEST].choiceBox.translateXProperty().bind(_pane.widthProperty().multiply(0.75)
				.subtract(_column[DEST].choiceBox.widthProperty().divide(2)));
		_column[DEST].choiceBox.setTranslateY(10);

		setContent(vbox);
		
		addListeners();
	}
	
	/*
	 * Ensure nodes are aligned
	 */
	 private void onWidthChanged() {
		double px = _column[SRC].line.getStartX();
		for (Node node : _column[SRC].nodes) {
			node.setTranslateX(px-node.getWidth()/2);
		}
		
		px = _column[DEST].line.getStartX();
		for (Node node : _column[DEST].nodes) {
			node.setTranslateX(px-node.getWidth()/2);
		}
	}
	
	private void addListeners() {
		
		_pane.widthProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				onWidthChanged();
			}	
		});
		
		// DnD
		_pane.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				boolean accept = false;
				DnD.LocalClipboard clipboard = getLocalClipboard();
				
				if (clipboard.hasContent(DnD.VALUE_FORMAT)) {
					Field field = clipboard.get(DnD.FIELD_FORMAT, Field.class);
					if (kindFactory.containsKey(field.getName())) {
						// accept if it near one of the lines
						double x = event.getX();
						if (Math.abs(x - _column[SRC].line.getStartX()) < 10) {
							if (_column[SRC].getKind() == null 
									|| _column[SRC].getKind().equals(field.getName())) 
							{
								setTargetLine(SRC);
								accept = true;
							}
						} else if (Math.abs(x - _column[DEST].line.getStartX()) < 10) {
							if (_column[DEST].getKind() == null 
									|| _column[DEST].getKind().equals(field.getName())) 
							{
								setTargetLine(DEST);
								accept = true;
							}
						} else {
							setTargetLine(-1);
						}
					}
					if (accept) {
						
						event.acceptTransferModes(TransferMode.COPY);
						event.consume();
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
				
				addNode(field, value, _targetLine, event.getY(), true);
				
				setTargetLine(-1);
				
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
		
		_timestepField.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				DnD.LocalClipboard clipboard = getLocalClipboard();
				
				if (clipboard.hasContent(DnD.VALUE_FORMAT)) {
					Field field = clipboard.get(DnD.FIELD_FORMAT, Field.class);
					if (field.getName().equals("EnterDate") || field.getName().equals("DeathDate")) {
						event.acceptTransferModes(TransferMode.COPY);
						event.consume();
					}
				}
			}
		});
		
		_timestepField.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				DnD.LocalClipboard clipboard = getLocalClipboard();
				
				if (clipboard.hasContent(DnD.VALUE_FORMAT)) {
					Integer i = clipboard.get(DnD.VALUE_FORMAT, Integer.class);					
					_timestepField.setValue(i);
					event.consume();
				}
			}
		});
		
		_forward.setOnMouseClicked(new EventHandler<MouseEvent>() {	
			@Override
			public void handle(MouseEvent event) {
				_timestepField.setValue(_timestepField.getValue()+1);			
			}
		});
		
		_backward.setOnMouseClicked(new EventHandler<MouseEvent>() {	
			@Override
			public void handle(MouseEvent event) {
				_timestepField.setValue(_timestepField.getValue()-1);			
			}
		});
	}
	
	class Column extends VBox {
		public final int SPACING = 10;
		public final int Y0 = 40;
		
		public int direction;
		private String kind = null;
		public Function<Facility, Object> kindFunc;
		public List<Node> nodes = new ArrayList<>();
		public Line line;
		public ChoiceBox<String> choiceBox;
		
		public Column(int direction) {
			this.direction = direction;
			build();
		}
		
		public void addNode(final Node node) {
			double y = Y0;
			if (nodes.size() > 0) {
				Node last = nodes.get(nodes.size()-1);
				y = last.getTranslateY()+last.getHeight()+SPACING;
			}
			nodes.add(node);
			node.setTranslateX(line.getStartX()); // will be centered once the width is known
			node.widthProperty().addListener( new InvalidationListener() {
				@Override
				public void invalidated(Observable observable) {
					node.setTranslateX(line.getStartX() - node.getWidth()/2);
				}
			});
			node.setTranslateY(y);
		}
		
		public void removeNode(Node node) {
			nodes.remove(node);
			if (nodes.size() == 0) {
				kind = null;
			}
		}
		
		public String getKind() {
			return kind;
		}
		
		public void setKind(String value) {
			kind = value;
			choiceBox.setValue(value);
		}
		
		public Node findNode(Object value) {
			for (Node node : nodes) {
				if (node.value.equals(value))
					return node;
			}
			
			return null;
		}
		
		private void build() {
			line = new Line();
			line.getStyleClass().add("flow-line");
			choiceBox = new ChoiceBox<>();
			choiceBox.getStyleClass().add("flow-choice");
			choiceBox.getItems().addAll(kindFactory.keySet());		
			
			getChildren().addAll(choiceBox, line);
			VBox.getVgrow(line);
			
			choiceBox.valueProperty().addListener(new InvalidationListener() {	
				@Override
				public void invalidated(Observable observable) {
					kind = choiceBox.getValue();
					kindFunc = kindFactory.get(kind);
					changeColumnKind(direction);
				}
			});
		}
	}
	
	class Connector extends CubicCurve {
		public Node from;
		public Node to;
		private List<Transaction> _transactions;
		private double total;
		private String units;
		public Text text;
		
		public Connector(Node from, Node to, List<Transaction> transactions) {
			super();
			getStyleClass().add("connector");
			this.from = from;
			this.to = to;
			from.addConnector(this);
			to.addConnector(this);
			_transactions = transactions;
			
			startXProperty().bind(from.anchorXProperty);
			startYProperty().bind(from.anchorYProperty);
			
			controlX1Property().bind(startXProperty().add(40));
			controlY1Property().bind(startYProperty());
			
			controlX2Property().bind(endXProperty().subtract(40));
			controlY2Property().bind(endYProperty());
			
			endXProperty().bind(to.anchorXProperty);
			endYProperty().bind(to.anchorYProperty);	
			
			total = 0;
			for (Transaction t : transactions) {
				total += t.quantity*t.fraction;
			}
			
			text = new Text( String.format("%.2e", total));
			text.getStyleClass().add("connector-text");
			
			
			DoubleBinding px = new DoubleBinding() {
				{
					super.bind(controlX1Property(), controlX2Property(), text.boundsInLocalProperty());
				}
				@Override
				protected double computeValue() {
					return (getControlX1()+getControlX2()-text.getBoundsInLocal().getWidth())/2;
				}
			};
			
			DoubleBinding py = new DoubleBinding() {
				{
					super.bind(controlY1Property(), controlY2Property(), text.boundsInLocalProperty());
				}
				@Override
				protected double computeValue() {
					return (getControlY1()+getControlY2()-text.getBoundsInLocal().getHeight())/2;
				}
			};
			
			text.translateXProperty().bind(px);
			text.translateYProperty().bind(py);
			
			DoubleBinding r = new DoubleBinding() {
				{
					super.bind(controlX1Property(), controlX2Property(), controlY1Property(), controlY2Property());
				}
				@Override
				protected double computeValue() {
					return Math.atan2(getControlY2()-getControlY1(), getControlX2()-getControlX1()) * 180/Math.PI;
				}
			};
			
			text.rotateProperty().bind(r);
		}
	}
	

	class Node extends Pane {
		public int direction;
		public Object value;
		public String type;
		private boolean _explicit;
		public boolean quering = false;
		public List<Transaction> transactions = new ArrayList<>();
		public List<Connector> connectors = new ArrayList<>();
		public DoubleProperty anchorXProperty = new SimpleDoubleProperty();
		public DoubleProperty anchorYProperty = new SimpleDoubleProperty();
		private Label _actionButton;
		
		private double my;
		private VBox _vbox;
		private boolean _selected = false;
		
		public Node(String type, Object value, int direction, boolean explicit) {
			this.type = type;
			this.value = value;
			this.direction = direction;
			
			build(type);
			this.setExplicit(explicit);
			
			setListeners();
			
			if (direction == FlowView.SRC) 
				anchorXProperty.bind( translateXProperty().add(widthProperty()));
			else
				anchorXProperty.bind( translateXProperty());
			
			anchorYProperty.bind( translateYProperty().add(heightProperty().divide(2)));
		}	
		
		public void setExplicit(boolean value) {
			_explicit = value;
			if (value) {
				_vbox.getStyleClass().add("node-explicit");
				_actionButton.setGraphic(GlyphRegistry.get(AwesomeIcon.TIMES, "10px"));
			}
			else {
				_vbox.getStyleClass().remove("node-explicit");
				_actionButton.setGraphic(GlyphRegistry.get(AwesomeIcon.EXTERNAL_LINK, "10px"));
			}
		}
		
		public boolean getExplicit() {
			return _explicit;
		}
		
		public void setQuering(boolean value) {
			quering = value;
			// TODO: indicate to the user the node is (not) in query mode
		}
		
		public void addConnector(Connector connector) {
			connectors.add(connector);
		}
		
		private void build(String label) {
			_vbox = new VBox();
			_vbox.getStyleClass().add("flow-node");
			
			// header
			HBox header = new HBox();
			header.getStyleClass().add("node-header");
					
			Text text = new Text(label+":");
			text.getStyleClass().add("node-kind");
						
			_actionButton = new Label();
			_actionButton.setVisible(false);
			_actionButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (getExplicit()) {
						removeNode(Node.this);
					} else {
						openNode(Node.this);
					}
				}
			});
				
			header.getChildren().addAll(
					text, 
					new Spring(),
					_actionButton);
			
			_vbox.getChildren().add(header);
				
			Text body = new Text(value.toString());
			body.getStyleClass().add("node-body");
			_vbox.getChildren().add(body);
		
			getChildren().add(_vbox);
			
			_vbox.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					_actionButton.setVisible(true);
				}
			});
			
			_vbox.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					_actionButton.setVisible(false);
				}
			});
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
					my = event.getSceneY() - getTranslateY();
				}
			});
			
			setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					setTranslateY(event.getSceneY()-my);
				}
			});
		}
	}
}