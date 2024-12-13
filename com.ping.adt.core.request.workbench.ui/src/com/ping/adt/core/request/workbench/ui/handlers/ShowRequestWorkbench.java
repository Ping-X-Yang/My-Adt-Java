 
package com.ping.adt.core.request.workbench.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.ui.PartInitException;

import com.ping.adt.core.request.workbench.ui.common.MyPluginContants;
import com.ping.adt.core.tools.MyAdtTools;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class ShowRequestWorkbench {
	@Execute
	public void execute() {
		try {
			MyAdtTools.getActivePage().showView(MyPluginContants.REQUEST_WORKBENCH_VIEW_ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	
	@CanExecute
	public boolean canExecute() {
		
		return true;
	}
		
}