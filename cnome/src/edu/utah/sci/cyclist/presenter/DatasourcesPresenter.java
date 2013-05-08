package edu.utah.sci.cyclist.presenter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.view.panels.TablesPanel;

public class DatasourcesPresenter extends PresenterBase {
	private TablesPanel _panel;
	private ObservableList<Table> _tables;
	private ObservableList<CyclistDatasource> _sources;
	
	public DatasourcesPresenter(EventBus bus) {
		super(bus);
	}
	
	public void setTables(ObservableList<Table> tables) {
		_tables = tables;
		_tables.addListener(new ListChangeListener<Table>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Table> c) {
				if (c.getList().size() == 1) {
					broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_FOCUS, c.getList().get(0)));
				}
				
			}});
	}
	
	public void setSources(ObservableList<CyclistDatasource> sources) {
		_sources = sources;
	}

	public void setPanel(TablesPanel panel) {
		_panel = panel;
		_panel.setItems(_tables);
		
		_panel.selectedItemProperty().addListener(new ChangeListener<Table>() {

			@Override
			public void changed(ObservableValue<? extends Table> observable, Table oldValue, Table newValue) {
				System.out.println("table selected: "+newValue.getName());
				broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_FOCUS, newValue));
			}
		});
	}
	

}
