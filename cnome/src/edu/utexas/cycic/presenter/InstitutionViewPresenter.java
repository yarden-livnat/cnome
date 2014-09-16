package edu.utexas.cycic.presenter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import edu.utah.sci.cyclist.core.event.notification.CyclistNotification;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utexas.cycic.CycicNotifications;


public class InstitutionViewPresenter extends ViewPresenter {
	
	public InstitutionViewPresenter(EventBus bus) {
		super(bus);
	}
	
	public void newInstitution(ComboBox<String>	cb){
		cb.valueProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				if(newValue == "New Institution"){
					CyclistNotification notification = new CyclistNotification(CycicNotifications.NEW_INSTIT);
					broadcast(notification);
				}
			}
		});
	
	}
}