/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved.
 *
 * License for the specific language governing rights and limitations under Permission
 * is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: The above copyright notice 
 * and this permission notice shall be included in all copies  or substantial portions of the Software. 
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR 
 *  A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
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
