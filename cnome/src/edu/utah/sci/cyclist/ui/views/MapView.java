package edu.utah.sci.cyclist.ui.views;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.model.Table.Row;
import edu.utah.sci.cyclist.ui.components.DropArea;
import edu.utah.sci.cyclist.ui.components.ViewBase;

public class MapView extends ViewBase {
	public static final String TITLE = "Map";
	
	private BorderPane _pane;
	private DropArea _colorArea;
	
	private ObjectProperty<Table> _currentTableProperty = new SimpleObjectProperty<>();
	private ListProperty<Row> _items = new SimpleListProperty<>();
	
	public MapView() {
		super();
		build();
	}

	private void build() {
		setTitle(TITLE);
		getStyleClass().add("map-view");
	
		_pane = BorderPaneBuilder.create().prefHeight(300).prefWidth(400).build(); 
		_pane.setBottom(createControl());
		
		setContent(_pane);
	}
	
	private Node createControl() {
		GridPane grid = GridPaneBuilder.create()
				.hgap(5)
				.vgap(5)
				.padding(new Insets(0, 0, 0, 0))
				.build();
		grid.getColumnConstraints().add(new ColumnConstraints(10));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().add(cc);
		
		_colorArea = createControlArea(grid, "Color", 0, 0, DropArea.Policy.SINGLE);
		
		return grid;
	}
	
	private DropArea createControlArea(GridPane grid, String title, int  row, int col, DropArea.Policy policy) {
		
		Text text = TextBuilder.create().text(title).styleClass("input-area-header").build();
		DropArea area = new DropArea(policy);
		area.tableProperty().bind(_currentTableProperty);
		area.addListener(_areaLister);
		grid.add(text, col, row);
		grid.add(area, col+1, row);
		
		return area;
	}
	
	private InvalidationListener _areaLister = new InvalidationListener() {
		
		@Override
		public void invalidated(Observable observable) {			
//			invalidateMap();		
//			if (getCurrentTable() == null) {
//				DropArea area = (DropArea) observable;
//				if (area.getFields().size() == 1) {
//					if (getOnTableDrop() != null)
//						getOnTableDrop().call(area.getFields().get(0).getTable());
//				}
//			}
//			fetchData();
		}
	};
}
