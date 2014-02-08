package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolFactory;

public class FacilitySorterToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return FacilitySorterTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return FacilitySorterTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new FacilitySorterTool();
	}



}