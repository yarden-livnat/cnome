package edu.utah.sci.cyclist.view;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.ListViewBuilder;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.controller.CyclistController;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.components.Spring;
import edu.utah.sci.cyclist.view.components.Workspace;
import edu.utah.sci.cyclist.view.panels.SchemaPanel;
import edu.utah.sci.cyclist.view.panels.TablesPanel;
import edu.utah.sci.cyclist.view.panels.ToolsPanel;
import edu.utah.sci.cyclist.view.wizard.DatasourceWizard;
import edu.utah.sci.cyclist.view.wizard.DatatableWizard;
import edu.utah.sci.cyclist.view.wizard.WorkspaceWizard;

public class MainScreen extends VBox {
	public static final String ID = "main-screen";
	
	private MenuBar _menubar;
	private VBox _toolsArea;
	private HBox _content;
	private TablesPanel _datasourcesPanel;
	private SchemaPanel _schemaPanel;
	private ToolsPanel _toolsPanel;
	
	private CyclistController _controller;
	
	/**
	 * Constructor
	 */
	public  MainScreen(Stage stage) {
		super();
		setId(ID);
		
		init(stage);
	}
	
	public Window getWindow() {
		return getScene().getWindow();
	}
	
	public void setControler(CyclistController controler) {
		this._controller = controler;
	}
	
	public ObjectProperty<String> selectWorkspace(ObservableList<String> list) {
		
		WorkspaceWizard wizard = new WorkspaceWizard();
		wizard.setItems(list);
		return wizard.show(getScene().getWindow());
	}
	
	
	public void setWorkspace(Workspace workspace) {
		HBox.setHgrow(workspace, Priority.ALWAYS);
		_content.getChildren().add(workspace);
	}
	
	public TablesPanel getDatasourcesPanel() {
		return _datasourcesPanel;
	}
	
	public SchemaPanel getSchemaPanel() {
		return _schemaPanel;
	}
	
	public ToolsPanel getToolsPanel() {
		return _toolsPanel;
	}
	
	private void init(Stage stage){
		// create the screen
		
		// -- menubar
		_menubar = createMenuBar(stage);
		
				
		// -- tables and schema
		_toolsArea = VBoxBuilder.create()
				.spacing(5)
				.prefWidth(100)
				.padding(new Insets(5))
				.children(
						_datasourcesPanel = new TablesPanel(),					
						_schemaPanel = new SchemaPanel(),
						_toolsPanel = new ToolsPanel(),
						new Spring()	
						)
				.build();
		VBox.setVgrow(_toolsArea, Priority.SOMETIMES);
		
		// -- workspace
		_content = HBoxBuilder.create()
						.children(
								_toolsArea
								// workspace
								)
						.build();
		
		// -- setup
		VBox.setVgrow(_content, Priority.ALWAYS);
		getChildren().addAll(_menubar, _content);
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
		return menubar;
	}
	
	private Menu createFileMenu() {
		_datasourceMenuItem = new MenuItem("Add Datatable", new ImageView(Resources.getIcon("open.png")));
		
		_workspaceMenuItem = new MenuItem("Workspace"); //new ImageView(Resources.getIcon("workspace.png")));
		
		_saveMenuItem = new MenuItem("Save");
		_saveMenuItem.setAccelerator(KeyCombination.keyCombination("Meta+S"));
		
		// -- Quit
		_quitMenuItem = new MenuItem("Quit");
		_quitMenuItem.setAccelerator(KeyCombination.keyCombination("Meta+Q"));
		
		// -- setup the menu 
		Menu fileMenu = new Menu("File");
		fileMenu.getItems().addAll(
					_datasourceMenuItem, 
					new SeparatorMenuItem(), 
					_workspaceMenuItem, 
					_saveMenuItem,
					new SeparatorMenuItem(), 
					_quitMenuItem);
		return fileMenu;
	}

}
