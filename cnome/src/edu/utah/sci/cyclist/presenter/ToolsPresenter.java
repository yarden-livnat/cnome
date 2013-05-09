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
package edu.utah.sci.cyclist.presenter;

import java.util.List;

import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.view.panels.ToolsPanel;
import edu.utah.sci.cyclist.view.tool.Tool;

public class ToolsPresenter extends PresenterBase {

	private ToolsPanel _panel;
	
	public ToolsPresenter(EventBus bus) {
		super(bus);
	}
	
	public void setPanel(ToolsPanel panel) {
		_panel = panel;
	}
	
	public void setTools(List<Tool> tools) {
		if (_panel != null) {
			_panel.setTools(tools);
		}
	}
}
