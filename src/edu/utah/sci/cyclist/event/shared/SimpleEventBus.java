package edu.utah.sci.cyclist.event.shared;

import javafx.event.EventHandler;
import javafx.event.EventType;

public class SimpleEventBus extends EventBus {

	@Override
	public <T extends CyclistEvent> void addHandler(EventType<T> type,
			EventHandler<T> handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireEvent(CyclistEvent event) {
		// TODO Auto-generated method stub

	}

}
