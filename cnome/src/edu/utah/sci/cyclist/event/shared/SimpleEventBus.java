package edu.utah.sci.cyclist.event.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleEventBus extends EventBus {

	private Map<String, List<CyclistEventHandler>> _handlers = new HashMap<>();
	
	@Override
	public void addHandler(String type, CyclistEventHandler handler) {
		List<CyclistEventHandler> list = _handlers.get(type);
		if (list == null) {
			list = new ArrayList<CyclistEventHandler>();
			_handlers.put(type, list);
		}
		if (!list.contains(handler)) {
			list.add(handler);
		}

	}

	@Override
	public void removeHandler(String type, CyclistEventHandler handler) {
		List<CyclistEventHandler> list = _handlers.get(type);
		if (list != null) {
			if (list.size() > 1) {
				list.remove(handler);
			} else {
				_handlers.remove(type);
			}
		}
	}
	
	@Override
	public void fireEvent(CyclistEvent event) {
		List<CyclistEventHandler> list = _handlers.get(event.getType());
		if (list != null) {
			for (CyclistEventHandler handler : list) {
				handler.handle(event);
			}
		}

	}
}
