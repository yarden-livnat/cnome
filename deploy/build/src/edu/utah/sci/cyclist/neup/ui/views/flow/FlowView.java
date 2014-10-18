package edu.utah.sci.cyclist.neup.ui.views.flow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.core.event.Pair;
import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.ui.components.CyclistViewBase;
import edu.utah.sci.cyclist.core.ui.components.RangeField;
import edu.utah.sci.cyclist.core.ui.views.SimpleTableView;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import edu.utah.sci.cyclist.neup.model.Facility;
import edu.utah.sci.cyclist.neup.model.Inventory;
import edu.utah.sci.cyclist.neup.model.Range;
import edu.utah.sci.cyclist.neup.model.Transaction;
import edu.utah.sci.cyclist.neup.model.proxy.SimulationProxy;

public class FlowView extends CyclistViewBase {
  public static final String ID = "flow-view";
  public static final String TITLE = "Material Flow";

  public static final int Y_OFFSET_TOP = 30;
  public static final int Y_OFFSET_BOTTOM = 10;

  public static final String NATURAL_U = "natl_u";
  public static final String ENRICHED_U = "enriched_u";
  public static final String WASTE = "waste";

  public static final int SRC = 0;
  public static final int DEST = 1;

  public static final int INIT_TIMESTEP = 1;
  public static final int MIN_TIMESTEP = 1;

  static final Logger log = LogManager.getLogger(FlowView.class.getName());

  private FlowLine _line[]; 
  private FlowChart _chart;

  private Map<String, Function<Facility, Object>> kindFactory = new HashMap<>();
  private Map<Integer, Facility> _facilities = new HashMap<>();
  private List<Connector> _connectors = new ArrayList<>();
  private Map<String, InventoryEntry> _selectedNodes = new HashMap<>();

  private SimulationProxy _simProxy = null;
  private int _targetLine = -1;
  private boolean _changingKid = false;

  /*
   * Properties
   */
  private ObjectProperty<Predicate<Transaction>> _transactionsPredicateProperty = new SimpleObjectProperty<>(t->true);

  private ObjectProperty<Function<Transaction, Object>> _aggregateFuncProperty = new SimpleObjectProperty<>();
  private IntegerProperty _chartModeProperty = new SimpleIntegerProperty(0);

  private Predicate<Transaction> _commodityPredicate = t->true;
  private Predicate<Transaction> _isoPredicate = t->true;


  // UI Components
  private Pane _pane;
  private RangeField _rangeField;
  private Text _total;
  private VBox _commodityVBox;

  /**
   * Constructor
   */

  public FlowView() {
	super();

	init();
	setupActions();
	build();
  }


  @Override
  public void selectSimulation(Simulation sim, boolean active) {
	super.selectSimulation(sim, active);
	update();
  }

  private Range<Integer> getTimeRange() {
	return _rangeField.getRange();
  }

  private void init() {
	setSupportsTables(false);

	kindFactory.put("Spec", f->f.spec);
	kindFactory.put("Prototype", f->f.prototype);
	kindFactory.put("AgentID", f->f.id);
	kindFactory.put("InstitutionID", f->f.intitution);
  }

  private void update() {
	Simulation currentSim = getCurrentSimulation();
	if (currentSim == null) {
	  _simProxy = null;
	}
	_simProxy = new SimulationProxy(currentSim);

	if (_simProxy != null)
	 fetchInfo();
  }

  private void fetchInfo() {
	Task<Pair<ObservableList<Facility>, ObservableList<String>>> task = new Task<Pair<ObservableList<Facility>, ObservableList<String>>>() {
	  @Override
	  protected Pair<ObservableList<Facility>, ObservableList<String>> call() throws Exception {
		Pair<ObservableList<Facility>, ObservableList<String>> info = new Pair<>();
		info.v2  = _simProxy.getCommodities();
		info.v1 = _simProxy.getFacilities();
		return info;
	  }
	};

	task.valueProperty().addListener(new ChangeListener<Pair<ObservableList<Facility>, ObservableList<String>>> () {
	  @Override
	  public void changed(
		  ObservableValue<? extends Pair<ObservableList<Facility>, ObservableList<String>>> observable,
			  Pair<ObservableList<Facility>, ObservableList<String>> oldValue,
			  Pair<ObservableList<Facility>, ObservableList<String>> newValue) 
	  {
    	  _facilities = new HashMap<>();
    		for (Facility f : newValue.v1) {
    		  _facilities.put(f.getId(), f);
    		}
//    		System.out.println("Flow: received "+newValue.v1.size()+" facilities");
    
    		updateSelectionCtrl(newValue.v2);
//    	  _rangeField.setRange(new Range<Integer>(1, getCurrentSimulation().getDuration()));
	  }
	});
	
	Thread th = new Thread(task);
	th.setDaemon(true);
	th.start();
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
		  System.out.println("Flow: received "+newValue.size()+" facilities");
		}
	  }
	});

	Thread th = new Thread(task);
	th.setDaemon(true);
	th.start();
  }

  private void fetchFilterValues() {
	// fetch facilities from the simulation
	Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
	  @Override
	  protected ObservableList<String> call() throws Exception { 
		return _simProxy.getCommodities();
	  }
	};

	task.valueProperty().addListener( new ChangeListener<ObservableList<String>>() {
	  @Override
	  public void changed(
		  ObservableValue<? extends ObservableList<String>> observable,
			  ObservableList<String> oldList,
			  ObservableList<String> newList) 
	  {
		if (newList != null) {
		  updateSelectionCtrl(newList);
		}
	  }
	});

	Thread th = new Thread(task);
	th.setDaemon(true);
	th.start();
  }

  private void updateTransactionsPredicate() {
	_transactionsPredicateProperty.set(_commodityPredicate.and(_isoPredicate));
	updateTotal();
  }

  private void updateTotal() {
	if (_line == null || _line[SRC] == null) return;

	double total = 0;
	int n = 0;
	for (Connector c : _connectors) {
	  total += c.getTotal();
	  n += c.getTransactions().size();
	}

	if (total == 0) {
	  _total.setText("");
	} else {
	  _total.setText(String.format("%.2e kg [%d]", total, n));
	}
  }

  private void timeChanged(Range<Integer> range) {
	// connect explicit nodes
	System.out.println("time changed");
	List<FlowNode> list = new ArrayList<>();
	for (FlowNode node : _line[SRC].getNodes()) {
	  if (node.isExplicit())
		list.add(node);
	}

	for (FlowNode node : _line[DEST].getNodes()) {
	  if (node.isExplicit())
		list.add(node);
	}

	if (list.size() > 0)
	  queryTransactions(list);	
  }

  private void removeAllConnectors() {
	for (Connector c : _connectors) {
	  c.release();
	  c.getFrom().removeConnector(c);
	  c.getTo().removeConnector(c);
	  _pane.getChildren().remove(c);
	}
	_connectors.clear();
  }

  private FlowNode createNode(String kind, Object value, int direction, boolean explicit) {
	FlowNode node = new FlowNode(kind, value, direction, explicit);
	node.setOnOpen(n->openNode(n));
	node.setOnClose(n->closeNode(n));
	node.setOnSelect(n->selectNode(n));
	node.getActiveTransactions().predicateProperty().bind(_transactionsPredicateProperty);
	node.getActiveTransactions().addListener((Observable o)->{transactionsChanged(node);});

	InventoryEntry entry = _selectedNodes.get(node.getName());
	if (entry == null) {
	  entry = new InventoryEntry(node.getName());
	  _selectedNodes.put(entry.getName(), entry);
	}

	node.setColor(entry.getColor());
	entry.add(node);

	return node;
  }


  private void addNode(Field field, Object value, int direction, double y, boolean explicit) {	
	FlowLine line = _line[direction];

	if (line.getKind() == null) {
	  line.setKind(field.getSemantic());
	} else if (!line.getKind().equals(field.getSemantic())) {
	  System.out.println("Error: REJECT node of this kind");
	  return;
	}

	FlowNode node = line.findNode(value);
	if (node == null) {
	  node = createNode(field.getSemantic(), value, direction, explicit);
	  line.addNode(node);
	} else {
	  if (node.isExplicit() || !explicit) {
		// nothing to do here
		return;
	  }
	}

	node.setExplicit(explicit);

	if (explicit) {
	  queryMaterialFlow(node);
	}
  } 

  private void closeNode(FlowNode node) {
	List<FlowNode> closeNodes = new ArrayList<>();

	Iterator<Connector> i = node.getConnectors().iterator();
	while (i.hasNext()) {
	  Connector c = i.next();
	  FlowNode other = c.getFrom() == node ? c.getTo() : c.getFrom();
	  if (other.isImplicit()) {
		c.release();
		other.removeConnector(c);
		if (other.isImplicit() && other.getConnectors().isEmpty()) {
		  closeNodes.add(other);
		}
		i.remove();
		_pane.getChildren().remove(c);
	  }
	}

	if (node.getConnectors().isEmpty()) {
	  removeNode(node);
	} else
	  node.setExplicit(false);

	for (FlowNode n : closeNodes) {
	  closeNode(n);
	}
  }

  private void removeNode(FlowNode node) {
	node.setTask(null);
	Iterator<Connector> i = _connectors.iterator();
	while (i.hasNext()) {
	  Connector c = i.next();
	  if (c.getFrom() == node || c.getTo() == node) {
		c.release();
		c.getFrom().removeConnector(c);
		c.getTo().removeConnector(c);
		_pane.getChildren().remove(c);
		i.remove();
	  }
	}
	_line[node.getDirection()].removeNode(node);
	InventoryEntry entry = _selectedNodes.get(node.getName());
	entry.remove(node);
	if (entry.isEmpty())
	  _chart.remove(entry);
  }

  public void removeNodes(List<FlowNode> nodes) {
	for (FlowNode node : nodes) {
	  removeNode(node);
	}
  }

  private void openNode(FlowNode node) {
	queryMaterialFlow(node);
	node.setExplicit(true);
  }

  @SuppressWarnings("unchecked")
  private void selectNode(final FlowNode node) {
	InventoryEntry entry = _selectedNodes.get(node.getName());
	if (entry.isSelected()) {
	  entry.select(false);
	  _chart.remove(entry);
	}
	else {
	  entry.select(true);
	  ObservableList<Inventory> inventory = entry.getInventory();
	  if (inventory == null) {
		queryInventory(node).addListener((Observable o)->{
		  ObjectProperty<ObservableList<Inventory>> p = (ObjectProperty<ObservableList<Inventory>>) o;
		  long t0 = System.currentTimeMillis();
		  entry.setInventory(p.get());
		  long t1 = System.currentTimeMillis();
		  addToChart(entry, p.get());
		  long t2 = System.currentTimeMillis();

		  System.out.println("inventory processing: "+(t1-t0)/1000.0+"  "+(t2-t1)/1000.0);
		});

	  } else {
		addToChart(entry, inventory);
	  }
	}
  }

  private void addToChart(InventoryEntry entry, ObservableList<Inventory> values) {
	// assume the data is sorted based on time
	// multiple items per timestep
	// TODO: apply filters
	List<Pair<Integer, Double>> series = new ArrayList<>();
	Pair<Integer, Double> current =  null;
	for (Inventory i : values) {
	  if (current == null || current.v1 != i.time) {
		if (current != null) {
		  series.add(current);
		}
		current = new Pair<>();
		current.v1 = i.time;
		current.v2 = i.amount;
	  } else {
		current.v2 += i.amount;
	  }
	}
	if (current != null) {
	  series.add(current);
	}

	_chart.add(entry, entry.getName(), series);
  }

  private ReadOnlyObjectProperty<ObservableList<Inventory>> queryInventory(FlowNode node) {
	Task<ObservableList<Inventory>> task = new Task<ObservableList<Inventory>>() {
	  @Override
	  protected ObservableList<Inventory> call() throws Exception {
		long t = System.currentTimeMillis();
		ObservableList<Inventory> list = _simProxy.getInventory(node.getType(), node.getValue().toString());
		long t1 = System.currentTimeMillis();
		System.out.println("query Inventory: "+(t1-t)/1000.0);
		return list;
	  }	
	};

	Thread thread = new Thread(task);
	thread.setDaemon(true);
	thread.start();

	node.setTask(task);

	return task.valueProperty();
  }

  private void queryMaterialFlow(final FlowNode node) {
	final int timestep = getTimeRange().from;
	Task<ObservableList<Transaction>> task = new Task<ObservableList<Transaction>>() {
	  @Override
	  protected ObservableList<Transaction> call() throws Exception {
		long t = System.currentTimeMillis();
		ObservableList<Transaction> list =  _simProxy.getTransactions(node.getType(), node.getValue().toString(), getTimeRange(), node.isSRC());
		long t1 = System.currentTimeMillis();
		System.out.println("query material flow: "+(t1-t)/1000.0);
		return list;
	  }	
	};

	task.valueProperty().addListener((o, p, n)->{
	  if (n != null) {
		addRelatedNodes(node, n, timestep);
		updateTotal();
	  }
	});

	Thread thread = new Thread(task);
	thread.setDaemon(true);
	thread.start();

	setCurrentTask(task);
  }

  private void queryTransactions(final List<FlowNode> list) {
	final int timestep = getTimeRange().from;

	Task<ObservableMap<FlowNode, ObservableList<Transaction>>> task = new Task<ObservableMap<FlowNode, ObservableList<Transaction>>>() {
	  @Override
	  protected ObservableMap<FlowNode,ObservableList<Transaction>> call() throws Exception {
		Map<FlowNode, ObservableList<Transaction>> map = new HashMap<>();
		for (FlowNode node : list) {
		  long t = System.currentTimeMillis();
		  ObservableList<Transaction> list = _simProxy.getTransactions(node.getType(), node.getValue().toString(), getTimeRange(), node.isSRC());
		  long t1 = System.currentTimeMillis();
		  System.out.println("getTransaction: node="+node.getValue().toString()+" time:"+(t1-t)/1000.0);
		  map.put(node, list);
		}

		return FXCollections.observableMap(map);
	  }	
	};

	task.valueProperty().addListener((o, p, n)->{
	  if (n != null) {
		if (getTimeRange().from == timestep) {
		  removeAllConnectors();
		  for (FlowNode node : n.keySet()) {
			addRelatedNodes(node, n.get(node), timestep);
		  }			
		  removeEmptyImplicitNodes();
		  updateTotal();
		}
	  }
	});

	Thread thread = new Thread(task);
	thread.setDaemon(true);
	thread.start();

	setCurrentTask(task);
  }

  private void addRelatedNodes(FlowNode node, ObservableList<Transaction> list, int time) {
	if (time == getTimeRange().from) {
	  node.setTransactions(list);
	}
  }

  private void removeEmptyImplicitNodes() {
	// remove empty implicit nodes
	for(Iterator<FlowNode> i = _line[SRC].getNodes().iterator(); i.hasNext();) {
	  FlowNode node = i.next();
	  if (node.isImplicit() && node.getConnectors().isEmpty()) {
		i.remove();
		removeNode(node);
	  }
	}

	for(Iterator<FlowNode> i = _line[DEST].getNodes().iterator(); i.hasNext();) {
	  FlowNode node = i.next();
	  if (node.isImplicit() && node.getConnectors().isEmpty()) {
		i.remove();
		removeNode(node);
	  }
	}

  }

  private void lineKindChanged(int direction) {
	FlowLine changed = _line[direction];
	FlowLine src = _line[1-direction];


	removeAllConnectors();

	String kind = changed.getKind();
	removeNodes(changed.selectNodes(n->true));
	changed.setKind(kind);

	// remove implicit nodes
	removeNodes(src.selectNodes(n->n.isImplicit()));

	// update remaining nodes
	src.getNodes()
	.stream()
	.forEach(n->transactionsChanged(n));
  }

  private void transactionsChanged(FlowNode node) {		
	int from = node.getDirection();
	int to = 1-from;

	FlowLine line = _line[to];

	if (line.getKind() == null) {
	  line.setKind(_line[from].getKind());			
	}

	Set<Object> set = groupSet(node.getActiveTransactions(), node.isSRC() ? t->t.receiver : t->t.sender, kindFactory.get(line.getKind()));

	for (final Object value : set) {
	  FlowNode target = line.findNode(value);
	  if (target == null) {
		target = createNode(line.getKind(), value, to, false);
		line.addNode(target);
	  }

	  boolean found = false;
	  for (Connector c: _connectors) {
		if ((c.getFrom() == node && c.getTo() == target)
			|| (c.getFrom() == target && c.getTo() == node)) 
		{
		  found = true;
		  break;
		}
	  }
	  if (!found) {
		final Function<Facility, Object> kind = kindFactory.get(line.getKind());
		Function<Transaction, Facility> f = node.isSRC() ? t->_facilities.get(t.receiver) : t->_facilities.get(t.sender);

		Connector c = new Connector(node, target, node.getActiveTransactions().filtered(t->kind.apply(f.apply(t)) == value));		
		node.addConnector(c);
		target.addConnector(c);
		_connectors.add(c);
		_pane.getChildren().add(c);
	  }
	}

  }

  private Set<Object> groupSet(ObservableList<Transaction> transactions, Function<Transaction, Integer> targetFunc, Function<Facility, Object> func) {
	Set<Object> set = new HashSet<>();
	for (Transaction t : transactions) {
	  Facility f = _facilities.get(targetFunc.apply(t));
	  set.add(func.apply(f));
	}
	return set;
  }

  private void isoFilterChanged(String value) {
	if (value == null || value.equals("")) {
	  _isoPredicate = t->true;
	  updateTransactionsPredicate();
	} else {
	  try {
		final int n = Integer.parseInt(value);	
		_isoPredicate =  
			n< 200 ? 
				t->Math.floorDiv(t.nucid, 1000*10000) == n:
				  n < 200000 ?
					  t->Math.floorDiv(t.nucid, 10000) == n:
						t->t.nucid == n  ;
						updateTransactionsPredicate();
	  } catch (Exception e)  {
		System.out.println("*** TODO: Indicate to user iso was invalid number");
	  }
	}
  }

	private void setupActions() {
		List<Node> actions = new ArrayList<>();
		actions.add(createExportActions());
		addActions(actions);
	}
	
	private Node createExportActions() {
		final Button button = new Button("Export", GlyphRegistry.get(AwesomeIcon.CARET_DOWN));
		button.getStyleClass().add("flat-button");

		// create menu
		final ContextMenu contextMenu = new ContextMenu();
		
		// export graph
		MenuItem item = new MenuItem("Graph");
		item.setOnAction(new EventHandler<ActionEvent>() {		
			@Override
			public void handle(ActionEvent event) {
				BorderPane bp = (BorderPane) getContent();
				export(bp.getCenter());
			}
		});
		contextMenu.getItems().add(item);

		// export chart
		item = new MenuItem("Plot");
		item.setOnAction(new EventHandler<ActionEvent>() {		
			@Override
			public void handle(ActionEvent event) {
				BorderPane bp = (BorderPane) getContent();
				export(bp.getBottom());
			}
		});
		
		contextMenu.getItems().add(item);
		button.setOnMousePressed(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				contextMenu.show(button, Side.BOTTOM, 0, 0);
			}
		});
		
		return button;
	}
	
	private void export(Node node) {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("Image Files (png, jpg, gif)", "*.png", "*.jpg", "*.gif" ));
		File file = chooser.showSaveDialog(Cyclist.cyclistStage);
		if (file != null) {
			WritableImage image = node.snapshot(new SnapshotParameters(), null);

		    try {
		    	String name = file.getName();
		    	String ext = name.substring(name.indexOf(".")+1, name.length());
		    	System.out.println("name: "+name+"  ext:"+ext);
		        ImageIO.write(SwingFXUtils.fromFXImage(image, null), ext, file);
		    } catch (IOException e) {
		        log.error("Error writing image to file: "+e.getMessage());
		    }
		}
	}
	
  private void build() {
	setTitle(TITLE);
	getStyleClass().add("flow");

	BorderPane pane = new BorderPane();
	pane.setLeft(buildControlls());
	pane.setCenter(buildMainArea());
	pane.setBottom(buildInventoryChart());

	_line[SRC].charModeProperty().bind(_chartModeProperty);
	_line[SRC].aggregationFunProperty().bind(_aggregateFuncProperty);
	_line[DEST].charModeProperty().bind(_chartModeProperty);
	_line[DEST].aggregationFunProperty().bind(_aggregateFuncProperty);

	_aggregateFuncProperty.set(t->t.commodity);

	setContent(pane);
  }

  private Node buildControlls() {
	VBox vbox = new VBox();
	vbox.getStyleClass().add("ctrl");

	vbox.getChildren().addAll(
		buildTimestepCtrl(),
		buildSelectionCtrl(),
		buildNuclideCtrl()
		//			new Separator(),
		//			buildChartMode(),
		//			buildChartAggr()
		);

	return vbox;
  }

  private Node buildTimestepCtrl() {
	VBox vbox = new VBox();
	vbox.getStyleClass().add("infobar");

	Label title = new Label("Time");
	title.getStyleClass().add("title");

	_rangeField = new RangeField();

	Label forward = GlyphRegistry.get(AwesomeIcon.CARET_RIGHT, "16px");
	Label backward = GlyphRegistry.get(AwesomeIcon.CARET_LEFT, "16px");

	forward.getStyleClass().add("flat-button");
	backward.getStyleClass().add("flat-button");			

	forward.setOnMouseClicked(e->_rangeField.inc());			
	backward.setOnMouseClicked(e->_rangeField.dec());

	HBox hbox = new HBox();
	hbox.setStyle("-fx-padding: 0");
	hbox.getStyleClass().add("infobar");
	hbox.getChildren().addAll( _rangeField, backward, forward);

	vbox.getChildren().addAll(
		title,
		hbox
		);

	_rangeField.rangeProperty().addListener(o->timeChanged(_rangeField.getRange()));

	_rangeField.setOnDragOver(e->{
	  DnD.LocalClipboard clipboard = getLocalClipboard();

	  if (clipboard.hasContent(DnD.VALUE_FORMAT)) {
		Field field = clipboard.get(DnD.FIELD_FORMAT, Field.class);
		if (field.getName().equals("EnterTime") || field.getName().equals("ExitTime")) {
		  e.acceptTransferModes(TransferMode.COPY);
		  e.consume();
		}
	  }
	});

	_rangeField.setOnDragDropped(e-> {
	  DnD.LocalClipboard clipboard = getLocalClipboard();	
	  if (clipboard.hasContent(DnD.VALUE_FORMAT)) {
		Integer i = clipboard.get(DnD.VALUE_FORMAT, Integer.class);	
		Range<Integer> r = _rangeField.getRange();
		if (_rangeField.getMode() == RangeField.Mode.DURATION) {
		  _rangeField.setRange(new Range<>(i, i+r.to-r.from));
		} else {
		  _rangeField.setRange(new Range<>(i, i));
		}
		e.consume();
	  }
	});

	return vbox;
  }

  private Node buildSelectionCtrl() {
	_commodityVBox = new VBox();
	_commodityVBox.getStyleClass().add("infobar");
	_commodityVBox.setMaxHeight(200);

	Text title = new Text("Commodity");
	title.getStyleClass().add("title");


	_commodityVBox.getChildren().addAll(
		title
		);

	return _commodityVBox;
  }

  private void updateSelectionCtrl(List<String> values) {
	List<CheckBox> list = new ArrayList<CheckBox>();

	// save title child
	Node title = _commodityVBox.getChildren().get(0);

	// update state of each existing checkbox
	int n = _commodityVBox.getChildren().size();
	for (int i=1; i<n; i++) {
	  CheckBox checkbox = (CheckBox) _commodityVBox.getChildren().get(i);
	  if (values.contains(checkbox.getText())) {
		checkbox.setDisable(false);
		values.remove(checkbox.getText());
	  } else {
		checkbox.setDisable(true);
	  }
	  list.add(checkbox);
	}

	// create new checkboxes
	for (String value : values) {
	  CheckBox checkbox = new CheckBox(value);
	  checkbox.selectedProperty().addListener(_commodityListener);
	  checkbox.setSelected(true);
	  list.add(checkbox);
	}

	list.sort(new Comparator<CheckBox>() {
	  @Override
	  public int compare(CheckBox cb1, CheckBox cb2) {
		return cb1.getText().compareTo(cb2.getText());
	  }
	});

	_commodityVBox.getChildren().clear();
	_commodityVBox.getChildren().add(title);
	_commodityVBox.getChildren().addAll(list);

	updateCommodityFilter();
  }

  private InvalidationListener _commodityListener = o->{
	updateCommodityFilter();
  };

  private void updateCommodityFilter() {
	removeAllConnectors();

	final ArrayList<String> values = new ArrayList<String>();

	boolean skip = true; // ignore the first child (Text)
	for (Node node : _commodityVBox.getChildren()) {
	  if (skip) {
		skip = false;
	  } else {
		CheckBox cb = (CheckBox) node;
		if (!cb.isDisabled() && cb.isSelected()) {
		  values.add(cb.getText());
		}
	  }
	}

	_commodityPredicate = t->values.contains(t.commodity);

	updateTransactionsPredicate();
  };

  private Node buildNuclideCtrl() {
	VBox vbox = new VBox();
	vbox.getStyleClass().add("infobar");

	Text title = new Text("Nuclide");
	title.getStyleClass().add("title");

	TextField entry = new TextField();
	entry.getStyleClass().add("nuclide");
	entry.setPromptText("filter");

	vbox.getChildren().addAll(
		title,
		entry
		);


	entry.setOnAction(e->isoFilterChanged(entry.getText()));
	return vbox;
  }

  private Node buildChartAggr() {
	VBox vbox = new VBox();
	vbox.getStyleClass().add("infobar");

	Text title = new Text("Info");
	title.getStyleClass().add("title");

	ToggleGroup group = new ToggleGroup();

	RadioButton commodity= new RadioButton("Commodity");
	commodity.getStyleClass().add("flat-button");
	commodity.setToggleGroup(group);

	RadioButton elem = new RadioButton("Element");
	elem.getStyleClass().add("flat-button");
	elem.setToggleGroup(group);

	RadioButton iso = new RadioButton("Iso");
	iso.getStyleClass().add("flat-button");
	iso.setToggleGroup(group);

	vbox.getChildren().addAll(
		title,
		commodity,
		elem,
		iso
		);

	commodity.setOnAction(e->_aggregateFuncProperty.set(t->t.commodity));
	elem.setOnAction(e->_aggregateFuncProperty.set(t->Math.floorDiv(t.nucid, 1000)));
	iso.setOnAction(e->_aggregateFuncProperty.set(t->t.nucid));

	commodity.setSelected(true);

	return vbox;
  }

  private Node buildInventoryChart() {
	VBox vbox = new VBox();
	_chart = new FlowChart();
	_chart.timeRangeProperty().bindBidirectional(_rangeField.rangeProperty());

	vbox.getChildren().addAll(
		new Separator(),
			_chart
		);
	return vbox;
  }

  private Node buildMainArea() {
	_pane = new Pane();
	_pane.getStyleClass().add("pane");

	Rectangle clip = new Rectangle(0, 0, 100, 100);
	clip.widthProperty().bind(_pane.widthProperty());
	clip.heightProperty().bind(_pane.heightProperty());
	_pane.setClip(clip);

	_line = new FlowLine[2];
	_line[SRC] = new FlowLine(SRC, _pane);
	_line[SRC].setKindItems(kindFactory.keySet());

	_line[DEST] = new FlowLine(DEST, _pane);
	_line[DEST].setKindItems(kindFactory.keySet());

	Text totalLabel = new Text("Total: ");
	totalLabel.getStyleClass().add("total");
	_total = new Text();
	TextFlow totalLine = new TextFlow();
	totalLine.getChildren().addAll(totalLabel, _total);

	_pane.getChildren().addAll(totalLine);		

	DoubleProperty w = new SimpleDoubleProperty();
	w.bind(_pane.widthProperty().divide(5)); 

	_line[SRC].centerXProperty().bind(w);
	_line[SRC].infoXProperty().set(10);
	_line[SRC].startYProperty().bind(_pane.translateYProperty().add(Y_OFFSET_TOP));
	_line[SRC].endYProperty().bind(_pane.heightProperty().subtract(Y_OFFSET_BOTTOM));

	_line[DEST].centerXProperty().bind(w.multiply(4));
	_line[DEST].infoXProperty().bind(
		_line[DEST].centerXProperty().add(
			(_line[DEST].widthProperty().divide(2)).add(10)));
	_line[DEST].startYProperty().bind(_pane.translateYProperty().add(Y_OFFSET_TOP));
	_line[DEST].endYProperty().bind(_pane.heightProperty().subtract(Y_OFFSET_BOTTOM));

	Label sender = new Label("Sender");
	sender.layoutXProperty().bind(w.subtract(sender.widthProperty().divide(2)));
	sender.layoutYProperty().set(Y_OFFSET_TOP/2);

	Label receiver = new Label("Receiver");
	receiver.layoutXProperty().bind(w.multiply(4).subtract(sender.widthProperty().divide(2)));
	receiver.layoutYProperty().set(Y_OFFSET_TOP/2);

	_pane.getChildren().addAll(sender, receiver);

	totalLine.translateXProperty().bind((_pane.widthProperty().subtract(totalLine.widthProperty()).divide(2)));
	totalLine.setTranslateY(Y_OFFSET_TOP/2);
	setPaneListeners();

	return _pane;
  }

  private void setPaneListeners() {
	_pane.setOnDragOver(e->{
	  boolean accept = false;
	  DnD.LocalClipboard clipboard = getLocalClipboard();

	  if (clipboard.hasContent(DnD.VALUE_FORMAT)) {
		Field field = clipboard.get(DnD.FIELD_FORMAT, Field.class);
		if (kindFactory.containsKey(field.getSemantic())) {
		  // accept if it near one of the lines
		  double x = e.getX();
		  if (Math.abs(x - _line[SRC].getCenter()) < 10) {
			if (_line[SRC].getKind() == null 
				|| _line[SRC].getKind().equals(field.getSemantic())) 
			{
			  _targetLine = SRC;
			  accept = true;
			}
		  } else if (Math.abs(x - _line[DEST].getCenter()) < 10) {
			if (_line[DEST].getKind() == null 
				|| _line[DEST].getKind().equals(field.getSemantic())) 
			{
			  _targetLine = DEST;
			  accept = true;
			}
		  } else {
			_targetLine = -1;
		  }
		}
		if (accept) {				
		  e.acceptTransferModes(TransferMode.COPY);
		  e.consume();
		}
	  } 
	});

	_pane.setOnDragDropped(e-> {
	  DnD.LocalClipboard clipboard = getLocalClipboard();

	  Object value = clipboard.get(DnD.VALUE_FORMAT, Object.class);
	  Field field = clipboard.get(DnD.FIELD_FORMAT, Field.class);
	  //			Table table = clipboard.get(DnD.TABLE_FORMAT, Table.class);

	  addNode(field, value, _targetLine, e.getY(), true);

	  _targetLine = -1;

	  e.setDropCompleted(true);
	  e.consume();
	});

	_line[SRC].getNodes().addListener((Observable o)-> {
	  updateTotal();
	});

	_line[SRC].kindProperty().addListener((o, p, n)->{
	  if (p != null && !_changingKid)	{
		_changingKid = true;
		lineKindChanged(SRC);
		_changingKid = false;
	  }
	});

	_line[DEST].kindProperty().addListener((o, p, n)->{
	  if (p != null && !_changingKid)	{
		_changingKid = true;
		lineKindChanged(DEST);
		_changingKid = false;
	  }
	});
  }
}