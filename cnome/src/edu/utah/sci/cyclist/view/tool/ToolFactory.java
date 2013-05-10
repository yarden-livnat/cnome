package edu.utah.sci.cyclist.view.tool;

public interface ToolFactory {
	String getToolName();
	String getIconName();
	
	Tool create();
}
