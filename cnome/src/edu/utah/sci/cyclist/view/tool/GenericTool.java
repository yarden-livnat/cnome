package edu.utah.sci.cyclist.view.tool;

import javafx.scene.image.Image;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.presenter.TablePresenter;
import edu.utah.sci.cyclist.presenter.Presenter;
import edu.utah.sci.cyclist.view.SimpleTableView;
import edu.utah.sci.cyclist.view.View;

public class GenericTool implements Tool {

	@Override
	public Image getIcon() {
		return Resources.getIcon("table", 16, 16);	
	}

	@Override
	public String getName() {
		return "Table";
	}

	@Override
	public View getView() {
		return new SimpleTableView();
	}

	@Override
	public Presenter getPresenter(EventBus bus) {
		return new TablePresenter(bus);
	}

}
