package edu.utah.sci.cyclist.view.tool;

public class TableToolFactory implements ToolFactory {



	@Override
	public String getToolName() {
		return TableTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return TableTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new TableTool();
	}



}
