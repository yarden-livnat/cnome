package edu.utah.sci.cyclist.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.event.shared.EventBus;
import edu.utah.sci.cyclist.view.MainScreen;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Table;

public class CyclistController {

	private final EventBus eventBus;
	private MainScreen screen;
	private ObservableList<String> workspaces = FXCollections.observableArrayList();
	
	private ObservableList<Table> tables = FXCollections.observableArrayList();
	private ObservableList<CyclistDatasource> sources = FXCollections.observableArrayList();
	
	public ObservableList<CyclistDatasource> getSources() { return sources; }
	
	public CyclistController(EventBus eventBus) {
		this.eventBus = eventBus;
		bind();
		workspaces.add("/Users/yarden/software");
		workspaces.add("/Users/yarden");
	
	}

	public void setScreen(final MainScreen screen) {
		this.screen = screen;
		screen.setControler(this);
		
		// do something?
		//selectWorkspace();
	}
	
	public void selectWorkspace() {
		ObjectProperty<String> selection = screen.selectWorkspace(workspaces);
		selection.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldVal, String newVal) {
				System.out.println("select:"+newVal);
				
			}
		});
	}
	
	public void selectDatatable(){
		
		ObjectProperty<Table> selection = screen.selectDatatable(sources);
		selection.addListener(new ChangeListener<Table>() {

			@Override
			public void changed(ObservableValue<? extends Table> arg0, Table oldVal, Table newVal) {
				System.out.println("New Table select:"+newVal.getName());
				tables.add(newVal);
			}
		});	
	}
	
	public void selectDatasource(){
		ObjectProperty<CyclistDatasource> selection = screen.selectDatasource(sources);
		selection.addListener(new ChangeListener<CyclistDatasource>(){
			@Override
			public void changed(ObservableValue<? extends CyclistDatasource> arg0, CyclistDatasource oldVal, CyclistDatasource newVal) {
				System.out.println("New Sourceselect:"+newVal.getName());
				sources.add(newVal);
			}
		});
		
	}
	
	
	public void quit() {
		System.exit(0);
	}

	private void bind() {
		// set the history mechanism
		
		// bind event handlers
		
		sources.addListener(new ListChangeListener<CyclistDatasource>(){

			@Override
			public void onChanged(ListChangeListener.Change<? extends CyclistDatasource> arg0) {
				System.out.println("throw the sources into the datatable");
				
			}
			
		});
	}	
}
