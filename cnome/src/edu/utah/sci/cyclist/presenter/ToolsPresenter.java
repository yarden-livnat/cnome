package edu.utah.sci.cyclist.presenter;

import java.util.List;

import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.view.panels.ToolsPanel;
import edu.utah.sci.cyclist.view.tool.Tool;

public class ToolsPresenter extends PresenterBase {

	private ToolsPanel _panel;
	
	public ToolsPresenter(EventBus bus) {
		super(bus);
	}
	
	public void setPanel(ToolsPanel panel) {
		_panel = panel;
	}
	
	public void setTools(List<Tool> tools) {
		if (_panel != null) {
			_panel.setTools(tools);
		}
	}
}
