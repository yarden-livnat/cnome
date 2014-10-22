package edu.utexas.cycic.presenter;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utah.sci.cyclist.core.ui.View;


public class CommoditiesViewPresenter extends ViewPresenter {

	public CommoditiesViewPresenter(EventBus bus) {
		super(bus);
	}

    public View getView() {
        return (View) super.getView();
    }

    public void setView(View view) {
        super.setView(view);

            getView().setOnSelectAction(new Closure.V0() {
                @Override
                public void call() {
                    onViewSelected(getView());              }
            });
    }

    public void onViewSelected(View view) {
        super.onViewSelected(view);
    }    
}