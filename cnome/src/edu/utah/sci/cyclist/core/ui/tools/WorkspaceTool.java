package edu.utah.sci.cyclist.core.ui.tools;

import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utah.sci.cyclist.core.presenter.WorkspacePresenter;
import edu.utah.sci.cyclist.core.ui.View;
import edu.utah.sci.cyclist.core.ui.views.Workspace;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;

public class WorkspaceTool implements Tool {

	public static final String ID 			= "edu.utah.sci.cyclist.WorkspaceTool";
	public static final String TOOL_NAME 	= "Workspace";
	public static final AwesomeIcon ICON 	= AwesomeIcon.DESKTOP; //"FontAwesome|DESKTOP";
	
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
			_view = new Workspace(false);
		return _view;
	}
	
	@Override
	public ViewPresenter getPresenter(EventBus bus) {
		if (_presenter == null)
			_presenter = new WorkspacePresenter(bus);
		return _presenter;
	}
}