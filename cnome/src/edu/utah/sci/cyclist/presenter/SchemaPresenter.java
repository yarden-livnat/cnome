package edu.utah.sci.cyclist.presenter;

import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.model.Schema;
import edu.utah.sci.cyclist.view.View;
import edu.utah.sci.cyclist.view.components.SchemaPanel;


public class SchemaPresenter  implements Presenter {
	private SchemaPanel _view;
	private EventBus _eventBus;
	private Schema _schema;
	
	public SchemaPresenter(EventBus bus) {
		_eventBus = bus;
	}
	
	@Override
	public void setView(View view) {
		if (view instanceof SchemaPanel) {
			_view = (SchemaPanel) view;
	}
	}
}
