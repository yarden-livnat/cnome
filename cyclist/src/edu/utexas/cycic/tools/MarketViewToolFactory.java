package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.tools.Tool;
import edu.utah.sci.cyclist.core.tools.ToolFactory;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;

public class MarketViewToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return MarketViewTool.TOOL_NAME;
	}

	@Override
	public AwesomeIcon getIcon() {
		return MarketViewTool.ICON;
	}
	
	@Override
	public Tool create() {
		return new MarketViewTool();
	}



}