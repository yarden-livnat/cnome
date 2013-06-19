package edu.utah.sci.cyclist.ui.tools;

public class MapToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return MapTool.TOOL_NAME;
	}
	
	@Override
	public String getIconName() {
		return MapTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new MapTool();
	}

}
