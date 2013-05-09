/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.view.panels;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ListViewBuilder;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.model.Table;

public class TablesPanel extends TitledPane  {
	public static final String ID 		= "datasources-panel";
	public static final String TITLE	= "Tables";
	
	private ListView<Table> _tables;
	
	public TablesPanel() {
		build();
	}
	
//	@Override
	public void setTitle(String title) {
		setTitle(title);
	}

	
	public void setItems(ObservableList<Table> items) {
		_tables.setItems(items);
	}
	
	
	public ReadOnlyObjectProperty<Table> selectedItemProperty() {
		return _tables.getSelectionModel().selectedItemProperty();
	}
	
	private void build() {
		setId(ID);
		setText(TITLE);
		
		_tables = ListViewBuilder.<Table>create()
					.maxHeight(150)
					.prefWidth(150)
					.build();
		
		_tables.setCellFactory(new Callback<ListView<Table>, ListCell<Table>>() {
			
			@Override
			public ListCell<Table> call(ListView<Table> param) {
				return new TableCell();
			}
		});
			
		setContent(_tables);
	}
	
	class TableCell extends TextFieldListCell<Table> {		
		@Override
		public void updateItem(Table table, boolean empty) {
			super.updateItem(table, empty);
			
			if (table != null) {
				setText(table.getName());
				setOnDragDetected(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						
						DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
						clipboard.put(DnD.DATA_SOURCE_FORMAT, Table.class, TableCell.this.getItem());
						
						Dragboard db = TableCell.this.startDragAndDrop(TransferMode.COPY);
						
						ClipboardContent content = new ClipboardContent();
						content.putString(TableCell.this.getItem().getName());
						content.put(DnD.DATA_SOURCE_FORMAT, TableCell.this.getItem().getName()) ;
						content.putImage(Resources.getIcon("table"));
						
						db.setContent(content);
						
						event.consume();
					}
				});
			} else {
				setText("");
			}
		}
	}
	
}
