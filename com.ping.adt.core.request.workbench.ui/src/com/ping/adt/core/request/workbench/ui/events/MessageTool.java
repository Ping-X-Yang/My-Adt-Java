package com.ping.adt.core.request.workbench.ui.events;

import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PartInitException;

import com.alibaba.fastjson2.JSON;
import com.ping.adt.core.request.workbench.ui.model.Output.Data;
import com.ping.adt.core.request.workbench.ui.parts.RequestWorkbenchView;
import com.ping.adt.core.tools.MyAdtTools;


public class MessageTool {
	
	public static void PostMessage(IEventBroker broker, String viewId, String topic, List<Data> data) {
		
		
		
		try {
			MyAdtTools.getActivePage().showView(viewId);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Message message = new Message();
		message.topic = topic;
		message.data.addAll(data);
		
//		broker.post(IRequestEventConstants.SEND_MESSAGES, message);
		broker.post(IRequestEventConstants.SEND_MESSAGES, JSON.toJSONString(message));
		
	}
}
