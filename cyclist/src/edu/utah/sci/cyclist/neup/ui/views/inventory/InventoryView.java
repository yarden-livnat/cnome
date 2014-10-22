package edu.utah.sci.cyclist.neup.ui.views.inventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.core.controller.IMemento;
import edu.utah.sci.cyclist.core.event.Pair;
import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.model.Configuration;
import edu.utah.sci.cyclist.core.model.Context;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.ui.components.CyclistAxis;
import edu.utah.sci.cyclist.core.ui.components.CyclistViewBase;
import edu.utah.sci.cyclist.core.ui.components.Spring;
import edu.utah.sci.cyclist.core.ui.panels.TitledPanel;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.ColorUtil;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import edu.utah.sci.cyclist.neup.model.Inventory;
import edu.utah.sci.cyclist.neup.model.NuclideFiltersLibrary;
import edu.utah.sci.cyclist.neup.model.proxy.SimulationProxy;
import edu.utah.sci.cyclist.neup.ui.views.inventory.InventoryChart.ChartMode;
import edu.utah.sci.cyclist.neup.ui.views.inventory.InventoryChart.ChartType;

public class InventoryView extends CyclistViewBase {
	public static final String ID = "inventory-view";
	public static final String TITLE = "Inventory";
	
	static final Logger log = LogManager.getLogger(InventoryView.class.getName());
	
	private ObservableList<AgentInfo> _agents = FXCollections.observableArrayList();
	private List<String> _acceptableFields = new ArrayList<>();
	
	private Map<String, IntPredicate> _nuclideFilters = new TreeMap<>();
	private ObservableList<String> _nuclideFilterNames = FXCollections.observableArrayList();
	private ObjectProperty<Predicate<Inventory>> _currentNuclideFilterProperty = new SimpleObjectProperty<>();
	
	private TitledPanel _agentListPanel;
	private ChoiceBox<ChartType> _chartType;
	private boolean _lastForceZero = false;

	private SimulationProxy _simProxy = null;
		
	private InventoryChart _chart = new InventoryChart();
	ComboBox<String> _filters = new ComboBox<>();
	
	/**
	 * Constructor
	 */
	public InventoryView() {
		super();
		init();
		build();
	}
	
	@Override
	public void selectSimulation(Simulation sim, boolean active) {
		super.selectSimulation(sim, active);
		
		Simulation currentSim = getCurrentSimulation();
		_simProxy = currentSim == null ?  null : new SimulationProxy(currentSim);
		
		for (AgentInfo info : _agents) {
			info.inventory.unbind();
			info.inventory.bind(fetchInventory(info)); 
		}
	}
	
	private void init() {
		_acceptableFields.add("Spec");
		_acceptableFields.add("Prototype");
		_acceptableFields.add("AgentID");
		_acceptableFields.add("InstitutionID");
		
		// default no-op filter
		_currentNuclideFilterProperty.set(inventory->true);
		
		_nuclideFilters.put("", i->true);
		for (Entry<String, IntPredicate> entry :  NuclideFiltersLibrary.getInstance().getFilters().entrySet())  {
			_nuclideFilters.put(entry.getKey(), entry.getValue());
			_nuclideFilterNames.add(entry.getKey());
		}
	}
	
	private void selectChartType(ChartType type) {
		_chart.selectChartType(type);
	}
	
	private void selectNuclideFilter(String key) {	
		IntPredicate p = key.matches(" *") ? n->true : _nuclideFilters.get(key);

		if (p == null) {
			p = createNuclideFilter(key);
			_nuclideFilters.put(key, p);
			_nuclideFilterNames.add(key);
		}
		final IntPredicate predicate = p;
		_currentNuclideFilterProperty.set(inventory->predicate.test(inventory.nucid));
	}
	
	Pattern pattern = Pattern.compile("(\\d+)( *- *(\\d+))?");
	
	private IntPredicate createNuclideFilter(String spec) {
		IntPredicate predicate = null;
		String specs[] = spec.split(",");
		for (int i=0; i<specs.length; i++) {
			Matcher matcher = pattern.matcher(specs[i]);
			if (matcher.find()) {
				if (!valid(matcher.group(1), matcher.group(3))) {
					// TODO: indicate an error
					log.error("Illegal filter");
					return n->true; // ignore the spec;
				}
				IntPredicate p;
				if (matcher.group(3) == null) {
					p = createPredicate(matcher.group(1));
				} else {
					p = createPredicate(matcher.group(1), matcher.group(3));
				}
				predicate = predicate == null ? p : predicate.or(p);
			} 
		}
		return  predicate;
	}
	
	private boolean valid(String from, String to) {
		int l = from.length();
		return (l == 2 || l==5 || l==9) && (to==null || to.length() == l);
	}
	
	private IntPredicate createPredicate(String str) {
		int v = Integer.parseInt(str);
		int l = str.length();
		return l >7 ? n->n == v :
			l > 4 ? n->n/10000 == v:
				n->n/10000000 == v;		
	}
	
	private IntPredicate createPredicate(String from, String to) {
		int f = Integer.parseInt(from);
		int t = Integer.parseInt(to);
		int l = from.length();
		
		return l >7 ? n->n>=f && n<=t :
			l > 4 ? n->n/10000>=f && n/10000<=t:
				n->n/10000000<=f && n/10000000<=t;		
	}
	

	private Node createExportActions() {
		final Button button = new Button("Export", GlyphRegistry.get(AwesomeIcon.CARET_DOWN));
		button.getStyleClass().add("flat-button");

		// create menu
		final ContextMenu contextMenu = new ContextMenu();
		
		// csv chart
		MenuItem item = new MenuItem("Graph");
		item.setOnAction(new EventHandler<ActionEvent>() {		
			@Override
			public void handle(ActionEvent event) {
				export();
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
	
	private void export() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("Image file (png, jpg, gif)", "*.png", "*.jpg", "*.gif") );
		File file = chooser.showSaveDialog(Cyclist.cyclistStage);
		if (file != null) {
			WritableImage image = _chart.snapshot(new SnapshotParameters(), null);

		    try {
		        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		    } catch (IOException e) {
		        log.error("Error writing image to file: "+e.getMessage());
		    }
		}
	}
	
	@Override
	public void save(IMemento memento) {
		IMemento group = memento.createChild("agents");
		for (AgentInfo info : _agents) {
			IMemento child = group.createChild("agent");
			child.putString("field", info.field);
			child.putString("value", info.value);
		}
		
		memento.putString("chart-type", _chartType.getValue().toString());
		
		String nucId =  _filters.getValue();
		if(nucId != null){
			IMemento filter = memento.createChild("filter");
			filter.putString("nuc-id", nucId);
		}
		
		//axis options
		IMemento axis = memento.createChild("axis-opt");
		axis.putBoolean("mode", _chart.axisMode().get() == CyclistAxis.Mode.LINEAR);
		axis.putBoolean("force-zero", _chart.forceZero().get());
		
		//chart options
		IMemento chart = memento.createChild("chart-opt");
		chart.putBoolean("mode", _chart.getMode().get() == ChartMode.LINE);
		chart.putBoolean("total", _chart.getShowTotal().getValue());
		
	}
	
	@Override 
	public void restore(IMemento memento, Context ctx) {
		IMemento group = memento.getChild("agents");
		if (group != null) {
    		for (IMemento child : group.getChildren("agent")) {
    			addAgent(child.getString("field"), child.getString("value"));
    		}
		}
		
		_chartType.setValue(ChartType.valueOf(memento.getString("chart-type")));
		IMemento filter = memento.getChild("filter");
		if(filter != null){
			String nucId = filter.getString("nuc-id");
			_filters.getSelectionModel().select(nucId);
		}
		
		IMemento axis = memento.getChild("axis-opt");
		_chart.axisMode().set( axis.getBoolean("mode") ? CyclistAxis.Mode.LINEAR : CyclistAxis.Mode.LOG);
		_chart.forceZero().set( axis.getBoolean("force-zero"));
		
		IMemento chart = memento.getChild("chart-opt");
		_chart.setMode(chart.getBoolean("mode")?ChartMode.LINE:ChartMode.STACKED);
		_chart.setShowTotal(chart.getBoolean("total"));
	}
	
	private void addAgent(String type, String name) {
		AgentInfo info = new AgentInfo(type, name);	
		AgentEntry entry = new AgentEntry(info);
		_agentListPanel.getContent().getChildren().add(entry);
		entry.setOnClose(item->{
			_agents.remove(item.info);
			_agentListPanel.getContent().getChildren().remove(item);
			_chart.remove(item.info);
		});
		
		addAgent(info);	
	}
	
	private void build() {
		setTitle(TITLE);
		getStyleClass().add("inventory");
	
		setupActions();
		
		BorderPane pane = new BorderPane();
		pane.setCenter(_chart); //buildChart());
		pane.setLeft(buildCtrl());

		setContent(pane);
	}
	
	private Node buildCtrl() {
		VBox vbox = new VBox();
		vbox.getStyleClass().add("ctrl");
		
		vbox.getChildren().addAll(
			buildChartCtrl(),
			buildAgentCtrl(),
			buildNuclideCtrl()
		);
		
		return vbox;
	}
	
	private Node buildChartCtrl() {
		VBox vbox = new VBox();
		vbox.getStyleClass().add("ctrl");
		
		_chartType = new ChoiceBox<>();
		_chartType.getStyleClass().add("choice");
		_chartType.getItems().addAll(ChartType.values());
		_chartType.valueProperty().addListener(e->{
			selectChartType(_chartType.getValue());
		});
		
		_chartType.setValue(ChartType.INVENTORY);
		
		vbox.getChildren().add(_chartType);
		return vbox;
	}
	
	public Node buildAgentCtrl() {		
		_agentListPanel = new TitledPanel("Agents");
		_agentListPanel.getStyleClass().add("agents-panel");
		
		Node pane = _agentListPanel.getPane();
		_agentListPanel.setFillWidth(true);
		pane.setOnDragOver(e->{
			DnD.LocalClipboard clipboard = getLocalClipboard();
			if (clipboard.hasContent(DnD.VALUE_FORMAT)) {
				Field field = clipboard.get(DnD.FIELD_FORMAT, Field.class);
				if (_acceptableFields.contains(field.getName())) {
					e.acceptTransferModes(TransferMode.COPY);
					e.consume();
				}
			}
		});
		
		pane.setOnDragDropped(e->{
			DnD.LocalClipboard clipboard = getLocalClipboard();
			
			String value = clipboard.get(DnD.VALUE_FORMAT, Object.class).toString();
			String field = clipboard.get(DnD.FIELD_FORMAT, Field.class).getName();
			
			// ensure we don't already have this field
			for (AgentInfo agent : _agents) {
				if (agent.field.equals(field) && agent.value.equals(value)) {
					e.consume();
					return;
				}	
			}
			
			addAgent(field, value);
			e.setDropCompleted(true);
			e.consume();
		});
		
		return _agentListPanel;
	}

	public Node buildNuclideCtrl() {
		VBox vbox = new VBox();
		vbox.getStyleClass().add("infobar");

		Text title = new Text("Nuclide");
		title.getStyleClass().add("title");
		
		_filters.getStyleClass().add("nuclide");
		
		_filters.setPromptText("filter");
		_filters.setEditable(true);
		_filters.setItems(_nuclideFilterNames);
		
		_filters.valueProperty().addListener(o->selectNuclideFilter(_filters.getValue()));

		vbox.getChildren().addAll(
			title,
			_filters
		);
		;
		return vbox;
	}
	

	private void addAgent(final AgentInfo info) {
		_agents.add(info);
		info.inventory.bind(fetchInventory(info));
	}
	
	private ReadOnlyObjectProperty<ObservableList<Inventory>>  fetchInventory(AgentInfo info) {
		final String field = info.field;
		final String value = info.value;
		
		Task<ObservableList<Inventory>> task = new Task<ObservableList<Inventory>>() {
			@Override
			protected ObservableList<Inventory> call() throws Exception {
				ObservableList<Inventory> list = FXCollections.observableArrayList();
				list.setAll(_simProxy.getInventory2(field, value));

				List<Integer> nuclides = new ArrayList<>();
				for (Inventory i : list) {
					if (!nuclides.contains(i.nucid))
						nuclides.add(i.nucid);
				}
				return list;
			}	
		};
		
		info.setTask(task);
		
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();	
				
		return task.valueProperty();
	}
	
	private void addToChart(AgentInfo info) {
		List<Pair<Integer, Double>> series = new ArrayList<>();
		Pair<Integer, Double> current =  null;
		
		for (Inventory i : info.filteredInventory) {
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
		
		info.series = series;
		if (info.active)
			_chart.add(info);
	}
	
//	private Node buildChart() {
//		_chart = new InventoryChart();	
//		return _chart;
//	}
	
	private void setupActions() {
		List<Node> actions = new ArrayList<>();
		actions.add(createAxisOptions());
		actions.add(createModeActions());
		actions.add(createExportActions());
		addActions(actions);
	}
	
	private Node createAxisOptions() {
		final Button btn = new Button("Axis", GlyphRegistry.get(AwesomeIcon.CARET_DOWN));
		btn.getStyleClass().add("flat-button");
		
		final ContextMenu menu = new ContextMenu();
		btn.setOnMousePressed(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				menu.show(btn, Side.BOTTOM, 0, 0);
			}
		});
		
		MenuItem item = new MenuItem("Y linear", GlyphRegistry.get(AwesomeIcon.CHECK));
		item.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				_chart.axisMode().set(CyclistAxis.Mode.LINEAR);
            }
		});
		item.getGraphic().visibleProperty().bind(Bindings.equal(_chart.axisMode(), CyclistAxis.Mode.LINEAR));
		menu.getItems().add(item);
		
		item = new MenuItem("Y log", GlyphRegistry.get(AwesomeIcon.CHECK));
		item.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				_chart.axisMode().set(CyclistAxis.Mode.LOG);
            }
		});
		item.getGraphic().visibleProperty().bind(Bindings.equal(_chart.axisMode(), CyclistAxis.Mode.LOG));
		menu.getItems().add(item);
		
		item = new MenuItem("Y force zero", GlyphRegistry.get(AwesomeIcon.CHECK));
		item.getGraphic().visibleProperty().bind(_chart.forceZero());
		item.disableProperty().bind(Bindings.equal(_chart.getMode(), InventoryChart.ChartMode.STACKED));
		menu.getItems().add(item);
		item.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				_chart.forceZero().set(!_chart.forceZero().get());
			}
		});
		
		return btn;
	}
	
	private Node createModeActions() {
		final Button button = new Button("Options", GlyphRegistry.get(AwesomeIcon.CARET_DOWN));
		button.getStyleClass().add("flat-button");

		// create menu
		final ContextMenu contextMenu = new ContextMenu();
		
		// line chart
		MenuItem item = new MenuItem("Line chart",GlyphRegistry.get(AwesomeIcon.CHECK));
		item.getGraphic().visibleProperty().bind(Bindings.equal(_chart.getMode(), InventoryChart.ChartMode.LINE));
		item.setOnAction(new EventHandler<ActionEvent>() {		
			@Override
			public void handle(ActionEvent event) {
				_chart.setMode(InventoryChart.ChartMode.LINE);
				if (!_lastForceZero)
					_chart.forceZero().set(false);
			}
		});
		contextMenu.getItems().add(item);
		
//		// area chart
//		item = new MenuItem("Area chart");
//		item.setOnAction(new EventHandler<ActionEvent>() {		
//			@Override
//			public void handle(ActionEvent event) {
//				_chart.setMode(InventoryChart.ChartMode.AREA);
//			}
//		});
//		contextMenu.getItems().add(item);
		
		// stacked chart
		item = new MenuItem("Stacked chart",GlyphRegistry.get(AwesomeIcon.CHECK));
		item.getGraphic().visibleProperty().bind(Bindings.equal(_chart.getMode(), InventoryChart.ChartMode.STACKED));
		item.setOnAction(new EventHandler<ActionEvent>() {		
			@Override
			public void handle(ActionEvent event) {
				_lastForceZero = _chart.forceZero().get();
				if (!_lastForceZero) 
					_chart.forceZero().set(true);
				_chart.setMode(InventoryChart.ChartMode.STACKED);
			}
		});
		contextMenu.getItems().add(item);
		
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		MenuItem checked = new MenuItem("Show total",GlyphRegistry.get(AwesomeIcon.CHECK));
		checked.getGraphic().visibleProperty().bind(_chart.getShowTotal());
		checked.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				_chart.setShowTotal(!_chart.getShowTotal().getValue());
            }
		});
		contextMenu.getItems().add(checked);
		
		button.setOnMousePressed(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				contextMenu.show(button, Side.BOTTOM, 0, 0);
			}
		});
		
		return button;
	}
	
	class AgentInfo {
		public String field;
		public String value;
		public Color color;
		public Boolean active;
		public ListProperty<Inventory> inventory = new SimpleListProperty<>();
		public FilteredList<Inventory> filteredInventory; 
		
		public List<Pair<Integer, Double>> series = null;
		public ObjectProperty<Task<?>> taskProperty = new SimpleObjectProperty<>();
		
		public AgentInfo(String field, String value) {
			this.field = field;
			this.value = value;
			this.active = true;
			color = Configuration.getInstance().getColor(getName());
			
			inventory.addListener((Observable o)->{
				if (inventory.get() != null) {
					filteredInventory = new FilteredList<Inventory>(inventory.get());
					filteredInventory.addListener((Observable e)->{
						addToChart(this);
					});
					filteredInventory.predicateProperty().bind(_currentNuclideFilterProperty);			
				}
			});
		}
		
		public String getName() {
			return field+"="+value;
		}
		
		public void setTask(Task<?> task) {
			taskProperty.set(task);
		}
		
		public Task<?> getTask() {
			return taskProperty.get();
		}
	}
	
	class AgentEntry extends HBox {
		public AgentInfo info;
		private Status _status;
		private Consumer<AgentEntry> _onClose = null;
		
		public AgentEntry(final AgentInfo info) {
			super();
			this.info = info;
			
			getStyleClass().add("agent");
			Label text = new Label(info.value);
			text.setStyle("-fx-background-color:"+ColorUtil.toString(info.color));
			
			Node button = GlyphRegistry.get(AwesomeIcon.TIMES, "10px");
			button.setVisible(false);
			
			_status = new Status();
			getChildren().addAll(text, new Spring(), _status, button);
			
			info.taskProperty.addListener(o->_status.setTask(info.getTask()));
			setOnMouseEntered(e->{
				button.setVisible(true);
				getStyleClass().add("hover");
			});
			
			setOnMouseExited(e->{
				button.setVisible(false);
				getStyleClass().remove("hover");
			});
			
			setOnMouseClicked(e->{
				info.active = !info.active;
				text.setDisable(!info.active);
				if (info.active) {
					_chart.add(info);
				} else {
					_chart.remove(info);
				}
				
			});
			
			button.setOnMouseClicked(e->{
				if (_onClose != null) {
					_onClose.accept(this);
				}
				e.consume();
			});
			
			HBox.setHgrow(text, Priority.ALWAYS);
		}
		
		public void setTask(Task<?> task) {
			_status.setTask(task);
		}
		
		public void setOnClose(Consumer<AgentEntry> cb) {
			setTask(null);
			_onClose = cb;
		}
	}
	
	class Status extends Pane {
		private Task<?> _task = null;
		private Node _icon;
		private RotateTransition _animation; 
		
		
		public Status() {
			super();
			_icon = GlyphRegistry.get(AwesomeIcon.REFRESH, "10px");
			getChildren().add(_icon);
			
			_animation = new RotateTransition(Duration.millis(500), _icon);
			_animation.setFromAngle(0);
			_animation.setByAngle(360);
			_animation.setCycleCount(Animation.INDEFINITE);
			_animation.setInterpolator(Interpolator.LINEAR);
			setVisible(false);
			setOnMouseClicked(e->_task.cancel());
		}
		
		public void setTask(Task<?> task) {
			if (_task != null) {
				_task.cancel();
				_animation.stop();
				visibleProperty().unbind();
				log.info("Task Canceled");
			}
			
			_task = task;
			if (_task != null) {
				visibleProperty().bind(task.runningProperty());
				_task.runningProperty().addListener(o->{
					SimpleBooleanProperty running= (SimpleBooleanProperty) o;
					if (running.get()) {
						_animation.play();
					} else {
						_animation.stop();
					}
				});

				task.setOnFailed(e->{
					log.error(_task.getMessage());
					setTask(null);
				});
			}
		}
		
	}
}
