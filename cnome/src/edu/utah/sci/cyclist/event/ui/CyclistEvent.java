/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.event.ui;

import javafx.event.Event;
import javafx.event.EventType;

public class CyclistEvent extends Event {

	public static final EventType<CyclistEvent> ANY = new EventType<CyclistEvent>(Event.ANY, "Cyclist");
	
	public CyclistEvent(EventType<? extends Event> event) {
		super(event);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1019350662405439659L;
	

}