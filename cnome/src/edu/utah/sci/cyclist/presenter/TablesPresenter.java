package edu.utah.sci.cyclist.presenter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.View;
import edu.utah.sci.cyclist.view.components.TablesPanel;

public class TablesPresenter extends PresenterBase {
	private TablesPanel _view;
	private ObservableList<Table> _tables;
	private ObservableList<CyclistDatasource> _sources;
	
	public TablesPresenter(EventBus bus) {
		super(bus);
	}
	
	public void setTables(ObservableList<Table> tables) {
		_tables = tables;
	}
	
	public void setSources(ObservableList<CyclistDatasource> sources) {
		_sources = sources;
	}
	
	@Override
	public void setView(View view) {
		if (view instanceof TablesPanel) {
			_view = (TablesPanel) view;
			_view.setItems(_tables);
			
			_view.selectedItemProperty().addListener(new ChangeListener<Table>() {

				@Override
				public void changed(ObservableValue<? extends Table> observable, Table oldValue, Table newValue) {
					System.out.println("table selected: "+newValue.getName());
					broadcast(new CyclistTableNotification(newValue));
				}
			});
		}
	}
	

}
