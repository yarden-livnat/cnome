package edu.utah.sci.cyclist.view;

import utils.Resources;
import edu.utah.sci.cyclist.controller.CyclistController;
import edu.utah.sci.cyclist.event.shared.EventBus;
import edu.utah.sci.cyclist.event.shared.SimpleEventBus;
import edu.utah.sci.cyclist.view.components.DatasourceWizard;
import edu.utah.sci.cyclist.view.components.DatatableWizard;
import edu.utah.sci.cyclist.view.components.WorkspaceWizard;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class MainScreen extends VBox {
	public static final String ID = "main-screen";
	
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
	
	public ObjectProperty<String> selectDatatable(ObservableList<String> list){

		DatatableWizard wizard = new DatatableWizard();
		//	wizard.setItems(list);
		wizard.getAddSourceButton().setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				controller.selectDatasource();
			}	
		});	

		return wizard.show(getScene().getWindow());
	}
	
	public ObjectProperty<String> selectDatasource(ObservableList<String> list){
		
		DatasourceWizard wizard = new DatasourceWizard();
//		wizard.setItems(list);
		return wizard.show(getScene().getWindow());		
	}
	
	
	private void init(Stage stage){
		// create the screen
		
		// -- menubar
		MenuBar menubar = createMenuBar(stage);
		
		// -- tables and schema
		
		// -- workspace
		
		// -- setup
		getChildren().addAll(menubar);
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
		MenuItem datatableItem = new MenuItem("Add Datatable");
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
}
