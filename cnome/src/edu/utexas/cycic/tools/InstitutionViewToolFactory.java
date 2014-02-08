package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolFactory;

public class InstitutionViewToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return InstitutionViewTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return InstitutionViewTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new InstitutionViewTool();
	}



}