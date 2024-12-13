 
package com.ping.adt.core.request.workbench.ui.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;

import com.ping.adt.core.tools.MyAdtTools;
import com.sap.adt.project.IAdtCoreProject;

import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * 请求工作台上下文菜单项在哪些地方可见
 */
public class RequestWorkbenchMenuItemWhereShow {
	
	@Inject
	@Named("activeEditor")
	IEditorPart editorPart;
	
	
	@Inject
	@Named(IServiceConstants.ACTIVE_SELECTION)
	ISelection selection;
	
	@Evaluate
	public boolean evaluate() {
		IProject project = null;
		boolean visible = false;
		
		
		// 在已登录的ABAP项目的上下文菜单可见
		try {
			project = MyAdtTools.getActiveProject(selection);
		} catch (Exception e) {
		}
		
		if (MyAdtTools.isLoggedOn(project)) {
			visible = true;
		}
		
		
		return visible;
	}
}
