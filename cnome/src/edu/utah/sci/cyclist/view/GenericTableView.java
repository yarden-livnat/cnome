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

public class GenericTableView extends ViewBase {
	public static final String ID = "table-view";
	public static final String TITLE = "Table";
	
	private TableView<Table.Row> _tableView;
	
	public GenericTableView() {
		super();
		build();
	}
	
	private void build() {
		setTitle(TITLE);
		
		_tableView = TableViewBuilder.<Table.Row>create()
				.prefWidth(200)
				.columnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY)
				.build();
		
		setContent(_tableView);
		VBox.setVgrow(_tableView, Priority.NEVER);
	}
	
	@Override
	public void addTable(Table table) {
		super.addTable(table);
		
		Schema schema = table.getSchema();
		
		_tableView.itemsProperty().unbind();
		_tableView.getItems().clear();
		_tableView.getColumns().clear();
		
		List<TableColumn<Table.Row, ?>> cols = new ArrayList<>();
		
		for (int f=0; f<schema.size(); f++) {
			Field field = schema.getField(f);
			System.out.println("Field: "+field.getName());
			
			cols.add(createColumn(field, f));
		}
		
		_tableView.getColumns().addAll(cols);
		_tableView.itemsProperty().bind(table.getRows(100));
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
