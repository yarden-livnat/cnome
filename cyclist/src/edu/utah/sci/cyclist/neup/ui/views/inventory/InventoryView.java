package edu.utah.sci.cyclist.neup.ui.views.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import edu.utah.sci.cyclist.core.event.Pair;
import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.model.Configuration;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.ui.components.CyclistViewBase;
import edu.utah.sci.cyclist.core.ui.panels.TitledPanel;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import edu.utah.sci.cyclist.neup.model.Inventory;
import edu.utah.sci.cyclist.neup.model.proxy.SimulationProxy;

public class InventoryView extends CyclistViewBase {
	public static final String ID = "inventory-view";
	public static final String TITLE = "Inventory";

	private static final String NET_CHART_LABEL = "Net";
	private static final String COMMULATIVE_CHART_LABEL = "Commulative";
	
	private ObservableList<AgentInfo> _agents = FXCollections.observableArrayList();
	private List<String> _acceptableFields = new ArrayList<>();
	
	private Simulation _currentSim = null;
	private SimulationProxy _simProxy = null;
	
	private InventoryChart _chart;
	
	class AgentInfo {
		public String field;
		public String value;
		public Color color;
		public ListProperty<Inventory> inventory = new SimpleListProperty<Inventory>();
		
		public AgentInfo(String field, String value) {
			this.field = field;
			this.value = value;
			color = Configuration.getInstance().getColor(field);;
		}
		
		public String getName() {
			return field+"="+value;
		}
	}
	
	public InventoryView() {
		super();
		init();
		build();
	}
	
	private void selectChartType(String value) {
	
	}
	
	@Override
	public void selectSimulation(Simulation sim, boolean active) {
		super.selectSimulation(sim, active);
		
		if (!active && sim != _currentSim) {
			return; // ignore
		}
		
		_currentSim = active? sim : null;

		_simProxy = _currentSim == null ?  null : new SimulationProxy(_currentSim);
		
		//TODO: re-fetch inventories 
	}
	
	private void init() {
		_acceptableFields.add("Implementation");
		_acceptableFields.add("Prototype");
		_acceptableFields.add("AgentID");
		_acceptableFields.add("InstitutionID");
	}
	
	private void build() {
		setTitle(TITLE);
		getStyleClass().add("inventory");
	
		BorderPane pane = new BorderPane();
		pane.setLeft(buildCtrl());
		pane.setCenter(buildChart());
		
		setContent(pane);
	}
	
	private Node buildCtrl() {
		VBox vbox = new VBox();
		vbox.getStyleClass().add("ctrl");
		
		vbox.getChildren().addAll(
//			buildChartCtrl(),
			buildAgentCtrl(),
			buildNuclideCtrl()
		);
		
		return vbox;
	}
	
	private Node buildChartCtrl() {
		VBox vbox = new VBox();
		vbox.getStyleClass().add("ctrl");
		
		ChoiceBox<String> type = new ChoiceBox<>();
		type.getStyleClass().add("choice");
		type.getItems().addAll(COMMULATIVE_CHART_LABEL, NET_CHART_LABEL);
		type.valueProperty().addListener(e->{
			selectChartType(type.getValue());
		});
		
		type.setValue(COMMULATIVE_CHART_LABEL);
		
		vbox.getChildren().add(type);
		return vbox;
	}
	
	public Node buildAgentCtrl() {
		
		TitledPanel panel = new TitledPanel("Agents", GlyphRegistry.get(AwesomeIcon.BUILDING));
		
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
			
			addAgent(entry);	

			panel.getContent().getChildren().add(entry);
//			entry.setOnClose(item->{
//				_agents.remove(item.info);
//				panel.getContent().getChildren().remove(item);
//			});
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
		
		TextField entry = new TextField();
		entry.getStyleClass().add("nuclide");
		entry.setPromptText("filter");

		vbox.getChildren().addAll(
			title,
			entry
		);
		
		
		//entry.setOnAction(e->isoFilterChanged(entry.getText()));
		return vbox;
	}
	

	private void addAgent(final AgentEntry entry) {
		_agents.add(entry.info);
		entry.info.inventory.addListener((Observable o)->{
			addToChart(entry.info);
		});

		entry.info.inventory.bind(fetchInventory(entry));
	}
	
	private ReadOnlyObjectProperty<ObservableList<Inventory>>  fetchInventory(AgentEntry entry) {
		final String field = entry.info.field;
		final String value = entry.info.value;
		
		Task<ObservableList<Inventory>> task = new Task<ObservableList<Inventory>>() {
			@Override
			protected ObservableList<Inventory> call() throws Exception {
				ObservableList<Inventory> list = _simProxy.getInventory(field, value);
				return list;
			}	
		};
		
		entry.setTask(task);
		
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();	
				
		return task.valueProperty();
	}
	
	 
	private void addToChart(AgentInfo info) {
		List<Pair<Integer, Double>> series = new ArrayList<>();
		Pair<Integer, Double> current =  null;
		
		// collect data. TODO: apply filters
		for (Inventory i : info.inventory) {
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

		_chart.add(info, info.getName(), series);
	}
	
	private Node buildChart() {
		_chart = new InventoryChart();
		
		return _chart;
	}
	
	
	class AgentEntry extends HBox {
		public AgentInfo info;
		private Status _status;
		private Consumer<AgentEntry> _onClose = null;
		
		public AgentEntry(final AgentInfo info) {
			super();
			this.info = info;
			
			getStyleClass().add("agent");
			Text text = new Text(info.value);
			
			Node button = GlyphRegistry.get(AwesomeIcon.TIMES, "10px");
			button.setVisible(false);
			
			_status = new Status();
			getChildren().addAll(text, _status, button);
			
			setOnMouseEntered(e->{
				button.setVisible(true);
				getStyleClass().add("hover");
			});
			
			setOnMouseExited(e->{
				button.setVisible(false);
				getStyleClass().remove("hover");
			});
			
			button.setOnMouseClicked(e->{
				if (_onClose != null) {
					_onClose.accept(this);
				}
			});
			
			HBox.setHgrow(this, Priority.ALWAYS);
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
			
			_animation = new RotateTransition(Duration.millis(10000), _icon);
			_animation.setFromAngle(0);
			_animation.setByAngle(3600);
			_animation.setCycleCount(Animation.INDEFINITE);
			setVisible(false);
			setOnMouseClicked(e->_task.cancel());
		}
		
		public void setTask(Task<?> task) {
			if (_task != null) {
				_task.cancel();
				_animation.stop();
				visibleProperty().unbind();
			}
			
			_task = task;
			if (_task != null) {
				visibleProperty().bind(task.runningProperty());
				_task.runningProperty().addListener(e->{
					if (_task.isRunning()) {
						_animation.play();
					} else {
						_animation.stop();
					}
				});

				task.setOnFailed(e->{
					System.out.println("Task failed:"+_task.getMessage());
					setTask(null);

					// TODO: save the error msg;
				});
			}
		}
		
	}
}
