package edu.utah.sci.cyclist.ui.tools;

public interface ToolFactory {
	String getToolName();
	String getIconName();
	
	Tool create();
}
