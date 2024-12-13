 
package com.ping.adt.core.request.workbench.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Shell;

import com.ping.adt.core.request.workbench.ui.events.MessageTool;
import com.ping.adt.core.request.workbench.ui.widgets.SettingDialog;


import jakarta.inject.Named;

public class ToolbarHandler {
	
//	@Execute
//	@Optional
//	public void execute() {
//		MessageTool.PostEvent("");
//	}
	
	@Execute
	@Optional
	public void execute(
			@Named("com.ping.adt.core.request.workbench.ui.commandparameter.toolbar") String eventName,
			IEventBroker eventBroker) {
		MessageTool.PostEvent(eventBroker, eventName);
	}

		
}