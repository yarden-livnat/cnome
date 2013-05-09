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
package edu.utah.sci.cyclist.event.notification;


public class CyclistNotification {

	private String _type;
	private Object _src = null;
	private String _destId;
	
	public CyclistNotification(String type) {
		this(type, null);
	}
	public CyclistNotification(String type, String dest) {
		_type = type;
		_destId = dest;
	}

	public void setSource(Object src) {
		if (_src != null) {
			// throw an illegal action
		}
		_src = src;
	}
	
	public String getType() {
		return _type;
	}
	
	public Object getSource() {
		return _src;
	}
	
	public String getDestID() {
		return _destId;
	}
	

}
