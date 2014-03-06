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
package edu.utah.sci.cyclist.core.presenter;

import org.mo.closure.v1.Closure;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.core.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.model.DataType;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.model.Schema;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.ui.panels.SchemaPanel;


public class SchemaPresenter  extends PresenterBase {
	private SchemaPanel _dimensionsPanel;
	private SchemaPanel _measuresPanel;
	private Schema _schema;
	private ObservableList<Field> _dimensions;
	private ObservableList<Field> _measures;
	
	public SchemaPresenter(EventBus bus) {
		super(bus);
		addNotificationListeners();
		
	}
	
	public void setPanels(SchemaPanel dimensions, SchemaPanel measures) {
		_dimensionsPanel = dimensions;
		_measuresPanel = measures;
		
		_dimensionsPanel.setOnFieldDropAction(new Closure.V1<Field>() {

			@Override
			public void call(Field field) {
				_measures.remove(field);
				_dimensions.add(field);	
				field.setRole(DataType.Role.DIMENSION);
				_dimensionsPanel.addForceNumericFilterMenu();
			}
			
		});
		
		_measuresPanel.setOnFieldDropAction(new Closure.V1<Field>() {

			@Override
			public void call(Field field) {
				_dimensions.remove(field);	
				_measures.add(field);
				field.setRole(DataType.Role.MEASURE);
			}
			
		});
	}
	
	private void addNotificationListeners() {
		addNotificationHandler(CyclistNotifications.DATASOURCE_FOCUS, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification notification) {
				CyclistTableNotification tableNotification = (CyclistTableNotification) notification;
				Table table = tableNotification.getTable();
//				System.out.println("SchemaPresenter: received Datasource focus from:"+tableNotification.getSource()+"  table:"+table);

				_schema = table.getSchema();
				
				_dimensions = FXCollections.observableArrayList();
				_measures = FXCollections.observableArrayList();
				
				for (int f=0; f < _schema.size(); f++) {
					Field field = _schema.getField(f);
					DataType.Role role = field.getRole();
					if(role != null)
					{
						switch (role) {
						case DIMENSION:
							_dimensions.add(field);
							break;
						case MEASURE:
							_measures.add(field);
							break;
						}
					}
				}
				
				_dimensionsPanel.setFields(_dimensions);
				_dimensionsPanel.addForceNumericFilterMenu();
				_measuresPanel.setFields(_measures);
			}

		});
	}
}
