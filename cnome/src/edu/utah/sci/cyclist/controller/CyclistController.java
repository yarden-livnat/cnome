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
package edu.utah.sci.cyclist.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Model;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.presenter.SchemaPresenter;
import edu.utah.sci.cyclist.presenter.ToolsPresenter;
import edu.utah.sci.cyclist.presenter.DatasourcesPresenter;
import edu.utah.sci.cyclist.presenter.WorkspacePresenter;
import edu.utah.sci.cyclist.ui.MainScreen;
import edu.utah.sci.cyclist.ui.tools.ToolsLibrary;
import edu.utah.sci.cyclist.ui.views.Workspace;
import edu.utah.sci.cyclist.ui.wizards.DatatableWizard;


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
		
		// ToolsLibrary panel
		ToolsPresenter tp = new ToolsPresenter(_eventBus);
		tp.setPanel(screen.getToolsPanel());
		tp.setFactories(Arrays.asList(ToolsLibrary.factories));
		
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
				ObjectProperty<Table> selection = wizard.show(_screen.getWindow());
				
				selection.addListener(new ChangeListener<Table>() {
					@Override
					public void changed(ObservableValue<? extends Table> arg0, Table oldVal, Table newVal) {
						_model.getTables().add(newVal);
						_model.setSelectedDatasource(wizard.getSelectedSource());
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
			source.save(memento.createChild("CyclistDatasource"));
		}
		
		// Save the data tables
		for(Table table: _model.getTables()){
			table.save(memento.createChild("Table"));
		}
		
		try {
			memento.save(new PrintWriter(saveFile));

		} catch (IOException e) {
			e.printStackTrace();
		} 	
		
	}
	
	// Load saved properties
	private void load() {
		
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
						_model.getTables().add(tbl);
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
	}
}
