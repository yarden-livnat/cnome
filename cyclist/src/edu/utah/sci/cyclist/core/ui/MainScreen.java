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
package edu.utah.sci.cyclist.core.ui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import edu.utah.sci.cyclist.ToolsLibrary;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.tools.ToolFactory;
import edu.utah.sci.cyclist.core.ui.panels.FiltersListPanel;
import edu.utah.sci.cyclist.core.ui.panels.JobsPanel;
import edu.utah.sci.cyclist.core.ui.panels.SchemaPanel;
import edu.utah.sci.cyclist.core.ui.panels.SimulationsPanel;
import edu.utah.sci.cyclist.core.ui.panels.TablesPanel;
import edu.utah.sci.cyclist.core.ui.panels.ToolsPanel;
import edu.utah.sci.cyclist.core.ui.views.Workspace;
import edu.utah.sci.cyclist.core.ui.wizards.WorkspaceWizard;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

public class MainScreen extends VBox {
	public static final String ID = "main-screen";
	
	private SplitPane _sp;
	private SplitPane _toolsPane;
	private TablesPanel _datasourcesPanel;
	private SchemaPanel _fieldsPanel;
	private ToolsPanel _toolsPanel;
	private StackPane _workspacePane;
	private SimulationsPanel _simulationPanel;
	private JobsPanel _jobsPanel;

	private Menu _perspectiveMenu;
	private Menu _viewMenu;
	private Menu _runMenu;
	
	private ObjectProperty<EventHandler<WindowEvent>> _stageCloseProperty;
		
	/**
	 */
	public  MainScreen(Stage stage) {
		super();
		setId(ID);
		
		build(stage);
	}
	
	public Window getWindow() {
		return getScene().getWindow();
	}
	
	public ObservableList<String> selectWorkspace(ObservableList<String> list, int chosenIndex) {
		WorkspaceWizard wizard = new WorkspaceWizard();
		wizard.setItems(list,chosenIndex);
		return wizard.show(getScene().getWindow());
	}
	
	
	public void setWorkspace(Workspace workspace) {
		_workspacePane.getChildren().add(workspace);
	}
	
	public TablesPanel getDatasourcesPanel() {
		return _datasourcesPanel;
	}
	
	public SchemaPanel getFieldsPanel(){
		return _fieldsPanel;
	}
	
	public ToolsPanel getToolsPanel() {
		return _toolsPanel;
	}
	
	public SimulationsPanel getSimulationPanel(){
		return _simulationPanel;
	}
	
	public JobsPanel getJobsPanel() {
		return _jobsPanel;
	}
	
	public Workspace getWorkSpace(){
		for(Object obj : _workspacePane.getChildren()){
			if (obj.getClass() == Workspace.class) {
				return (Workspace)obj;
			}
		}
		return null;
	}
	
	private double TOOLS_WIDTH = 120; 
	
	private void build(Stage stage){
		getStyleClass().add("main-screen");
		double[] div = {0.2, 0.4, 0.8, 1.0};
		
		double [] mainDividers = {TOOLS_WIDTH/600.0};
		
		this.setPrefWidth(600);
		this.setPrefHeight(400);
		this.setPadding(new Insets(0));
		this.setSpacing(0);
		
		_sp = new SplitPane();
		_sp.setOrientation(Orientation.HORIZONTAL);
		_sp.setDividerPositions(mainDividers);
		
		_toolsPane = new SplitPane();
		_toolsPane.getStyleClass().add("hiddenSplitter");
		_toolsPane.setPrefWidth(USE_COMPUTED_SIZE);
		_toolsPane.setPrefHeight(USE_COMPUTED_SIZE);
		_toolsPane.setOrientation(Orientation.VERTICAL);
		_toolsPane.getItems().addAll(
				_simulationPanel = new SimulationsPanel(),
				_jobsPanel = new JobsPanel(),
				_datasourcesPanel = new TablesPanel(),
				_fieldsPanel = new SchemaPanel("Fields"),
				
				_toolsPanel = new ToolsPanel(),
				/*_filtersPanel = */new FiltersListPanel()
				);
		_toolsPane.setDividerPositions(div);
		
		_sp.getItems().addAll(
				_toolsPane,
				_workspacePane = new StackPane());
		
		this.getChildren().addAll(
				createMenuBar(stage),
				_sp
				);
			
		VBox.setVgrow(_sp, Priority.ALWAYS);
		
		SplitPane.setResizableWithParent(_toolsPane, false);
		
		_stageCloseProperty = stage.onCloseRequestProperty();
	}
	
	/*
	 * Menus & Actions
	 */
	
	public Menu getViewMenu() {
		return _viewMenu;
	}
	
	public Menu getPerspectiveMenu() {
		return _perspectiveMenu;
	}
	
	public Menu getRunMenu() {
		return _runMenu;
	}
	
	private MenuItem _datasourceMenuItem;
	private MenuItem _workspaceMenuItem;
	private MenuItem _quitMenuItem;
	private MenuItem _saveMenuItem;
	private MenuItem _simulationMenuItem;
	private MenuItem _sqliteLoaderMenuItem;
	private MenuItem _runMenuItem;
	
	public ObjectProperty<EventHandler<ActionEvent>> onAddDatasource() {
		return _datasourceMenuItem.onActionProperty();
	}
	
	public ObjectProperty<EventHandler<ActionEvent>> onAddSimulation() {
		return _simulationMenuItem.onActionProperty();
	}
	
	public ObjectProperty<EventHandler<ActionEvent>> onLoadSqlite() {
		return _sqliteLoaderMenuItem.onActionProperty();
	}
	
	public ObjectProperty<EventHandler<ActionEvent>> onSelectWorkspace() {
		return _workspaceMenuItem.onActionProperty();
	}
	
	public ObjectProperty<EventHandler<ActionEvent>> onSave() {
		return _saveMenuItem.onActionProperty();
	}
	
	public ObjectProperty<EventHandler<ActionEvent>> onQuit() {
		return _quitMenuItem.onActionProperty();
	}
	
	public ObjectProperty<EventHandler<ActionEvent>> onRun() {
		return _runMenuItem.onActionProperty();
	}
	
	public ObjectProperty<Boolean> editDataSourceProperty() {
		return _datasourcesPanel.editTableProperty();
	}

	
	/**
	 * Property which is changed when user wants to edit a simulation entry in the simulations panel.
	 * (Changes the simulation alias field).
	 * (keyboard - escape or enter pressed)
	 * @return ObjectProperty<Simulation> - property is set to the new Simulation value on enter/escape pressed.
	 */
	public ObjectProperty<Simulation> editSimulationProperty() {
		return _simulationPanel.editSimulationProperty();
	}
	
	/**
	 * Property which is changed when the user selects a simulation entry in the simulations panel.
	 * (trigger - mouse click)
	 * @return ObjectProperty<Simulation> - the selected simulation.
	 */
	public ObjectProperty<Simulation> selectSimulationProperty() {
		return _simulationPanel.selectedItemProperty();
	}
	
	
	public ObjectProperty<EventHandler<WindowEvent>> onSystemClose(){
		return _stageCloseProperty;
	}
	
	
	private MenuBar createMenuBar(Stage stage) {
		MenuBar menubar = new MenuBar();
		
		Menu fileMenu = createFileMenu();
		Menu dataMenu = createDataMenu();
		_viewMenu = createViewMenu();
		_runMenu = createRunMenu();
//		Menu panelMenu = createPanelMenu();
//		_perspectiveMenu = createPerspectiveMenu();
		
		menubar.getMenus().addAll(fileMenu, dataMenu, _viewMenu, _runMenu /*, _perspectiveMenu*/);
		
		return menubar;
	}
	
	private Menu createFileMenu() {
		
		_workspaceMenuItem = new MenuItem("Work Directory",GlyphRegistry.get(AwesomeIcon.FOLDER_OPEN_ALT)); 
		
		_saveMenuItem = new MenuItem("Save", GlyphRegistry.get(AwesomeIcon.SAVE));
		_saveMenuItem.setAccelerator(KeyCombination.keyCombination("Meta+S"));
		
		// -- Quit
		_quitMenuItem = new MenuItem("Quit", GlyphRegistry.get(AwesomeIcon.SIGN_OUT));
		_quitMenuItem.setAccelerator(KeyCombination.keyCombination("Meta+Q"));
		
		// -- setup the menu 
		Menu fileMenu = new Menu("File");
		fileMenu.getItems().addAll(
					new SeparatorMenuItem(), 
					_workspaceMenuItem, 
					_saveMenuItem,
					new SeparatorMenuItem(), 
					_quitMenuItem);
		return fileMenu;
	}
	
	private Menu createDataMenu() {
		_datasourceMenuItem = new MenuItem("Datatable", GlyphRegistry.get(AwesomeIcon.FOLDER_OPEN_ALT));	
		_simulationMenuItem = new MenuItem("Simulation", GlyphRegistry.get(AwesomeIcon.FOLDER_OPEN_ALT));
		_sqliteLoaderMenuItem = new MenuItem("Load Sqlite", GlyphRegistry.get(AwesomeIcon.FOLDER_OPEN_ALT));
		
		// -- setup the menu 
		Menu dataMenu = new Menu("Data");
		dataMenu.getItems().addAll(
				_datasourceMenuItem,_simulationMenuItem,_sqliteLoaderMenuItem);
		return dataMenu;
	}

	private Menu createViewMenu() {
		Menu menu = new Menu("Views");			

		for (final ToolFactory factory : ToolsLibrary.factories) {
			MenuItem item = new MenuItem(factory.getToolName(), GlyphRegistry.get(factory.getIcon()));
			menu.getItems().add(item);
		}

		return menu;
	}
	
	private Menu createRunMenu() {
		Menu menu= new Menu("Run");
		_runMenuItem = new MenuItem("Submit file", GlyphRegistry.get(AwesomeIcon.EXCHANGE));
		_runMenuItem.setAccelerator(KeyCombination.keyCombination("Meta+R"));

		menu.getItems().add(_runMenuItem);
	
		return menu;
	}
	
	private Menu createPerspectiveMenu() {
		Menu menu = new Menu("Perspectives");
		
		MenuItem cycic = new MenuItem("Design");
		MenuItem cyclist = new MenuItem("Analysis");
		
		menu.getItems().addAll(cycic, cyclist);
		return menu;
	}
}
