package edu.utexas.cycic.tools;

import javafx.scene.image.Image;
import edu.utah.sci.cyclist.core.Resources;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utah.sci.cyclist.core.ui.View;
import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utexas.cycic.SimulationInfo;
import edu.utexas.cycic.presenter.SimulationInfoPresenter;

public class SimulationInfoTool implements Tool {

	public static final String ID 			= "edu.utexas.cycic.SimulationInfoTool";
	public static final String TOOL_NAME 	= "Simualtion Information";
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
			_view = new SimulationInfo();
		return _view;
	}

	@Override
	public ViewPresenter getPresenter(EventBus bus) {
		if (_presenter == null)
			_presenter = new SimulationInfoPresenter(bus);
		return _presenter;
	}

}
