package edu.utah.sci.cyclist.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.event.shared.EventBus;
import edu.utah.sci.cyclist.presenter.WorkspacePresenter;
import edu.utah.sci.cyclist.view.MainScreen;
import edu.utah.sci.cyclist.view.components.Workspace;

public class CyclistController {

	private final EventBus _eventBus;
	private MainScreen _screen;
	private ObservableList<String> _workspaces = FXCollections.observableArrayList();
	
	public CyclistController(EventBus eventBus) {
		this._eventBus = eventBus;
		bind();
		_workspaces.add("/Users/yarden/software");
		_workspaces.add("/Users/yarden");
	}

	public void setScreen(final MainScreen screen) {
		this._screen = screen;
		screen.setControler(this);
		
		// set up the main workspace
		Workspace workspace = new Workspace();
		screen.setWorkspace(workspace);
		
		WorkspacePresenter presenter = new WorkspacePresenter();
		presenter.setEventBus(_eventBus);
		presenter.setView(workspace);
		
		// do something?
		//selectWorkspace();
	}
	
	public void selectWorkspace() {
		ObjectProperty<String> selection = _screen.selectWorkspace(_workspaces);
		selection.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String oldVal, String newVal) {
				System.out.println("select:"+newVal);
				
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
