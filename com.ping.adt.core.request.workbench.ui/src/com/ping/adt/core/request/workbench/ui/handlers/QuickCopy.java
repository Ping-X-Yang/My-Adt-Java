 
package com.ping.adt.core.request.workbench.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.alibaba.fastjson2.JSON;
import com.ping.adt.core.request.workbench.ui.QuickTransport.QuickTransport;
import com.ping.adt.core.request.workbench.ui.common.MyPluginContants;
import com.ping.adt.core.request.workbench.ui.events.IRequestEventConstants;
import com.ping.adt.core.request.workbench.ui.events.Message;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class QuickCopy {
	
	
	@Execute
	public void execute(IEventBroker eventBroker) {
		
		//一键打包副本并导入测试系统
		QuickTransport.transWithAdtObject(MyPluginContants.ONE_KEY_COPY, MyPluginContants.TASK, eventBroker);
	
	}
	
	
	@CanExecute
	public boolean canExecute() {
		
		return true;
	}
		
}