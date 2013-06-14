package edu.utah.sci.cyclist.ui.tools;

public class ChartToolFactory implements ToolFactory {
	@Override
	public String getToolName() {
		return ChartTool.TOOL_NAME;
	}
	
	@Override
	public String getIconName() {
		return ChartTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new ChartTool();
	}
}
