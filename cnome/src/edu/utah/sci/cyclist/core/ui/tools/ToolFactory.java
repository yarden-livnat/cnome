package edu.utah.sci.cyclist.core.ui.tools;

public interface ToolFactory {
	String getToolName();
	String getIconName();
	
	Tool create() throws InstantiationException, IllegalAccessException;
}
