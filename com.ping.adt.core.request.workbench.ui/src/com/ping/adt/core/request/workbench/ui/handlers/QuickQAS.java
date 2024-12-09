 
package com.ping.adt.core.request.workbench.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.ping.adt.core.request.workbench.ui.QuickTransport.QuickTransport;
import com.ping.adt.core.request.workbench.ui.common.MyPluginContants;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class QuickQAS {
	@Execute
	public void execute(IEventBroker eventBroker) {
		
		//将当前对象的修改请求号，一键传递倒是生产系统
	    QuickTransport.transWithAdtObject(MyPluginContants.ONE_KEY_QAS, MyPluginContants.ONE_KEY_QAS, eventBroker);
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		
		return true;
	}
		
}