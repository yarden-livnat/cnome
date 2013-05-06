package edu.utah.sci.cyclist.view;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBuilder;
import javafx.scene.control.TableView;
import javafx.scene.control.TableViewBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import edu.utah.sci.cyclist.view.components.ViewBase;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Schema;
import edu.utah.sci.cyclist.model.Table;

public class GenericTableView extends ViewBase {
	public static final String ID = "table-view";
	public static final String TITLE = "Table";
	
	private TableView<Table.TableRow> _tableView;
	
	public GenericTableView() {
		super();
		build();
	}
	
	private void build() {
		setTitle(TITLE);
		
		_tableView = TableViewBuilder.<Table.TableRow>create()
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
		
		_tableView.getColumns().clear();
		
		List<TableColumn<Table.TableRow, ?>> cols = new ArrayList<>();
		
		for (int f=0; f<schema.size(); f++) {
			Field field = schema.getField(f);
			System.out.println("Field: "+field.getName());
			
			cols.add(createColumn(field));
		}
		
		_tableView.getColumns().addAll(cols);
	}
	
	private <T> TableColumn<Table.TableRow, T> createColumn(Field field) {
		return TableColumnBuilder.<Table.TableRow, T>create()
				.text(field.getName())
				.build();
	}
}
