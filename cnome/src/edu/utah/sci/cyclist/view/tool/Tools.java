package edu.utah.sci.cyclist.view.tool;

public class Tools {

	public static Tool[] list = {
		new GenericTool()
	};
	
	public static Tool getTool(String name) {
		for (Tool tool : list) {
			if (tool.getName().equals(name))
				return tool;
		}
		return null;
	}
}
