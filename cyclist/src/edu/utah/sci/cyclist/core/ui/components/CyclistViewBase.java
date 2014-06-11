package edu.utah.sci.cyclist.core.ui.components;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.event.dnd.DnD.LocalClipboard;
import edu.utah.sci.cyclist.core.event.dnd.DnD.Status;
import edu.utah.sci.cyclist.core.event.dnd.DnDSource;
import edu.utah.sci.cyclist.core.event.ui.FilterEvent;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Filter;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.model.ValueFilter;
import edu.utah.sci.cyclist.core.ui.CyclistView;
import edu.utah.sci.cyclist.core.ui.panels.SchemaPanel;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

public class CyclistViewBase extends ViewBase implements CyclistView {
	static final Logger log = LogManager.getLogger(CyclistViewBase.class.getName());
	
	class Info<T> {
		public T item;
		public boolean remote;
		
		public Info(T item, boolean remote) {
			this.item = item;
			this.remote = remote;
		}
	}
	
	private TaskControl _taskControl;

	private Map<String, Info<Table>> _tables = new HashMap<>();
	private Map<String, Info<Simulation>> _sims = new HashMap<>(); 

	private ChoiceBox<String> _tableChoice;
	private ChoiceBox<String> _simChoice;
	private Label _tableGlyph;
	private Label _simGlyph;
	
    private Map<Class<?>, TransferMode[]> _sourcesTransferModes;
	private FilterArea _filtersArea;
	private Button _filterGlyph;
	private boolean _supportsFiltering = true;
	private boolean _supportsTables = true;
	
	private Simulation _currentSim = null;
	
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
				
		//Sets for the drop area all the possible drag and drop sources and their accepted transfer modes.
		_sourcesTransferModes = createDragAndDropModes();

		_tableGlyph = GlyphRegistry.get(AwesomeIcon.COLUMNS);
		_tableGlyph.setVisible(false);
		_tableChoice = new ChoiceBox<>();
		_tableChoice.getStyleClass().add("flat-button");
		_tableChoice.setVisible(false);
		
		_simGlyph = GlyphRegistry.get(AwesomeIcon.COGS);
		_simGlyph.setVisible(isToplevel());
		_simChoice = new ChoiceBox<>();
		_simChoice.getStyleClass().add("flat-button");
		_simChoice.setVisible(false);
		
		_filtersArea = new FilterArea();
		_filtersArea.setVisible(false);
		_filterGlyph = new Button("", GlyphRegistry.get(AwesomeIcon.FILTER));
		_filterGlyph.getStyleClass().add("flat-button");
		_filterGlyph.setVisible(false);
		
		getHeader().getChildren().addAll(1,
				asList(
				_tableGlyph,
				_tableChoice,
				_simGlyph,
				_simChoice,
				_filterGlyph,
				_filtersArea
				));
		
		List<Node> actions = new ArrayList<>();
		actions.add(_taskControl);
		addActions(actions);
		
		setListeners();
		setFiltersListeners();
	}
	
	public ViewBase clone() {
		return null;
	}
	
	
	public void setCurrentTask(Task<?> task) {
		_taskControl.setTask(task);
		
	}
	
	public boolean getSupportsFiltering() {
		return _supportsFiltering;
	}
	
	public void setSupportsFiltering(boolean value) {
		_supportsFiltering = value;
	}
	
	public boolean getSupportsTables() {
		return _supportsTables;
	}
	
	public void setSupportsTables(boolean value) {
		_supportsTables = value;
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
//		System.out.println("CViewBase:add table:"+table.getName()+" r:"+remote+" active:"+active);
		if (!_supportsTables) return;
		
		Info<Table> info = new Info<Table>(table, remote);
		_tables.put(table.getName(), info);
		_tableChoice.getItems().add(table.getName());
		_tableChoice.setVisible(true);
		_tableGlyph.setVisible(true);
		if (active) {
			_tableChoice.setValue(table.getName());
		}
	}
	
	@Override
	public void removeTable(Table table) {
		if (!_supportsTables) return;
		
		_tables.remove(table.getName());
		_tableChoice.getItems().remove(table.getName());
		_tableChoice.setVisible(_tableChoice.getItems().size()>0);
		_tableGlyph.setVisible(_tableChoice.getItems().size()>0);
	}
	
	@Override
	public void selectTable(Table table, boolean value) {
//		System.out.println("CViewBase:select table:"+table.getName()+" v:"+value);
		if (!_supportsTables) return;
		
		if (value) {
			_tableChoice.setValue(table.getName());
		}
		else if (table.getName().equals(_tableChoice.getValue())) {
			_tableChoice.setValue(null);
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
		Info<Simulation> info = new Info<>(simulation, remote);
		_sims.put(simulation.getAlias(), info);
		_simChoice.getItems().add(simulation.getAlias()); 
		if (_sims.size() > 1 || !remote) {
			_simChoice.setVisible(true);
			_simGlyph.setVisible(true);
		}
		if (active) {
			_simChoice.setValue(simulation.getAlias());
		}
	}
	
	/**
	 * Handles the removal of a simulation button.
	 * @param Simulation : the simulation to be removed.
	 */
	@Override
	public void removeSimulation(Simulation simulation) {
		_sims.remove(simulation.getAlias());
		_simChoice.getItems().remove(simulation.getAlias());
		boolean visible = _sims.size()> 1 || (_sims.size() == 1 && !_sims.get(_simChoice.getItems().get(0)).remote);
		_simChoice.setVisible(visible);
		_simGlyph.setVisible(isToplevel() || visible);
	}
	
	/**
	 * Selects/deselects  the button of the specified simulation and makes it active/not active.
	 * @param Simulation : the simulation to be selected.
	 * @param boolean value : whether to select or deselect the button.
	 */
	@Override
	public void selectSimulation(Simulation simulation, boolean active) {
		if (active)
			_simChoice.setValue(simulation.getAlias());
		else if (simulation.getAlias().equals(_simChoice.getValue())) {
			_simChoice.setValue(null);
		}
		if (!active && simulation != _currentSim) {
			return; // ignore
		}
		
		_currentSim = active? simulation : null;
	}
	
	//Let the sub classes have access to the filters area
	protected FilterArea getFiltersArea(){
		return _filtersArea;
	}
	
	//Let the sub classes have access to the current simulation.
	protected Simulation getCurrentSimulation(){
		return _currentSim;
	}
	
	public ObservableList<Filter> filters() {
		return _filtersArea.getFilters();
	}
	
	public ObservableList<Filter> remoteFilters() {
		return _filtersArea.getRemoteFilters();
	}
	
	//Notification about changes in the simulations list
	public ObservableList<String> simulations(){
		return _simChoice.getItems();
	}
	
	public ObservableValue<String> lastChosenSimulation(){
		return _simChoice.valueProperty();
	}
	
	/*
	 * Listeners
	 */


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
		
		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				_filtersArea.setVisible(filters().size() > 0 
						|| (_filtersArea.getShowRemote() && remoteFilters().size() > 0));
				_filterGlyph.setVisible(filters().size() > 0 || remoteFilters().size() > 0);		
			}
		};
		
		filters().addListener(listener);
		remoteFilters().addListener(listener);
		_filtersArea.showRemoteProperty().addListener(listener);
	}
	
	//Virtual method - should only be implemented in the sub class.
	public void removeFilterFromDropArea(Filter filter){
		;
	}
	
	
	private void setListeners() {
		getHeader().setOnDragEntered(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
			}
		});
		
		getHeader().setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				boolean handle = true;
				LocalClipboard clipboard = getLocalClipboard();
				if (getLocalClipboard().hasContent(DnD.VALUE_FORMAT)) {
					if (_supportsFiltering)
						event.acceptTransferModes(TransferMode.COPY);
				}
				else if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT) || getLocalClipboard().hasContent(DnD.FILTER_FORMAT)) {
                    if(_supportsFiltering && getLocalClipboard().hasContent(DnD.DnD_SOURCE_FORMAT)){
                    	Class<?> key = getLocalClipboard().getType(DnD.DnD_SOURCE_FORMAT);
                	    
                    	//Accepts the drag and drop transfer mode according to the source and the 
                    	//predefined accepted transfer modes.}
                    	if(key != null && _sourcesTransferModes!= null &&_sourcesTransferModes.containsKey(key)){
                	    	event.acceptTransferModes(_sourcesTransferModes.get(key));
                	    }
                    }
				} else if (clipboard.hasContent(DnD.TABLE_FORMAT)) {
					if (_supportsTables) {
						Table table = clipboard.get(DnD.TABLE_FORMAT, Table.class);
						if (_tables.containsKey(table.getName())) {
							event.acceptTransferModes(TransferMode.NONE);
						} else {
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				} else if (clipboard.hasContent(DnD.SIMULATION_FORMAT)) {
					Simulation sim = clipboard.get(DnD.SIMULATION_FORMAT, Simulation.class);
					if (_sims.containsKey(sim.getAlias())) {
						event.acceptTransferModes(TransferMode.NONE);
					} else {
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					}
				} 
				else {
					handle = false;
				}
				
				if (handle)
					event.consume();
			}
		});
		
		getHeader().setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				//event.consume();
			}
		});
		
		getHeader().setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				boolean accept = true;
				// check if a value
				if (getLocalClipboard().hasContent(DnD.VALUE_FORMAT)) {
					if (_supportsFiltering) {
						Field field = getLocalClipboard().get(DnD.FIELD_FORMAT, Field.class);
						Object value = getLocalClipboard().get(DnD.VALUE_FORMAT, Object.class);
	             		Filter filter = new ValueFilter(field, value);
	             		 _filtersArea.getFilters().add(filter);
					}
				}
				// check if a field
				else if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT) ) {
					if (_supportsFiltering) {
	                    Field oldField = getLocalClipboard().get(DnD.FIELD_FORMAT, Field.class);
	                    if(oldField != null){
	                    	Field field = new Field(getLocalClipboard().get(DnD.FIELD_FORMAT, Field.class));
	                        boolean found = false;
	                    	for (Filter f : _filtersArea.getFilters()) {
	                    		if (f.getField().similarWithTable(field)) {
	                    			// already have a filter for this field
	                    			found = true;
	                    			break;
	                    		}
	                    	}
	                    	if (!found) {
		                    	Filter filter = new Filter(field);
		                        _filtersArea.getFilters().add(filter);
		                        if (_filtersArea.getOnAction() != null) {
		                        	_filtersArea.getOnAction().handle(new FilterEvent(FilterEvent.SHOW, filter));
		                        }
	                    	}
	                    }
					}
	            } 
				// check if table
				else if (getLocalClipboard().hasContent(DnD.TABLE_FORMAT)) {
					if (_supportsTables) {
						Table table = getLocalClipboard().get(DnD.TABLE_FORMAT, Table.class);
						if (_tables.containsKey(table.getName())) {
							getLocalClipboard().setStatus(Status.IGNORED);
						} else if (_onTableDrop != null) {
							getLocalClipboard().setStatus(Status.ACCEPTED);
							_onTableDrop.call(table);	
						}
					}
				}
				// check if simulation
				else if (getLocalClipboard().hasContent(DnD.SIMULATION_FORMAT)) {
					Simulation sim = getLocalClipboard().get(DnD.SIMULATION_FORMAT, Simulation.class);
					if (_sims.containsKey(sim.getAlias())) {
						getLocalClipboard().setStatus(Status.IGNORED);
					} else if (_onSimulationDrop != null) {
						getLocalClipboard().setStatus(Status.ACCEPTED);
						_onSimulationDrop.call(sim);	
					}
				} 
				
				// check if a filter
				else if (getLocalClipboard().hasContent(DnD.FILTER_FORMAT)) {
	                    Filter filter = getLocalClipboard().get(DnD.FILTER_FORMAT, Filter.class);
	                    _filtersArea.getFilters().add(filter);
	            }
				
				// reject
				else {
					accept = false;
				}
				
				if (accept) {		
					event.setDropCompleted(true);
					event.consume();
				}
				
			}
		});
		
		_tableChoice.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String prev, String selected) {
//				System.out.println("CViewBase:table choice changed: selected:"+selected);
				if (_onTableSelectedAction != null) {
					if (selected != null)
						_onTableSelectedAction.call(_tables.get(selected).item, true);
					else
						_onTableSelectedAction.call(_tables.get(prev).item, false);
				}
			}
		});
		
		_simChoice.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String prev, String selected) {
				if (_onSimulationSelectedAction != null) {
					if (selected != null)
						_onSimulationSelectedAction.call(_sims.get(selected).item, true);
					else
						_onSimulationSelectedAction.call(_sims.get(prev).item, false);
				}
			}
		});
		
		// FilterGlyph
		final ContextMenu contextMenu = new ContextMenu();

		MenuItem item = new MenuItem("Show remotes", GlyphRegistry.get(AwesomeIcon.CHECK));
		item.getGraphic().visibleProperty().bind(_filtersArea.showRemoteProperty());
		item.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				_filtersArea.setShowRemote(!_filtersArea.getShowRemote());
			}
		});
		contextMenu.getItems().add(item);
		_filterGlyph.setOnMousePressed(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				contextMenu.show(_filterGlyph, Side.BOTTOM, 0, 0);
			}
		});
	}
	
//	 public void setDragAndDropModes( Map<Class<?>, TransferMode[]> sourcesTransferModes){
// 		_sourcesTransferModes = sourcesTransferModes;
// 	}
	
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
        sourcesTransferModes.put(DnDSource.class, new TransferMode[]{TransferMode.COPY});
        return sourcesTransferModes;
	}
}
