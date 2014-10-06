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
 *     Kristi Potter
 *******************************************************************************/
package edu.utah.sci.cyclist.core.ui.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.model.CyclusJob;
import edu.utah.sci.cyclist.core.model.CyclusJob.Status;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

public class JobsPanel extends TitledPanel  {
	public static final String ID 		= "jobs-panel";
	public static final String TITLE	= "Jobs";
	
	public static final String SELECTED_STYLE = "-fx-background-color: #99ccff";
	public static final String UNSELECTED_STYLE = "-fx-background-color: #f0f0f0";
	public static final long ALIAS_EDIT_WAIT_TIME = 1000;
	
	
	private ContextMenu _menu = new ContextMenu();
	private Timer _timer = null;
	VBox _vbox = null;
	Boolean _entryEdit = false;
	private ListProperty<CyclusJob> _jobs = new SimpleListProperty<>();
	
	private List<Entry> _entries = new ArrayList<>();
	private ObservableList<CyclusJob> _items;
	private ObjectProperty<CyclusJob> _jobProperty = new SimpleObjectProperty<>();
	private Entry _selected = null;
	private ObjectProperty<CyclusJob> _editJobProperty = new SimpleObjectProperty<>();
	private InvalidationListener _listener = new InvalidationListener() {
		
		@Override
		public void invalidated(Observable observable) {
			resetContent();

		}
	};
	
	public JobsPanel() {
		super(TITLE, GlyphRegistry.get(AwesomeIcon.COGS));
		build();
	}
	
//	@Override
	public void setTitle(String title) {
		setTitle(title);
	}

	public ListProperty<CyclusJob> jobsProperty() {
		return _jobs;
	}
	
	private void addJob(CyclusJob job) {
		Entry entry = createEntry(job);
		_entries.add(entry);
		_vbox.getChildren().add(entry.title);
	}
	
	public void setItems(final ObservableList<CyclusJob> items) {
		if (_items != items) {
			if (_items != null) {
				_items.removeListener(_listener);
			}
			
			items.addListener(_listener);
			_items = items;
		}
		resetContent();
	}
	
	public ObjectProperty<CyclusJob> editJobProperty() {
        return _editJobProperty;
}

	public CyclusJob getEditJob() {
	        return _editJobProperty.get();
	}
	
	public void setEditJob(CyclusJob value) {
		 _editJobProperty.set(value);
	}
	
	/*
	 * In the first time of an "entry pressed" event, assign the event of pressing keyboard "esc" button to the Scene.
	 * If event has already been assigned - do nothing
	 * The event returns an entry which is in edit mode to its non-edit mode, 
	 * and saves the new edited alias in the corresponding simulation.
	 *  
	 */
	private void setKeyboardEvent(){
		Scene scene = getScene();
		if(scene.getOnKeyPressed() == null){
			scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER) {
						//Check If there is any entry in edit mode.
						if(_entryEdit){
							resetTimer();
							
							List<Node> nodes = new ArrayList<Node>(_vbox.getChildren());
							_vbox.getChildren().clear();
							
							int index=0;
							Entry entry = null;
							for(Node node : nodes){
								//For a node which has been edited - save the edited text and return to non-edit mode.
								if (node.getClass() == TextField.class){
									 entry = updateEntry((TextField)node, index);
									_vbox.getChildren().add(entry.title);
								}else{
									//Node which has not been edited - do nothing.
									Label lbl = (Label)node;
									_vbox.getChildren().add(lbl);
								}
								index++;
							}
							//Set edit mode to false.
							_entryEdit=false;
							
							//Let cyclicControler listener to know about the change.
							if(entry != null){
								setEditJob(entry.job);
							}
						}
					}
				}
			});
		}
	}
	
	/*
	 * Update an entry which has been edited with the new alias name.
	 * 
	 * @param TextField txt - the text field which contains the edited text
	 * @param int index - the index of the entry to update.
	 * #return Entry - the updated entry.
	 */
	private Entry updateEntry(TextField txt, int index){
		Entry entry = _entries.get(index);
		entry.job.setAlias(txt.getText());
		entry.title.setText(txt.getText());
		return entry;
	}
	
	/*
	 * Resets the timer which is set on entry mouse-pressed event.
	 * 
	 */
	private void resetTimer(){
		if(_timer != null){
			_timer.cancel();
			_timer.purge();
			_timer=null;
		}
	}
	
	/* 
	 * Changes a label entry to a textField entry so the text can be edited.
	 * @param - Node : the node to change from a non editable label to an editable textField.
	 */
	private void createEditableEntry(Node node){
		//Remove the label from the entry
		int index = _vbox.getChildren().indexOf(node);
		_vbox.getChildren().remove(index);
		
		//Create an editable text field with the same text as the label
		Label lbl = (Label)node;
		TextField txt = new TextField(lbl.getText());
		txt.getStyleClass().add("simulation-text-entry");
		txt.setPrefWidth(lbl.getWidth()*1.2);
		_vbox.getChildren().add(index, txt);
	}

	
	private void resetContent() {
		_vbox = (VBox) getContent();
		_vbox.getChildren().clear();
		
		if(_entries != null && _entries.size()>0){
			unbindEntries();
		}
		
		_entries = new ArrayList<>();
		if(_items != null && _items.size()>0){
			for (CyclusJob job : _items) {
				Entry entry = createEntry(job);
				entry.title.textProperty().bindBidirectional(job.aliasProperty());
				_entries.add(entry);
				_vbox.getChildren().add(entry.title);
			}
			//If this is the first and only simulation in the panel - make it also the selected simulation.
			if(_entries.size() == 1){
				_jobProperty.set(_items.get(0));
				select(_entries.get(0));
			}
		}else{
			//If the list has been reset - clean the last selection as well.
			_jobProperty.set(null);
		}
	}	
	
	private void unbindEntries(){
		for(Entry entry: _entries){
			entry.title.textProperty().unbind();
		}
	}
		
	private void select(Entry entry) {
		if (_selected != null) {
			_selected.title.setStyle(UNSELECTED_STYLE);
		}
		
		_selected = entry;
		_selected.title.setStyle(SELECTED_STYLE);
	}
	
	
	/**
	 * property is set each time a new simulation entry is selected.
	 */
	public ObjectProperty<CyclusJob> selectedItemProperty() {
		return _jobProperty;
	}
	
	/**
	 * Select a simulation entry by the code (instead of manually by the user).
	 * Mark the entry as selected and load the simulation tables.
	 * @param simulationId - the simulation to select.
	 */
	public void selectJob(String jobId){
		for(Entry entry : _entries){
			if(entry.job.getId().toString().equals(jobId)){
				select(entry);
				_jobProperty.set(entry.job);
				break;
			}
		}
	}
	
	private Label getStatusIcon(CyclusJob job) {
		AwesomeIcon icon = null;
		switch( job.getStatus()) {
		case COMPLETED:
			icon = AwesomeIcon.CHECK;
			break;
		case FAILED:
			icon = AwesomeIcon.TIMES;
			break;
		case SUBMITTED:
		case INIT:
			icon = AwesomeIcon.COG;
			break;
		}
		return GlyphRegistry.get(icon);
	}
	
	private Entry createEntry(CyclusJob job) {
		
		final Entry entry = new Entry();
		entry.job = job;
		entry.title = new Label(job.getAlias(), getStatusIcon(job));
		
		job.statusProperty().addListener(new ChangeListener<CyclusJob.Status>() {

			@Override
			public void changed(ObservableValue<? extends Status> observable,
					Status oldValue, Status newValue) {
				entry.title.setGraphic(getStatusIcon(entry.job));
				
			}
		});
		//When mouse pressed, start a timer, when timer expires- start a task to make the pressed entry editable. 
		entry.title.setOnMousePressed(new EventHandler<Event>(){
			@Override
			public void handle(Event event) {
				
				if(!_entryEdit){
					//Should be set here because need to be sure the Scene has already been created.
					setKeyboardEvent();
		
					_timer = new Timer();
					_timer.schedule( 
					        new java.util.TimerTask() {
					            @Override
					            public void run() {
					            	//Should run later because cannot do javafx actions directly from a timer task.
					            	Platform.runLater(new Runnable() {
					                    @Override
					                    public void run() {
					                    	_entryEdit = true;
					                    	for(Node node :_vbox.getChildren()){
							            		Label lbl = (Label)node;
							            		if(lbl.getText().equals(entry.title.getText())){
							            			createEditableEntry(node);
							            			break;
							            		}
							            	}
					                    }
					                });
					            }
					        }, 
					        ALIAS_EDIT_WAIT_TIME 
					);
				}
			}
		});
		
		//If mouse released event is before the "mouse pressed" timer has expired - 
		//cancel the timer. So the timer task would not be activated.
		entry.title.setOnMouseReleased(new EventHandler<Event>(){
			@Override
			public void handle(Event event) {
				resetTimer();
			}
		});
		
		entry.title.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
			    
				MouseEvent mouseEvent = (MouseEvent)event;
				select(entry);
				//Right click loads the "delete" simulation dialog box.
				if( mouseEvent.getButton()   == MouseButton.SECONDARY){
						_menu.show(entry.title, Side.BOTTOM, 0, 0);
					
				}  else if( mouseEvent.getButton()   == MouseButton.PRIMARY){
					//Left click on the mouse - loads the simulation tables.
					_jobProperty.set(entry.job);
				}
			}
		});
		
		entry.title.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {					
				resetTimer();
				select(entry);
		    	
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.JOB_FORMAT, CyclusJob.class, entry.job);
				
				Dragboard db = entry.title.startDragAndDrop(TransferMode.COPY);
				
				ClipboardContent content = new ClipboardContent();
				content.put(DnD.JOB_FORMAT, entry.title.getText());
				
				SnapshotParameters snapParams = new SnapshotParameters();
	            snapParams.setFill(Color.TRANSPARENT); 
	            content.putImage(entry.title.snapshot(snapParams, null)); 
				
				db.setContent(content);
				_jobProperty.set(entry.job);
			}
		});	
		
		return entry;
	}
		
		
		
	public void removeJob(Entry entry) {
		_items.remove(entry.job);
	}
	
	
	private void build() {
		setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		_vbox = getContent();
		
		createMenu();
		_jobs.addListener(new ListChangeListener<CyclusJob>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends CyclusJob> c) {
				while (c.next()) {
					if (c.wasPermutated()) {
						//ignore
					} else if (c.wasUpdated()) {
						for (int i = c.getFrom(); i < c.getTo(); ++i) {
							System.out.println("jobs list updated");
						}
					} else {
						for (CyclusJob job: c.getRemoved()) {
							System.out.print("job remove: "+job.getId());
						}
						for (CyclusJob job: c.getAddedSubList()) {
							System.out.println("job added: "+job.getId());
							addJob(job);
						}
					}
				}
			}
			
		});
	}
	private void createMenu(){
		 MenuItem deleteJob = new MenuItem("Delete job");
		 deleteJob.setOnAction(new EventHandler<ActionEvent>() {
		 							public void handle(ActionEvent e) { 
		 								removeJob(_selected);
		 								setEditJob(null);
		 								_selected = null;
		 							}
		 });
		 _menu.getItems().add(deleteJob);
	}
	
	class Entry {
		Label title;
		CyclusJob job;
	}
}
