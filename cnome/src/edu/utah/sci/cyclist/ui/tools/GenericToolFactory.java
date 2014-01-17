package edu.utah.sci.cyclist.ui.tools;

public class GenericToolFactory<ToolImpl extends Tool> implements ToolFactory {

	private Class<ToolImpl> cls;
	private String name;
	private String iconName;
	
	public GenericToolFactory(Class<ToolImpl> cls, String name, String iconName) {
		this.cls = cls;
		this.name = name;
		this.iconName = iconName;
	}
	@Override
	public String getToolName() {
		return name;
	}

	@Override
	public String getIconName() {
		return iconName;
	}

	@Override
	public Tool create() throws InstantiationException, IllegalAccessException {
		return cls.newInstance();
	}

}
