package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.ui.tools.Tool;
import edu.utah.sci.cyclist.ui.tools.ToolFactory;

public class RegionCorralViewToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return RegionCorralViewTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return RegionCorralViewTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new RegionCorralViewTool();
	}



}