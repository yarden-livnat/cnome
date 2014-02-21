package edu.utah.sci.cyclist.core.ui.tools;

import edu.utah.sci.cyclist.core.util.AwesomeIcon;

public interface ToolFactory {
	String getToolName();
	AwesomeIcon getIcon();
	
	Tool create() throws InstantiationException, IllegalAccessException;
}
