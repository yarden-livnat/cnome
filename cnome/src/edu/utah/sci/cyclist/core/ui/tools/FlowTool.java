package edu.utah.sci.cyclist.core.ui.tools;

import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.FlowPresenter;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utah.sci.cyclist.core.ui.View;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.neup.ui.views.FlowView;

public class FlowTool implements Tool {
	
	public static final String ID 			= "edu.utah.sci.cyclist.FlowTool";
	public static final String TOOL_NAME 	= "Flow";
	public static final AwesomeIcon ICON 	= AwesomeIcon.CODE_FORK; //"FontAwesome|CODE_FORK";
	
	private View _view = null;
	private ViewPresenter _presenter = null;
	
	@Override
	public String getId() {
		return ID;
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
