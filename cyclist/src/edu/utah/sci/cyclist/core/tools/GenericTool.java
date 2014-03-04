package edu.utah.sci.cyclist.core.tools;

import java.lang.reflect.InvocationTargetException;

import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utah.sci.cyclist.core.ui.View;

public class GenericTool implements Tool {
	
	private String _id;
	private String _name;
	private String _viewName;
	private String _presenterName;
	
//	private Class<View> _viewClass;
//	private Class<ViewPresenter> _presenterClass;
	
	private View _view = null;
	private ViewPresenter _presenter = null;
	
	public GenericTool(String id, String name, String viewName, String presenterName) {
		_id = id;
		_name = name;
		_viewName = viewName;
		_presenterName = presenterName;
	}
	
	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public View getView() {
		if (_view == null) {
			try {
				_view = (View)Class.forName(_viewName).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return _view;
	}

	@Override
	public ViewPresenter getPresenter(EventBus bus) {
		if (_presenter == null) {
			try {
				_presenter = (ViewPresenter) Class.forName(_presenterName).getConstructor(EventBus.class).newInstance(bus);;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return _presenter;
	}

}
