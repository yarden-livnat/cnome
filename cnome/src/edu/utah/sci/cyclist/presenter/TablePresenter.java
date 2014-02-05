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

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.event.notification.CyclistNotifications;
import edu.utah.sci.cyclist.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.Simulation;
import edu.utah.sci.cyclist.model.Table;
import edu.utah.sci.cyclist.ui.View;


public class TablePresenter extends CyclistViewPresenter {
	
	/**
	 * TablePresenter
	 * Constructor
	 * @param bus
	 */
	
	public TablePresenter(EventBus bus) {
		super(bus);
		
		SingleSelection<Table> selectionModelTbl = new SingleSelection<Table>();
		SingleSelection<Simulation> selectionModelSim = new SingleSelection<Simulation>();
		selectionModelTbl.setOnSelectItemAction(new Closure.V2<Table, Boolean>() {

			@Override
			public void call(Table table, Boolean value) {
				getView().selectTable(table, value);
				
			}
		
		});
		
		setSelectionModelTbl(selectionModelTbl);
		setSelectionModelSim(selectionModelSim);
		addNotificationHandlers();
	}

	/**
	 * setView
	 * @param view
	 */
	
	public void setView(View view) {
		super.setView(view);
		
		getView().setOnTableDrop(new Closure.V1<Table>() {
			
			@Override
			public void call(Table table) {
				addTable(table, false /* remote */, true /* active */, false /* remoteActive */);
			}
		});
	}
	
	/**
	 * addNotficicationHandlers
	 */
	
	public void addNotificationHandlers() {
		addNotificationHandler(CyclistNotifications.DATASOURCE_ADD, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistTableNotification notification = (CyclistTableNotification) event;
				
				addTable(notification.getTable(), true /*remote*/, false /* active */, false /* remoteActive */);			
			}
		});
		
		addNotificationHandler(CyclistNotifications.DATASOURCE_REMOVE, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistTableNotification notification = (CyclistTableNotification) event;
				removeTable(notification.getTable());			
			}
		});
		
		addNotificationHandler(CyclistNotifications.DATASOURCE_SELECTED, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistTableNotification notification = (CyclistTableNotification) event;
				getSelectionModelTbl().selectItem(notification.getTable(), true);
			}
		});
		
		addNotificationHandler(CyclistNotifications.DATASOURCE_UNSELECTED, new CyclistNotificationHandler() {
			
			@Override
			public void handle(CyclistNotification event) {
				CyclistTableNotification notification = (CyclistTableNotification) event;
				getSelectionModelTbl().selectItem(notification.getTable(), false);			
			}
		});
	}
	
	
	
}
