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
 *******************************************************************************/
package edu.utah.sci.cyclist.core.ui.views;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.event.dnd.DnDSource;
import edu.utah.sci.cyclist.core.model.CyclistDatasource;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Schema;
import edu.utah.sci.cyclist.core.model.Simulation;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.model.TableRow;
import edu.utah.sci.cyclist.core.model.proxy.TableProxy;
import edu.utah.sci.cyclist.core.ui.components.CyclistViewBase;

public class SimpleTableView extends CyclistViewBase {
	public static final String ID = "table-view";
	public static final String TITLE = "Table";
	
	private TableView<TableRow> _tableView;
	private Table _currentTable = null;
	private Simulation _currentSim = null;
	
	public SimpleTableView() {
		super();
		build();
	}
	
	private void build() {
		setTitle(null);
		
		_tableView = new TableView<TableRow>();
		_tableView.getStyleClass().add("simple-table-view");
		_tableView.setPrefSize(450, 200);
		_tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		setContent(_tableView);
		VBox.setVgrow(_tableView, Priority.NEVER);
	}
	
	@Override
	public void selectTable(Table table, boolean active) {
		super.selectTable(table, active);

		if (!active && table != _currentTable) {
			// ignore
			return;
		}
		
		if (active) {
			_currentTable = table;	
		} else {
			_currentTable = null;
		}
		
		loadTable();
	}
	
	@Override
	public void selectSimulation(Simulation sim, boolean active) {
		super.selectSimulation(sim, active);
		
		if (!active && sim != _currentSim) {
			// ignore
			return;
		}
		
		if (active) {
			_currentSim = sim;	
		} else {
			_currentSim = null;
		}
		
		if (_currentTable != null) {
			loadTable();
		}

	}
	
	private void loadTable() {
		_tableView.itemsProperty().unbind();
		if (_tableView.getItems() != null) _tableView.getItems().clear();
		_tableView.getColumns().clear();
		
		if (_currentTable != null) {
			//TODO: this be done only if the table is active
			Schema schema = _currentTable.getSchema();	
			
			List<TableColumn<TableRow, Object>> cols = new ArrayList<>();
			for (int f=0; f<schema.size(); f++) {
				Field field = schema.getField(f);				
				cols.add(createColumn(field, f));
			}
			
			_tableView.getColumns().addAll(cols);
			fetchRows();
		}
	}
	
	private void fetchRows() {	
		Task<ObservableList<TableRow>> task = new Task<ObservableList<TableRow>>() {

			@Override
			protected ObservableList<TableRow> call() throws Exception {
				TableProxy proxy = new TableProxy(_currentTable);
			
				CyclistDatasource ds = _currentSim != null? _currentSim.getDataSource() : null;
				return proxy.getRows(ds, 10000);
			}
		};
		
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		
		setCurrentTask(task);

		_tableView.itemsProperty().bind( task.valueProperty());		
	}
	
	private TableColumn<TableRow, Object> createColumn(final Field field, final int col) {
				
		TableColumn<TableRow, Object> tc = new TableColumn<>();
		tc.setText(field.getName());
		tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TableRow,Object>, ObservableValue<Object>>() {
			@Override
			public ObservableValue<Object> call(CellDataFeatures<TableRow, Object> cell) {
				return new SimpleObjectProperty<Object>(cell.getValue(), field.getName()) {
					
					@Override
					public Object getValue() {
						TableRow row = (TableRow) getBean();
						return row.value[col];
					}
				};
			}
		});		
		tc.setCellFactory(new Callback<TableColumn<TableRow, Object>, TableCell<TableRow, Object>>() {

			@Override
			public TableCell<TableRow, Object> call(TableColumn<TableRow, Object> arg0) {
				return new GenericCell<Object>(field);
			}
			
		});
		
		return tc;
					
	}
	
	class GenericCell<T> extends TableCell<TableRow, T> {
		private Field _field; 
		private Label _label;

		public GenericCell(Field field) {
			_field = field;
			_label = new Label("");
			_label.setAlignment(Pos.CENTER);
			setGraphic(_label);
			
			_label.setOnDragDetected(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
					clipboard.put(DnD.SOURCE_FORMAT, DnDSource.class, DnDSource.VALUE);
					clipboard.put(DnD.VALUE_FORMAT, Object.class, getItem()); // fix the class
					clipboard.put(DnD.FIELD_FORMAT, Field.class, _field);
					clipboard.put(DnD.TABLE_FORMAT, Table.class, _currentTable);
					
					Dragboard db = _label.startDragAndDrop(TransferMode.COPY);
					
					ClipboardContent content = new ClipboardContent();
					content.putString("value dnd"); // fix: do we need this?
					
					SnapshotParameters snapParams = new SnapshotParameters();
//		            snapParams.setFill(Color.TRANSPARENT);
		            snapParams.setFill(Color.AQUA);
		            
		            content.putImage(_label.snapshot(snapParams, null));	            
					
					db.setContent(content);
					event.consume();
				}
			});
		}
		
		@Override
		protected void updateItem(T item, boolean empty){
			super.updateItem(item,empty);
			
			_label.setText(item != null ? item.toString() : "");
		}
	}
}
