package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.ui.tools.Tool;
import edu.utah.sci.cyclist.ui.tools.ToolFactory;

public class CommoditiesViewToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return CommoditiesViewTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return CommoditiesViewTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new CommoditiesViewTool();
	}



}