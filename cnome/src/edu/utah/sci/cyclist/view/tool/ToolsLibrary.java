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
package edu.utah.sci.cyclist.view.tool;

public class ToolsLibrary {

	public static Tool[] list = {
		new GenericTool()
	};
	
	public static Tool getTool(String name) {
		for (Tool tool : list) {
			if (tool.getName().equals(name))
				return tool;
		}
		return null;
	}
}
