/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved.
 *
 * License for the specific language governing rights and limitations under Permission
 * is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: The above copyright notice 
 * and this permission notice shall be included in all copies  or substantial portions of the Software. 
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR 
 *  A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.core.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URI;
import java.util.Arrays;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.ToolsLibrary;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.event.ui.CyclistDropEvent;
import edu.utah.sci.cyclist.core.model.CyclistDatasource;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Model;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.model.ToolData;
import edu.utah.sci.cyclist.core.presenter.DatasourcesPresenter;
import edu.utah.sci.cyclist.core.presenter.SchemaPresenter;
import edu.utah.sci.cyclist.core.presenter.SimulationPresenter;
import edu.utah.sci.cyclist.core.presenter.ToolsPresenter;
import edu.utah.sci.cyclist.core.presenter.WorkspacePresenter;
import edu.utah.sci.cyclist.core.tools.TableTool;
import edu.utah.sci.cyclist.core.tools.ToolFactory;
import edu.utah.sci.cyclist.core.ui.MainScreen;
import edu.utah.sci.cyclist.core.ui.views.Workspace;
import edu.utah.sci.cyclist.core.ui.wizards.DatatableWizard;
import edu.utah.sci.cyclist.core.ui.wizards.SaveWsWizard;
import edu.utah.sci.cyclist.core.ui.wizards.SimulationWizard;


public class CyclistController {
	
	private final EventBus _eventBus;
	private MainScreen _screen;
	private WorkspacePresenter _presenter;
	private Model _model = new Model();
	//private String SAVE_DIR = System.getProperty("user.dir") + "/.cyclist/";
	private String SAVE_FILE = "save.xml";
	private WorkDirectoryController _workDirectoryController;
	private Boolean _dirtyFlag = false;
	private EventHandler<CyclistDropEvent> _handleToolDropped;
	private ListChangeListener<String> _handleMainWsSimulationsChanged;
	private ChangeListener<String> _handleMainWsSelectedSimulationChanged;
	
	private static final String SIMULATIONS_TABLES_FILE = "assets/SimulationTablesDef.xml";
	
	/**
	 * Constructor
	 * 
	 * @param eventBus
	 */
	public CyclistController(EventBus eventBus) {
		this._eventBus = eventBus;
		
		_workDirectoryController = new WorkDirectoryController();
		
		// If the save directory does not exist, create it
		File saveDir = new File(WorkDirectoryController.SAVE_DIR);
		if (!saveDir.exists())	
			saveDir.mkdir();  
	
		
		if(_workDirectoryController.initGeneralConfigFile())
		{
			_workDirectoryController.restoreGeneralConfigFile();
		}
		
//		load();
	}

	/**
	 * initialize the main screen
	 * @param screen
	 */
	public void setScreen(final MainScreen screen) {
		this._screen = screen;
		addActions();
			
		/*
		 *  wire panels
		 */
		
		// Tables panel
		DatasourcesPresenter ds = new DatasourcesPresenter(_eventBus);
		ds.setSources(_model.getSources());
		ds.setTables(_model.getTables());
		ds.setPanel(screen.getDatasourcesPanel());
		
		// Schema panel
		SchemaPresenter sp = new SchemaPresenter(_eventBus);
		sp.setPanel(screen.getFieldsPanel());
		
		//Simulation panel
		SimulationPresenter sip = new SimulationPresenter(_eventBus);
		sip.setSimIds(_model.getSimulationIds());
		sip.setSimPanel(screen.getSimulationPanel());
		
		
		// ToolsLibrary panel
		ToolsPresenter tp = new ToolsPresenter(_eventBus);
		tp.setPanel(screen.getToolsPanel());
		tp.setFactories(Arrays.asList(ToolsLibrary.factories));
		
		// set up the main workspace
		Workspace workspace = new Workspace(true);
//		workspace.setWorkDirPath(getLastChosenWorkDirectory());
		screen.setWorkspace(workspace);
		
		_presenter = new WorkspacePresenter(_eventBus);
		_presenter.setView(workspace);
		_screen.getWorkSpace().setOnToolDrop(_handleToolDropped);
		_screen.getWorkSpace().simulations().addListener(_handleMainWsSimulationsChanged);
		_screen.getWorkSpace().lastChosenSimulation().addListener(_handleMainWsSelectedSimulationChanged);
		
		// do something?
		//selectWorkspace();
		load();
	}
	
	/**
	 * selectWorkspace
	 * 
	 */
	public void selectWorkspace() {
		
		if(_workDirectoryController == null){
			return;
		}
		
		ObservableList<String> selection = _screen.selectWorkspace(_workDirectoryController.getWorkDirectories(),
																   _workDirectoryController.getLastChosenIndex());
		
		selection.addListener(new ListChangeListener<String>(){

			@Override
			public void onChanged(Change<? extends String> list ){
				if(_workDirectoryController != null){
					if(_workDirectoryController.handleWorkDirectoriesListChangedEvent(list)){
						load();
						
						//Set all the views to match the new tables.
						ObservableList<Field> emptyList = FXCollections.observableArrayList();
						_screen.getFieldsPanel().setFields(emptyList);
						
//						//Set the workspace to display the new path at the title.
//						Workspace workspace = _screen.getWorkSpace();
//						if(workspace != null){
//							workspace.setWorkDirPath(getLastChosenWorkDirectory());
//						}
						
					}
				}
				
			}
		});
		
	}
		
	private void addActions() {
		
		_screen.onAddDatasource().set(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				final DatatableWizard wizard = new DatatableWizard();
				wizard.setItems(_model.getSources());
				wizard.setSelectedSource(_model.getSelectedDatasource());
				String currDirectory = getLastChosenWorkDirectory();
				wizard.setWorkDir(currDirectory);
				ObjectProperty<Table> selection = wizard.show(_screen.getWindow());
				
			//	wizard.getDataSources()
				
				selection.addListener(new ChangeListener<Table>() {
					@Override
					public void changed(ObservableValue<? extends Table> arg0, Table oldVal, Table newVal) {
						if(newVal != null)
						{
							Table tbl = new Table(newVal);
							_model.getTables().add(tbl);
							_model.setSelectedDatasource(wizard.getSelectedSource());
							_dirtyFlag = true;
						}
					}
				});
				
			}
		});
		
		_screen.onAddSimulation().set(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				final SimulationWizard wizard = new SimulationWizard();
				
				wizard.setItems(_model.getSources());
				wizard.setSelectedSource(_model.getSelectedDatasource());
				String currDirectory = getLastChosenWorkDirectory();
				wizard.setWorkDir(currDirectory);
				ObservableList<Simulation> selection = wizard.show(_screen.getWindow());
				
				
				selection.addListener(new ListChangeListener<Simulation>() {
					@Override
					public void onChanged(ListChangeListener.Change<? extends Simulation> newList) {
						if(newList != null)
						{
							for(Simulation simulation:newList.getList()){
								if(!_model.simExists(simulation)){
									Simulation sim = simulation.clone();
									_model.getSimulationIds().add(sim);
									_dirtyFlag = true;
								}
							}
							_model.setSelectedDatasource(wizard.getSelectedSource());
						}
					}
				});
			}
		});
		
		_screen.onSelectWorkspace().set(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				selectWorkspace();
			}
			
		});
		
		_screen.onSave().set(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				save();
			}
		});
		
		_screen.onQuit().set(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				quit();
			}
		});
		
		_screen.editDataSourceProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				if(newVal){
					_dirtyFlag = true;
					_screen.editDataSourceProperty().setValue(false);
				}
			}
		});
		
		_screen.editSimulationProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
				if(newVal){
					_dirtyFlag = true;
					_screen.editSimulationProperty().setValue(false);
				}
			}
		});
		
		_screen.selectSimulationProperty().addListener(new ChangeListener<Simulation>() {
			@Override
			public void changed(ObservableValue<? extends Simulation> arg0, Simulation oldVal, Simulation newVal) {
				
				//Meanwhile - all the tables are the same for all the simulation - so load them once,
				//when the first simulation is selected. In the future - can compare between old and new simulations
				//and each time a different simulation is selected - change the tables accordingly.
				
				if(oldVal==null && newVal!=null){
					readSimulationsTables();
				}
				_model.setLastSelectedSimulation(newVal);
			}
		});
		
		_screen.onSystemClose().set(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				event.consume();
				quit();
			}
		});
		
		EventHandler<ActionEvent> viewAction = new EventHandler<ActionEvent>() {		
			@Override
			public void handle(ActionEvent event) {
				MenuItem item = (MenuItem) event.getSource();
				for (ToolFactory factory : 	ToolsLibrary.factories) {
					if (factory.getToolName().equals(item.getText())) {
						try {
							_presenter.addTool(factory.create());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		};
		
		_handleMainWsSimulationsChanged = new ListChangeListener<String>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends String> newList) {
				if(newList != null)
				{
					_model.getMainWorkspaceSelectedSimulations().clear();
					_model.getMainWorkspaceSelectedSimulations().addAll(newList.getList());
				}
			}
		};
		
		_handleMainWsSelectedSimulationChanged = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String prev, String selected) {
				_model.setlastWslSelectedSimulation(selected);
			}
		};
		
		for (MenuItem item : _screen.getViewMenu().getItems()) {
			item.setOnAction(viewAction);
		}
		
	}
	
	private void quit() {
		// TODO: check is we need to save  
		if(_dirtyFlag){
			SaveWsWizard wizard = new SaveWsWizard();
			ObjectProperty<Boolean> selection = wizard.show(_screen.getParent().getScene().getWindow());
			selection.addListener(new ChangeListener<Boolean>(){
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal,Boolean newVal) {
					if(newVal){
						save();
					}
					System.exit(0);
				}
			});
		}else{
			System.exit(0);
		}
	}
	
	private void save() {
		
		String currDirectory = getLastChosenWorkDirectory();
		
		// If the save directory does not exist, create it
		File saveDir = new File(currDirectory);
		if (!saveDir.exists())	
			saveDir.mkdir();  
	
		// The save file
		File saveFile = new File(currDirectory+"/"+SAVE_FILE);

		// Create the root memento
		XMLMemento memento = XMLMemento.createWriteRoot("root");
			
		// Save the data sources
		for(CyclistDatasource source: _model.getSources()){			
			source.save(memento.createChild("CyclistDatasource"));
		}
		
		// Save the data tables
		// Saves only tables added by the user (not loaded by the simulation configuration file).
		for(Table table: _model.getTables()){
			if(!table.getIsStandardSimulation()){
				table.save(memento.createChild("Table"));
			}
		}
		
		//Save the last selected simulation
		if(_model.getLastSelectedSimulation() != null && _model.getSimulationIds().size() >0){
			IMemento selectedSim = memento.createChild("LastSelectedSimulation");
			selectedSim.putString("simulation-id", _model.getLastSelectedSimulation().getSimulationId());
		}
		//Save the Simulations
		for(Simulation simulation: _model.getSimulationIds()){
			simulation.save(memento.createChild("Simulation"));
		}
		
		_presenter.save(memento.createChild("Tools"));
		
		//First save the main workspace
		IMemento mainWs = memento.createChild("mainWorkSpace");
		saveMainWorkspace(mainWs);
		
		for(ToolData tool : _model.getTools()){
			tool.save(mainWs.createChild("Tool"));
		}
		
		
			
		
		try {
			memento.save(new PrintWriter(saveFile));
			_dirtyFlag = false;

		} catch (IOException e) {
			e.printStackTrace();
		} 	
		
	}
	
	// Load saved properties
	private void load() {
		
		String currDirectory = _workDirectoryController.getWorkDirectories().get(_workDirectoryController.getLastChosenIndex());
		
		// Check if the save file exists
		File saveFile = new File(currDirectory+"/"+SAVE_FILE);
		
		//Clear the previous data
		_model.getSources().clear();
		_model.getTables().clear();
		_model.getSimulationIds().clear();
			
		// If we have a save file, read it in
		if(saveFile.exists()){
			
			Reader reader;
			try {
				reader = new FileReader(saveFile);
				try {
					// Create the root memento
					XMLMemento memento = XMLMemento.createReadRoot(reader);
					
					// Read in the data sources
					IMemento[] sources = memento.getChildren("CyclistDatasource");
					for(IMemento source: sources){
						CyclistDatasource datasource = new CyclistDatasource();
						datasource.restore(source);
						_model.getSources().add(datasource);
					}
					
					// Read in the tables
					IMemento[] tables = memento.getChildren("Table");
					//System.out.println("tables " + tables.length);
					for(IMemento table: tables){
						Table tbl = new Table();
						tbl.restore(table, _model.getSources());
						tbl.setLocalDatafile(getLastChosenWorkDirectory());
						_model.getTables().add(tbl);
					}
					
					//Read the simulations
					IMemento[] simulations = memento.getChildren("Simulation");
					for(IMemento simulation:simulations){
						Simulation sim = new Simulation();
						sim.restore(simulation,_model.getSources());
						_model.getSimulationIds().add(sim);
					}
					
					//Find if there is last selected simulation
					IMemento lastSimulation = memento.getChild("LastSelectedSimulation");
					String lastSimulationId = "";
					if(lastSimulation != null){
						lastSimulationId = lastSimulation.getString("simulation-id");
					}
					_screen.getSimulationPanel().selectSimulation(lastSimulationId);
					
					_dirtyFlag = false;
					
					//Read the main workspace
					IMemento mainWs = memento.getChild("mainWorkSpace");
					loadMainWorkspace(mainWs);
					
					IMemento[] tools = mainWs.getChildren("Tool");
					for(IMemento tool:tools){
						ToolData toolData = new ToolData();
						toolData.restore(tool);
						Table table = null;
						if(toolData.getTool().getClass().equals(TableTool.class)){
							table = findTable(toolData.getTableName(), toolData.getTableDatasource());
						}
						_screen.getWorkSpace().showLoadedTool(toolData, table);
						_model.getTools().add(toolData);
					}
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 		
		}
		//readSimulationsTables();
	}
	
	/*
	 * Reads the configuration file which defines the tables required for simulation.
	 * The tables are loaded with a null database property.
	 * The tables are added to the model tables list.
	 */
	private void readSimulationsTables(){
		try {
			URI uri = Cyclist.class.getResource(SIMULATIONS_TABLES_FILE).toURI();
			File simulationsFile = new File(uri);
			if(simulationsFile.exists()){
				
				Reader reader = new FileReader(simulationsFile);
				// Create the root memento
				XMLMemento memento = XMLMemento.createReadRoot(reader);
				
				// Read in the data sources
				IMemento[] tables = memento.getChildren("Table");
				for(IMemento table:tables){
					
					Table tbl = new Table();
					tbl.restoreSimulated(table);
					tbl.setLocalDatafile(getLastChosenWorkDirectory());
					_model.getSimulationsTablesDef().add(tbl);
					_model.getTables().add(tbl);
				}
			}
		} catch (Exception e) {
				e.printStackTrace();
		}
	}
	
	
	/*
	 * Finds a table in the Model tables list according to it's name and data source.
	 * @param : tableName
	 * @param:  datasource
	 * @return: Table. The table instance if found, null otherwise.
	 */
	private Table findTable(String tableName, String dataSource){
		if(tableName != null)
		{
			for(Table tbl:_model.getTables()){
				if(tbl.getName().equals(tableName)){
					if(dataSource!=null && !dataSource.isEmpty()){
						if(tbl.getDataSource() != null && tbl.getDataSource().getUID().equals(dataSource)){
							return tbl;
						}
					} else if((dataSource == null || dataSource.isEmpty()) && tbl.getDataSource() == null){
						return tbl;
					}
				}
			}
		}
		return null;
	}
	
	/*
	 * Finds a simulation in the Model simulations list according to it's alias.
	 * @param : simulationAlias
	 * @return: Simulation. The simulation instance if found, null otherwise.
	 */
	private Simulation findSimulation(String simulationAlias){
		if(simulationAlias != null && !simulationAlias.isEmpty())
		{
			for(Simulation sim:_model.getSimulationIds()){
				if(sim.getAlias().equals(simulationAlias)){
						return sim;
				}
			}
		}
		return null;
	}
	
	/*
	 * Gets the path of the last chosen work directory.
	 * If not available - return the default work directory
	 */
	private String getLastChosenWorkDirectory(){
		if(_workDirectoryController == null){
			return WorkDirectoryController.SAVE_DIR;
		}
		return _workDirectoryController.getWorkDirectories().get(_workDirectoryController.getLastChosenIndex());
	}
	
	/*
	 * Saves the data of the main window:
	 * 1. size and location
	 * 2. dropped simulations and the last chosen simulation
	 * @param IMemento mainWs 
	 */
	private void saveMainWorkspace(IMemento mainWs){
		Stage stage = (Stage)_screen.getParent().getScene().getWindow();
		mainWs.putString("x", Double.toString(stage.getX()));
		mainWs.putString("y", Double.toString(stage.getY()));
		mainWs.putString("width", Double.toString(stage.getWidth()));
		mainWs.putString("height", Double.toString(stage.getHeight()));
		mainWs.putString("selectedSim", _model.getlastWslSelectedSimulation());
		for(String simulation : _model.getMainWorkspaceSelectedSimulations()){
			IMemento simMemento = mainWs.createChild("WsSimulation");
			simMemento.putString("alias", simulation);
		}
	}
	
	/*
	 * Restores the data of the main window:
	 * 1. size and location.
	 * 2. dropped simulations and the last chosen simulation
	 * @param IMemento mainWs
	 */
	private void loadMainWorkspace(IMemento mainWs){
		double width = Double.parseDouble(mainWs.getString("width"));
		double height = Double.parseDouble(mainWs.getString("height"));
		double x = Double.parseDouble(mainWs.getString("x"));
		double y = Double.parseDouble(mainWs.getString("y"));
		Stage stage = (Stage)_screen.getParent().getScene().getWindow();
		stage.setWidth(width);
		stage.setHeight(height);
		stage.setX(x);
		stage.setY(y);
		
		Simulation lastActiveSim = null;
		String lastActiveSimAlias = mainWs.getString("selectedSim");
		Closure.V1<Simulation> simulationDropAction = _screen.getWorkSpace().getOnSimulationDrop();
		
		IMemento[] simulations = mainWs.getChildren("WsSimulation");
		for(IMemento simulation: simulations){
			String alias = simulation.getString("alias");
			Simulation sim = findSimulation(alias);
			//Add the last active simulation at the end, so it would become the active simulation again.
			if(sim != null && !alias.equals(lastActiveSimAlias) ){
				if(simulationDropAction != null){
					simulationDropAction.call(sim);
				}
			}else if(alias.equals(lastActiveSimAlias)){
				lastActiveSim = sim;
			}
		}
		//Add the last active simulation at the end.
		if(lastActiveSim != null && simulationDropAction != null){
			simulationDropAction.call(lastActiveSim);
		}
	}
	
	
	
}
