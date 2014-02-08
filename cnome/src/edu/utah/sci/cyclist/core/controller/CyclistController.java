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
import javafx.scene.layout.Region;
import javafx.stage.WindowEvent;
import edu.utah.sci.cyclist.Cyclist;
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
import edu.utah.sci.cyclist.core.ui.MainScreen;
import edu.utah.sci.cyclist.core.ui.components.ViewBase;
import edu.utah.sci.cyclist.core.ui.tools.TableTool;
import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolsLibrary;
import edu.utah.sci.cyclist.core.ui.tools.WorkspaceTool;
import edu.utah.sci.cyclist.core.ui.views.Workspace;
import edu.utah.sci.cyclist.core.ui.wizards.DatatableWizard;
import edu.utah.sci.cyclist.core.ui.wizards.SaveWsWizard;
import edu.utah.sci.cyclist.core.ui.wizards.SimulationWizard;


public class CyclistController {
	
	private final EventBus _eventBus;
	private MainScreen _screen;
	private Model _model = new Model();
	//private String SAVE_DIR = System.getProperty("user.dir") + "/.cnome/";
	private String SAVE_FILE = "save.xml";
	private WorkDirectoryController _workDirectoryController;
	private Boolean _dirtyFlag = false;
	private EventHandler<CyclistDropEvent> handleToolDropped;
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
		sp.setPanels(screen.getDimensionPanel(), screen.getMeauresPanel());
		
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
		
		WorkspacePresenter presenter = new WorkspacePresenter(_eventBus);
		presenter.setView(workspace);
		setWorkspaceDragAndDropAction();
		_screen.getWorkSpace().setOnToolDrop(handleToolDropped);
		
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
						_screen.getDimensionPanel().setFields(emptyList);
						_screen.getMeauresPanel().setFields(emptyList);
						
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
			}
		});
		
		_screen.onSystemClose().set(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				event.consume();
				quit();
			}
		});
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
		
		//Save the Simulation
		for(Simulation simulation: _model.getSimulationIds()){
			simulation.save(memento.createChild("Simulation"));
		}
		
		for(ToolData tool : _model.getTools()){
				tool.save(memento.createChild("Tool"));
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
					_dirtyFlag = false;
					
					//Read the main workspace
					IMemento[] tools = memento.getChildren("Tool");
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
		if(tableName != null && dataSource != null)
		{
			for(Table tbl:_model.getTables()){
				if(tbl.getName().equals(tableName) && tbl.getDataSource().getUID().equals(dataSource)){
					return tbl;
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
	
	private void setWorkspaceDragAndDropAction(){
		
		 handleToolDropped = new EventHandler<CyclistDropEvent>() {
	        
	        @Override
	        public void handle(CyclistDropEvent event) {
	                if(event.getEventType() == CyclistDropEvent.DROP){
	                	Tool tool = event.getTool();
	                	ToolData toolData = new ToolData(tool, event.getX(), event.getY(), 
	                									((Region)tool.getView()).getPrefWidth(),((Region)tool.getView()).getPrefHeight());
	                	_model.getTools().add(toolData);
	                	if(tool.getClass().equals(WorkspaceTool.class)){
	                	   ((Workspace)tool.getView()).setOnToolDrop(handleToolDropped);
	                	}
	                        
	                }else if(event.getEventType() == CyclistDropEvent.DROP_DATASOURCE){
	                	Tool tool = event.getTool();
	                	ToolData toolData = new ToolData(tool, event.getX(), event.getY(), 
								((Region)tool.getView()).getPrefWidth(),((Region)tool.getView()).getPrefHeight(),
								event.getTable());
	                	_model.getTools().add(toolData);
	                }else if(event.getEventType() == CyclistDropEvent.REMOVE){
	                	removeTool(event.getView());
	                }
	                _dirtyFlag = true;
	        }
	  };
	}
	
	private void removeTool(ViewBase view){
		for(ToolData tool : _model.getTools()){
			if(tool.getTool().getView() == view){
				 _model.getTools().remove(tool);
				 break;
			}
		}
	}
	
}
