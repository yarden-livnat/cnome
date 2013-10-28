package edu.utexas.cycic.tools;

import javafx.scene.image.Image;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.presenter.ViewPresenter;
import edu.utah.sci.cyclist.ui.View;
import edu.utah.sci.cyclist.ui.tools.Tool;
import edu.utexas.cycic.InstitutionView;
import edu.utexas.cycic.presenter.InstitutionViewPresenter;

public class InstitutionViewTool implements Tool {

	public static final String ID 			= "edu.utexas.cycic.InstitutionViewTool";
	public static final String TOOL_NAME 	= "Institution View";
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
			_view = new InstitutionView();
		return _view;
	}

	@Override
	public ViewPresenter getPresenter(EventBus bus) {
		if (_presenter == null)
			_presenter = new InstitutionViewPresenter(bus);
		return _presenter;
	}

}
