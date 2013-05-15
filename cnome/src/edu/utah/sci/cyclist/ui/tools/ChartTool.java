package edu.utah.sci.cyclist.ui.tools;

import javafx.scene.image.Image;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.presenter.ChartPresenter;
import edu.utah.sci.cyclist.presenter.Presenter;
import edu.utah.sci.cyclist.ui.View;
import edu.utah.sci.cyclist.ui.views.ChartView;

public class ChartTool implements Tool {

	public static final String ID 			= "edu.utah.sci.cyclist.ChartTool";
	public static final String TOOL_NAME 	= "Chart";
	public static final String ICON_NAME 	= "chart";
	
	private View _view = null;
	private Presenter _presenter = null;
	
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
			_view = new ChartView();
		return _view;
	}
	
	@Override
	public Presenter getPresenter(EventBus bus) {
		if (_presenter == null)
			_presenter = new ChartPresenter(bus);
		return _presenter;
	}
}