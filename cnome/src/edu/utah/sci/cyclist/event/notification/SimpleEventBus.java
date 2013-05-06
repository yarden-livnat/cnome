package edu.utah.sci.cyclist.event.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleEventBus extends EventBus {

	private Map<String, List<CyclistNotificationHandler>> _handlers = new HashMap<>();
	
	@Override
	public void addHandler(String type, CyclistNotificationHandler handler) {
		List<CyclistNotificationHandler> list = _handlers.get(type);
		if (list == null) {
			list = new ArrayList<CyclistNotificationHandler>();
			_handlers.put(type, list);
		}
		if (!list.contains(handler)) {
			list.add(handler);
		}

	}

	@Override
	public void removeHandler(String type, CyclistNotificationHandler handler) {
		List<CyclistNotificationHandler> list = _handlers.get(type);
		if (list != null) {
			if (list.size() > 1) {
				list.remove(handler);
			} else {
				_handlers.remove(type);
			}
		}
	}
	
	@Override
	public void notify(CyclistNotification event) {
		List<CyclistNotificationHandler> list = _handlers.get(event.getType());
		if (list != null) {
			for (CyclistNotificationHandler handler : list) {
				handler.handle(event);
			}
		}

	}
}
