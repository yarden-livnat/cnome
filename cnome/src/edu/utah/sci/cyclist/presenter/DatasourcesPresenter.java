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
package edu.utah.sci.cyclist.presenter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.panels.TablesPanel;

public class DatasourcesPresenter extends PresenterBase {
	private TablesPanel _panel;
	private ObservableList<Table> _tables;
//	private ObservableList<CyclistDatasource> _sources;
	
	public DatasourcesPresenter(EventBus bus) {
		super(bus);
		
		addListeners();
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
//		_sources = sources;
	}

	public void setPanel(TablesPanel panel) {
		_panel = panel;
		_panel.setItems(_tables);
		
		_panel.selectedItemProperty().addListener(new ChangeListener<Table>() {

			@Override
			public void changed(ObservableValue<? extends Table> observable, Table oldValue, Table newValue) {
				broadcast(new CyclistTableNotification(CyclistNotifications.DATASOURCE_FOCUS, newValue));
			}
		});
	}
	
	private void addListeners() {
		addNotificationHandler(CyclistNotifications.DATASOURCE_FOCUS, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification notification) {
				CyclistTableNotification tableNotification = (CyclistTableNotification) notification;
				Table table = tableNotification.getTable();
				_panel.focus(table);
			
			}
		});
	}

}
