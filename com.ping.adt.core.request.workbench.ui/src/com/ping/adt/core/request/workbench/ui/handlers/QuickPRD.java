 
package com.ping.adt.core.request.workbench.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

import com.ping.adt.core.request.workbench.ui.QuickTransport.QuickTransport;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class QuickPRD {
	@Execute
	public void execute() {
		
		//将当前对象的修改请求号，一键传递倒是生产系统
		QuickTransport.transWithAdtObject("one_key_prd", "R");
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		
		return true;
	}
		
}