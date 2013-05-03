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
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.controller.CyclistController;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.components.DatasourceWizard;
import edu.utah.sci.cyclist.view.components.DatatableWizard;
import edu.utah.sci.cyclist.view.components.Spring;
import edu.utah.sci.cyclist.view.components.Workspace;
import edu.utah.sci.cyclist.view.components.WorkspaceWizard;

public class MainScreen extends VBox {
	public static final String ID = "main-screen";
	
	private MenuBar _menubar;
	private VBox _toolsArea;
	private HBox _content;
	
	private ListView<Table> tableList;
	
	private CyclistController controller;
	/**
	 * Constructor
	 */
	public  MainScreen(Stage stage) {
		super();
		setId(ID);
		
		init(stage);
	}
	
	public void setControler(CyclistController controler) {
		this.controller = controler;
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
	
	public ObjectProperty<Table> selectDatatable(ObservableList<CyclistDatasource> list){
	
		final DatatableWizard wizard = new DatatableWizard();
		wizard.setItems(list);
		wizard.getAddSourceButton().setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				controller.selectDatasource(new CyclistDatasource());		
			}	
		});	
		
		wizard.getEditSourceButton().setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				
				CyclistDatasource current = ((DatatableWizard)wizard).getCurrentDataSource();
				controller.selectDatasource(current);		
			}	
		});	
		
		wizard.getRemoveSourceButton().setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				
				CyclistDatasource current = ((DatatableWizard)wizard).getCurrentDataSource();
				controller.removeDatasource(current);		
			}	
		});	

		return wizard.show(getScene().getWindow());
	}
	
	public ObjectProperty<CyclistDatasource> selectDatasource(CyclistDatasource source){
		
		DatasourceWizard wizard = new DatasourceWizard(source);
		return wizard.show(getScene().getWindow());		
	}
	
	
	private void init(Stage stage){
		// create the screen
		
		// -- menubar
		_menubar = createMenuBar(stage);
		
				
		// -- tables and schema
		_toolsArea = VBoxBuilder.create()
				.spacing(5)
				.padding(new Insets(5))
				.children(
						// tables
						TextBuilder.create().text("Tables:").build(),
						tableList = ListViewBuilder.<Table>create()
						.maxHeight(150)
						.build(),
						new Spring()	
						// schema
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
	
	private MenuBar createMenuBar(Stage stage) {
		MenuBar menubar = new MenuBar();
		
		// -- File menu
		Menu fileMenu = createFileMenu();

		menubar.getMenus().add(fileMenu);
		return menubar;
	}
	
	private Menu createFileMenu() {
		// -- Workspace
		MenuItem datatableItem = new MenuItem("Add Datatable", new ImageView(Resources.getIcon("open.png")));
		datatableItem.setOnAction(new EventHandler<ActionEvent>(){
			
			@Override
			public void handle(ActionEvent event) {
				controller.selectDatatable();
			}		
		});

		
		MenuItem workspaceItem = new MenuItem("Workspace"); //new ImageView(Resources.getIcon("workspace.png")));
		workspaceItem.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				controller.selectWorkspace();
			}
		});
		
		// -- Quit
		MenuItem quitItem = new MenuItem("Quit");
		quitItem.setAccelerator(KeyCombination.keyCombination("Alt+Q"));
		quitItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// save state
				controller.quit();
			}
		});
		
		// -- setup the menu 
		Menu fileMenu = new Menu("File");
		fileMenu.getItems().addAll(datatableItem, new SeparatorMenuItem(), workspaceItem, new SeparatorMenuItem(), quitItem);
		return fileMenu;
	}

	public void setTables(ObservableList<Table> tables) {

		tableList.getItems().clear();
		
		// Set the tables
		for(int i = 0; i < tables.size(); i++)
			tableList.getItems().add(tables.get(i));	
		
	}
}
