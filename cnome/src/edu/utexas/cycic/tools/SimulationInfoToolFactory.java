package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.ui.tools.Tool;
import edu.utah.sci.cyclist.ui.tools.ToolFactory;

public class SimulationInfoToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return SimulationInfoTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return SimulationInfoTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new SimulationInfoTool();
	}



}