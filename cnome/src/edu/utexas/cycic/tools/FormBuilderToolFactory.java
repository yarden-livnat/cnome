package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolFactory;

public class FormBuilderToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return FormBuilderTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return FormBuilderTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new FormBuilderTool();
	}
}