package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolFactory;

public class RegionViewToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return RegionViewTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return RegionViewTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new RegionViewTool();
	}



}