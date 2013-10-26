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
package edu.utah.sci.cyclist.ui;

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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.ui.panels.FiltersListPanel;
import edu.utah.sci.cyclist.ui.panels.SchemaPanel;
import edu.utah.sci.cyclist.ui.panels.TablesPanel;
import edu.utah.sci.cyclist.ui.panels.ToolsPanel;
import edu.utah.sci.cyclist.ui.views.Workspace;
import edu.utah.sci.cyclist.ui.wizards.WorkspaceWizard;

public class MainScreen extends VBox {
	public static final String ID = "main-screen";
	
//	private MenuBar _menubar;
	private SplitPane _sp;
	private SplitPane _toolsPane;
	private TablesPanel _datasourcesPanel;
	private SchemaPanel _dimensionsPanel;
	private SchemaPanel _measuresPanel;
	private ToolsPanel _toolsPanel;
//	private FiltersListPanel _filtersPanel;
	private StackPane _workspacePane;
		
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
	
	public SchemaPanel getDimensionPanel() {
		return _dimensionsPanel;
	}
	
	public SchemaPanel getMeauresPanel() {
		return _measuresPanel;
	}
	
	public ToolsPanel getToolsPanel() {
		return _toolsPanel;
	}
	
	public Workspace getWorkSpace(){
		for(Object obj : _workspacePane.getChildren()){
			if (obj.getClass() == Workspace.class) {
				return (Workspace)obj;
			}
		}
		return null;
	}
	
	private double toolsWidth = 120; 
	private void build(Stage stage){
		double[] div = {0.2, 0.4, 0.6, 0.8};
		
		double [] mainDividers = {toolsWidth/600.0};
		
		this.setPrefWidth(600);
		this.setPrefHeight(400);
		this.setPadding(new Insets(0));
		this.setSpacing(0);
		
		_sp = new SplitPane();
		_sp.setId("hiddenSplitter");
		_sp.setOrientation(Orientation.HORIZONTAL);
		_sp.setDividerPositions(mainDividers);
		
		_toolsPane = new SplitPane();
		_toolsPane.setId("hiddenSplitter");
		_toolsPane.setPrefWidth(USE_COMPUTED_SIZE);
		_toolsPane.setPrefHeight(USE_COMPUTED_SIZE);
		_toolsPane.setOrientation(Orientation.VERTICAL);
		_toolsPane.getItems().addAll(
				_datasourcesPanel = new TablesPanel(),
				_dimensionsPanel = new SchemaPanel("Category"),
<<<<<<< HEAD
				_measuresPanel = new SchemaPanel("Numeric"),
=======
				_measuresPanel = new SchemaPanel("Measure"),
>>>>>>> 000913116c1c6bc46009daf6cdb2b1d193d5d41c
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
	}
	
	/*
	 * Menus & Actions
	 */
	
	private MenuItem _datasourceMenuItem;
	private MenuItem _workspaceMenuItem;
	private MenuItem _quitMenuItem;
	private MenuItem _saveMenuItem;
	
	
	public ObjectProperty<EventHandler<ActionEvent>> onAddDatasource() {
		return _datasourceMenuItem.onActionProperty();
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
	
	private MenuBar createMenuBar(Stage stage) {
		MenuBar menubar = new MenuBar();
		
		// -- File menu
		Menu fileMenu = createFileMenu();

		menubar.getMenus().add(fileMenu);
		
		//Data menu
		Menu dataMenu = createDataMenu();
		menubar.getMenus().add(dataMenu);
		
		return menubar;
	}
	
	private Menu createFileMenu() {
		
		_workspaceMenuItem = new MenuItem("Work Directory"); //new ImageView(Resources.getIcon("workspace.png")));
		
		_saveMenuItem = new MenuItem("Save");
		_saveMenuItem.setAccelerator(KeyCombination.keyCombination("Meta+S"));
		
		// -- Quit
		_quitMenuItem = new MenuItem("Quit");
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
		_datasourceMenuItem = new MenuItem("Add Datatable", new ImageView(Resources.getIcon("open.png")));
		
		// -- setup the menu 
		Menu dataMenu = new Menu("Data");
		dataMenu.getItems().addAll(
				_datasourceMenuItem);
		return dataMenu;
	}

}
