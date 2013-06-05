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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.wizards.DatasourceWizard;
import edu.utah.sci.cyclist.ui.wizards.TableEditorWizard;

public class TablesPanel extends Panel  {
	public static final String ID 		= "cnome-panel";
	public static final String TITLE	= "Tables";
	
	public static final String SELECTED_STYLE = "-fx-background-color: #99ccff";
	public static final String UNSELECTED_STYLE = "-fx-background-color: #f0f0f0";
	
	
	private List<Entry> _entries;
	private ObservableList<Table> _items;
	private ObjectProperty<Table> _tableProperty = new SimpleObjectProperty<>();
	private Entry _selected = null;
	private InvalidationListener _listener = new InvalidationListener() {
		
		@Override
		public void invalidated(Observable observable) {
			resetContent();

		}
	};
	
	public TablesPanel() {
		super(TITLE);
	}
	
//	@Override
	public void setTitle(String title) {
		setTitle(title);
	}

	
	public void setItems(final ObservableList<Table> items) {
		if (_items != items) {
			if (_items != null) {
				_items.removeListener(_listener);
			}
			
			items.addListener(_listener);	
			_items = items;
		}
		
		resetContent();
	}
	
	public void focus(Table table) {
		for (Entry entry : _entries) {
			if (entry.table == table) {
				select(entry);
				break;
			}
		}
	}
	
	private void resetContent() {
		VBox vbox = (VBox) getContent();
		vbox.getChildren().clear();
		
		_entries = new ArrayList<>();
		for (Table table : _items) {
			Entry entry = createEntry(table);
			_entries.add(entry);
			vbox.getChildren().add(entry.title);
		}
	}
	
	private void select(Entry entry) {
		if (_selected != null) 
			_selected.title.setStyle(UNSELECTED_STYLE);
		_selected = entry;
		_selected.title.setStyle(SELECTED_STYLE);
	}
	
	public ReadOnlyObjectProperty<Table> selectedItemProperty() {
		return _tableProperty;
	}
	
	private Entry createEntry(Table table) {
		final Entry entry = new Entry();
		entry.table = table;
		entry.title = LabelBuilder.create()
						.text(table.getAlias())
						.graphic(new ImageView(Resources.getIcon("table")))
						.build();
		
		entry.title.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
			    if(((MouseEvent) event).getClickCount() == 1){
			    	_tableProperty.set(entry.table);
			    	select(entry);
			    }
			    else if(((MouseEvent) event).getClickCount() == 2){
			    	editTable(entry);
			    }
				
			}
		});
		
		
		entry.title.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {					
				
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.TABLE_FORMAT, Table.class, entry.table);
				
				Dragboard db = entry.title.startDragAndDrop(TransferMode.COPY);
				
				ClipboardContent content = new ClipboardContent();
				content.put(DnD.TABLE_FORMAT, entry.title.getText());
				content.putImage(Resources.getIcon("table"));
				
				db.setContent(content);
			}
		});		
		return entry;
	}
	
		
	public void removeTable(Entry entry) {
		_items.remove(entry.table);
	}

		
	private void editTable(final Entry entry) {
		TableEditorWizard wizard = new TableEditorWizard(entry.table);
		
		ObjectProperty<Table> selection = wizard.show(this.getParent().getScene().getWindow());
		
		selection.addListener(new ChangeListener<Table>(){
			@Override
			public void changed(ObservableValue<? extends Table> arg0, Table oldVal,Table newVal) {
				if(newVal.getName().equals("DELETE_ME"))
					removeTable(entry);
				else{
					entry.table = newVal;
					entry.title.setText(entry.table.getAlias());
				}		
			}	
		});
	}	
	
	class Entry {
		Label title;
		Table table;
	}
}
