package com.ping.adt.core.request.workbench.ui.QuickTransport;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.IEditorPart;

import com.ping.adt.core.request.workbench.ui.jobs.TransportObjectRequestJob;
import com.ping.adt.core.tools.MyAdtTools;
import com.sap.adt.tools.core.model.adtcore.IAdtObject;

public class QuickTransport {
	
	/**
	 * @param action 传输ABAP编辑器中的对象
	 */
	public static void transWithAdtObject(String action, String requestType, IEventBroker eventBroker) {
		IEditorPart editor;

		
		editor = MyAdtTools.getActiveEditor();
		if (editor == null) {
			return;
		}
		
		IAdtObject adtObject = MyAdtTools.getAdtObject(editor);
		if (adtObject == null) {
			return;
		}
		
		
		//找到这个对象的请求并传输到目标系统
		TransportObjectRequestJob job = new TransportObjectRequestJob( 
					MyAdtTools.getActiveShell(), 
					adtObject.getType(), 
					adtObject.getName(), 
					action,
					requestType,
					eventBroker
					);
		job.run();
	}
}
