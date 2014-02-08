package edu.utexas.cycic.tools;

import javafx.scene.image.Image;
import edu.utah.sci.cyclist.core.Resources;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utah.sci.cyclist.core.ui.View;
import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utexas.cycic.CommoditiesView;
import edu.utexas.cycic.presenter.CommoditiesViewPresenter;

public class CommoditiesViewTool implements Tool {

	public static final String ID 			= "edu.utexas.cycic.CommoditiesViewTool";
	public static final String TOOL_NAME 	= "Commodities";
	public static final String ICON_NAME 	= "CycIC";
	
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
			_view = new CommoditiesView();
		return _view;
	}

	@Override
	public ViewPresenter getPresenter(EventBus bus) {
		if (_presenter == null)
			_presenter = new CommoditiesViewPresenter(bus);
		return _presenter;
	}

}
