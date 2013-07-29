package edu.utah.sci.cyclist.ui.tools;

public class WorkspaceToolFactory implements ToolFactory {
	@Override
	public String getToolName() {
		return WorkspaceTool.TOOL_NAME;
	}
	
	@Override
	public String getIconName() {
		return WorkspaceTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new WorkspaceTool();
	}
}