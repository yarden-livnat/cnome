package edu.utah.sci.cyclist.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.FileReader;
import java.util.Properties;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import edu.utah.sci.cyclist.event.dnd.DnDIcon;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Model;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.presenter.DatasourcesPresenter;
import edu.utah.sci.cyclist.presenter.TablesPresenter;
import edu.utah.sci.cyclist.presenter.SchemaPresenter;
import edu.utah.sci.cyclist.presenter.ToolsPresenter;
import edu.utah.sci.cyclist.presenter.WorkspacePresenter;
import edu.utah.sci.cyclist.view.MainScreen;
import edu.utah.sci.cyclist.view.components.DnDIcon;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Model;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.components.Workspace;
import edu.utah.sci.cyclist.view.tool.ToolsLibrary;
import edu.utah.sci.cyclist.view.wizard.DatasourceWizard;
import edu.utah.sci.cyclist.view.wizard.DatatableWizard;


public class CyclistController {
	
	private final EventBus _eventBus;
	private MainScreen _screen;
	private Model _model = new Model();
	private String SAVE_DIR = System.getProperty("user.dir") + "/.cnome/";
	private String SAVE_FILE = SAVE_DIR+"save.xml";
	private ObservableList<String> _workspaces = FXCollections.observableArrayList();
	
	/**
	 * Constructor
	 * 
	 * @param eventBus
	 */
	public CyclistController(EventBus eventBus) {
		this._eventBus = eventBus;
		_workspaces.add("/Users/yarden/software");
		_workspaces.add("/Users/yarden");
		
		load();
	}

	/**
	 * initialize the main screen
	 * @param screen
	 */
	public void setScreen(final MainScreen screen) {
		this._screen = screen;
		screen.setControler(this);
		addActions();
			
		/*
		 *  wire panels
		 */
		
		// Tables panel
		TablesPresenter ds = new TablesPresenter(_eventBus);
		ds.setSources(_model.getSources());
		ds.setTables(_model.getTables());
		ds.setPanel(screen.getDatasourcesPanel());
		
		// Schema panel
		SchemaPresenter sp = new SchemaPresenter(_eventBus);
		sp.setPanel(screen.getSchemaPanel());
		
		// ToolsLibrary panel
		ToolsPresenter tp = new ToolsPresenter(_eventBus);
		tp.setPanel(screen.getToolsPanel());
		tp.setTools(Arrays.asList(ToolsLibrary.list));
		
		// set up the main workspace
		Workspace workspace = new Workspace();
		screen.setWorkspace(workspace);
		
		WorkspacePresenter presenter = new WorkspacePresenter(_eventBus, _model);
		presenter.setView(workspace);
		
		// do something?
		//selectWorkspace();
	}
	
	/**
	 * selectWorkspace
	 * 
	 */
	public void selectWorkspace() {
		ObjectProperty<String> selection = _screen.selectWorkspace(_workspaces);
		selection.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldVal, String newVal) {
				System.out.println("select:"+newVal);
				
			}
		});
	}	
		
	private void addActions() {
		_screen.onAddDatasource().set(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				final DatatableWizard wizard = new DatatableWizard();
				wizard.setItems(_model.getSources());
				ObjectProperty<Table> selection = wizard.show(_screen.getWindow());
				
				selection.addListener(new ChangeListener<Table>() {
					@Override
					public void changed(ObservableValue<? extends Table> arg0, Table oldVal, Table newVal) {
						_model.getTables().add(newVal);
					}
				});	
				
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
	}
	
	private void quit() {
		// TODO: check is we need to save  
		System.exit(0);
	}
	
	private void save() {
		
		// If the save directory does not exist, create it
		File saveDir = new File(SAVE_DIR);
		if (!saveDir.exists())	
			saveDir.mkdir();  
	
		// The save file
		File saveFile = new File(SAVE_FILE);

		// Create the root memento
		XMLMemento memento = XMLMemento.createWriteRoot("root");
			
		// Save the data sources
		for(CyclistDatasource source: _model.getSources()){			
			source.save(memento.createChild("sources"));
		}
		
		// Save the data tables
		for(Table table: _model.getTables()){
			table.save(memento.createChild("tables"));
		}
		
		try {
			memento.save(new PrintWriter(saveFile));

		} catch (IOException e) {
			e.printStackTrace();
		} 	
		
	}
	
	// Load saved properties
	private void load() {
	
		System.out.println("Load!");
	/*	
		// Check if the save file exists
		File saveFile = new File(SAVE_FILE);
			
		// If we have a save file, read it in
		if(saveFile.exists()){
			
			
			Reader reader;
			try {
				reader = new FileReader(saveFile);
				try {
					// Create the root memento
					XMLMemento memento = XMLMemento.createReadRoot(reader);
					
					// Read in the data sources
					IMemento sources_list = memento.getChild("sources-list");
					IMemento[] sources = sources_list.getChildren("CyclistDatasource");
					System.out.println("sourcs " + sources.length);
					for(IMemento source: sources){
						
						CyclistDatasource datasource = new CyclistDatasource();
						datasource.restore(source);
						_model.getSources().add(datasource);
					}
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 		
		}*/
	}
}