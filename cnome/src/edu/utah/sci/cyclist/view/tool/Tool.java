package edu.utah.sci.cyclist.view.tool;

import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.presenter.Presenter;
import edu.utah.sci.cyclist.view.View;
import javafx.scene.image.Image;

public interface Tool {
	Image getIcon();
	String getName();
	View getView();
	Presenter getPresenter(EventBus bus);
}
