package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolFactory;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;

public class CommoditiesViewToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return CommoditiesViewTool.TOOL_NAME;
	}

	@Override
	public AwesomeIcon getIcon() {
		return CommoditiesViewTool.ICON;
	}
	
	@Override
	public Tool create() {
		return new CommoditiesViewTool();
	}



}