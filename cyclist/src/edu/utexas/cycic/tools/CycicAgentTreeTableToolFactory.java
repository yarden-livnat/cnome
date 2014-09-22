package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.tools.Tool;
import edu.utah.sci.cyclist.core.tools.ToolFactory;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;

public class CycicAgentTreeTableToolFactory implements ToolFactory {
	@Override
	public String getToolName() {
		return CycicAgentTreeTableTool.TOOL_NAME;
	}

	@Override
	public AwesomeIcon getIcon() {
		return CycicAgentTreeTableTool.ICON;
	}
	
	@Override
	public Tool create() {
		return new CycicAgentTreeTableTool();
	}
}
