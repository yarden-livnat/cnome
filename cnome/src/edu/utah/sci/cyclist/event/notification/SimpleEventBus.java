package edu.utah.sci.cyclist.event.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleEventBus extends EventBus {

	private Map<String, List<HandlerInfo>> _handlers = new HashMap<>();
	
	@Override
	public void addHandler(String type, String target, CyclistNotificationHandler handler) {
		HandlerInfo info = new HandlerInfo(target, handler);
		
		List<HandlerInfo> list = _handlers.get(type);
		if (list == null) {
			list = new ArrayList<HandlerInfo>();
			_handlers.put(type, list);
		}

		list.add(info);
	}

	@Override
	public void removeHandler(String type, String target, CyclistNotificationHandler handler) {
		List<HandlerInfo> list = _handlers.get(type);
		if (list != null) {
			if (list.size() > 1) {
				for (HandlerInfo info : list) {
					if (info.targetId.equals(target)) {
						list.remove(info);
						break;
					}
				}
			} else {
				_handlers.remove(type);
			}
		}
	}
	
	@Override
	public void notify(CyclistNotification event) {
		List<HandlerInfo> list = _handlers.get(event.getType());
		if (list != null) {
			for (HandlerInfo info : list) {
				if (event.getDestID() == null || event.getDestID().equals(info.targetId))
						info.handler.handle(event);
			}
		}

	}
	
	class HandlerInfo {
		public String targetId;
		public CyclistNotificationHandler handler;
		
		public HandlerInfo(String target, CyclistNotificationHandler notificationHanlder) {
			targetId = target;
			handler = notificationHanlder;
		}
	}
}
