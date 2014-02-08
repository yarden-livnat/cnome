package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolFactory;

public class CycicToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return CycicTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return CycicTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new CycicTool();
	}



}