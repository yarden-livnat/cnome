package edu.utah.sci.cyclist.ui.components;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.event.dnd.DnD.Status;
import edu.utah.sci.cyclist.event.ui.FilterEvent;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.model.Simulation;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.CyclistView;
import edu.utah.sci.cyclist.ui.panels.SchemaPanel;

public class CyclistViewBase extends ViewBase implements CyclistView {
	private TaskControl _taskControl;

	private HBox _dataBar;
	private FilterArea _filtersArea;
	private HBox _simulationBar;
	
	class ButtonEntry {
		public ToggleButton button;
		public Boolean remote;
		
		public ButtonEntry(ToggleButton button, Boolean remote) {
			this.button = button;
			this.remote = remote;
		}	
	}
	
	private Map<Table, ButtonEntry> _buttons = new HashMap<>();
	private Map<Simulation, ButtonEntry> _simulationButtons = new HashMap<>();
	private int _numOfRemotes = 0;
	private int _numOfRemotesSimulations = 0;
	
	// Actions
	private Closure.V1<Table> _onTableDrop = null;
	private Closure.V1<Table> _onTableRemoved = null;
	private Closure.V2<Table, Boolean> _onTableSelectedAction = null;
	private Closure.V1<Filter> _onShowFilter = null;
	private Closure.V1<Filter> _onRemoveFilter = null;
	
	private Closure.V1<Simulation> _onSimulationDrop = null;
	private Closure.V2<Simulation, Boolean> _onSimulationSelectedAction = null;
	private Closure.V1<Simulation> _onSimulationRemoved = null;
	
	/**
	 * Constructor
	 * A non toplevel default constructor
	 */
	public CyclistViewBase() {	
		this(false);
	}
	
	/**
	 * Constructor
	 * @param toplevel specify if this view is a toplevel view that can not be moved or resized
	 */
	public CyclistViewBase(boolean toplevel) {
		super(toplevel);
		getStyleClass().add("view");

		_taskControl = new TaskControl();
		
		_dataBar = new HBox();
		_dataBar.setId("databar");
		_dataBar.getStyleClass().add("data-bar");
		_dataBar.setSpacing(2);
		_dataBar.setMinWidth(5);
		_dataBar.setFillHeight(true);
		_dataBar.setAlignment(Pos.CENTER_LEFT); 
		
		_dataBar.getChildren().add(new Label("|"));
		
		_simulationBar = new HBox();
		_simulationBar.setId("simulationbar");
		_simulationBar.getStyleClass().add("data-bar");
		_simulationBar.setSpacing(2);
		_simulationBar.setMinWidth(5);
		_simulationBar.setFillHeight(true);
		_simulationBar.setAlignment(Pos.CENTER_LEFT);
		_simulationBar.getChildren().add(new Label("|"));
		
				
		_filtersArea = new FilterArea();
		//Sets for the drop area all the possible drag and drop sources and their accepted transfer modes.
		Map<Class<?>, TransferMode[]> sourcesTransferModes = createDragAndDropModes();
		_filtersArea.setDragAndDropModes(sourcesTransferModes);

		 
		getHeader().getChildren().addAll(1,
				asList(
				_taskControl,
				new Label("Simulations:"),
				new Text("["),
				_simulationBar,
				new Text("] "),
				new Label("Tables:"),
				new Text("["),
				_dataBar,
				new Text("] "),
				new Label(" Filters:"),
				new Text("["),
				_filtersArea,
				new Text("]")
				));
		
		_dataBar.setAlignment(Pos.CENTER_LEFT);
		_simulationBar.setAlignment(Pos.CENTER_LEFT);
		
		setDatasourcesListeners();
		setFiltersListeners();
		setSimulationsListeners();
	}
	
	public ViewBase clone() {
		return null;
	}
	
	
	public void setCurrentTask(Task<?> task) {
		_taskControl.setTask(task);
		
	}
	
	public ObservableList<Filter> remoteFilters() {
		return _filtersArea.getRemoteFilters();
	}
	
	/*
	 * Actions 
	 */
	
	public void setOnTableDrop(Closure.V1<Table> action) {
		_onTableDrop = action;
	}
	
	public Closure.V1<Table> getOnTableDrop() {
		return _onTableDrop;
	}
	
	public void setOnTableRemoved(Closure.V1<Table> action) {
		_onTableRemoved = action;
		
	}
	public void setOnTableSelectedAction(Closure.V2<Table, Boolean> action) {
		_onTableSelectedAction = action;
	}
	
	/**
	 * Sets an external code to run from the current class, when simulation is dropped to the simulation bar.
	 * @param - Closure.V1<Simulation> action : the code to run when the simulation is dropped.
	 */
	public void setOnSimulationDrop(Closure.V1<Simulation> action) {
		_onSimulationDrop = action;
	}
	
	public Closure.V1<Simulation> getOnSimulationDrop() {
		return _onSimulationDrop;
	}
	
	/**
	 * Sets an external code to run from the current class, when simulation is selected.
	 * @param Closure.V2<Simulation, Boolean> action : the code to run when the simulation is selected.
	 */
	public void setOnSimulationSelectedAction(Closure.V2<Simulation, Boolean> action) {
		_onSimulationSelectedAction = action;
	}
	
	/**
	 * Sets an external code to run from the current class, when simulation is removed.
	 * @param Closure.V1<Simulation action : the code to run when the simulation is removed.
	 */
	public void setOnSimulationRemoved(Closure.V1<Simulation> action){
		_onSimulationRemoved = action;
	}
	
	public Closure.V1<Simulation> getOnSimulationRemoved(){
		return _onSimulationRemoved;
	}
	
	
	public void setOnShowFilter(Closure.V1<Filter> action) {
		_onShowFilter = action;
	}
	
	public Closure.V1<Filter> getOnShowFilter() {
		return _onShowFilter;
	}
	
	public void setOnRemoveFilter(Closure.V1<Filter> action) {
		_onRemoveFilter = action;
	}
	
	public Closure.V1<Filter> getOnRemoveFilter() {
		return _onRemoveFilter;
	}
	
	
	@Override
	public void addTable(final Table table, boolean remote, boolean active) {
		final ToggleButton button = new ToggleButton(table.getAlias().substring(0, 1));
		button.getStyleClass().add("flat-toggle-button");
//		button.setMaxSize(12, 12);
		button.setSelected(active);
		
		button.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean prevState, Boolean activate) {
				if (_onTableSelectedAction != null)
					_onTableSelectedAction.call(table, activate);
			}
		});
		
		button.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Dragboard db = button.startDragAndDrop(TransferMode.MOVE);
				
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.TABLE_FORMAT, Table.class, table);
				
				ClipboardContent content = new ClipboardContent();
				content.putString(table.getName());
				db.setContent(content);
			}
		});
		
		button.setOnDragDone(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				// ignore if the button was dragged onto self 
				if (getLocalClipboard().getStatus() != Status.IGNORED) {
					if (_onTableRemoved != null) {
						_onTableRemoved.call(table);
					}
				}
				
			}
		});
		
		_buttons.put(table, new ButtonEntry(button, remote));
		
		if (remote) {
			_dataBar.getChildren().add(_numOfRemotes, button);
			_numOfRemotes++;
		} else {
			_dataBar.getChildren().add(button);
		}
	}
	
	@Override
	public void removeTable(Table table) {
		ButtonEntry entry = _buttons.remove(table);
		if(entry != null){
			_dataBar.getChildren().remove(entry.button);
			if (entry.remote)
				_numOfRemotes--;
		}
	}
	
	@Override
	public void selectTable(Table table, boolean value) {
		try{
			_buttons.get(table).button.setSelected(value);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a new simulation button to the view. (remote or local)
	 * Decides whether or not make this button active (selected)
	 * Adds events handling.
	 * 
	 *  @param Simulation: The simulation to be added.
	 *  @param boolean remote: true if remote, false if local.
	 *  @param boolean active: Whether or not the button could be active. 
	 */
	@Override
	public void addSimulation(final Simulation simulation, boolean remote, boolean active ) {
		final ToggleButton button = new ToggleButton(simulation.getAlias().substring(0, 1));
		button.getStyleClass().add("flat-toggle-button");
		button.setSelected(active);
		
		button.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean prevState, Boolean activate) {
				if (_onSimulationSelectedAction != null)
					_onSimulationSelectedAction.call(simulation, activate);
			}
		});
		
		button.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Dragboard db = button.startDragAndDrop(TransferMode.MOVE);
				
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.SIMULATION_FORMAT, Simulation.class, simulation);
				
				ClipboardContent content = new ClipboardContent();
				content.putString(simulation.getSimulationId());
				db.setContent(content);
			}
		});
		
		button.setOnDragDone(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				// ignore if the button was dragged onto self 
				if (getLocalClipboard().getStatus() != Status.IGNORED) {
					if (_onSimulationRemoved != null) {
						_onSimulationRemoved.call(simulation);
					}
					//Should be done only as the last action to unable unselection of the removed button.
					removeSimulation(simulation);
				}
				
			}
		});
		
		_simulationButtons.put(simulation, new ButtonEntry(button, remote));
		if (remote) {
			_simulationBar.getChildren().add(_numOfRemotesSimulations, button);
			_numOfRemotesSimulations++;
		} else {
			_simulationBar.getChildren().add(button);
		}
	}
	
	/**
	 * Handles the removal of a simulation button.
	 * @param Simulation : the simulation to be removed.
	 */
	@Override
	public void removeSimulation(Simulation simulation) {
		ButtonEntry entry = _simulationButtons.remove(simulation);
		if(entry != null){
			_simulationBar.getChildren().remove(entry.button);
			if (entry.remote)
				_numOfRemotesSimulations--;
		}
	}
	
	/**
	 * Selects/deselects  the button of the specified simulation and makes it active/not active.
	 * @param Simulation : the simulation to be selected.
	 * @param boolean value : whether to select or deselect the button.
	 */
	@Override
	public void selectSimulation(Simulation simulation, boolean value) {
		try{
			_simulationButtons.get(simulation).button.setSelected(value);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	

	/*
	 * 
	 */
	
	//Let the sub classes have access to the filters area
	protected FilterArea getFiltersArea(){
		return _filtersArea;
	}
	
	/*
	 * Listeners
	 */

	public ObservableList<Filter> filters() {
		return _filtersArea.getFilters();
	}
	
	private void setFiltersListeners() {
		_filtersArea.setOnAction(new EventHandler<FilterEvent>() {
			
			@Override
			public void handle(FilterEvent event) {
				if (event.getEventType() == FilterEvent.SHOW) {
						if (_onShowFilter != null) {
							_onShowFilter.call(event.getFilter());
						} 
				} else if (event.getEventType() == FilterEvent.REMOVE_FILTER_FIELD) {
					// If filter is connected to a field and its sql function, clean the field when the filter is removed.
					removeFilterFromDropArea(event.getFilter());
				} 
			}
		});
	}
	
	//Virtual method - should only be implemented in the sub class.
	public void removeFilterFromDropArea(Filter filter){
		;
	}
	
	private void setSimulationsListeners() {
		_simulationBar.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Simulation simulation = getLocalClipboard().get(DnD.SIMULATION_FORMAT, Simulation.class);
				if ( simulation != null ) {
					if (_simulationButtons.containsKey(simulation)) {
						event.acceptTransferModes(TransferMode.NONE);
					} else {
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					}
				}
				event.consume();
			}
		});
		
		_simulationBar.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Simulation simulation = getLocalClipboard().get(DnD.SIMULATION_FORMAT, Simulation.class);
				if (simulation != null) {
					if (_simulationButtons.containsKey(simulation)) {
						getLocalClipboard().setStatus(Status.IGNORED);
					} else {	
						getLocalClipboard().setStatus(Status.ACCEPTED);
						addSimulation(simulation, false /*remote*/, true /*active*/);
						if (_onSimulationDrop != null) {
							_onSimulationDrop.call(simulation);
						}
						//selectSimulation(simulation, true);
						event.setDropCompleted(true);
						event.consume();
					}
				}
				
			}
		});
	}
	
	private void setDatasourcesListeners() {
		_dataBar.setOnDragEntered(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Table table = getLocalClipboard().get(DnD.TABLE_FORMAT, Table.class);
				if ( table != null ) {
					if (_buttons.containsKey(table)) {
						event.acceptTransferModes(TransferMode.NONE);
					} else {
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					}
				}
				event.consume();
			}
		});
		
		_dataBar.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Table table = getLocalClipboard().get(DnD.TABLE_FORMAT, Table.class);
				if ( table != null ) {
					if (_buttons.containsKey(table)) {
						event.acceptTransferModes(TransferMode.NONE);
					} else {
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					}
				}
				event.consume();
			}
		});
		
		_dataBar.setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
//				event.consume();
			}
		});
		
		_dataBar.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				Table table = getLocalClipboard().get(DnD.TABLE_FORMAT, Table.class);
				if (table != null) {
					if (_buttons.containsKey(table)) {
						getLocalClipboard().setStatus(Status.IGNORED);
					} else 	if (_onTableDrop != null) {
						getLocalClipboard().setStatus(Status.ACCEPTED);
						_onTableDrop.call(table);
						event.setDropCompleted(true);
						event.consume();
					}
				}
				
			}
		});
	}
	
	/*
     * Name: createDragAndDropModes
     * Returns: Map<Class<?>, TransferMode[]>
     * Description: Maps for each possible drag and drop source the accepted transfer modes.
     */
	private Map<Class<?>, TransferMode[]> createDragAndDropModes() {
		Map<Class<?>, TransferMode[]> sourcesTransferModes = new HashMap<Class<?>, TransferMode[]>();
		sourcesTransferModes.put(DropArea.class, new TransferMode[]{TransferMode.COPY});
        sourcesTransferModes.put(SchemaPanel.class, new TransferMode[]{TransferMode.COPY});
        sourcesTransferModes.put(FilterArea.class, new TransferMode[]{TransferMode.COPY});
        return sourcesTransferModes;
	}
}
