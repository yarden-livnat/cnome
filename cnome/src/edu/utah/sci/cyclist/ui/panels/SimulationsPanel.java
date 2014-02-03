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
package edu.utah.sci.cyclist.ui.panels;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.model.Simulation;
import edu.utah.sci.cyclist.ui.wizards.SimulationEditorWizard;

public class SimulationsPanel extends TitledPanel  {
	public static final String ID 		= "simulations-panel";
	public static final String TITLE	= "Simulations ids";
	
	public static final String SELECTED_STYLE = "-fx-background-color: #99ccff";
	public static final String UNSELECTED_STYLE = "-fx-background-color: #f0f0f0";
	
	
	private List<Entry> _entries;
	private ObservableList<Simulation> _items;
	private ObjectProperty<Simulation> _simulationProperty = new SimpleObjectProperty<>();
	private Entry _selected = null;
	private ObjectProperty<Boolean> _editSimulationProperty = new SimpleObjectProperty<>();
	private InvalidationListener _listener = new InvalidationListener() {
		
		@Override
		public void invalidated(Observable observable) {
			resetContent();

		}
	};
	
	public SimulationsPanel() {
		super(TITLE);
		setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
	}
	
//	@Override
	public void setTitle(String title) {
		setTitle(title);
	}

	
	public void setItems(final ObservableList<Simulation> items) {
		if (_items != items) {
			if (_items != null) {
				_items.removeListener(_listener);
			}
			
			items.addListener(_listener);	
			_items = items;
		}
		
		resetContent();
	}
	
	public ObjectProperty<Boolean> editSimulationProperty() {
        return _editSimulationProperty;
}

	public Boolean getEditSimulation() {
	        return _editSimulationProperty.get();
	}
	
	public void setEditSimulation(Boolean value) {
		 _editSimulationProperty.set(value);
	}

	
	private void resetContent() {
		VBox vbox = (VBox) getContent();
		vbox.getChildren().clear();
		
		_entries = new ArrayList<>();
		if(_items != null){
			for (Simulation simulation : _items) {
				Entry entry = createEntry(simulation);
				_entries.add(entry);
				vbox.getChildren().add(entry.title);
			}
		}
	}
	
	private void select(Entry entry) {
		if (_selected != null) 
			_selected.title.setStyle(UNSELECTED_STYLE);
		_selected = entry;
		_selected.title.setStyle(SELECTED_STYLE);
	}
	
	public ReadOnlyObjectProperty<Simulation> selectedItemProperty() {
		return _simulationProperty;
	}
	
	private Entry createEntry(Simulation simulation) {
		final Entry entry = new Entry();
		entry.simulation = simulation;
		entry.title = new Label(simulation.getAlias());
		
		final SimulationsPanel _panel = this;
		
		entry.title.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
			    
				MouseEvent mouseEvent = (MouseEvent)event;
				select(entry);
				if( mouseEvent.getButton()   == MouseButton.SECONDARY){
					Window window = _panel.getParent().getScene().getWindow();
					SimulationEditorWizard wizard = new SimulationEditorWizard(entry.simulation);
					ObjectProperty<Simulation> selection = wizard.show(window);
					
					selection.addListener(new ChangeListener<Simulation>(){
						@Override
						public void changed(ObservableValue<? extends Simulation> arg0, Simulation oldVal,Simulation newVal) {
							if(newVal.getSimulationId().equals("delete")){
								removeSimulation(entry);
								setEditSimulation(true);
							}else{
								entry.simulation.setAlias(newVal.getAlias());
								entry.title.setText(newVal.getAlias());
								setEditSimulation(true);
							}
						}	
					});
				}
			}
		});
		
		entry.title.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {					
				_simulationProperty.set(entry.simulation);
		    	select(entry);
		    	
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.SIMULATION_FORMAT, Simulation.class, entry.simulation);
				
				Dragboard db = entry.title.startDragAndDrop(TransferMode.COPY);
				
				ClipboardContent content = new ClipboardContent();
				content.put(DnD.SIMULATION_FORMAT, entry.title.getText());
				
				db.setContent(content);
			}
		});	
		
		return entry;
	}
		
		
		
	public void removeSimulation(Entry entry) {
		_items.remove(entry.simulation);
	}
	
	class Entry {
		Label title;
		Simulation simulation;
	}
}
