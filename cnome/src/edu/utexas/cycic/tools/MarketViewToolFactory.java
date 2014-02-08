package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolFactory;

public class MarketViewToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return MarketViewTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return MarketViewTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new MarketViewTool();
	}



}