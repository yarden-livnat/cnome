package edu.utexas.cycic.tools;

import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolFactory;

public class RecipeFormToolFactory implements ToolFactory {

	@Override
	public String getToolName() {
		return RecipeFormTool.TOOL_NAME;
	}

	@Override
	public String getIconName() {
		return RecipeFormTool.ICON_NAME;
	}
	
	@Override
	public Tool create() {
		return new RecipeFormTool();
	}



}