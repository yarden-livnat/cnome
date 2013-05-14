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
package edu.utah.sci.cyclist.view;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumnBuilder;
import javafx.scene.control.TableView;
import javafx.scene.control.TableViewBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Schema;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.model.Table.Row;
import edu.utah.sci.cyclist.view.components.ViewBase;

public class SimpleTableView extends ViewBase {
	public static final String ID = "table-view";
	public static final String TITLE = "Table";
	
	private TableView<Table.Row> _tableView;
	private Table _currentTable = null;
	
	public SimpleTableView() {
		super();
		build();
	}
	
	private void build() {
		setTitle(TITLE);
		
		_tableView = TableViewBuilder.<Table.Row>create()
				.prefWidth(300)
				.prefHeight(200)
				.columnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY)
				.build();
		
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
		
		_tableView.itemsProperty().unbind();
		if (_tableView.getItems() != null) _tableView.getItems().clear();
		_tableView.getColumns().clear();	
		
		if (active) {
			setTitle(table.getName());
			Schema schema = table.getSchema();	
			
			List<TableColumn<Table.Row, ?>> cols = new ArrayList<>();
			for (int f=0; f<schema.size(); f++) {
				Field field = schema.getField(f);				
				cols.add(createColumn(field, f));
			}
			
			_tableView.getColumns().addAll(cols);
			_tableView.itemsProperty().bind(table.getRows(100));
		} else {
			setTitle("");
		}
		
		_currentTable = table;
	}
	
	
	private <T> TableColumn<Table.Row, T> createColumn(final Field field, final int col) {
		return TableColumnBuilder.<Table.Row, T>create()
				.text(field.getName())
				.cellValueFactory( new Callback<TableColumn.CellDataFeatures<Row,T>, ObservableValue<T>>() {

					@Override
					public ObservableValue<T> call(CellDataFeatures<Row, T> cell) {
						return new SimpleObjectProperty<T>(cell.getValue(), field.getName()) {
							
							@Override
							public T getValue() {
								Row row = (Row) getBean();
								return (T)row.value[col];
							}
						};
					}
				
				})
//					new PropertyValueFactory<Table.Row, T>(field.getName()))
//				.cellFactory(new Callback<TableColumn<Row,T>, TableCell<Row,T>>() {
//
//					@Override
//					public TableCell<Row, T> call(TableColumn<Row, T> col) {
//						return new GenericCell<T>(field);
//					}
//				})
				.build();
	}
	
	class GenericCell<T> extends TableCell<Table.Row, T> {
		private Field _field; 

		public GenericCell(Field field) {
			_field = field;
		}
		
		@Override
		protected void updateItem(T item, boolean empty){
			super.updateItem(item,empty);
			System.out.println("field: "+_field.getName()+"  item: "+(item == null? "null" : item.toString())+"  empty: "+empty);
		}
	}
}
