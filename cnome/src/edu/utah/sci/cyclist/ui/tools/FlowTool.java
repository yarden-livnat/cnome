package edu.utah.sci.cyclist.ui.tools;

import javafx.scene.image.Image;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.presenter.FlowPresenter;
import edu.utah.sci.cyclist.presenter.ViewPresenter;
import edu.utah.sci.cyclist.ui.View;
import edu.utah.sci.cyclist.ui.views.FlowView;

public class FlowTool implements Tool {
	
	public static final String ID 			= "edu.utah.sci.cyclist.FlowTool";
	public static final String TOOL_NAME 	= "Flow";
	public static final String ICON_NAME 	= "flow";
	
	private View _view = null;
	private ViewPresenter _presenter = null;
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public Image getIcon() {
		return Resources.getIcon(ICON_NAME, 16, 16);	
	}

	@Override
	public String getName() {
		return TOOL_NAME;
	}

	@Override
	public View getView() {
		if (_view == null) 
			_view = new FlowView();
		return _view;
	}
	
	@Override
	public ViewPresenter getPresenter(EventBus bus) {
		if (_presenter == null)
			_presenter = new FlowPresenter(bus);
		return _presenter;
	}
}
