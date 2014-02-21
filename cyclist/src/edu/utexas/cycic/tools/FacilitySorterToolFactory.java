package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolFactory;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;

public class FacilitySorterToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return FacilitySorterTool.TOOL_NAME;
	}

	@Override
	public AwesomeIcon getIcon() {
		return FacilitySorterTool.ICON;
	}
	
	@Override
	public Tool create() {
		return new FacilitySorterTool();
	}



}