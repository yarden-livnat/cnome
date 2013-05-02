package edu.utah.sci.cyclist.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.event.shared.EventBus;
import edu.utah.sci.cyclist.view.MainScreen;

public class CyclistController {

	private final EventBus eventBus;
	private MainScreen screen;
	private ObservableList<String> workspaces = FXCollections.observableArrayList();
	
	private ObservableList<String> tables = FXCollections.observableArrayList();
	private ObservableList<String> sources = FXCollections.observableArrayList();
	
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
		
		ObjectProperty<String> selection = screen.selectDatatable(tables);
		selection.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldVal, String newVal) {
				System.out.println("New Table select:"+newVal);
				
			}
		});	
	}
	
	public void selectDatasource(){
		ObjectProperty<String> selection = screen.selectDatasource(sources);
		selection.addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldVal, String newVal) {
				System.out.println("New Sourceselect:"+newVal);
				
			}
		});
		
	}
	
	
	public void quit() {
		System.exit(0);
	}

	private void bind() {
		// set the history mechanism
		
		// bind event handlers
	}	
}
