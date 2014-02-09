package edu.utah.sci.cyclist.core.ui.tools;

import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.ChartPresenter;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utah.sci.cyclist.core.ui.View;
import edu.utah.sci.cyclist.core.ui.views.ChartView;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;

public class ChartTool implements Tool {

	public static final String ID 			= "edu.utah.sci.cyclist.ChartTool";
	public static final String TOOL_NAME 	= "Chart";
	public static final AwesomeIcon ICON 	= AwesomeIcon.BAR_CHART_ALT;//"FontAwesome|BAR_CHART";
	
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
			_view = new ChartView();
		return _view;
	}
	
	@Override
	public ViewPresenter getPresenter(EventBus bus) {
		if (_presenter == null)
			_presenter = new ChartPresenter(bus);
		return _presenter;
	}
}