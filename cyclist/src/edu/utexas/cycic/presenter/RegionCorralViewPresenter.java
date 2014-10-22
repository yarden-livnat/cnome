package edu.utexas.cycic.presenter;

import edu.utah.sci.cyclist.core.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotificationHandler;
import edu.utah.sci.cyclist.core.event.notification.CyclistTableNotification;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utexas.cycic.CycicNotifications;
import edu.utexas.cycic.RegionCorralView;


public class RegionCorralViewPresenter extends ViewPresenter {

	public RegionCorralViewPresenter(EventBus bus) {
		super(bus);
	}
	
	public void addNotificationHandlers(){
		
		addNotificationHandler(CycicNotifications.NEW_INSTIT, new CyclistNotificationHandler(){
			@Override
			public void handle(CyclistNotification event) {				
				RegionCorralView.addUnassInstit();
			}
		});	
		
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