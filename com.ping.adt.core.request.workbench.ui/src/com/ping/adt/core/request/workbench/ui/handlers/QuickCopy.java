 
package com.ping.adt.core.request.workbench.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import com.ping.adt.core.request.workbench.ui.QuickTransport.QuickTransport;
import org.eclipse.e4.core.di.annotations.CanExecute;

public class QuickCopy {
	
	
	@Execute
	public void execute() {
		
		//一键打包副本并导入测试系统
		QuickTransport.transWithAdtObject("one_key_copy", "T");
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		
		return true;
	}
		
}