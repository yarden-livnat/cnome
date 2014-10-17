package edu.utah.sci.cyclist.neup.ui.views.inventory;

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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.core.event.Pair;
import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.model.Configuration;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.ui.components.CyclistViewBase;
import edu.utah.sci.cyclist.core.ui.components.Spring;
import edu.utah.sci.cyclist.core.ui.panels.TitledPanel;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.ColorUtil;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import edu.utah.sci.cyclist.neup.model.Inventory;
import edu.utah.sci.cyclist.neup.model.NuclideFiltersLibrary;
import edu.utah.sci.cyclist.neup.model.proxy.SimulationProxy;
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
	
	private SimulationProxy _simProxy = null;
		
	private InventoryChart _chart = new InventoryChart();
	
	
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
		
		ChoiceBox<ChartType> type = new ChoiceBox<>();
		type.getStyleClass().add("choice");
		type.getItems().addAll(ChartType.values());
		type.valueProperty().addListener(e->{
			selectChartType(type.getValue());
		});
		
		type.setValue(ChartType.INVENTORY);
		
		vbox.getChildren().add(type);
		return vbox;
	}
	
	public Node buildAgentCtrl() {		
		TitledPanel panel = new TitledPanel("Agents");
		panel.getStyleClass().add("agents-panel");
		
		Node pane = panel.getPane();
		panel.setFillWidth(true);
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
			AgentInfo info = new AgentInfo(field, value);	
			AgentEntry entry = new AgentEntry(info);
			panel.getContent().getChildren().add(entry);
			entry.setOnClose(item->{
				_agents.remove(item.info);
				panel.getContent().getChildren().remove(item);
				_chart.remove(item.info);
			});
			
			addAgent(info);	
			e.setDropCompleted(true);
			e.consume();
		});
		
		return panel;
	}

	public Node buildNuclideCtrl() {
		VBox vbox = new VBox();
		vbox.getStyleClass().add("infobar");

		Text title = new Text("Nuclide");
		title.getStyleClass().add("title");
		
		ComboBox<String> filters = new ComboBox<>();
		filters.getStyleClass().add("nuclide");
		
		filters.setPromptText("filter");
		filters.setEditable(true);
		filters.setItems(_nuclideFilterNames);
		
		filters.valueProperty().addListener(o->selectNuclideFilter(filters.getValue()));

		vbox.getChildren().addAll(
			title,
			filters
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
		actions.add(createModeActions());
		addActions(actions);
	}
	
	private Node createModeActions() {
		final Button button = new Button("Options", GlyphRegistry.get(AwesomeIcon.CARET_DOWN));
		button.getStyleClass().add("flat-button");

		// create menu
		final ContextMenu contextMenu = new ContextMenu();
		
		// line chart
		MenuItem item = new MenuItem("Line chart");
		item.setOnAction(new EventHandler<ActionEvent>() {		
			@Override
			public void handle(ActionEvent event) {
				_chart.setMode(InventoryChart.ChartMode.LINE);
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
		item = new MenuItem("Stacked chart");
		item.setOnAction(new EventHandler<ActionEvent>() {		
			@Override
			public void handle(ActionEvent event) {
				_chart.setMode(InventoryChart.ChartMode.STACKED);
			}
		});
		contextMenu.getItems().add(item);
		
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		CheckMenuItem checked = new CheckMenuItem("Show total");
		checked.setSelected(_chart.getShowTotal());
		checked.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				_chart.setShowTotal(!_chart.getShowTotal());
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
						log.info("Fetch invetory");
					} else {
						_animation.stop();
						log.info("Fetch invetory completed");
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
