package edu.utexas.cycic.tools;

import javafx.scene.image.Image;
import edu.utah.sci.cyclist.core.Resources;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utah.sci.cyclist.core.ui.View;
import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utexas.cycic.FormBuilder;
import edu.utexas.cycic.presenter.FormBuilderPresenter;

public class FormBuilderTool implements Tool {

	public static final String ID 			= "edu.utexas.cycic.FormBuilderTool";
	public static final String TOOL_NAME 	= "Facility Form";
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
			_view = new FormBuilder();
		return _view;
	}

	@Override
	public ViewPresenter getPresenter(EventBus bus) {
		if (_presenter == null)
			_presenter = new FormBuilderPresenter(bus);
		return _presenter;
	}

}
